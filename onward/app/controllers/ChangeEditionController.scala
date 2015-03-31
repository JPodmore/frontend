package controllers

import common.Edition
import play.api.mvc._

object ChangeEditionController extends Controller with PreferenceController {

  def render(editionId: String) = Action { implicit request =>
    fromEdition(editionId).map{ id =>
      switchTo("GU_EDITION" -> id.toUpperCase, s"/${id.toLowerCase}")
    }.getOrElse(NotFound)
  }

  private def fromEdition(editionId: String)(implicit request: RequestHeader) = Edition.byId(editionId).map(_.id)

}