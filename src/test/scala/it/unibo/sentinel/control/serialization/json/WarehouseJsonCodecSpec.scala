package it.unibo.sentinel.control.serialization.json

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.{Warehouse, Position, Tile, Area}
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.control.serialization.Codec.Validation
import it.unibo.sentinel.control.serialization.Codec
import it.unibo.sentinel.control.serialization.JsonSerialization.given

class WarehouseJsonCodecSpec extends UnitTest:

  private val correctWarehouse: Warehouse = Warehouse
    .empty(4, 4)
    .withArea(Area(Position(1, 1), Position(2, 2))):
      Tile.Floor(Tick.unit)

  val codec = summon[Codec[Warehouse]]
  private val validJsonInput: String = codec.encode(correctWarehouse)

  "The WarehouseJsonCodec" should:

    "correctly decode a valid JSON into a Warehouse domain object" in:
      val result = codec.decode(validJsonInput)
      result shouldBe Right(correctWarehouse)

    "correctly encode a Warehouse domain object into JSON" in:
      val jsonOutput = codec.encode(correctWarehouse)
      val parsedResult = codec.decode(jsonOutput)
      parsedResult shouldBe Right(correctWarehouse)

    "roundtrip encode and decode seamlessly" in:
      val jsonOutput = codec.encode(correctWarehouse)
      val decoded = codec.decode(jsonOutput)
      decoded shouldBe Right(correctWarehouse)

    "return SyntaxError when given invalid JSON syntax" in:
      val malformedJson = """{ "width": 4, "height": 4, "tiles": """
      val result = codec.decode(malformedJson)
      result should matchPattern {
        case Left(Validation.Syntax(_)) =>
      }

    "return WarehouseSerializationError(InvalidDimensions) when dimensions are non-positive" in:
      val invalidDimJson =
        """{
          |  "width": 0,
          |  "height": -2,
          |  "tiles": []
          |}""".stripMargin
      val result = codec.decode(invalidDimJson)
      result shouldBe Left(
        Validation.WarehouseValidation(
          Warehouse.Validation.InvalidSize(0, -2)
        )
      )

    "return WarehouseSerializationError(PositionOutOfBounds) when a tile position is out of grid bounds" in:
      val outOfBoundsJson =
        """{
          |  "width": 1,
          |  "height": 1,
          |  "tiles": [
          |    [{"x": 5, "y": 5}, {"$type": "Floor", "cost": 1}]
          |  ]
          |}""".stripMargin
      val result = codec.decode(outOfBoundsJson)
      result shouldBe Left(
        Validation.WarehouseValidation(
          Warehouse.Validation.TilesOutOfBounds(Seq(Position(5, 5)), 1, 1)
        )
      )
