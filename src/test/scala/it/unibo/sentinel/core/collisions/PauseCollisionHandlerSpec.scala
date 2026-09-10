package it.unibo.sentinel.core.collisions

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.scenario.Intent
import it.unibo.sentinel.core.warehouse.Position

class PauseCollisionHandlerSpec extends UnitTest with CollisionHandlerBehavior:

  private val pausing: CollisionHandler = CollisionHandler.pause()

  "A CollisionHandler.pause" when:

    correctCollisionResolver(pausing)

    "resolving indirect collisions" should:

      "make the yielding loser wait" in:
        val r1Id = RobotId("R1")
        val r2Id = RobotId("R2")
        val target = Position(1, 1)

        val i1 = Intent(
          r1Id,
          Position(0, 0),
          target,
          Some(createMission("R1", target))
        )
        val i2 = Intent(
          r2Id,
          Position(0, 1),
          target,
          Some(createMission("R2", target))
        )

        val actions = pausing.resolveCollisions(Seq(i1, i2))
        actions shouldBe Map(
          r1Id -> Action.Move,
          r2Id -> Action.Wait
        )

      "make all contenders wait if a stationary robot occupies the target cell" in:
        val r1Id = RobotId("R1")
        val r2Id = RobotId("R2")
        val r3Id = RobotId("R3")
        val target = Position(1, 1)

        val i1 = Intent(
          r1Id,
          Position(0, 0),
          target,
          Some(createMission("R1", target))
        )
        val i2 = Intent(
          r2Id,
          Position(0, 1),
          target,
          Some(createMission("R2", target))
        )
        val i3Stationary = Intent(r3Id, target, target, None)

        val actions = pausing.resolveCollisions(Seq(i1, i2, i3Stationary))
        actions(r1Id) shouldBe Action.Wait
        actions(r2Id) shouldBe Action.Wait

    "resolving direct collisions" should:

      "make both robots wait" in:
        val r4Id = RobotId("R4")
        val r5Id = RobotId("R5")
        val p0 = Position(0, 0)
        val p1 = Position(1, 0)

        val i4 = Intent(r4Id, p0, p1, Some(createMission("R4", p1)))
        val i5 = Intent(r5Id, p1, p0, Some(createMission("R5", p0)))

        val actions = pausing.resolveCollisions(Seq(i4, i5))
        actions shouldBe Map(
          r4Id -> Action.Wait,
          r5Id -> Action.Wait
        )

    "handling chain dependencies" should:

      "cascade wait decisions when a robot cannot move into an occupied cell" in:
        val r1Id = RobotId("R1")
        val r2Id = RobotId("R2")
        val r3Id = RobotId("R3")
        val r4StatId = RobotId("R4")

        val i1 = Intent(
          r1Id,
          Position(0, 0),
          Position(1, 0),
          Some(createMission("R1", Position(1, 0)))
        )
        val i2 = Intent(
          r2Id,
          Position(1, 0),
          Position(2, 0),
          Some(createMission("R2", Position(2, 0)))
        )
        val i3 = Intent(
          r3Id,
          Position(2, 0),
          Position(3, 0),
          Some(createMission("R3", Position(3, 0)))
        )
        val i4Stationary =
          Intent(r4StatId, Position(3, 0), Position(3, 0), None)

        val actions = pausing.resolveCollisions(Seq(i1, i2, i3, i4Stationary))
        actions shouldBe Map(
          r1Id -> Action.Wait,
          r2Id -> Action.Wait,
          r3Id -> Action.Wait
        )
