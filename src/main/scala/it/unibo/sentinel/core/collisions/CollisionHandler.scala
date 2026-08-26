package it.unibo.sentinel.core.collisions

import it.unibo.sentinel.core.robot.Robot

/** Defines how to handle collisions between [[Robot]]s
  */
trait CollisionHandler:
  given policy: SelectionPolicy

  /** Resolves collisions between a group of [[Robot]]s
    *
    * @param robots
    *   list of colliding [[Robot]]s
    */
  def resolveCollisions(robots: Seq[Robot]): Unit

object CollisionHandler:

  /** Handler based on pausing [[Robot]]s
    *
    * @param selectionPolicy
    *   policy used to select the [[Robot]](s) that can move
    */
  def wait()(using selectionPolicy: SelectionPolicy): CollisionHandler =
    new CollisionHandler:

      override given policy: SelectionPolicy = selectionPolicy

      override def resolveCollisions(robots: Seq[Robot]): Unit =
        val selectedIds = policy.select(robots)
        val selectedRobots =
          robots.filter(r => selectedIds.toSeq.contains(r.id))
        val notSelected = robots.toSeq.diff(selectedRobots.toSeq)
        selectedRobots.foreach(_.resume())
        notSelected.foreach(_.pause())
