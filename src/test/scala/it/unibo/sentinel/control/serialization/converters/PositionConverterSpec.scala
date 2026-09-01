package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.Position
import it.unibo.sentinel.control.serialization.schemas.PositionSchema

class PositionConverterSpec extends UnitTest with ConverterBehavior[Position, PositionSchema]:

  override def model: Position = Position(1, 1)
  override def schema: PositionSchema = PositionSchema(1, 1)

  "A position converter" when:
    behave like basicConverter(PositionConverter)
