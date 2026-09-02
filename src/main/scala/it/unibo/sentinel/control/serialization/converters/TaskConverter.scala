package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.control.serialization.Converter
import it.unibo.sentinel.core.mission.Task
import it.unibo.sentinel.control.serialization.schemas.TaskSchema

object TaskConverter extends Converter[Task, TaskSchema]:

  override def toSchema(model: Task): TaskSchema = model match
    case Task.Single(action) =>
      TaskSchema.Single(ActionConverter.toSchema(action))
    case Task.Done => 
      TaskSchema.Done

  override def toDomain(schema: TaskSchema): Task = schema match
    case TaskSchema.Single(action) =>
      Task.Single(ActionConverter.toDomain(action))
    case TaskSchema.Done => 
      Task.Done
