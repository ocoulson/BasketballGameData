import org.scalatest.{FunSpec, Matchers}

class GameSpec extends FunSpec with Matchers {

  describe("Adding valid inputs in valid order") {
    it("should create a valid list and the correct values are returned") {
      val input1 = "0x801002"
      val input2 = "0xf81016"
      val input3 = "0x1d8102f"
      val input4 = "0x248202a"
      val inputs = List(input1, input2, input3, input4).map(input => (input, GameSnapshot(input))).toMap

      val game = new Game
      game.addSnapshot(input1)

      game.lastEvent shouldEqual inputs(input1)

    }
  }
}
