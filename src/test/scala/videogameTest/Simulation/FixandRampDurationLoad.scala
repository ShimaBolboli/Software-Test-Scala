package videogameTest.Simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class FixandRampDurationLoad extends Simulation {

  // 1. HTTP Configuration
  val httpProtocol = http.baseUrl(url = "https://www.videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  // 2. Scenario Definition
  def getAllVideoGames() = {
    exec(http("Get All Games")
      .get("/videogame")
      .check(status.is(200)))
  }

  def getSpecificGame() = {
    exec(http("Get a Specific Games")
      .get("/videogame/2")
      .check(status.is(200)))
  }

  val scn = scenario(name = "Basic Load Simulation")
    .exec(getAllVideoGames())
    .pause(3)
    .exec(getSpecificGame())
    .pause(3)
    .exec(getSpecificGame())

  before {
    println("Starting the load test simulation for Video Game API")
  }

  after {
    println("Completed the load test simulation for Video Game API")
  }
  // 3. Load Scenario
  setUp(
    scn.inject(
      // First Phase: Initial wait and constant user injection
      nothingFor(5), // Wait for 5 seconds
      constantUsersPerSec(10).during(10), // Inject 10 users per second for 10 seconds

      // Second Phase: Ramp up users
      rampUsersPerSec(1).to(5).during(20), // Ramp up from 1 to 5 users per second over 20 seconds

      // Third Phase: Immediate burst of users
      atOnceUsers(10), // Inject 10 users at once

      // Fourth Phase: Gradual ramp up of users
      rampUsers(20).during(30) // Ramp up from 0 to 20 users over 30 seconds
    ).protocols(httpProtocol)
  ).maxDuration(60)
}