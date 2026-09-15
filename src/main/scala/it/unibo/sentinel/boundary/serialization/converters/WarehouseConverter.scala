package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.boundary.serialization.Converter
import it.unibo.sentinel.boundary.serialization.Codec.Validation
import it.unibo.sentinel.boundary.serialization.schemas.WarehouseSchema
import it.unibo.sentinel.core.warehouse.Warehouse
import it.unibo.sentinel.core.warehouse.value
import it.unibo.sentinel.core.warehouse.WarehouseId

/** [[Converter]] used to convert from [[Warehouse]] to [[WarehouseSchema]] and
  * viceversa.
  */
object WarehouseConverter extends Converter[Warehouse, WarehouseSchema]:

  override def toSchema(model: Warehouse): WarehouseSchema =
    val tilesMap = model.tiles.map: (pos, tile) =>
      val posSchema = PositionConverter.toSchema(pos)
      val tileSchema = TileConverter.toSchema(tile)
      (posSchema, tileSchema)
    WarehouseSchema(model.id.value, model.width, model.height, tilesMap)

  override def toDomain(
      schema: WarehouseSchema
  ): Either[Validation, Warehouse] =
    var warehouse =
      Warehouse.empty(WarehouseId(schema.id), schema.width, schema.height)
    for
      (posSchema, tileSchema) <- schema.tiles
      pos <- PositionConverter.toDomain(posSchema)
      tile <- TileConverter.toDomain(tileSchema)
    do warehouse = warehouse.withTile(pos)(tile)
    Right(warehouse)
