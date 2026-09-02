package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.control.serialization.Converter
import it.unibo.sentinel.core.mission.Action
import it.unibo.sentinel.control.serialization.schemas.ActionSchema

object ActionConverter extends Converter[Action, ActionSchema]:

  override def toSchema(model: Action): ActionSchema = model match
    case Action.Move(target) =>
      ActionSchema.Move(PositionConverter.toSchema(target))

  override def toDomain(schema: ActionSchema): Action = schema match
    case ActionSchema.Move(to) => Action.Move(PositionConverter.toDomain(to))
