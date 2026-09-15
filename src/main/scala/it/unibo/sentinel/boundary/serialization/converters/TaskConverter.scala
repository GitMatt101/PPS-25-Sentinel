package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.boundary.serialization.Converter
import it.unibo.sentinel.core.mission.Task
import it.unibo.sentinel.boundary.serialization.schemas.TaskSchema
import it.unibo.sentinel.boundary.serialization.Codec.Validation

/** [[Converter]] used to convert from [[Task]] to [[TaskSchema]] and viceversa.
  */
object TaskConverter extends Converter[Task, TaskSchema]:

  override def toSchema(model: Task): TaskSchema = model match
    case Task.Then(head, tail) =>
      TaskSchema.Then(toSchema(head), toSchema(tail))
    case Task.Single(action) =>
      TaskSchema.Single(ActionConverter.toSchema(action))
    case Task.Done => TaskSchema.Done

  override def toDomain(schema: TaskSchema): Either[Validation, Task] =
    schema match
      case TaskSchema.Then(head, tail) =>
        for
          domainHead <- toDomain(head)
          domainTail <- toDomain(tail)
        yield Task.Then(domainHead, domainTail)
      case TaskSchema.Single(action) =>
        for domainAction <- ActionConverter.toDomain(action)
        yield Task.Single(domainAction)
      case TaskSchema.Done =>
        Right(Task.Done)
