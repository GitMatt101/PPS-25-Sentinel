package it.unibo.sentinel.control.serialization.converters

import it.unibo.sentinel.control.serialization.Converter
import it.unibo.sentinel.control.serialization.schemas.ScenarioSchema
import it.unibo.sentinel.core.scenario.Spawn
import it.unibo.sentinel.control.serialization.schemas.SpawnSchema
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.robot.value
import it.unibo.sentinel.core.mission.Mission
import it.unibo.sentinel.core.warehouse.Warehouse
import it.unibo.sentinel.core.scenario.Scenario
import it.unibo.sentinel.control.serialization.Repository
import it.unibo.sentinel.control.serialization.Codec.Validation

object ScenarioConverter:

  private given spawnConverter: Converter[Spawn, SpawnSchema] =
    new Converter[Spawn, SpawnSchema]:

      override def toSchema(model: Spawn): SpawnSchema =
        SpawnSchema(model.id.value, PositionConverter.toSchema(model.at))

      override def toDomain(schema: SpawnSchema): Either[Validation, Spawn] =
        for pos <- PositionConverter.toDomain(schema.position)
        yield Spawn(RobotId(schema.id), pos)

  given (using repo: Repository[String, Warehouse], warehousePath: String): Converter[
    Scenario,
    ScenarioSchema
  ] with

    override def toSchema(model: Scenario): ScenarioSchema =
      ScenarioSchema(
        warehousePath,
        model.spawns.map(spawnConverter.toSchema),
        model.missions.map(MissionConverter.toSchema),
        model.routing,
        model.assignment,
        model.collisionSelection,
        model.collisionAvoidance
      )

    override def toDomain(
        schema: ScenarioSchema
    ): Either[Validation, Scenario] =
      repo.load(schema.warehousePath).map { warehouse =>
        var scenario = Scenario.in(warehouse)
        for
          either <- schema.spawns.map(spawnConverter.toDomain)
          spawn <- either
          step <- scenario.place(spawn)
        do scenario = step
        for
          either <- schema.missions.map(MissionConverter.toDomain)
          mission <- either
          step <- scenario.load(mission)
        do scenario = step
        scenario
          .withRouting(schema.routing)
          .withAssignment(schema.assignment)
          .withCollisionSelection(schema.collisionSelection)
          .withCollisionAvoidance(schema.collisionAvoidance)
      }
