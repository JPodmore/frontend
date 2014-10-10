package test

import org.scalatest.{DoNotDiscover, FlatSpec, Matchers}
import play.api.test.Helpers._
import play.api.test.FakeRequest
import controllers.CommentsController
import discussion.model.DiscussionKey

@DoNotDiscover class CommentPageControllerTest extends FlatSpec with Matchers with ConfiguredTestSuite {

  val callbackName = "foo"

  "Discussion" should "return 200" in {
    val result = CommentsController.comments(DiscussionKey("p/37v3a"))(TestRequest())
    status(result) should be(200)
  }

  it should "return JSONP when callback is supplied" in {
    val fakeRequest = FakeRequest(GET, "/discussion/p/37v3a.json?callback=" + callbackName).withHeaders("host" -> "localhost:9000")
    val result = CommentsController.commentsJson(DiscussionKey("p/37v3a"))(fakeRequest)

    status(result) should be(200)
    contentType(result).get should be("application/javascript")
    contentAsString(result).substring(0, 200) should startWith(callbackName + "({") // the callback
  }

}
