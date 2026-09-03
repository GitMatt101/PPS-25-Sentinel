package it.unibo.sentinel.control.serialization.json

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.{Warehouse, Position, Tile, Area}
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.core.scenario.{Scenario, Spawn}
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.control.serialization.Repository
import it.unibo.sentinel.control.serialization.Codec.Validation
import it.unibo.sentinel.control.serialization.Codec
import it.unibo.sentinel.control.serialization.JsonSerialization.given

class ScenarioJsonCodecSpec extends UnitTest:

  given warehousePath: String = "warehouses/test-warehouse.json"
  val testWarehouse: Warehouse = Warehouse
    .empty(5, 5)
    .withArea(Area(Position(0, 0), Position(4, 4))):
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

  val scenario: Scenario = Scenario
    .in(testWarehouse)
    .place(Spawn(RobotId("robot-1"), Position(1, 1)))
    .getOrElse(fail("Could not place spawn"))

  val codec = summon[Codec[Scenario]]

  "The ScenarioJsonCodec" should:

    "correctly encode and decode a Scenario domain object" in:
      val jsonOutput = codec.encode(scenario)
      val decoded = codec.decode(jsonOutput)
      println(jsonOutput)
      decoded shouldBe Right(scenario)

    "return Validation.FileNotFound when the warehouse path is missing in the repository" in:
      val fakeWarehouse: String = "warehouses/fake-warehouse.json"
      val invalidWarehouseJson =
        s"""{
          |  "warehousePath": "$fakeWarehouse",
          |  "spawns": [
          |    {
          |      "id": "robot-1",
          |      "position": {
          |        "x": 1,
          |        "y": 1
          |      }
          |    }
          |  ],
          |  "missions": [],
          |  "routing": "Distance",
          |  "assignment": "Nearest",
          |  "collisionSelection": "Random",
          |  "collisionAvoidance": "Wait"
          |}""".stripMargin

      val result = codec.decode(invalidWarehouseJson)
      result shouldBe Left(
        Validation.FileNotFound(fakeWarehouse)
      )

    "return SyntaxError when given malformed JSON" in:
      val malformedJson =
        """{ "warehousePath": "warehouses/test.json", "spawns": [ """
      val result = codec.decode(malformedJson)
      result should matchPattern { case Left(Validation.Syntax(_)) => }
