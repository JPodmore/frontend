package controllers.admin

import play.api.mvc.{RequestHeader, Action, Controller, Result => PlayResult}
import play.api.libs.ws.WS
import play.twirl.api.Html
import common.{Logging, ExecutionContexts}
import football.services.GetPaClient
import football.model.{SnapFields, PA}
import model.{NoCache, Cached}
import conf.Configuration
import scala.concurrent.Future
import pa._
import concurrent.FutureOpt
import org.joda.time.LocalDate
import football.model.SnapFields
import pa.Season
import pa.Fixture
import pa.Result
import pa.LiveMatch
import play.api.libs.json.JsResultException


object FrontsController extends Controller with ExecutionContexts with GetPaClient with Logging {
  val SNAP_TYPE = "json.html"
  val SNAP_CSS = "football"

  def index = Action.async { implicit request =>
    for {
      competitions <- client.competitions.map(PA.filterCompetitions)
      competitionTeams <- Future.traverse(competitions){comp => client.teams(comp.competitionId, comp.startDate, comp.endDate)}
      allTeams = competitionTeams.flatten.distinct
    } yield {
      Cached(3600)(Ok(views.html.football.fronts.index(competitions, allTeams)))
    }
  }

  def matchDay = Action.async { implicit request =>
    val snapFields = SnapFields(SNAP_TYPE, SNAP_CSS, s"$host/football/live.json", s"${Configuration.site.host}/football/live", "Live matches", "Today's matches")
    previewFrontsComponent(snapFields)
  }

  def resultsRedirect = Action { implicit request =>
    val submission = request.body.asFormUrlEncoded.get
    val competitionId = submission.get("competition").get.head
    Cached(60)(SeeOther(s"/admin/football/fronts/results/$competitionId"))
  }

  def results(competitionId: String) = Action.async { implicit request =>
    val foResult =
      if ("all" == competitionId) {
        val snapFields = SnapFields(SNAP_TYPE, SNAP_CSS, s"$host/football/results.json", s"${Configuration.site.host}/football/results", "Results", "View the full results from today's matches")
        FutureOpt.fromFuture(previewFrontsComponent(snapFields))
      } else {
        for {
          season <- getCompetition(competitionId)
          competitionName = PA.competitionName(season)
          snapFields = SnapFields(SNAP_TYPE, SNAP_CSS, s"$host/football/$competitionId/results.json", s"${Configuration.site.host}/football/results", s"$competitionName results", s"View the full results from today's $competitionName matches")
          previewContent <- FutureOpt.fromFuture(previewFrontsComponent(snapFields))
        } yield previewContent
      }
    foResult.getOrElse(NoCache(NotFound(views.html.football.error(s"Competition $competitionId not found"))))
  }

  def fixturesRedirect = Action { implicit request =>
    val submission = request.body.asFormUrlEncoded.get
    val competitionId = submission.get("competition").get.head
    Cached(60)(SeeOther(s"/admin/football/fronts/fixtures/$competitionId"))
  }

  def fixtures(competitionId: String) = Action.async { implicit request =>
    val foResult =
      if ("all" == competitionId) {
        val snapFields = SnapFields(SNAP_TYPE, SNAP_CSS, s"$host/football/fixtures.json", s"${Configuration.site.host}/football/fixtures", "Upcoming fixtures", "See which teams are up against each other")
        FutureOpt.fromFuture(previewFrontsComponent(snapFields))
      } else {
        for {
          season <- getCompetition(competitionId)
          competitionName = PA.competitionName(season)
          snapFields = SnapFields(SNAP_TYPE, SNAP_CSS, s"$host/football/$competitionId/fixtures.json", s"${Configuration.site.host}/football/fixtures", s"$competitionName upcoming fixtures", s"See which $competitionName teams are up against each other")
          previewContent <- FutureOpt.fromFuture(previewFrontsComponent(snapFields))
        } yield previewContent
      }
    foResult.getOrElse(NoCache(NotFound(views.html.football.error(s"Competition $competitionId not found"))))
  }

  def tablesRedirect = Action { implicit request =>
    val submission = request.body.asFormUrlEncoded.get
    val competitionId = submission.get("competition").get.head
    val url = submission.get("group").flatMap(_.filterNot(_.isEmpty).headOption) match {
      case Some(group) => s"/admin/football/fronts/tables/$competitionId/$group"
      case None => s"/admin/football/fronts/tables/$competitionId"
    }
    NoCache(SeeOther(url))
  }

  def tables(competitionId: String) = Action.async { implicit request =>
    val foResult =
      for {
        season <- getCompetition(competitionId)
        competitionName = PA.competitionName(season)
        snapFields = SnapFields(SNAP_TYPE, SNAP_CSS, s"$host/football/$competitionId/table.json", s"${Configuration.site.host}/football/tables", s"$competitionName table", s"View the full standing for the $competitionName")
        previewContent <- FutureOpt.fromFuture(previewFrontsComponent(snapFields))
      } yield previewContent
    foResult.getOrElse(NoCache(NotFound(views.html.football.error(s"Competition $competitionId not found"))))
  }

  def groupTables(competitionId: String, group: String) = Action.async { implicit request =>
    val fancyGroupName = group.replace('-', ' ')
    val foResult =
      for {
        season <- getCompetition(competitionId)
        competitionName = PA.competitionName(season)
        snapFields = SnapFields(SNAP_TYPE, SNAP_CSS, s"$host/football/$competitionId/$group/table.json", s"${Configuration.site.host}/football/tables", s"$competitionName $fancyGroupName table", s"View the $fancyGroupName standing for the $competitionName")
        previewContent <- FutureOpt.fromFuture(previewFrontsComponent(snapFields))
      } yield previewContent
    foResult.getOrElse(NoCache(NotFound(views.html.football.error(s"Competition $competitionId not found"))))
  }

  /* Matches */


  def matchesRedirect = Action { implicit request =>
    val submission = request.body.asFormUrlEncoded.get
    val competitionId = submission.get("competition").get.head
    val url = (submission.get("team1").flatMap(_.filterNot(_.isEmpty).headOption), submission.get("team2").flatMap(_.filterNot(_.isEmpty).headOption)) match {
      case (Some(team1Id), Some(team2Id)) => s"/admin/football/fronts/matches/$competitionId/$team1Id/$team2Id"
      case (Some(team1Id), None) => s"/admin/football/fronts/matches/$competitionId/$team1Id"
      case (None, Some(team2Id)) => s"/admin/football/fronts/matches/$competitionId/$team2Id"
      case (_, _) => s"/admin/football/fronts/matches/$competitionId"
    }
    Cached(60)(SeeOther(url))
  }

  def chooseMatchForComp(competitionId: String) = chooseMatch(competitionId, None, None)
  def chooseMatchForCompAndTeam(competitionId: String, team1Id: String) = chooseMatch(competitionId, Some(team1Id), None)
  def chooseMatchForCompAndTeams(competitionId: String, team1Id: String, team2Id: String) = chooseMatch(competitionId, Some(team1Id), Some(team2Id))
  private def chooseMatch(competitionId: String, team1IdOpt: Option[String], team2IdOpt: Option[String]) =AuthActions.AuthActionTest.async { implicit request =>
    for {
      (liveMatches, fixtures, results) <- getMatchesFor(competitionId, team1IdOpt, team2IdOpt)
    } yield Cached(60)(Ok(views.html.football.fronts.matchesList(liveMatches, fixtures, results)))
  }

  def bigMatchSpecial(matchId: String) =AuthActions.AuthActionTest.async { implicit request =>
    for {
      matchInfo <- client.matchInfo(matchId)
      trailText = {
        matchInfo.competition.fold("")(c => s"${c.name}, ") + matchInfo.venue.fold("")(c => s"${c.name}, ") + matchInfo.date.toString("HH:mm")
      }
      snapFields = SnapFields(SNAP_TYPE, SNAP_CSS, s"$host/football/api/big-match-special/$matchId.json",
        s"${Configuration.site.host}/football/match-redirect/$matchId",
        s"${matchInfo.homeTeam.name} v ${matchInfo.awayTeam.name}",
        trailText)
      previewContent <- previewFrontsComponent(snapFields)
    } yield previewContent
  }

  private def getCompetition(competitionId: String): FutureOpt[Season] = {
    FutureOpt {
      for {
        competitionOpt <- client.competitions.map(PA.filterCompetitions(_).find(_.competitionId == competitionId))
      } yield competitionOpt
    }
  }

  private def getMatchesFor(competitionId: String, team1IdOpt: Option[String], team2IdOpt: Option[String]): Future[(List[LiveMatch], List[Fixture], List[pa.Result])] = {
    val today = LocalDate.now
    val fLiveMatches = client.liveMatches(competitionId)
    val fFixtures = client.fixtures(competitionId)
    val fResults = client.results(competitionId, today.minusDays(2))
    for {
      liveMatches <- fLiveMatches
      fixtures <- fFixtures
      results <- fResults
    } yield {
      val filterFn: FootballMatch => Boolean = (team1IdOpt, team2IdOpt) match {
        case (Some(team1Id), Some(team2Id)) =>
          (lm: FootballMatch) => hasTeam(lm, team1Id) && hasTeam(lm, team2Id)
        case (Some(team1Id), None) =>
          (lm: FootballMatch) => hasTeam(lm, team1Id)
        case (None, Some(team2Id)) =>
          (lm: FootballMatch) => hasTeam(lm, team2Id)
        case (None, None) => (_) =>
          true
      }
      (
        liveMatches.filter(filterFn),
        fixtures.filter(filterFn),
        results.filter(filterFn)
      )
    }
  }
  private def hasTeam(m: FootballMatch, teamId: String) = m.homeTeam.id == teamId || m.awayTeam.id == teamId

  private def previewFrontsComponent(snapFields: SnapFields): Future[PlayResult] = {
    import play.api.Play.current
    val result = (for {
      previewResponse <- WS.url(snapFields.uri).get()
    } yield {
      val embedContent = (previewResponse.json \ "html").as[String]
      Cached(60)(Ok(views.html.football.fronts.viewEmbed(Html(embedContent), snapFields)))
    }).recover { case e =>
       log.error(s"Failed to preview snap content from ${snapFields.uri}", e)
       NoCache(Ok(views.html.football.fronts.failedEmbed(Html(e.getMessage), snapFields)))
    }
    result
  }

  private def host(implicit request: RequestHeader): String = {
    if (Configuration.sport.apiUrl.isEmpty) s"http://${request.host}"
    else Configuration.sport.apiUrl
  }
}