package it.unibo.sentinel.core.collisions

import it.unibo.sentinel.core.robot.RobotId

trait CollisionsChecker:

  /** @param intents
    *   the intent of each robot to move to a specific position
    * @return
    *   a list of groups of [[RobotId]]s, where each group represents the robots
    *   that will collide (intend to move to the same position)
    */
  def checkCollisions(intents: Iterable[Intent]): Iterable[Iterable[RobotId]]

object CollisionsChecker:

  /** @return
    *   a [[CollisionChecker]] that simply checks which robots collide
    */
  def apply(): CollisionsChecker = new CollisionsChecker:

    override def checkCollisions(
        intents: Iterable[Intent]
    ): Iterable[Iterable[RobotId]] =
      intents
      .groupBy(_.position)
      .map(_._2)
      .filter(_.size > 1)
      .map(i => i.map(_.robotId))
