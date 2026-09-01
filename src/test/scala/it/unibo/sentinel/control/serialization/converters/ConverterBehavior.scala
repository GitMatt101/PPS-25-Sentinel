package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.control.serialization.Converter

trait ConverterBehavior[Model, Schema]:
  self: UnitTest =>

  def model: Model
  def schema: Schema

  def basicConverter(build: => Converter[Model, Schema]): Unit =

    "convert from domain model to schema correctly" in:
      build.toSchema(model) shouldBe schema

    "convert from schema to domain model correctly" in:
      build.toDomain(schema) shouldBe model
