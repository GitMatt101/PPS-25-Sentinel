package it.unibo.sentinel.control.serialization

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.{Position, Tile}
import it.unibo.sentinel.control.serialization.schemas.{PositionSchema, TileSchema}
import it.unibo.sentinel.control.serialization.converters.{PositionConverter, TileConverter}
import it.unibo.sentinel.core.simulation.Tick

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

  private def basicConverter[M, S](model: M, schema: S, converter: Converter[M, S]): Unit =
    "convert from domain model to schema correctly" in:
      converter.toSchema(model) shouldBe schema

    "convert from schema to domain model correctly" in:
      converter.toDomain(schema) shouldBe model
