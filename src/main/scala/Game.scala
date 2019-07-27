
class Game {
  private var gameSnapshots: List[GameSnapshot] = List()

  def lastEvent: Option[GameSnapshot] = gameSnapshots.headOption

  def lastNEvents(n: Int): List[GameSnapshot] = gameSnapshots.take(n)

  def allEvents: List[GameSnapshot] = gameSnapshots

  def addEvent(inputString: String): Unit = {
    GameSnapshot(inputString) match {
      case None => println("Invalid input, could not parse")
      case Some(snapshot) =>
        gameSnapshots = addSnapshotInOrder(snapshot, gameSnapshots)
    }
  }

  private def addSnapshotInOrder(gameSnapshot: GameSnapshot, snapshots: List[GameSnapshot]): List[GameSnapshot] = {
    snapshots match {
      case Nil => List(gameSnapshot)
      case head::tail =>
        if (gameSnapshot == head) head::tail
        else if (gameSnapshot.compare(head) > 0) gameSnapshot :: head :: tail
        else head :: addSnapshotInOrder(gameSnapshot, tail)
    }
  }

}
