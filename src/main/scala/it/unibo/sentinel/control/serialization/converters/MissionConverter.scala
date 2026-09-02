package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.control.serialization.Converter
import it.unibo.sentinel.control.serialization.schemas.MissionSchema
import it.unibo.sentinel.core.mission.{Mission, MissionId, Action, Task}
import it.unibo.sentinel.control.serialization.schemas.TaskSchema
import it.unibo.sentinel.control.serialization.schemas.ActionSchema
import it.unibo.sentinel.core.simulation.Tick

object MissionConverter extends Converter[Mission, MissionSchema]:

  given Converter[Action, ActionSchema] with

    override def toSchema(model: Action): ActionSchema = model match
      case Action.Move(target) =>
        ActionSchema.Move(PositionConverter.toSchema(target))

    override def toDomain(schema: ActionSchema): Action = schema match
      case ActionSchema.Move(to) => Action.Move(PositionConverter.toDomain(to))

  given Converter[Task, TaskSchema] with

    override def toSchema(model: Task): TaskSchema = model match
      case Task.Single(action) =>
        TaskSchema.Single(
          summon[Converter[Action, ActionSchema]].toSchema(action)
        )
      case Task.Done =>
        TaskSchema.Done

    override def toDomain(schema: TaskSchema): Task = schema match
      case TaskSchema.Single(action) =>
        Task.Single(summon[Converter[Action, ActionSchema]].toDomain(action))
      case TaskSchema.Done =>
        Task.Done

  override def toSchema(model: Mission): MissionSchema =
    MissionSchema(
      model.id.value,
      summon[Converter[Task, TaskSchema]].toSchema(model.task),
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
            Mission(
              MissionId(id),
              summon[Converter[Task, TaskSchema]].toDomain(task),
              Tick(duration)
            )
