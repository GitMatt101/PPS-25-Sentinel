package it.unibo.sentinel.control.serialization

trait Converter[Model, ModelSchema <: Schema[Model]]:

  def toSchema(model: Model): ModelSchema

  def toDomain(schema: ModelSchema): Model
