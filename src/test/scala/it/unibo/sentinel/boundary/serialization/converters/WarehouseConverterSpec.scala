package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.Warehouse
import it.unibo.sentinel.core.warehouse.Position
import it.unibo.sentinel.core.warehouse.WarehouseId
import it.unibo.sentinel.core.warehouse.Tile
import it.unibo.sentinel.boundary.serialization.schemas.WarehouseSchema
import it.unibo.sentinel.boundary.serialization.schemas.PositionSchema
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.boundary.serialization.schemas.TileSchema
import it.unibo.sentinel.core.item.Item
import it.unibo.sentinel.boundary.serialization.schemas.ItemSchema

class WarehouseConverterSpec extends UnitTest with ConverterBehavior:

  "A WarehouseConverter" when:
    val model: Warehouse = Warehouse
      .empty(WarehouseId("W"), 3, 3)
      .withTile(Position(1, 1))(Tile.Floor(Tick.unit))
    val schema: WarehouseSchema = WarehouseSchema(
      "W",
      3,
      3,
      Seq(PositionSchema(1, 1) -> TileSchema.Floor(1))
    )
    behave like basicConverter(
      model = model,
      schema = schema,
      converter = WarehouseConverter
    )

    "encode and decode a warehouse containing multiple tiles" in:
      val richModel = Warehouse
        .empty(WarehouseId("W"), 3, 3)
        .withTile(Position(0, 0))(Tile.Shelf(Item.Table))
        .withTile(Position(2, 2))(Tile.LoadingBay(Tick(2)))
      val richSchema = WarehouseSchema(
        "W",
        3,
        3,
        Seq(
          PositionSchema(0, 0) -> TileSchema.Shelf(ItemSchema.Table(10.0)),
          PositionSchema(2, 2) -> TileSchema.LoadingBay(2)
        )
      )
      WarehouseConverter.toSchema(richModel).shouldBe(richSchema)
      WarehouseConverter.toDomain(richSchema).shouldBe(Right(richModel))
