package it.unibo.sentinel.control.serialization.json

import it.unibo.sentinel.control.serialization.{Codec, Converter, Schema}
import it.unibo.sentinel.control.serialization.Codec.Validation
import upickle.default.{ReadWriter, read, write, macroRW}
import scala.util.Try
import it.unibo.sentinel.core.warehouse.Warehouse
import it.unibo.sentinel.control.serialization.schemas.WarehouseSchema
import it.unibo.sentinel.control.serialization.converters.WarehouseConverter
import it.unibo.sentinel.control.serialization.schemas.PositionSchema
import it.unibo.sentinel.control.serialization.schemas.TileSchema

trait JsonCodec[Model, ModelSchema <: Schema[Model] { type Self = ModelSchema }](using
    converter: Converter[Model, ModelSchema]
) extends Codec[Model]:

  given rw: ReadWriter[ModelSchema]

  override def encode(model: Model): String =
    write(converter.toSchema(model))

  override def decode(input: String): Either[Validation, Model] =
    parseJson(input).flatMap(_.validated).map(converter.toDomain)

  protected def parseJson(
      input: String
  ): Either[Validation, ModelSchema] =
    Try(read[ModelSchema](input)).toEither.left.map: e =>
      Validation.Syntax(e.getMessage())

object JsonCodec:

  given Converter[Warehouse, WarehouseSchema] = WarehouseConverter
  given ReadWriter[PositionSchema] = macroRW
  given ReadWriter[TileSchema.Floor] = macroRW
  given ReadWriter[TileSchema] = macroRW

  given warehouseCodec: JsonCodec[Warehouse, WarehouseSchema] = 
    new JsonCodec[Warehouse, WarehouseSchema]:
      override final val rw: ReadWriter[WarehouseSchema] = macroRW