package it.unibo.sentinel.core.collisions

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.routing.{Navigator, Path, Step}
import it.unibo.sentinel.core.scenario.Intent
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.core.warehouse.{
  Area,
  Position,
  Tile,
  Warehouse,
  WarehouseId
}
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*

class RerouteCollisionHandlerSpec
    extends UnitTest
    with CollisionHandlerBehavior:

  private val alternativePath = Path(
    Step(Position(0, 1), Tick.unit),
    Step(Position(1, 1), Tick.unit)
  )

  private given dummyWarehouse: Warehouse = Warehouse
    .empty(WarehouseId("dummy"), 10, 10)
    .withArea(Area(Position(0, 0), Position(9, 9)))(Tile.Floor(Tick.unit))

  "A CollisionHandler.reroute" when:

    "a new alternative path exists" should:

      val mockNavigator = mock(classOf[Navigator])
      when(mockNavigator.warehouse).thenReturn(dummyWarehouse)
      when(
        mockNavigator.path(
          any[Position],
          any[Set[Position]],
          any[Set[Position]]
        )
      ).thenReturn(Some(alternativePath))
      when(
        mockNavigator.path(
          any[Position],
          any[Position],
          any[Set[Position]]
        )
      ).thenReturn(Some(alternativePath))

      given Navigator = mockNavigator
      val rerouting: CollisionHandler = CollisionHandler.reroute()

      correctCollisionResolver(rerouting)

      "return Action.Reroute with the new path for the yielding robot in indirect collisions" in:
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

        val actions = rerouting.resolveCollisions(Seq(i1, i2))
        actions(r1Id) shouldBe Action.Move
        actions(r2Id) shouldBe Action.Reroute(alternativePath)

      "make the winner wait for the other robot to reroute and move in direct swap collisions" in:
        val r4Id = RobotId("R4")
        val r5Id = RobotId("R5")
        val p0 = Position(0, 0)
        val p1 = Position(1, 0)

        val i4 = Intent(r4Id, p0, p1, Some(createMission("R4", p1)))
        val i5 = Intent(r5Id, p1, p0, Some(createMission("R5", p0)))

        val actions = rerouting.resolveCollisions(Seq(i4, i5))
        actions shouldBe Map(
          r4Id -> Action.Wait,
          r5Id -> Action.Reroute(alternativePath)
        )

    "no alternative path exists" should:

      val mockNavigator = mock(classOf[Navigator])
      when(mockNavigator.warehouse).thenReturn(dummyWarehouse)
      when(
        mockNavigator.path(
          any[Position],
          any[Set[Position]],
          any[Set[Position]]
        )
      ).thenReturn(None)
      when(
        mockNavigator.path(
          any[Position],
          any[Position],
          any[Set[Position]]
        )
      ).thenReturn(None)

      given Navigator = mockNavigator
      val fallbackRerouting: CollisionHandler = CollisionHandler.reroute()

      "fallback to Action.Wait for the yielding robot in indirect collisions" in:
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

        val actions = fallbackRerouting.resolveCollisions(Seq(i1, i2))
        actions(r1Id) shouldBe Action.Move
        actions(r2Id) shouldBe Action.Wait

      "fallback to Action.Wait for both robots in direct swap collisions when no path exists" in:
        val r4Id = RobotId("R4")
        val r5Id = RobotId("R5")
        val p0 = Position(0, 0)
        val p1 = Position(1, 0)

        val i4 = Intent(r4Id, p0, p1, Some(createMission("R4", p1)))
        val i5 = Intent(r5Id, p1, p0, Some(createMission("R5", p0)))

        val actions = fallbackRerouting.resolveCollisions(Seq(i4, i5))
        actions shouldBe Map(
          r4Id -> Action.Wait,
          r5Id -> Action.Wait
        )
