package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.boundary.serialization.Converter

trait ConverterBehavior:
  self: UnitTest =>

  protected def basicConverter[M, S](
      model: M,
      schema: S,
      converter: Converter[M, S]
  ): Unit =
    "convert from domain model to schema correctly" in:
      converter.toSchema(model).shouldBe(schema)

    "convert from schema to domain model correctly" in:
      converter.toDomain(schema).shouldBe(Right(model))
