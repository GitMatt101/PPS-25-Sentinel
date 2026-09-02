package it.unibo.sentinel.control.serialization.schemas

import it.unibo.sentinel.control.serialization.Schema
import it.unibo.sentinel.core.warehouse.Warehouse
import it.unibo.sentinel.control.serialization.Codec.Validation
import it.unibo.sentinel.control.serialization.converters.PositionConverter

/** Schema of a [[Warehouse]].
  */
case class WarehouseSchema(
    width: Int,
    height: Int,
    tiles: Seq[(PositionSchema, TileSchema)]
) extends Schema:

  override def validated: Either[Validation, WarehouseSchema] =
    for
      _ <- validateSize
      _ <- validateBounds
    yield this

  private def validateSize: Either[Validation, Unit] =
    validate(width > 0 && height > 0):
      Validation.WarehouseValidation:
        Warehouse.Validation.InvalidSize(width, height)

  private def validateBounds: Either[Validation, Unit] =
    val outOfBounds = tiles
      .map(p => PositionConverter.toDomain(p._1))
      .filter: pos =>
        pos.x < 0 || pos.x >= width || pos.y < 0 || pos.y >= height
    validate(outOfBounds.isEmpty):
      Validation.WarehouseValidation:
        Warehouse.Validation.TilesOutOfBounds(outOfBounds, width, height)

  private def validate(condition: Boolean)(
      error: Validation
  ): Either[Validation, Unit] =
    Either.cond(condition, (), error)
