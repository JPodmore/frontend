package jobs

import common.{AkkaAgent, Logging, ExecutionContexts}
import services.S3Infosec

object BlockedEmailDomainList extends ExecutionContexts with Logging {

  private val blockedDomainAgent = AkkaAgent[Set[String]](Set.empty)

  def run () {
    log.info("Updating email blocked domains list")

    val domains = S3Infosec.getBlockedEmailDomains map {
      blockedDomains => blockedDomains.split("\n").toSet
    } getOrElse Set()
    blockedDomainAgent.send(domains)
  }

  def getBlockedDomains = blockedDomainAgent.get()

}
