package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.boundary.serialization.Converter
import it.unibo.sentinel.boundary.serialization.schemas.MissionSchema
import it.unibo.sentinel.core.mission.{Mission, MissionId, Task}
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.boundary.serialization.Codec.Validation
import it.unibo.sentinel.core.mission.Priority

/** [[Converter]] used to convert from [[Mission]] to [[MissionSchema]] and
  * viceversa.
  */
object MissionConverter extends Converter[Mission, MissionSchema]:

  override def toSchema(model: Mission): MissionSchema =
    MissionSchema(
      model.id.value,
      TaskConverter.toSchema(model.task),
      model.deadline.value,
      model.priority.value
    )

  override def toDomain(schema: MissionSchema): Either[Validation, Mission] =
    for
      domainTask <- TaskConverter.toDomain(schema.task)
      priority <- Priority
        .from(schema.priority)
        .toRight(
          Validation.MissionValidation(
            Mission.Validation.InvalidPriority(
              MissionId(schema.id),
              schema.priority
            )
          )
        )
      mission <- domainTask match
        case Task.Done =>
          Left(
            Validation.MissionValidation(
              Mission.Validation.AlreadyCompleted(MissionId(schema.id))
            )
          )
        case validTask =>
          Right(
            Mission(
              MissionId(schema.id),
              validTask,
              Tick(schema.duration),
              priority
            )
          )
    yield mission
