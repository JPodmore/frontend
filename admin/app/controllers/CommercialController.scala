package controllers.admin

import common.{ExecutionContexts, Edition, Logging}
import conf.{LiveContentApi, Configuration}
import controllers.AuthLogging
import dfp.DfpDataHydrator
import model.{Content, NoCache}
import ophan.SurgingContentAgent
import play.api.mvc.Controller
import tools.Store
import views.support.TemplateDeduping

object CommercialController extends Controller with Logging with AuthLogging with ExecutionContexts {

  private implicit def getTemplateDedupingInstance: TemplateDeduping = TemplateDeduping()

  def renderCommercial = AuthActions.AuthActionTest { implicit request =>
    NoCache(Ok(views.html.commercial.commercial(Configuration.environment.stage)))
  }

  def renderFluidAds = AuthActions.AuthActionTest { implicit request =>
    NoCache(Ok(views.html.commercial.fluidAds(Configuration.environment.stage)))
  }

  def renderSponsorships = AuthActions.AuthActionTest { implicit request =>
    val sponsoredTags = Store.getDfpSponsoredTags()
    val advertisementTags = Store.getDfpAdvertisementTags()
    val foundationSupportedTags = Store.getDfpFoundationSupportedTags()

    NoCache(Ok(views.html.commercial.sponsorships(Configuration.environment.stage, sponsoredTags, advertisementTags, foundationSupportedTags)))
  }

  def renderPageskins = AuthActions.AuthActionTest { implicit request =>
    val pageskinnedAdUnits = Store.getDfpPageSkinnedAdUnits()

    NoCache(Ok(views.html.commercial.pageskins(Configuration.environment.stage, pageskinnedAdUnits)))
  }

  def renderSurgingContent = AuthActions.AuthActionTest { implicit request =>
    val surging = SurgingContentAgent.getSurging
    NoCache(Ok(views.html.commercial.surgingpages(Configuration.environment.stage, surging)))
  }

  def renderInlineMerchandisingTargetedTags = AuthActions.AuthActionTest { implicit request =>
    val report = Store.getDfpInlineMerchandisingTargetedTagsReport()
    NoCache(Ok(views.html.commercial.inlineMerchandisingTargetedTags(Configuration.environment.stage, report)))
  }

  def renderCreativeTemplates = AuthActions.AuthActionTest.async { implicit request =>
    val templates = DfpDataHydrator.loadActiveUserDefinedCreativeTemplates()
    // get some example trails
    LiveContentApi.search(Edition(request))
      .pageSize(4)
      .response.map { response  =>
        response.results.map {
          Content(_)
        }
    } map { trails =>
      NoCache(Ok(views.html.commercial.templates(Configuration.environment.stage, templates, trails)))
    }
  }
}