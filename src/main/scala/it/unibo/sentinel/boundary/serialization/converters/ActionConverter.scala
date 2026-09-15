package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.boundary.serialization.Converter
import it.unibo.sentinel.core.mission.Action
import it.unibo.sentinel.boundary.serialization.schemas.ActionSchema
import it.unibo.sentinel.boundary.serialization.Codec.Validation

/** [[Converter]] used to convert from [[Action]] to [[ActionSchema]] and
  * viceversa.
  */
object ActionConverter extends Converter[Action, ActionSchema]:

  override def toSchema(model: Action): ActionSchema = model match
    case Action.Move(to) =>
      ActionSchema.Move(PositionConverter.toSchema(to))
    case Action.PickUp(target, at) =>
      ActionSchema.PickUp(
        ItemConverter.toSchema(target),
        PositionConverter.toSchema(at)
      )
    case Action.Drop(target, at) =>
      ActionSchema.Drop(
        ItemConverter.toSchema(target),
        PositionConverter.toSchema(at)
      )

  override def toDomain(schema: ActionSchema): Either[Validation, Action] =
    schema match
      case ActionSchema.Move(to) =>
        for pos <- PositionConverter.toDomain(to)
        yield Action.Move(pos)
      case ActionSchema.PickUp(target, at) =>
        for
          item <- ItemConverter.toDomain(target)
          pos <- PositionConverter.toDomain(at)
        yield Action.PickUp(item, pos)
      case ActionSchema.Drop(target, at) =>
        for
          item <- ItemConverter.toDomain(target)
          pos <- PositionConverter.toDomain(at)
        yield Action.Drop(item, pos)
