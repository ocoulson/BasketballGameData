import org.scalatest.{FunSpec, Matchers}

class GameSnapshotSpec extends FunSpec with Matchers{

  describe("A GameSnapshot") {

    it("is correctly decoded from a given hexString") {
      val point1: String = "0x1310c8a1"
      //At 10:10, a single point for Team 1 gives them a 5 point lead – 25-20
      val expected1: GameSnapshot = GameSnapshot(1, team2Scored = false, 25, 20, 610)

      GameSnapshot(point1) shouldEqual Some(expected1)

      val point2: String = "0x29f981a2"
      //At 22:23, a 2-point shot for Team 1 leaves them 4 points behind at 48-52
      val expected2 = GameSnapshot(2, team2Scored = false, 48, 52, 1343)

      GameSnapshot(point2) shouldEqual Some(expected2)

      val point3: String = "0x48332327"
      //At 38:30, a 3-point shot levels the game for Team 2 at 100 points each
      val expected3 = GameSnapshot(3, team2Scored = true, 100, 100, 2310)

      GameSnapshot(point3) shouldEqual Some(expected3)
    }

    it("should correctly decode hex strings that result in binary strings of less than 32 bits") {
      val point1 = "0x801002"
      val expected1 = GameSnapshot(2, team2Scored = false, 2, 0, 16)

      GameSnapshot(point1) shouldEqual Some(expected1)

      val point2 = "0xf81016"
      val expected2 = GameSnapshot(2, team2Scored = true, 2, 2, 31)
      GameSnapshot(point2) shouldEqual Some(expected2)
    }

    describe("is not valid if") {
      it("points are not between 1 and 3") {
        GameSnapshot(0, team2Scored = false, 12, 12, 1000).isValid shouldEqual false
        GameSnapshot(-1, team2Scored = false, 12, 12, 1000).isValid shouldEqual false
        GameSnapshot(5, team2Scored = false, 12, 12, 1000).isValid shouldEqual false
        GameSnapshot(100, team2Scored = false, 12, 12, 1000).isValid shouldEqual false
        GameSnapshot(-100, team2Scored = false, 12, 12, 1000).isValid shouldEqual false
      }

      //These should not be possible as we don't consider the binary representations to be signed
      it("team1Total or team2Total are less than 0") {
        GameSnapshot(1, team2Scored = true, -12, 12, 1000).isValid shouldEqual false
        GameSnapshot(2, team2Scored = true, 12, -12, 1000).isValid shouldEqual false
      }

      it("elapsedTime is less than 0") {
        GameSnapshot(1, team2Scored = true, 12, 12, -1).isValid shouldEqual false
        GameSnapshot(2, team2Scored = true, 12, 12, -1000).isValid shouldEqual false
      }
    }
  }

  describe("ToHexString") {
    it("should correctly encode a GameSnapshot") {

      val expected: String = "0x1310c8a1"
      val snapShot: GameSnapshot = GameSnapshot(1, team2Scored = false, 25, 20, 610)

      snapShot.toHexString shouldEqual Some(expected)

      val expected2: String = "0x29f981a2"

      val snapshot2 = GameSnapshot(2, team2Scored = false, 48, 52, 1343)

      snapshot2.toHexString shouldEqual Some(expected2)
    }
  }
}
