package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.boundary.serialization.Converter
import it.unibo.sentinel.core.warehouse.Tile
import it.unibo.sentinel.core.warehouse.Tile.*
import it.unibo.sentinel.boundary.serialization.schemas.TileSchema
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.boundary.serialization.Codec

/** [[Converter]] used to convert from [[Tile]] to [[TileSchema]] and viceversa.
  */
object TileConverter extends Converter[Tile, TileSchema]:

  override def toSchema(model: Tile): TileSchema = model match
    case Shelf(item)      => TileSchema.Shelf(ItemConverter.toSchema(item))
    case LoadingBay(cost) => TileSchema.LoadingBay(cost.value)
    case Tile.Floor(cost) => TileSchema.Floor(cost.value)

  override def toDomain(schema: TileSchema): Either[Codec.Validation, Tile] =
    schema match
      case TileSchema.Shelf(itemSchema) =>
        for item <- ItemConverter.toDomain(itemSchema)
        yield Tile.Shelf(item)
      case TileSchema.LoadingBay(cost) => Right(Tile.LoadingBay(Tick(cost)))
      case TileSchema.Floor(cost)      => Right(Tile.Floor(Tick(cost)))
