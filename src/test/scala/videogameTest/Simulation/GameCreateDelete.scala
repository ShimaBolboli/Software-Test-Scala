package videogameTest.Simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class GameCreateDelete extends Simulation {

  before {
    println("Starting the GameCreateDelete simulation...")
  }

  // After block to print message after the simulation ends
  after {
    println("GameCreateDelete simulation has ended.")
  }

  // 1. HTTP Configuration
  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // 2. Scenario Definition
  def authenticate() = {
    exec(http("Authenticate")
      .post("/authenticate")
      .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
      .check(jsonPath("$.token").saveAs("token")))
  }

  def createNewGame() = {
    exec(http(requestName = "Create a New Game")
      .post("/videogame")
      .header(name = "Authorization", value = "Bearer #{token}")
      .body(StringBody(string = "{\n  \"category\": \"Animation\",\n  \"name\": \"InsideOut2\",\n  \"rating\": \"Mature\",\n  \"releaseDate\": \"2024-05-04\",\n  \"reviewScore\": 95\n}")))
  }

  def deleteGame() = {
    exec(http("Delete Game")
      .delete("/videogame/6")
      .header(name = "Authorization", value = "Bearer #{token}")
      .check(status.is(200))
      .check(bodyString.is("Video game deleted")))
  }
  def getAllGames() = {
    repeat(times =3) {
      exec(http(requestName = "Get All Games")
        .get("/videogame")
        .check(status.is(200)))
    }
  }
  val scn = scenario("Authenticate and Create/Delete Game")
    .exec(authenticate())
    .exec(createNewGame())
    .exec(deleteGame())
    .exec(getAllGames())

  // 3. Load Scenario
  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}
