package videogameTest.Feeder

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


class JsonFeederGame extends Simulation{
  // 1. HTTP Configuration
  val httpProtocal = http.baseUrl(url = "https://www.videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  // 2. Scenario Definition
  val jsonFeeder = jsonFile("data/gameJsonFile.Json").circular

  val scn = scenario("Json Feeder Test")
    .feed(jsonFeeder)
    .exec(http("Get video game with name - ${name}")
      .get("/videogame/${gameId}")
      .check(jsonPath("$.name").is("${name}"))
    )
    .pause(1.second)




  // 3. Load Scenario
  setUp(scn.inject(atOnceUsers(users = 1)).protocols(httpProtocal))
}


