package it.unibo.sentinel.core.scenario

import it.unibo.sentinel.core.warehouse.{Warehouse, Position}
import it.unibo.sentinel.core.robot.{Robot, RobotId}
import it.unibo.sentinel.core.mission.Mission

/** Represents a [[Robot]] placed in a [[Position]] in the [[Warehouse]].
  *
  * @param robot
  *   the [[Robot]] to place in the [[Warehouse]].
  * @param at
  *   the [[Position]] where to place the [[Robot]].
  */
final case class Placement(robot: Robot, at: Position)

/** Represents a description of a [[Robot]] to spawn in a [[Scenario]]. It will
  * be used to create a [[Robot]] in the given [[Position]] when the
  * [[Scenario]] is started.
  *
  * @param id
  *   the [[RobotId]] of the [[Robot]] to spawn.
  * @param at
  *   the [[Position]] where to spawn the [[Robot]].
  */
final case class Spawn(id: RobotId, at: Position):
  /** @return
    *   the [[Placement]] of the [[Robot]] to spawn in the [[Warehouse]].
    */
  def toPlacement: Placement = Placement(Robot(id), at)

/** Represents the dynamic context of the environment to simulate.
  */
trait Scenario:
  /** @return
    *   the [[Warehouse]] the [[Scenario]] refers to.
    */
  def warehouse: Warehouse

  /** @return
    *   the [[Spawn]]s of the [[Scenario]].
    */
  def spawns: Seq[Spawn]

  /** @return
    *   the [[Mission]]s of the [[Scenario]].
    */
  def missions: Seq[Mission]

object Scenario:
  /** @param warehouse
    *   the [[Warehouse]] the [[Scenario]] refers to.
    * @return
    *   a new [[Scenario]] with no robots nor missions for the given
    *   [[Warehouse]].
    */
  def in(warehouse: Warehouse): Scenario =
    Blueprint(warehouse, Seq.empty, Seq.empty)

  private case class Blueprint(
      warehouse: Warehouse,
      spawns: Seq[Spawn],
      missions: Seq[Mission]
  ) extends Scenario
