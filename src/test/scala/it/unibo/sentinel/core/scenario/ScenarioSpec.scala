package it.unibo.sentinel.core.scenario

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.{Warehouse, Position, Area, Tile}
import it.unibo.sentinel.core.robot.RobotId

class ScenarioSpec extends UnitTest:
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
          s0.place(Spawn(id = RobotId("R1"), at = Position(1, 1)))
        result.spawns should contain only Spawn(
          id = RobotId("R1"),
          at = Position(1, 1)
        )
