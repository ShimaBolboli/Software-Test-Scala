package videogameTest.feeders

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicCustomFeeder extends Simulation{

  // 1. HTTP Configuration
  val httpProtocal = http.baseUrl(url = "https://www.videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  // 2. Scenario Definition
  var idNumbers = (1 to 10).iterator
  var customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getSpecificVideoGame() = {
    repeat(10) {
      feed(customFeeder)
        .exec(http("Get video game with id - #{gameId}")
          .get("/videogame/#{gameId}")
          .check(status.is(200)))
        .pause(1)
    }
  }

  val scn = scenario(name = "Basic Custom videogameTest.Feeder Test")
    .exec(getSpecificVideoGame())

  // 3. Load Scenario
  setUp(scn.inject(atOnceUsers(users = 1)).protocols(httpProtocal))
  before {
    println("Starting the Gatling test...")
  }

  after {
    println("Gatling test execution completed.")
  }

}
