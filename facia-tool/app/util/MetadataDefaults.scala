package util

import model.Content
import views.support.{Podcast, Comment, CardStyle}

/** TODO this needs to be moved to Facia scala client & be integrated with Facia */
object MetadataDefaults {
  // Defaults unaffected by tone of the item
  val ImmutableDefaults = Map(
    ("isBreaking" , false),
    ("isBoosted", false),
    ("imageHide", false),
    ("imageReplace", false),
    ("showKickerSection", false),
    ("showKickerCustom", false),
    ("showBoostedHeadline", false)
  )

  // Defaults affected by tone of the item
  val MutableDefaults = Map(
    ("showMainVideo", false),
    ("showKickerTag", false),
    ("showByline", false),
    ("imageCutoutReplace", false),
    ("showQuotedHeadline", false)
  )

  val Defaults = ImmutableDefaults ++ MutableDefaults

  def apply(content: Content) = CardStyle(content) match {
    case Comment => Defaults ++ Map(
      ("showByline", true),
      ("showQuotedHeadline", true),
      ("imageCutoutReplace", true)
    )

    case Podcast => Defaults + ("showKickerTag" -> true)

    case _ if content.isVideo => Defaults + ("showMainVideo" -> true)

    case _ => Defaults
  }
}
