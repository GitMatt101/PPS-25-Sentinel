package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.Position
import it.unibo.sentinel.core.mission.Task
import it.unibo.sentinel.boundary.serialization.schemas.TaskSchema
import it.unibo.sentinel.boundary.serialization.schemas.ActionSchema
import it.unibo.sentinel.core.item.Item
import it.unibo.sentinel.boundary.serialization.schemas.PositionSchema
import it.unibo.sentinel.boundary.serialization.schemas.ItemSchema

class TaskConverterSpec extends UnitTest with ConverterBehavior:

  "A TaskConverter" when:
    behave like basicConverter(
      model = Task.move(Position(1, 1)),
      schema = TaskSchema.Single(ActionSchema.Move(PositionSchema(1, 1))),
      converter = TaskConverter
    )

    "encode and decode a PickUp single task" in:
      val model = Task.pick(Item.Computer, Position(1, 1))
      val schema = TaskSchema.Single(
        ActionSchema.PickUp(ItemSchema.Computer(1.0), PositionSchema(1, 1))
      )
      TaskConverter.toSchema(model).shouldBe(schema)
      TaskConverter.toDomain(schema).shouldBe(Right(model))

    "encode and decode a Drop single task" in:
      val model = Task.drop(Item.Table, Position(2, 2))
      val schema = TaskSchema.Single(
        ActionSchema.Drop(ItemSchema.Table(10.0), PositionSchema(2, 2))
      )
      TaskConverter.toSchema(model).shouldBe(schema)
      TaskConverter.toDomain(schema).shouldBe(Right(model))

    "encode and decode a Then task" in:
      val model =
        Task.pickAndDrop(Item.Computer, Position(1, 1), Position(2, 2))
      val schema = TaskSchema.Then(
        TaskSchema.Single(
          ActionSchema.PickUp(ItemSchema.Computer(1.0), PositionSchema(1, 1))
        ),
        TaskSchema.Single(
          ActionSchema.Drop(ItemSchema.Computer(1.0), PositionSchema(2, 2))
        )
      )
      TaskConverter.toSchema(model).shouldBe(schema)
      TaskConverter.toDomain(schema).shouldBe(Right(model))

    "encode and decode a Done task" in:
      TaskConverter.toSchema(Task.Done).shouldBe(TaskSchema.Done)
      TaskConverter.toDomain(TaskSchema.Done).shouldBe(Right(Task.Done))
