package it.unibo.sentinel.boundary.serialization.converters

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.Position
import it.unibo.sentinel.core.scenario.Spawn
import it.unibo.sentinel.core.robot.RobotId
import it.unibo.sentinel.core.robot.value
import it.unibo.sentinel.core.scenario.RobotClass
import it.unibo.sentinel.boundary.serialization.schemas.SpawnSchema
import it.unibo.sentinel.boundary.serialization.schemas.PositionSchema

class SpawnConverterSpec extends UnitTest with ConverterBehavior:

  "A SpawnConverter" when:

    "converting a spawn" should:

      "preserve RobotClass and position in SpawnSchema" in:
        val spawn =
          Spawn(RobotId("R1"), Position(1, 1), RobotClass.HeavyCarrier)
        val schema =
          SpawnSchema("R1", PositionSchema(1, 1), RobotClass.HeavyCarrier)
        schema.id.shouldBe(spawn.id.value)
        schema.position.shouldBe(PositionConverter.toSchema(spawn.at))
        schema.ofClass.shouldBe(spawn.ofClass)
