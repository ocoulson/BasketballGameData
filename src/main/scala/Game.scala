
class Game {
  private var gameSnapshots: List[GameSnapshot] = List()

  private var cachedSnapshots: List[GameSnapshot] = List()

  def lastEvent: Option[GameSnapshot] = gameSnapshots.headOption

  def lastNEvents(n: Int): List[GameSnapshot] = gameSnapshots.take(n)

  def allEvents: List[GameSnapshot] = gameSnapshots

  def addEvent(inputString: String): Unit = {
    GameSnapshot(inputString) match {
      case None => println("Invalid input, could not parse")
      case Some(snapshot) =>
        gameSnapshots = addSnapshotInOrder(snapshot, gameSnapshots)
        for {cachedEvent <- cachedSnapshots} {
          gameSnapshots = tryAddCachedEvent(cachedEvent, gameSnapshots)
        }

    }
  }

  private def addSnapshotInOrder(gameSnapshot: GameSnapshot, snapshots: List[GameSnapshot]): List[GameSnapshot] = {
    snapshots match {
      case Nil => List(gameSnapshot)
      case head::tail => if (gameSnapshot == head || shouldDiscard(gameSnapshot, head)) head :: tail
      else if (gameSnapshot.compare(head) > 0) {
        if (gameSnapshot.elapsedTime - head.elapsedTime > 180) {
          cacheSnapshot(gameSnapshot)
          head :: tail
        } else {
          correctIfNecessary(gameSnapshot, head) :: head :: tail
        }
      }
      else head :: addSnapshotInOrder(gameSnapshot, tail)
    }
  }

  private def tryAddCachedEvent(cachedEvent: GameSnapshot, gameEvents: List[GameSnapshot]): List[GameSnapshot] = {
    gameEvents match {
      case Nil => Nil
      case head :: tail => if (cachedEvent == head || shouldDiscard(cachedEvent, head)) head :: tail
      else if (cachedEvent.compare(head) > 0) {
        if (cachedEvent.elapsedTime - head.elapsedTime > 180) {
          head :: tail
        } else {
          removeFromCache(cachedEvent)
          correctIfNecessary(cachedEvent, head) :: head :: tail
        }
      }
      else head :: addSnapshotInOrder(cachedEvent, tail)
    }
  }

  private def cacheSnapshot(gameSnapshot: GameSnapshot): Unit = cachedSnapshots = cachedSnapshots :+ gameSnapshot

  private def removeFromCache(snapshot: GameSnapshot): Unit = cachedSnapshots = cachedSnapshots.filterNot(_ == snapshot)

  private def shouldDiscard(current: GameSnapshot, previous: GameSnapshot): Boolean = {
    lazy val pointsTotalsUnchanged = current.team1Total == previous.team1Total && current.team2Total == previous.team2Total
    lazy val invalidTotalChange = current.team1Total - previous.team1Total > 3 || current.team2Total - previous.team2Total > 3
    current.hasZeroPoints && (pointsTotalsUnchanged || invalidTotalChange)
  }

  private def correctIfNecessary(current: GameSnapshot, previous: GameSnapshot): GameSnapshot = {
    if (current.team1Scored && current.team1Total - current.points != previous.team1Total)
      current.copy(team1Total = previous.team1Total + current.points)
    else if (current.team2Scored && current.team2Total - current.points != previous.team2Total)
      current.copy(team2Total = previous.team2Total + current.points)
    else current
  }


}
