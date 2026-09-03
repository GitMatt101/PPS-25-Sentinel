package it.unibo.sentinel.control.serialization.json

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.{Warehouse, Position, Tile, Area}
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.core.scenario.{Scenario, Spawn}
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.mission.MissionId
import it.unibo.sentinel.control.serialization.Repository
import it.unibo.sentinel.control.serialization.Codec.Validation
import it.unibo.sentinel.control.serialization.Codec
import it.unibo.sentinel.control.serialization.JsonSerialization.given
import it.unibo.sentinel.core.scenario.Validation as DomainValidation
import it.unibo.sentinel.core.mission.Mission

class ScenarioJsonCodecSpec extends UnitTest:

  given warehousePath: String = "warehouses/test-warehouse.json"

  val testWarehouse: Warehouse = Warehouse
    .empty(5, 5)
    .withArea(Area(Position(1, 1), Position(3, 3))):
      Tile.Floor(Tick.unit)

  given Repository[String, Warehouse] = new Repository[String, Warehouse]:
    private val storage: Map[String, Warehouse] = Map(
      warehousePath -> testWarehouse
    )

    override def load(key: String): Either[Validation, Warehouse] =
      storage.get(key) match
        case Some(w) => Right(w)
        case None    => Left(Validation.FileNotFound(key))

    override def save(
        entity: Warehouse,
        key: String
    ): Either[Validation, Unit] = Right(())

  val robotId: RobotId = RobotId("R1")
  val missionId: MissionId = MissionId("M1")
  val scenario: Scenario = Scenario
    .in(testWarehouse)
    .place(Spawn(robotId, Position(1, 1)))
    .getOrElse(fail("Could not place spawn"))
    .load(Mission.relocate(missionId, Position(2, 2), Tick(10)))
    .getOrElse(fail("Could not load mission"))

  val codec: Codec[Scenario] = summon[Codec[Scenario]]

  private def buildScenarioJson(
      spawnsJson: String =
        s"""[{"id": "$robotId", "position": {"x": 1, "y": 1}}]""",
      missionsJson: String = "[]",
      path: String = warehousePath
  ): String =
    s"""{
       |  "warehousePath": "$path",
       |  "spawns": $spawnsJson,
       |  "missions": $missionsJson,
       |  "routing": "Distance",
       |  "assignment": "Nearest",
       |  "collisionSelection": "Random",
       |  "collisionAvoidance": "Wait"
       |}""".stripMargin

  "The ScenarioJsonCodec" should:

    "correctly encode and decode a valid Scenario domain object" in:
      val jsonOutput = codec.encode(scenario)
      val decoded = codec.decode(jsonOutput)
      decoded shouldBe Right(scenario)

    "return Validation.FileNotFound when the warehouse path is missing in the repository" in:
      val fakeWarehouse = "warehouses/fake-warehouse.json"
      val result = codec.decode(buildScenarioJson(path = fakeWarehouse))
      result shouldBe Left(Validation.FileNotFound(fakeWarehouse))

    "return SyntaxError when given malformed JSON" in:
      val malformedJson =
        """{ "warehousePath": "warehouses/test.json", "spawns": [ """
      val result = codec.decode(malformedJson)
      result should matchPattern { case Left(Validation.Syntax(_)) => }

    "fail validation when spawns contain duplicate robot IDs" in:
      val duplicateSpawnsJson =
        s"""[
           |  {"id": "$robotId", "position": {"x": 1, "y": 1}},
           |  {"id": "$robotId", "position": {"x": 2, "y": 2}}
           |]""".stripMargin

      val result =
        codec.decode(buildScenarioJson(spawnsJson = duplicateSpawnsJson))
      result shouldBe Left(
        Validation.ScenarioValidation(
          DomainValidation.RobotAlreadyExists(robotId)
        )
      )

    "fail validation when spawns contain duplicate positions" in:
      val duplicatePositionsJson =
        s"""[
           |  {"id": "$robotId", "position": {"x": 1, "y": 1}},
           |  {"id": "R2", "position": {"x": 1, "y": 1}}
           |]""".stripMargin

      val result =
        codec.decode(buildScenarioJson(spawnsJson = duplicatePositionsJson))
      result shouldBe Left(
        Validation.ScenarioValidation(
          DomainValidation.PositionOccupied(Position(1, 1))
        )
      )

    "fail validation when missions contain duplicate mission IDs" in:
      val duplicateMissionsJson =
        s"""[
           |  {
           |    "id": "$missionId",
           |    "task": {"$$type": "Single", "action": {"$$type": "Move", "to": {"x": 2, "y": 2}}},
           |    "duration": 10
           |  },
           |  {
           |    "id": "$missionId",
           |    "task": {"$$type": "Single", "action": {"$$type": "Move", "to": {"x": 3, "y": 3}}},
           |    "duration": 10
           |  }
           |]""".stripMargin

      val result =
        codec.decode(buildScenarioJson(missionsJson = duplicateMissionsJson))
      result shouldBe Left(
        Validation.ScenarioValidation(
          DomainValidation.MissionAlreadyExists(missionId)
        )
      )
