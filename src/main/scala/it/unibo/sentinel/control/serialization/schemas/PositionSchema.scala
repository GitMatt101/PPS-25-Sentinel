package it.unibo.sentinel.control.serialization.schemas

import it.unibo.sentinel.control.serialization.Schema
import it.unibo.sentinel.core.warehouse.Position
import it.unibo.sentinel.control.serialization.Codec.Validation

case class PositionSchema(x: Int, y: Int) extends Schema[Position]:
  override type Self = PositionSchema

  override def validated: Either[Validation, PositionSchema] = Right(this)
