package jobs

import common.{Logging, ExecutionContexts, StopWatch}
import indexes.ContentApiTagsEnumerator
import indexes.TagPages._
import model.{TagIndexListings, TagIndexPage}
import play.api.libs.iteratee.Enumeratee
import services.TagIndexesS3

import scala.concurrent.{Future, blocking}

object RebuildIndexJob extends ExecutionContexts with Logging {
  def saveToS3(parentKey: String, tagPages: Seq[TagIndexPage]) {
    val s3StopWatch = new StopWatch

    tagPages foreach { tagPage =>
      log.info(s"Uploading $parentKey ${tagPage.title} index to S3")
      TagIndexesS3.putIndex(parentKey, tagPage)
    }

    log.info(s"Uploaded ${tagPages.length} $parentKey index pages to S3 after ${s3StopWatch.elapsed}ms")

    val listingStopWatch = new StopWatch

    TagIndexesS3.putListing(parentKey, TagIndexListings.fromTagIndexPages(tagPages))

    log.info(s"Uploaded $parentKey listing in ${listingStopWatch.elapsed}ms")
  }

  /** The title for the alpha keys (A, B, C ... )
    *
    * Replace the hyphen with an ndash here as it looks better in the HTML. (The key needs to be a hyphen though so it
    * works in a web uri.)
    */
  private def alphaTitle(key: String) = key.toUpperCase.replace("-", "–")

  def rebuildKeywordIndexes() = {
    val keywords = ContentApiTagsEnumerator.enumerateTagTypeFiltered("keyword")
    val series = ContentApiTagsEnumerator.enumerateTagTypeFiltered("series")

    /** Subjects are indexed both alphabetically and by their parent section */
    (keywords andThen series).run(Enumeratee.zip(bySection, byWebTitle)) map { case (sectionMap, alphaMap) =>
      blocking {
        saveToS3("keywords", toPages(alphaMap)(alphaTitle, asciiLowerWebTitle))
        saveToS3("keywords_by_section", toPages(sectionMap)(ValidSections(_), asciiLowerWebTitle))
      }
    }
  }

  def rebuildContributorIndex() = {
    ContentApiTagsEnumerator.enumerateTagTypeFiltered("contributor").run(byContributorNameOrder) map { alphaMap =>
      blocking {
        saveToS3("contributors", toPages(alphaMap)(alphaTitle, nameOrder))
      }
    }
  }

  implicit class RichFuture[A](future: Future[A]) {
    def withErrorLogging = {
      future onFailure {
        case throwable: Throwable => log.error("Error rebuilding index", throwable)
      }

      future
    }
  }

  def run() {
    rebuildKeywordIndexes().withErrorLogging andThen { case _ => rebuildContributorIndex().withErrorLogging }
  }
}
