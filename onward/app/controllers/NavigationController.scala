package controllers

import common.{LinkTo, SectionLink, NavItem, Edition}
import model.Cached
import play.api.libs.json.Json._
import play.api.libs.json.Writes
import play.api.mvc.{Action, Controller}

object NavigationController extends Controller {

  private case class SectionLinkAndEdition(link: SectionLink, edition: Edition)
  private case class NavItemAndEdition(link: NavItem, edition: Edition)

  def nav() = Action{ implicit request =>
    Cached(500) {

      implicit val sectionLinkWrites = new Writes[SectionLinkAndEdition] {
        def writes(item: SectionLinkAndEdition) = obj(
          "title" -> item.link.title,
          "href" -> LinkTo(item.link.href, item.edition)
        )
      }

      implicit val navItemWrites = new Writes[NavItemAndEdition] {
        def writes(item: NavItemAndEdition) = obj(
          "section" -> SectionLinkAndEdition(item.link.name, item.edition),
          "subSections" -> item.link.links.map(link => SectionLinkAndEdition(link, item.edition))
        )
      }

      Ok(arr(Edition.all.map { edition =>
        obj(edition.id -> arr(edition.navigation.map(item => NavItemAndEdition(item, edition))))
      }))
    }
  }
}
