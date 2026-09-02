package it.unibo.sentinel.control.serialization.schemas

import it.unibo.sentinel.control.serialization.Schema
import it.unibo.sentinel.control.serialization.Codec.Validation
import it.unibo.sentinel.core.mission.Mission
import it.unibo.sentinel.core.mission.MissionId

final case class MissionSchema(id: String, task: TaskSchema, duration: Int)
    extends Schema:

  override def validated: Either[Validation, Schema] =
    for
      _ <- task.validated
      _ <- Either.cond(
        duration > 0,
        (),
        Validation.MissionValidation:
          Mission.Validation.NegativeDuration(MissionId(id), duration)
      )
    yield this
