package views.support

import model.Trail

object TrailCssClasses {
  def toneClass(trail: Trail, suffix: String) = {
    val tone = CardStyle(trail).toneString
    s"tone-$tone$suffix"
  }

  /** Article will soon support all tone classes so we'll be able to remove this silliness */
  val SupportedArticleTones: Set[CardStyle] = Set(
    Media,
    Comment,
    LiveBlog,
    DeadBlog,
    Feature
  )

  def articleToneClass(trail: Trail) = {
    if (SupportedArticleTones.contains(CardStyle(trail))) {
      toneClass(trail, "")
    }
  }
}
