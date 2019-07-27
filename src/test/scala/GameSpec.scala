import org.scalatest.{FunSpec, Matchers}

class GameSpec extends FunSpec with Matchers {

  private val input1 = "0x801002"
  private val input2 = "0xf81016"
  private val input3 = "0x1d8102f"
  private val input4 = "0x248202a"
  private val inputs = List(input1, input2, input3, input4).map(input => (input, GameSnapshot(input).get)).toMap

  describe("Adding valid inputs") {

    it("should behave correctly with one valid added value") {
      val game = new Game
      game.addEvent(input1)

      game.lastEvent shouldEqual Some(inputs(input1))
      val allEvents = List(inputs(input1))
      game.lastNEvents(10) shouldEqual allEvents
      game.allEvents shouldEqual allEvents
    }

    it("should behave correctly with multiple valid values added in order") {
      val game = new Game
      game.addEvent(input1)
      game.addEvent(input2)
      game.addEvent(input3)
      game.addEvent(input4)

      game.lastEvent shouldEqual Some(inputs(input4))
      val allEvents = List(inputs(input4), inputs(input3), inputs(input2), inputs(input1))
      game.lastNEvents(5) shouldEqual allEvents
      game.allEvents shouldEqual allEvents

      game.lastNEvents(2) shouldEqual List(inputs(input4), inputs(input3))
    }

    it("should behave correctly with multiple valid values added out of order") {
      val game = new Game
      game.addEvent(input3)
      game.addEvent(input2)
      game.addEvent(input1)
      game.addEvent(input4)

      game.lastEvent shouldEqual Some(inputs(input4))
      val allEvents = List(inputs(input4), inputs(input3), inputs(input2), inputs(input1))
      game.lastNEvents(5) shouldEqual allEvents
      game.allEvents shouldEqual allEvents

      game.lastNEvents(2) shouldEqual List(inputs(input4), inputs(input3))
    }

    it("should behave correctly with the good example dataset") {
      val game = new Game
      TestGameData.goodData.foreach(game.addEvent)

      game.lastEvent shouldEqual Some(GameSnapshot(2, team2Scored = false, 27, 29, 598))
      game.allEvents shouldEqual TestGameData.goodData.reverse.map(GameSnapshot(_).get)

      game.lastNEvents(3) shouldEqual List(
        GameSnapshot(2, team2Scored = false, 27, 29, 598),
        GameSnapshot(2, team2Scored = false, 25, 29, 581),
        GameSnapshot(1, team2Scored = true, 23, 29, 559))
    }
  }

  describe("adding valid and invalid inputs") {
    it("should ignore an input that is a duplicate of the last input") {
      val game = new Game
      game.addEvent(input1)
      game.addEvent(input2)
      game.addEvent(input3)
      game.addEvent(input3)

      game.lastEvent shouldEqual Some(inputs(input3))
      val allEvents = List(inputs(input3), inputs(input2), inputs(input1))
      game.lastNEvents(5) shouldEqual allEvents
      game.allEvents shouldEqual allEvents

      game.lastNEvents(2) shouldEqual List(inputs(input3), inputs(input2))
    }

    it("should ignore an input that is a duplicate of a previous event") {
      val game = new Game
      game.addEvent(input1)
      game.addEvent(input2)
      game.addEvent(input3)
      game.addEvent(input2)

      game.lastEvent shouldEqual Some(inputs(input3))
      val allEvents = List(inputs(input3), inputs(input2), inputs(input1))
      game.lastNEvents(5) shouldEqual allEvents
      game.allEvents shouldEqual allEvents

      game.lastNEvents(2) shouldEqual List(inputs(input3), inputs(input2))
    }

    it("should ignore an input that has a zero points score and no changes to previous score") {
      val game = new Game
      game.addEvent(input1)
      game.addEvent(input2)

      val invalidInput = inputs(input2).copy(points = 0, elapsedTime = inputs(input2).elapsedTime + 10).toHexString.get
      game.addEvent(invalidInput)

      game.lastEvent shouldEqual Some(inputs(input2))
      game.allEvents shouldEqual List(inputs(input2), inputs(input1))

    }

    it("should ignore an input that has zero points and has an impossible points total change from the previous") {
      val game = new Game
      game.addEvent(input1)
      game.addEvent(input2)

      val invalidInput = inputs(input2)
        .copy(
          points = 0,
          elapsedTime = inputs(input2).elapsedTime + 10,
          team1Total = inputs(input2).team1Total + 5).toHexString.get
      game.addEvent(invalidInput)

      game.lastEvent shouldEqual Some(inputs(input2))
      game.allEvents shouldEqual List(inputs(input2), inputs(input1))
    }

    it("if there is a non-zero score, it should correct erroneous points total changes") {
      val game = new Game
      game.addEvent(input1)
      game.addEvent(input2)

      val points = 2

      val erroneousSnapshot = GameSnapshot(
        points,
        team2Scored = false,
        inputs(input2).team1Total + 5,
        inputs(input2).team2Total,
        inputs(input2).elapsedTime + 10)
      val erroneousInput = erroneousSnapshot.toHexString.get
      game.addEvent(erroneousInput)

      val correctedSnapshot = erroneousSnapshot.copy(team1Total = inputs(input2).team1Total + points)

      game.lastEvent shouldEqual Some(correctedSnapshot)
      game.allEvents shouldEqual List(correctedSnapshot, inputs(input2), inputs(input1))
    }

    it("if there is an event where the timestamp is more than 2 minutes after the last, store it and attempt to reinsert later") {
      val game = new Game
      game.addEvent(input1)
      game.addEvent(input2)

      val laterSnapshot = GameSnapshot(
        2,
        team2Scored = false,
        inputs(input2).team1Total + 5,
        inputs(input2).team2Total + 5,
        inputs(input2).elapsedTime + 190)
      val laterInput = laterSnapshot.toHexString.get
      game.addEvent(laterInput)

      game.lastEvent shouldEqual Some(inputs(input2))
      game.allEvents shouldEqual List(inputs(input2), inputs(input1))


      val anotherInput = GameSnapshot(3, team2Scored = false, inputs(input2).team1Total + 3, inputs(input2).team2Total, inputs(input2).elapsedTime + 45)
      val anotherInput2 = GameSnapshot(2, team2Scored = true, anotherInput.team1Total, anotherInput.team2Total + 2, anotherInput.elapsedTime + 30)
      val anotherInput3 = GameSnapshot(3, team2Scored = true, anotherInput2.team1Total, anotherInput2.team2Total + 3, anotherInput2.elapsedTime + 45)

      game.addEvent(anotherInput.toHexString.get)
      game.addEvent(anotherInput2.toHexString.get)
      game.addEvent(anotherInput3.toHexString.get)

      game.lastEvent shouldEqual Some(laterSnapshot)
      game.allEvents shouldEqual List(laterSnapshot, anotherInput3, anotherInput2, anotherInput, inputs(input2), inputs(input1))

    }

    it("should handle the inconsistant test data correctly") {
      val game = new Game
      TestGameData.inconsistentData.foreach(game.addEvent)

      game.lastEvent shouldEqual Some(GameSnapshot(2,team2Scored = false,25,29,579))

      game.lastNEvents(3) shouldEqual List(
        GameSnapshot(2,team2Scored = false,25,29,579),
        GameSnapshot(2,team2Scored = false,23,29,560),
        GameSnapshot(1,team2Scored = true,21,29,533))
    }

  }
}
