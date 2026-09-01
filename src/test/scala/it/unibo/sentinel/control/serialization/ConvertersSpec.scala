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

  private def basicConverter[M, S](
      model: M,
      schema: S,
      converter: Converter[M, S]
  ): Unit =
    "convert from domain model to schema correctly" in:
      converter.toSchema(model) shouldBe schema

    "convert from schema to domain model correctly" in:
      converter.toDomain(schema) shouldBe model
