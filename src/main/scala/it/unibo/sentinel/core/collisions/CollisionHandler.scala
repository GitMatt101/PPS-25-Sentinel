package it.unibo.sentinel.core.collisions

import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.routing.Path
import it.unibo.sentinel.core.routing.Navigator
import it.unibo.sentinel.core.scenario.Intent

/** Represents an action that a [[Robot]] must perform.
  */
enum Action:
  /** The [[Robot]] must move.
    */
  case Move

  /** The [[Robot]] must wait.
    */
  case Wait

  /** The [[Robot]] must follow a new [[Path]].
    *
    * @param path
    *   new [[Path]] to follow.
    */
  case Reroute(path: Path)

/** Defines how to handle collisions between [[Robot]]s
  */
trait CollisionHandler:

  /** @param intents
    *   the movement intents of the [[Robot]]s.
    * @param selector
    *   [[SelectionPolicy]] to use to resolve conflicts.
    * @return
    *   a `Map` association of [[RobotId]] to the assigned [[Action]].
    */
  def resolveCollisions(intents: Seq[Intent])(using
      selector: SelectionPolicy
  ): Map[RobotId, Action]

object CollisionHandler:

  /** [[CollisionHandler]] that makes the losers of collision disputes wait for
    * the cell to become unoccupied.
    */
  def pause(): CollisionHandler =
    new Resolver(_ => Action.Wait)

  /** [[CollisionHandler]] that makes the losers of collision disputes choose
    * another path towards their goal. If no path exists, they wait for the cell
    * to become unoccupied.
    */
  def reroute()(using navigator: Navigator): CollisionHandler =
    new Resolver(intent =>
      val alternative = for
        mission <- intent.mission
        action <- mission.currentAction
        path <- action match
          case it.unibo.sentinel.core.mission.Action.Move(to) =>
            navigator.path(
              intent.from,
              to,
              avoiding = Set(intent.to)
            )
          case it.unibo.sentinel.core.mission.Action.PickUp(_, to) =>
            navigator.path(
              intent.from,
              navigator.warehouse.neighbors(to).toSet,
              avoiding = Set(intent.to)
            )
          case it.unibo.sentinel.core.mission.Action.Drop(_, to) =>
            navigator.path(
              intent.from,
              to,
              avoiding = Set(intent.to)
            )
      yield path
      alternative match
        case Some(p) => Action.Reroute(p)
        case None    => Action.Wait
    )
