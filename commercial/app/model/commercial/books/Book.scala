package model.commercial.books

import common.ExecutionContexts
import model.commercial.{Ad, AdAgent, Segment, intersects, lastPart}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

import scala.concurrent.Future


case class Book(title: String,
                author: Option[String],
                isbn: String,
                price: Option[Double] = None,
                offerPrice: Option[Double] = None,
                description: Option[String] = None,
                jacketUrl: Option[String],
                buyUrl: Option[String] = None,
                position: Option[Int] = None,
                category: Option[String] = None,
                keywordIds: Seq[String] = Nil
               )
  extends Ad {

  def isTargetedAt(segment: Segment): Boolean = intersects(lastPart(keywordIds), segment.context.keywords)
}

object Book {

  private val authorReads = {
    ((JsPath \ "author_firstname").readNullable[String] and
      (JsPath \ "author_lastname").readNullable[String])
      .tupled.map { case (optFirstName, optLastName) =>
      for {
        firstName <- optFirstName
        lastName <- optLastName
      } yield s"$firstName $lastName"
    }
  }

  implicit val bookReads: Reads[Book] = (
    (JsPath \ "name").read[String] and
      authorReads and
      (JsPath \ "isbn").read[String] and
      (JsPath \ "regular_price_with_tax").readNullable[String].map(_.map(_.toDouble)) and
      (JsPath \ "final_price_with_tax").readNullable[Double] and
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "images")(0).readNullable[String] and
      (JsPath \ "product_url").readNullable[String] and
      (JsPath \ "guardian_bestseller_rank").readNullable[String].map(_.map(_.toDouble.toInt)) and
      ((JsPath \ "categories")(0) \ "name").readNullable[String] and
      (JsPath \ "keywordIds").readNullable[Seq[String]].map(_ getOrElse Nil)
    )(Book.apply _)
}


object BestsellersAgent extends AdAgent[Book] with ExecutionContexts {

  private lazy val feeds = Seq(MagentoBestsellersFeed)

  def getSpecificBook(isbn: String) = currentAds find(_.isbn == isbn)
  def getSpecificBooks(specifics: Seq[String]) = currentAds.filter(specifics contains _.isbn)

  override def adsTargetedAt(segment: Segment): Seq[Book] = super.adsTargetedAt(segment).sortBy(_.position).take(10)

  override def defaultAds: Seq[Book] = currentAds filter (_.category.exists(_ == "General"))

  def refresh() {

    val bookListsLoading: Future[Seq[Seq[Book]]] = Future.sequence {
      feeds.foldLeft(Seq[Future[Seq[Book]]]()) {
        (soFar, feed) =>
          soFar :+ feed.loadAds().recover {
            case _ => Nil
          }
      }
    }

    for (books <- bookListsLoading) {
      updateCurrentAds(books.flatten.distinct)
    }
  }
}
