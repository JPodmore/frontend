package controllers

import play.api.mvc.{ Controller, Action, RequestHeader }
import common._
import model._
import scala.concurrent.Future
import implicits.Requests
import conf.LiveContentApi
import com.gu.openplatform.contentapi.ApiError
import com.gu.openplatform.contentapi.model.{Content => ApiContent}
import views.support.{MultimediaContainer, TemplateDeduping}

object MediaInSectionController extends Controller with Logging with Paging with ExecutionContexts with Requests {

  implicit def getTemplateDedupingInstance: TemplateDeduping = TemplateDeduping()

  // These exist to work around the absence of default values in Play routing.
  def renderSectionMediaWithSeries(mediaType: String, sectionId: String, seriesId: String) =
    renderMedia(mediaType, sectionId, Some(seriesId))
  def renderSectionMedia(mediaType: String, sectionId: String) = renderMedia(mediaType, sectionId, None)

  private def renderMedia(mediaType: String, sectionId: String, seriesId: Option[String]) = Action.async { implicit request =>
    val response = lookup(Edition(request), mediaType, sectionId, seriesId) map { seriesItems =>
      seriesItems map { trail => renderSectionTrails(mediaType, trail, sectionId) }
    }
    response map { _ getOrElse NotFound }
  }

  private def lookup(edition: Edition, mediaType: String, sectionId: String, seriesId: Option[String])(implicit request: RequestHeader): Future[Option[Seq[Content]]] = {
    val currentShortUrl = request.getQueryString("shortUrl").getOrElse("")
    log.info(s"Fetching $mediaType content in section: ${sectionId}")

    // Subtract the series id from the content type.
    val tags = List(Some(s"type/$mediaType"), seriesId).flatten.mkString(",-")

    def isCurrentStory(content: ApiContent) = content.safeFields.get("shortUrl").map{ shortUrl => !shortUrl.equals(currentShortUrl) }.getOrElse(false)

    val promiseOrResponse = LiveContentApi.search(edition)
      .section(sectionId)
      .tag(tags)
      .showTags("all")
      .showFields("all")
      .response
      .map {
      response =>
        response.results filter { content => isCurrentStory(content) } map { result =>
          Content(result)
        } match {
          case Nil => None
          case results => Some(results)
        }
    }

    promiseOrResponse.recover{ case ApiError(404, message) =>
      log.info(s"Got a 404 calling content api: $message" )
      None
    }
  }

  private def renderSectionTrails(mediaType: String, trails: Seq[Content], sectionId: String)(implicit request: RequestHeader) = {
    val sectionName = trails.headOption.map(t => t.sectionName).getOrElse("")

    // Content API doesn't understand the alias 'uk-news'.
    val sectionTag = sectionId match {
      case "uk-news" => "uk"
      case _ => sectionId
    }
    val tagCombinedHref = s"$sectionTag/$sectionTag+content/$mediaType"
    val pluralMediaType = mediaType match {
      case "audio" => "audio"
      case m => s"${m}s"
    }
    implicit val config = Config(id = sectionId, href = Some(tagCombinedHref), displayName = Some(s"More ${sectionName} $pluralMediaType") )
    val response = () => views.html.fragments.containers.multimedia(Collection(trails.take(3)), MultimediaContainer(), 1, "content", useInlinePlayer = false)
    renderFormat(response, response, 1)
  }
}
