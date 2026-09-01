package it.unibo.sentinel.control.serialization

trait Converter[Model, Schema]:

  def toSchema(model: Model): Schema

  def toDomain(schema: Schema): Model
