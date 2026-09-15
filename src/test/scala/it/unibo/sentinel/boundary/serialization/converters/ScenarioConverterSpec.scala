package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.*
import it.unibo.sentinel.boundary.serialization.Codec.Validation
import it.unibo.sentinel.boundary.serialization.Converter
import it.unibo.sentinel.core.scenario.Scenario
import it.unibo.sentinel.boundary.serialization.schemas.ScenarioSchema
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.boundary.serialization.converters.ScenarioConverter.scenarioConverter

class ScenarioConverterSpec extends UnitTest with ConverterBehavior:

  "A ScenarioConverter" when:
    val warehouse: Warehouse = Warehouse
      .empty(WarehouseId("W"), 3, 3)
      .withTile(Position(1, 1))(Tile.Floor(Tick.unit))

    given (String => Either[Validation, Warehouse]) = _ => Right(warehouse)
    val converter: Converter[Scenario, ScenarioSchema] = scenarioConverter

    "converting a Scenario" should:

      "preserve a non-default seed in toSchema" in:
        val scenario = Scenario.in(warehouse).withSeed(123L)
        converter.toSchema(scenario).seed.shouldBe(123L)

      "restore the seed in toDomain" in:
        val scenario = Scenario.in(warehouse).withSeed(123L)
        val schema = converter.toSchema(scenario)
        converter.toDomain(schema).map(_.seed).shouldBe(Right(123L))

      "preserve the default seed" in:
        val scenario = Scenario.in(warehouse)
        converter
          .toDomain(converter.toSchema(scenario))
          .shouldBe(Right(scenario))
