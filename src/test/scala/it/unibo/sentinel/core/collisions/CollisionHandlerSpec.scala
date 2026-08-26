package it.unibo.sentinel.core.collisions

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.robot.RobotStatus

trait CollisionHandlerFixture extends CollisionCheckerFixture:
  self: UnitTest =>

  given SelectionPolicy = SelectionPolicy.random()
  val handler: CollisionHandler = CollisionHandler.wait()

class CollisionHandlerSpec extends UnitTest with CollisionHandlerFixture:

  "A wait-based collision handler using random choice" when:

    "handling collisions" should:

      "pause all but one random robot" in:
        handler.resolveCollisions(group1)
        forExactly(1, group1) { robot =>
          robot.status should not be RobotStatus.Waiting
        }
