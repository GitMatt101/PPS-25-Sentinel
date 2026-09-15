package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.Position
import it.unibo.sentinel.core.mission.Action
import it.unibo.sentinel.boundary.serialization.schemas.ActionSchema
import it.unibo.sentinel.boundary.serialization.schemas.PositionSchema
import it.unibo.sentinel.core.item.Item
import it.unibo.sentinel.boundary.serialization.schemas.ItemSchema

class ActionConverterSpec extends UnitTest with ConverterBehavior:

  "An ActionConverter" when:
    behave like basicConverter(
      model = Action.Move(Position(1, 1)),
      schema = ActionSchema.Move(PositionSchema(1, 1)),
      converter = ActionConverter
    )

    "encode and decode a PickUp action" in:
      val actionModel: Action = Action.PickUp(Item.Computer, Position(1, 1))
      val actionSchema: ActionSchema =
        ActionSchema.PickUp(ItemSchema.Computer(1.0), PositionSchema(1, 1))
      ActionConverter.toSchema(actionModel).shouldBe(actionSchema)
      ActionConverter.toDomain(actionSchema).shouldBe(Right(actionModel))

    "encode and decode a Drop action" in:
      val actionModel: Action = Action.Drop(Item.Table, Position(2, 2))
      val actionSchema: ActionSchema =
        ActionSchema.Drop(ItemSchema.Table(10.0), PositionSchema(2, 2))
      ActionConverter.toSchema(actionModel).shouldBe(actionSchema)
      ActionConverter.toDomain(actionSchema).shouldBe(Right(actionModel))
