package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.control.serialization.Converter
import it.unibo.sentinel.core.warehouse.Tile
import it.unibo.sentinel.control.serialization.schemas.TileSchema
import it.unibo.sentinel.core.simulation.Tick

object TileConverter extends Converter[Tile, TileSchema]:

  override def toSchema(model: Tile): TileSchema = model match
    case Tile.Floor(cost) => TileSchema.Floor(cost.value)

  override def toDomain(schema: TileSchema): Tile = schema match
    case TileSchema.Floor(cost) => Tile.Floor(Tick(cost))
