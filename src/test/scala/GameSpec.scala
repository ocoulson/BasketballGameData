import org.scalatest.{FunSpec, Matchers}

class GameSpec extends FunSpec with Matchers {

  describe("Adding valid inputs in valid order") {
    val input1 = "0x801002"
    val input2 = "0xf81016"
    val input3 = "0x1d8102f"
    val input4 = "0x248202a"
    val inputs = List(input1, input2, input3, input4).map(input => (input, GameSnapshot(input))).toMap

    it("should behave correctly with one valid added value") {
      val game = new Game
      game.addSnapshot(input1)

      game.lastEvent shouldEqual inputs(input1)
      val allEvents = List(inputs(input1).get)
      game.lastNEvents(10) shouldEqual allEvents
      game.allEvents shouldEqual allEvents
    }

    it("should behave correctly with multiple valid values added in order") {
      val game = new Game
      game.addSnapshot(input1)
      game.addSnapshot(input2)
      game.addSnapshot(input3)
      game.addSnapshot(input4)

      game.lastEvent shouldEqual inputs(input4)
      val allEvents = List(inputs(input4).get, inputs(input3).get, inputs(input2).get, inputs(input1).get)
      game.lastNEvents(5) shouldEqual allEvents
      game.allEvents shouldEqual allEvents

      game.lastNEvents(2) shouldEqual List(inputs(input4).get, inputs(input3).get)
    }

    it("should behave correctly with multiple valid values added out of order") {
      val game = new Game
      game.addSnapshot(input3)
      game.addSnapshot(input2)
      game.addSnapshot(input1)
      game.addSnapshot(input4)

      game.lastEvent shouldEqual inputs(input4)
      val allEvents = List(inputs(input4).get, inputs(input3).get, inputs(input2).get, inputs(input1).get)
      game.lastNEvents(5) shouldEqual allEvents
      game.allEvents shouldEqual allEvents

      game.lastNEvents(2) shouldEqual List(inputs(input4).get, inputs(input3).get)
    }
  }
}
