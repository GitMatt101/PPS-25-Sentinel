package it.unibo.sentinel.control.serialization.schemas

import it.unibo.sentinel.control.serialization.Schema
import it.unibo.sentinel.control.serialization.Codec.Validation

enum ActionSchema extends Schema:

  case Move(to: PositionSchema)

  override def validated: Either[Validation, Schema] = this match
    case Move(to) => to.validated.map(_ => this)
