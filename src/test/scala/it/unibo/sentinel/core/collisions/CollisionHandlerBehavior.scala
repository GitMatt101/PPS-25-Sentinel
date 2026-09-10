package it.unibo.sentinel.core.collisions

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.mission.{Mission, MissionId, Priority}
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.scenario.Intent
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.core.warehouse.Position

trait CollisionHandlerBehavior:
  this: UnitTest =>

  protected given SelectionPolicy = new SelectionPolicy:
    override def select(robots: Seq[Intent]): Option[RobotId] =
      robots.headOption.map(_.robotId)

  protected def createMission(id: String, target: Position): Mission =
    Mission.relocate(
      MissionId(s"m-$id"),
      target,
      Tick(10),
      Priority.normal
    )

  def correctCollisionResolver(handler: CollisionHandler): Unit =

    "resolving indirect collisions" should:

      "allow the selected winner to move" in:
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

        val actions = handler.resolveCollisions(Seq(i1, i2))
        actions(r1Id) shouldBe Action.Move
        actions(r2Id) shouldNot be(Action.Move)

      "prevent contenders from moving if a stationary robot occupies the target cell" in:
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

        val actions = handler.resolveCollisions(Seq(i1, i2, i3Stationary))
        actions(r1Id) shouldNot be(Action.Move)
        actions(r2Id) shouldNot be(Action.Move)

      "ignore stationary robots and allow moving robots to proceed to empty cells" in:
        val r1StatId = RobotId("R1_stat")
        val r2Id = RobotId("R2")

        val i1Stationary =
          Intent(r1StatId, Position(0, 0), Position(0, 0), None)
        val i2 = Intent(
          r2Id,
          Position(0, 1),
          Position(1, 1),
          Some(createMission("R2", Position(1, 1)))
        )

        val actions = handler.resolveCollisions(Seq(i1Stationary, i2))
        actions.get(r1StatId) shouldBe None
        actions(r2Id) shouldBe Action.Move

    "handling chain dependencies" should:

      "cascade non-move decisions when a robot cannot move into an occupied cell" in:
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

        val actions = handler.resolveCollisions(Seq(i1, i2, i3, i4Stationary))
        actions(r1Id) shouldNot be(Action.Move)
        actions(r2Id) shouldNot be(Action.Move)
        actions(r3Id) shouldNot be(Action.Move)
