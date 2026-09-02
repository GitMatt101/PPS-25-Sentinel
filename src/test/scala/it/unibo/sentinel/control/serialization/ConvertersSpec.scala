package it.unibo.sentinel.control.serialization

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.{Position, Tile}
import it.unibo.sentinel.control.serialization.schemas.{
  PositionSchema,
  TileSchema,
  WarehouseSchema
}
import it.unibo.sentinel.control.serialization.converters.{
  PositionConverter,
  TileConverter,
  WarehouseConverter
}
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.core.warehouse.Warehouse
import it.unibo.sentinel.core.scenario.Spawn
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.control.serialization.schemas.SpawnSchema
import it.unibo.sentinel.control.serialization.converters.SpawnConverter
import it.unibo.sentinel.core.mission.Mission
import it.unibo.sentinel.core.mission.MissionId
import it.unibo.sentinel.control.serialization.schemas.MissionSchema
import it.unibo.sentinel.control.serialization.schemas.TaskSchema
import it.unibo.sentinel.control.serialization.schemas.ActionSchema
import it.unibo.sentinel.control.serialization.converters.MissionConverter

class ConvertersSpec extends UnitTest:

  "A PositionConverter" when:
    behave like basicConverter(
      model = Position(1, 1),
      schema = PositionSchema(1, 1),
      converter = PositionConverter
    )

  "A TileConverter" when:
    behave like basicConverter(
      model = Tile.Floor(Tick.unit),
      schema = TileSchema.Floor(1),
      converter = TileConverter
    )

  "A WarehouseConverter" when:
    val model: Warehouse = Warehouse
      .empty(3, 3)
      .withTile(Position(1, 1))(Tile.Floor(Tick.unit))
    val schema: WarehouseSchema =
      WarehouseSchema(3, 3, Seq(PositionSchema(1, 1) -> TileSchema.Floor(1)))
    behave like basicConverter(
      model = model,
      schema = schema,
      WarehouseConverter
    )

  "A SpawnConverter" when:
    behave like basicConverter(
      model = Spawn(RobotId("R1"), Position(1, 1)),
      schema = SpawnSchema("R1", PositionSchema(1, 1)),
      converter = SpawnConverter
    )

  "A MissionConverter" when:
    behave like basicConverter(
      model = Mission.relocate(MissionId("M1"), Position(5, 5), Tick(10)),
      schema = MissionSchema(
        "M1",
        TaskSchema.Single(ActionSchema.Move(PositionSchema(5, 5))),
        10
      ),
      converter = MissionConverter
    )

  private def basicConverter[M, S](
      model: M,
      schema: S,
      converter: Converter[M, S]
  ): Unit =
    "convert from domain model to schema correctly" in:
      converter.toSchema(model) shouldBe schema

    "convert from schema to domain model correctly" in:
      converter.toDomain(schema) shouldBe model
