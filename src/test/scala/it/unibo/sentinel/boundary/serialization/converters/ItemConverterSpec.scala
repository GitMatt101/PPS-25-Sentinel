package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.item.Item
import it.unibo.sentinel.boundary.serialization.schemas.ItemSchema

class ItemConverterSpec extends UnitTest with ConverterBehavior:

  "An ItemConverter" when:
    behave like basicConverter(
      model = Item.Computer,
      schema = ItemSchema.Computer(1.0),
      converter = ItemConverter
    )

    "convert all item kinds preserving identity" in:
      Seq(Item.Computer, Item.Table, Item.Fridge, Item.Dishwasher).foreach:
        item =>
          ItemConverter
            .toDomain(ItemConverter.toSchema(item))
            .shouldBe(Right(item))
