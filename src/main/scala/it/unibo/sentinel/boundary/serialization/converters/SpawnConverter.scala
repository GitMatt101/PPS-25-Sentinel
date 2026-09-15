package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.boundary.serialization.Converter
import it.unibo.sentinel.core.scenario.Spawn
import it.unibo.sentinel.boundary.serialization.schemas.SpawnSchema
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.robot.value
import it.unibo.sentinel.boundary.serialization.Codec.Validation

/** [[Converter]] used to convert from [[Spawn]] to [[SpawnSchema]] and
  * viceversa.
  */
object SpawnConverter extends Converter[Spawn, SpawnSchema]:

  override def toSchema(model: Spawn): SpawnSchema =
    SpawnSchema(
      model.id.value,
      PositionConverter.toSchema(model.at),
      model.ofClass
    )

  override def toDomain(schema: SpawnSchema): Either[Validation, Spawn] =
    for pos <- PositionConverter.toDomain(schema.position)
    yield Spawn(RobotId(schema.id), pos, schema.ofClass)
