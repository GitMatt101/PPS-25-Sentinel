package it.unibo.sentinel.core.collisions

import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.warehouse.Position

/** Represents a [[Robot]] wanting to move to a specific [[Position]]
  */
opaque type Intent = (RobotId, Position)

object Intent:

  /** @param id
    *   the [[Robot]]'s id
    * @param position
    *   the destination
    * @return
    *   an [[Intent]] with the given [[RobotId]] and [[Position]] (destination)
    */
  def apply(id: RobotId, position: Position): Intent = (id, position)

extension (i: Intent)

  /** @return
    *   the id of the [[Robot]] that wants to move
    */
  def robotId: RobotId = i._1

  /** @return
    *   the destination of the [[Robot]] that wants to move
    */
  def position: Position = i._2
