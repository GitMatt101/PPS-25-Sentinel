package it.unibo.sentinel.core.collisions

import it.unibo.sentinel.core.scenario.Placement
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.robot.value
import scala.annotation.tailrec
import it.unibo.sentinel.core.routing.Path
import it.unibo.sentinel.core.routing.Navigator

enum Action:
  case Move
  case Wait
  case Reroute(path: Path)

/** Defines how to handle collisions between [[Robot]]s
  */
trait CollisionHandler:

  def resolveCollisions(placements: Seq[Placement])(using
      selector: SelectionPolicy
  ): Map[RobotId, Action]

object CollisionHandler:

  def pause(): CollisionHandler =
    new Resolver(_ => Action.Wait)

  /** Yielding robots avoid the disputed cell when routing the current action.
    * If no alternative exists, they wait with their original route intact.
    */
  def reroute()(using navigator: Navigator): CollisionHandler =
    new Resolver(placement =>
      val alternative = for
        currentPath <- placement.robot.path
        destination <- currentPath.destination
        path <- navigator.path(
          placement.intent.from,
          destination,
          avoiding = Set(placement.intent.to)
        )
      yield path
      alternative.fold[Action](Action.Wait)(Action.Reroute.apply)
    )

  private final class Resolver(onYield: Placement => Action)
      extends CollisionHandler:

    override def resolveCollisions(
        placements: Seq[Placement]
    )(using selector: SelectionPolicy): Map[RobotId, Action] =
      val ordered = placements.sortBy(_.robot.id.value)
      val movers = ordered.filter(p => p.intent.from != p.intent.to)
      val occupants = ordered.map(p => p.intent.from -> p.intent.robotId).toMap
      val stationary = ordered
        .filter(p => p.intent.from == p.intent.to)
        .map(_.intent.from)
        .toSet
      val cellLosers = movers
        .groupBy(_.intent.to)
        .toSeq
        .sortBy((position, _) => (position.x, position.y))
        .flatMap { (target, candidates) =>
          val chosen =
            if stationary.contains(target) then None
            else selector.select(candidates.map(_.robot))
          candidates.filterNot(p => chosen.contains(p.robot.id)).map(_.robot.id)
        }
        .toSet
      val contenders = movers.filterNot(p => cellLosers.contains(p.robot.id))
      val byOrigin = contenders.map(p => p.intent.from -> p).toMap
      val swapLosers = (for
        first <- contenders
        second <- byOrigin.get(first.intent.to).toSeq
        if second.intent.to == first.intent.from && first.robot.id.value < second.robot.id.value
        pair = Seq(first, second)
        chosen = selector.select(pair.map(_.robot))
        loser <- pair.filterNot(p => chosen.contains(p.robot.id))
      yield loser.robot.id).toSet
      val yielding = cellLosers ++ swapLosers

      @tailrec
      def movable(candidates: Set[RobotId]): Set[RobotId] =
        val remaining = movers
          .filter { placement =>
            candidates.contains(placement.robot.id) &&
            occupants.get(placement.intent.to).forall(candidates.contains)
          }
          .map(_.robot.id)
          .toSet
        if remaining == candidates then remaining else movable(remaining)
      val moving = movable(movers.map(_.robot.id).toSet -- yielding)
      movers.map { placement =>
        val decision =
          if yielding.contains(placement.robot.id) then onYield(placement)
          else if moving.contains(placement.robot.id) then Action.Move
          else Action.Wait
        placement.robot.id -> decision
      }.toMap
