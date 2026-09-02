package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.control.serialization.Converter
import it.unibo.sentinel.control.serialization.schemas.ScenarioSchema
import it.unibo.sentinel.core.scenario.Spawn
import it.unibo.sentinel.control.serialization.schemas.SpawnSchema
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.robot.value
import it.unibo.sentinel.core.mission.Mission
import it.unibo.sentinel.core.scenario.Policies.Routing
import it.unibo.sentinel.core.scenario.Policies.Assignment
import it.unibo.sentinel.core.scenario.Policies.CollisionSelection
import it.unibo.sentinel.core.scenario.Policies.CollisionAvoidance

case class ScenarioSubstitute(
    warehousePath: String,
    spawns: Seq[Spawn],
    missions: Seq[Mission],
    routing: Routing,
    assignment: Assignment,
    collisionSelection: CollisionSelection,
    collisionAvoidance: CollisionAvoidance
)

object ScenarioConverter extends Converter[ScenarioSubstitute, ScenarioSchema]:

  private given spawnConverter: Converter[Spawn, SpawnSchema] =
    new Converter[Spawn, SpawnSchema]:

      override def toSchema(model: Spawn): SpawnSchema =
        SpawnSchema(model.id.value, PositionConverter.toSchema(model.at))

      override def toDomain(schema: SpawnSchema): Spawn =
        Spawn(RobotId(schema.id), PositionConverter.toDomain(schema.position))

  override def toSchema(model: ScenarioSubstitute): ScenarioSchema =
    ScenarioSchema(
      model.warehousePath,
      model.spawns.map(spawnConverter.toSchema),
      model.missions.map(MissionConverter.toSchema),
      model.routing,
      model.assignment,
      model.collisionSelection,
      model.collisionAvoidance
    )

  override def toDomain(schema: ScenarioSchema): ScenarioSubstitute =
    ScenarioSubstitute(
      schema.warehousePath,
      schema.spawns.map(spawnConverter.toDomain),
      schema.missions.map(MissionConverter.toDomain),
      schema.routing,
      schema.assignment,
      schema.collisionSelection,
      schema.collisionAvoidance
    )
