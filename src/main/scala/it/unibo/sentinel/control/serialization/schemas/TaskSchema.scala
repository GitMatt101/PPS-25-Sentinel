package it.unibo.sentinel.control.serialization.schemas

import it.unibo.sentinel.control.serialization.Schema
import it.unibo.sentinel.control.serialization.Codec.Validation

enum TaskSchema extends Schema:

  case Single(action: ActionSchema)
  case Done

  override def validated: Either[Validation, Schema] = this match
    case Single(action) => action.validated.map(_ => this)
    case Done => Right(this)
