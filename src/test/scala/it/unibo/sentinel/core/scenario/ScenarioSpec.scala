package it.unibo.sentinel.core.scenario

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.{Warehouse, Position, Area, Tile}
import it.unibo.sentinel.core.robot.RobotId

class ScenarioSpec extends UnitTest:
  import Validation.*
  "A Scenario" when:
    val width = 5
    val height = 5
    val topCorner = Position(1, 1)
    val bottomCorner = Position(width - 2, height - 2)
    val warehouse = Warehouse
      .empty(width, height)
      .withArea(Area(topCorner, bottomCorner))(Tile.Floor())
    val s0 = Scenario.in(warehouse)

    "created" should:

      "refer to the given Warehouse" in:
        s0.warehouse shouldBe warehouse

      "should not contain any robot" in:
        s0.spawns shouldBe empty

      "should not contain any mission" in:
        s0.missions shouldBe empty

    "place a robot" should:

      "return a new scenario with the robot placed" in:
        val result =
          s0.place(Spawn(id = RobotId("R1"), at = Position(1, 1))).right.value
        result.spawns should contain only Spawn(
          id = RobotId("R1"),
          at = Position(1, 1)
        )

      "signal that the position is occupied" in:
        val position = Position(1, 1)
        val result =
          for
            s1 <- s0.place(Spawn(id = RobotId("R1"), at = position))
            s2 <- s1.place(Spawn(id = RobotId("R2"), at = position))
          yield s2
        result.left.value shouldBe PositionOccupied(Position(1, 1))

      "signal that the position is not a floor tile" in:
        val position = Position(0, 0)
        val result =
          s0.place(Spawn(id = RobotId("R1"), at = position))
        result.left.value shouldBe NotFloorTile(position)
