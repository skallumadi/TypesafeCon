package controllers

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import anorm.Id
import org.joda.time.DateTime
import models.S1Event
import models.Speaker
import tools.LoremIpsum
import models.User
import tools.TestTools._
import tools.StringTools._

class GetUserTest extends Specification {

	"The Get User API call" should {
		
		"Return error when requesting an inexisting id" in new GetUserTestCase {
			running(FakeApplication()) {
				val Some(result) = routeAndCall(FakeRequest(GET, "/users/1000000"))
					
				status(result) must beEqualTo(BAD_REQUEST)
				contentType(result) must beSome("application/json")
				
				contentAsString(result) match {
					case ValidResponse(status, message, result) =>
						status must beEqualTo("ERROR")
						message must beEqualTo("User not found")
					case content => failure("Invalid response format: '" + content + "'")
				}
			}
		}
		
		"Retrieve a user when provided with an existing id" in new GetUserTestCase {
			running(FakeApplication()) {
				val Some(result) = routeAndCall(FakeRequest(GET, "/users/" + testUser.id))
					
				status(result) must beEqualTo(OK)
				contentType(result) must beSome("application/json")
				
				contentAsString(result) match {
					case ValidResponse(status, message, result) =>
						status must beEqualTo("OK")
						result must contain(""""firstName":"John"""")
						result must contain(""""lastName":"Doe"""")
						result must contain(""""email":"john@example.com"""")
						result must contain(""""twitter":"john"""")
						result must contain(""""facebook":"JohnDoe"""")
						result must contain(""""phone":"987-1234567"""")
						result must contain(""""website":"example.com"""")
					case content => failure("Invalid response format: '" + content + "'")
				}
			}
		}
		
	}
}

trait GetUserTestCase extends After {
	
	var testUser:User = _
	
	// Create a test user before the test case
	running(FakeApplication()) {
		testUser = User("john@example.com","John", "Doe", "john", "JohnDoe", "987-1234567", "example.com").create.get
	}
		
	// Remove the test data
	def after = running(FakeApplication()) { 
		User.findById(testUser.id.get).map(_.delete)
	}
}