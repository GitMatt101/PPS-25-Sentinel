package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.boundary.serialization.Converter
import it.unibo.sentinel.core.warehouse.Tile
import it.unibo.sentinel.boundary.serialization.schemas.TileSchema
import it.unibo.sentinel.core.item.Item
import it.unibo.sentinel.boundary.serialization.schemas.ItemSchema
import it.unibo.sentinel.core.simulation.Tick

class TileConverterSpec extends UnitTest with ConverterBehavior:

  "A TileConverter" when:

    "encode and decode a Shelf tile" in:
      val tileConverter = summon[Converter[Tile, TileSchema]]
      val tileModel: Tile = Tile.Shelf(Item.Fridge)
      val tileSchema: TileSchema = TileSchema.Shelf(ItemSchema.Fridge(50.0))
      tileConverter.toSchema(tileModel).shouldBe(tileSchema)
      tileConverter.toDomain(tileSchema).shouldBe(Right(tileModel))

    "encode and decode a LoadingBay tile" in:
      val tileConverter = summon[Converter[Tile, TileSchema]]
      val tileModel: Tile = Tile.LoadingBay(Tick(3))
      val tileSchema: TileSchema = TileSchema.LoadingBay(3)
      tileConverter.toSchema(tileModel).shouldBe(tileSchema)
      tileConverter.toDomain(tileSchema).shouldBe(Right(tileModel))
