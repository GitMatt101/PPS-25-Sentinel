package it.unibo.sentinel.core.collisions

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.robot.Robot
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.warehouse.Position
import it.unibo.sentinel.core.routing.Path

trait CollisionCheckerFixture:
  self: UnitTest =>

  val checker: CollisionsChecker = CollisionsChecker()

  val path1: Path = Seq(Position(1, 1))
  val path2: Path = Seq(Position(5, 5))

  val r1: Robot = Robot(RobotId("R1"))
  val r2: Robot = Robot(RobotId("R2"))
  val r3: Robot = Robot(RobotId("R3"))
  val rFree: Robot = Robot(RobotId("Free"))
  rFree.follow(Seq(Position(10, 10)))

  val r4: Robot = Robot(RobotId("R4"))
  val r5: Robot = Robot(RobotId("R5"))
  val r6: Robot = Robot(RobotId("R6"))

  val group1: Seq[Robot] = Seq(r1, r2, r3)
  val group2: Seq[Robot] = Seq(r4, r5, r6)

  group1.foreach(_.follow(path1))
  group2.foreach(_.follow(path2))

  val allRobots = group1 ++ group2 ++ Seq(rFree)

class CollisionCheckerSpec extends UnitTest with CollisionCheckerFixture:

  "A collision checker" when:

    "checking collisions" should:

      "return a list of groups of robots that collide" in:
        val intents = allRobots.flatMap { r =>
          r.next.map(value => Intent(r.id, value))
        }
        checker.checkCollisions(intents) shouldBe Seq(
          group1.map(_.id),
          group2.map(_.id)
        )
