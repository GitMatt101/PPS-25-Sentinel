package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.control.serialization.Converter
import it.unibo.sentinel.core.mission.Mission
import it.unibo.sentinel.control.serialization.schemas.MissionSchema
import it.unibo.sentinel.core.mission.MissionId
import it.unibo.sentinel.control.serialization.schemas.TaskSchema
import it.unibo.sentinel.control.serialization.schemas.ActionSchema
import it.unibo.sentinel.core.simulation.Tick

object MissionConverter extends Converter[Mission, MissionSchema]:

  override def toSchema(model: Mission): MissionSchema =
    MissionSchema(
      model.id.value,
      TaskConverter.toSchema(model.task),
      model.deadline.value
    )

  override def toDomain(schema: MissionSchema): Mission =
    schema match
      case MissionSchema(id, task, duration) =>
        task match
          case TaskSchema.Single(ActionSchema.Move(to)) =>
            Mission.relocate(
              MissionId(id),
              PositionConverter.toDomain(to),
              Tick(duration)
            )
          case TaskSchema.Done =>
            Mission(MissionId(id), TaskConverter.toDomain(task), Tick(duration))
