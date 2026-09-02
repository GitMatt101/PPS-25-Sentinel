package it.unibo.sentinel.control.serialization.schemas

import it.unibo.sentinel.control.serialization.Schema
import it.unibo.sentinel.control.serialization.Codec.Validation
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.mission.MissionId
import it.unibo.sentinel.control.serialization.converters.PositionConverter
import it.unibo.sentinel.core.scenario.Validation as DomainValidation
import it.unibo.sentinel.core.scenario.Policies.Routing
import it.unibo.sentinel.core.scenario.Policies.Assignment
import it.unibo.sentinel.core.scenario.Policies.CollisionSelection
import it.unibo.sentinel.core.scenario.Policies.CollisionAvoidance

extension (schemas: Seq[Schema])
  private def validateAll: Either[Validation, Unit] =
    schemas.iterator
      .map(_.validated)
      .collectFirst { case Left(err) => err }
      .toLeft(())

extension [A](seq: Seq[A])
  private def getFirstDuplicate: Option[A] =
    seq.filter(e => seq.count(_ == e) > 1).headOption

final case class ScenarioSchema(
    warehousePath: String,
    spawns: Seq[SpawnSchema],
    missions: Seq[MissionSchema],
    routing: Routing,
    assignment: Assignment,
    collisionSelection: CollisionSelection,
    collisionAvoidance: CollisionAvoidance
) extends Schema:

  override def validated: Either[Validation, ScenarioSchema] =
    for
      _ <- spawns.validateAll
      _ <- missions.validateAll
      _ <- checkUniqueBy(spawns)(
        _.id,
        id => DomainValidation.RobotAlreadyExists(RobotId(id))
      )
      _ <- checkUniqueBy(spawns)(
        _.position,
        pos =>
          DomainValidation.PositionOccupied(PositionConverter.toDomain(pos))
      )
      _ <- checkUniqueBy(missions)(
        _.id,
        id => DomainValidation.MissionAlreadyExists(MissionId(id))
      )
    yield this

  private def checkUniqueBy[A, Key](items: Seq[A])(
      extractKey: A => Key,
      makeDomainError: Key => DomainValidation
  ): Either[Validation, Unit] =
    items.map(extractKey).getFirstDuplicate match
      case Some(duplicateKey) =>
        Left(Validation.ScenarioValidation(makeDomainError(duplicateKey)))
      case None =>
        Right(())
