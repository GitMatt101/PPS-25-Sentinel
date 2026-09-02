package it.unibo.sentinel.control.serialization

import it.unibo.sentinel.control.serialization.{Codec, Converter, Schema}
import it.unibo.sentinel.control.serialization.Codec.Validation
import upickle.default.{ReadWriter, read, write, macroRW}
import scala.util.Try
import it.unibo.sentinel.core.warehouse.Warehouse
import it.unibo.sentinel.control.serialization.JsonSerialization.JsonCodec
import it.unibo.sentinel.control.serialization.schemas.WarehouseSchema
import it.unibo.sentinel.control.serialization.converters.WarehouseConverter
import it.unibo.sentinel.control.serialization.schemas.PositionSchema
import it.unibo.sentinel.control.serialization.schemas.TileSchema

object JsonSerialization:

  trait JsonCodec[Model, ModelSchema <: Schema: ReadWriter](using
      converter: Converter[Model, ModelSchema]
  ) extends Codec[Model]:

    override def encode(entity: Model): String =
      write(converter.toSchema(entity))

    override def decode(input: String): Either[Validation, Model] =
      parseJson(input)
        .flatMap(schema => schema.validated.map(_ => schema))
        .map(converter.toDomain)

    protected def parseJson(
        input: String
    ): Either[Validation, ModelSchema] =
      Try(read[ModelSchema](input)).toEither.left.map: e =>
        Validation.Syntax(e.getMessage())

object JsonCodecs:

  given Converter[Warehouse, WarehouseSchema] = WarehouseConverter
  given ReadWriter[PositionSchema] = macroRW
  given ReadWriter[TileSchema.Floor] = macroRW
  given ReadWriter[TileSchema] = macroRW
  given ReadWriter[WarehouseSchema] = macroRW

  given Codec[Warehouse] = new JsonCodec[Warehouse, WarehouseSchema] {}
