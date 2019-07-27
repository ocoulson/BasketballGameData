import scodec.bits._

case class GameSnapshot(points: Int, team2Scored: Boolean, team1Total: Int, team2Total: Int, elapsedTime: Int) extends Ordered[GameSnapshot] {
  def prettyPrint: String = {
    val elapsedMins = Math.floor(elapsedTime / 60)
    val elapsedSeconds = {
      val seconds = elapsedTime % 60
      if (seconds < 10) "0"+seconds.intValue else seconds.intValue
    }
    val elapsedTimeString = s"${elapsedMins.intValue}:$elapsedSeconds"
    val teamString = if(team2Scored) "Team 2" else "Team 1"
    s"At $elapsedTimeString, $teamString scored $points. Team 1: $team1Total, Team 2: $team2Total"
  }

  def isValid: Boolean = {
    points >= 1 && points <= 3 && team2Total >= 0 && team1Total >= 0 && elapsedTime >= 0
  }

  override def compare(that: GameSnapshot): Int = {
    this.elapsedTime - that.elapsedTime
  }
}

object GameSnapshot {

  private val BinStringLength: Int = 32

  def apply(hex: String): Option[GameSnapshot] = {
    ByteVector.fromHex(hex).flatMap(
      byteVector => binaryStringToGameSnapshot(byteVector.bits)
    )
  }

  private def binaryStringToGameSnapshot(bitVector: BitVector): Option[GameSnapshot] = {
    val binString = getPaddedBinString(bitVector)
    implicit def toUnsignedInt(bitVector: BitVector): Int = bitVector.toInt(signed = false)
    if (isBinaryString(binString)) {
      for {
        matchTime <- BitVector.fromBin(binString.substring(1,13))
        team1Total <- BitVector.fromBin(binString.substring(13, 21))
        team2Total <- BitVector.fromBin(binString.substring(21, 29))
        points <- BitVector.fromBin(binString.substring(30))
        team2Scored = binString.charAt(29) == '1'
      } yield new GameSnapshot(points, team2Scored, team1Total, team2Total, matchTime)
    } else {
      println(s"Error - unexpected character in binary string: $binString")
      None
    }
  }

  private def isBinaryString(string: String): Boolean = string.forall(c => c == '1' || c == '0')

  private def getPaddedBinString(bitVector: BitVector): String =
    if (bitVector.toBin.length == BinStringLength) bitVector.toBin else {
      (0 until (BinStringLength - bitVector.toBin.length)).map(_ => '0').mkString + bitVector.toBin
    }
}
