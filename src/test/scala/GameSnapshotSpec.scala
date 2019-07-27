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
  }
}
