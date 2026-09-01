package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.control.serialization.Converter
import it.unibo.sentinel.core.warehouse.Warehouse
import it.unibo.sentinel.control.serialization.schemas.WarehouseSchema

object WarehouseConverter extends Converter[Warehouse, WarehouseSchema]:

  override def toSchema(model: Warehouse): WarehouseSchema =
    val tilesMap = model.tiles.map: (pos, tile) =>
      val posSchema = PositionConverter.toSchema(pos)
      val tileSchema = TileConverter.toSchema(tile)
      (posSchema, tileSchema)
    WarehouseSchema(model.width, model.height, tilesMap)

  override def toDomain(schema: WarehouseSchema): Warehouse =
    var warehouse = Warehouse.empty(schema.width, schema.height)
    for
      (posSchema, tileSchema) <- schema.tiles
      pos = PositionConverter.toDomain(posSchema)
      tile = TileConverter.toDomain(tileSchema)
    do warehouse = warehouse.withTile(pos)(tile)
    warehouse
