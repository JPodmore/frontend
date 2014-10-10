package controllers

import com.gu.facia.client.models.CollectionConfig
import common._
import model._
import views.support.{TemplateDeduping, ForceGroupsCollection, MultimediaContainer}
import play.api.mvc.{RequestHeader, Controller, Action}
import feed.MostViewedGalleryAgent

object MostViewedGalleryController extends Controller with Logging with ExecutionContexts {

  private val page = Page("More galleries", "inpictures", "More Galleries", "more galleries")
  private val dataId: String = "multimedia/gallery"
  private val config = CollectionConfig.withDefaults(displayName = Some("more galleries"), groups = Some(List("multimedia/gallery")))

  val featuredSeries = Seq(
    ("Photographs of the day", "/news/series/ten-best-photographs-of-the-day"),
    ("Eyewitness", "/world/series/eyewitness"),
    ("From the agencies", "/artanddesign/series/from-the-agencies"),
    ("Sport picture of the day", "/sport/series/sport-picture-of-the-day")
  )

  def renderMostViewed() = Action { implicit request =>
    getMostViewedGallery match {
      case Nil => Cached(60) { JsonNotFound() }
      case galleries => Cached(900) { renderMostViewedGallery(galleries) }
    }
  }
  def renderMostViewedHtml() = renderMostViewed()

  private def getMostViewedGallery()(implicit request: RequestHeader): Seq[Content] = {
    val size = request.getQueryString("size").getOrElse("6").toInt
    MostViewedGalleryAgent.mostViewedGalleries().take(size)
  }

  private def renderMostViewedGallery(galleries: Seq[Content])(implicit request: RequestHeader) = {
    val collection = Collection(galleries)
    val templateDeduping = new TemplateDeduping

    val html = views.html.fragments.containers.gallery(
      page,
      ForceGroupsCollection.firstTwoBig(collection),
      MultimediaContainer(),
      1,
      featuredSeries = featuredSeries,
      dataId = dataId
    )(request, templateDeduping, config)

    val htmlResponse = () => views.html.mostViewedGalleries(page, html)
    val jsonResponse = () => html

    renderFormat(htmlResponse, jsonResponse, 900)
  }
}
