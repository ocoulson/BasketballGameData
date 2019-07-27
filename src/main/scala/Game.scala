

class Game {
  var gameSnapshots: List[GameSnapshot] = List()

  def lastEvent: Option[GameSnapshot] = gameSnapshots.headOption

  def lastNEvents(n: Int): List[GameSnapshot] = gameSnapshots.take(n)

  def allEvents: List[GameSnapshot] = gameSnapshots

  def addSnapshot(inputString: String): Unit = {
    GameSnapshot(inputString) match {
      case None => println("Invalid input, could not parse")
      case Some(snapshot) =>
        gameSnapshots = snapshot +: gameSnapshots
    }
  }

}
