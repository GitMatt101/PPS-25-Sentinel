package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.control.serialization.Converter
import it.unibo.sentinel.core.scenario.Spawn
import it.unibo.sentinel.control.serialization.schemas.SpawnSchema
import it.unibo.sentinel.core.robot.value
import it.unibo.sentinel.core.robot.RobotId

object SpawnConverter extends Converter[Spawn, SpawnSchema]:

  override def toSchema(model: Spawn): SpawnSchema =
    SpawnSchema(model.id.value, PositionConverter.toSchema(model.at))

  override def toDomain(schema: SpawnSchema): Spawn =
    Spawn(RobotId(schema.id), PositionConverter.toDomain(schema.position))
