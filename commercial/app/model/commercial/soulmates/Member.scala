package model.commercial.soulmates

case class Member(username: String, gender: Gender, age: Int, profilePhoto: String, location: String) {

  val profileId: Option[String] = profilePhoto match {
    case Member.IdPattern(id) => Some(id)
    case _ => None
  }

  val profileUrl: String = profileId.map(id => s"https://soulmates.theguardian.com/landing/$id")
    .getOrElse("http://soulmates.theguardian.com/")

}

object Member {
  val IdPattern = """.*/([\da-f]+)/.*""".r
}


sealed case class Gender(name: String) {
  override def toString = name
}

object Woman extends Gender("Woman")

object Man extends Gender("Man")
