import scodec.bits._

case class GameSnapshot(points: Int, team2Scored: Boolean, team1Total: Int, team2Total: Int, elapsedTime: Int) {
  override def toString: String = {
    val elapsedMins = Math.floor(elapsedTime / 60)
    val elapsedSeconds = elapsedTime % 60
    val elapsedTimeString = s"${elapsedMins.intValue}:${elapsedSeconds.intValue}"
    val teamString = if(team2Scored) "Team 2" else "Team 1"
    s"At $elapsedTimeString, $teamString scored $points. Team 1: $team1Total, Team 2: $team2Total"
  }
}

object GameSnapshot {

  def apply(hex: String): Option[GameSnapshot] = {
    ByteVector.fromHex(hex).flatMap(
      byteVector => binaryStringToGameSnapshot(byteVector.bits)
    )
  }

  private def binaryStringToGameSnapshot(bitVector: BitVector): Option[GameSnapshot] = {
    val binString = bitVector.toBin
    implicit def toUnsignedInt(bitVector: BitVector): Int = bitVector.toInt(signed = false)
    if (binString.length == 32 && binString.forall(c => c == '1' || c == '0')) {
      for {
        matchTime <- BitVector.fromBin(binString.substring(1,13))
        team1Total <- BitVector.fromBin(binString.substring(13, 21))
        team2Total <- BitVector.fromBin(binString.substring(21, 29))
        points <- BitVector.fromBin(binString.substring(30))
        team2Scored = bitVector.get(29)
      } yield new GameSnapshot(points, team2Scored, team1Total, team2Total, matchTime)
    } else {
      None
    }

  }
}
