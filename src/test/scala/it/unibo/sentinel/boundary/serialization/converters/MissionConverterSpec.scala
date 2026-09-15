package it.unibo.sentinel.boundary.serialization

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.mission.{Mission, MissionId, Priority}
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.core.warehouse.Position
import it.unibo.sentinel.boundary.serialization.Codec.Validation
import it.unibo.sentinel.boundary.serialization.converters.*
import it.unibo.sentinel.boundary.serialization.schemas.*

class MissionConverterSpec extends UnitTest with ConverterBehavior:

  "A MissionConverter" when:
    behave like basicConverter(
      model = Mission
        .relocate(MissionId("M1"), Position(5, 5), Tick(10), Priority(2)),
      schema = MissionSchema(
        "M1",
        TaskSchema.Single(ActionSchema.Move(PositionSchema(5, 5))),
        10,
        2
      ),
      converter = MissionConverter
    )

    "reject out-of-range priority as InvalidPriority" in:
      val badSchema = MissionSchema(
        "M1",
        TaskSchema.Single(ActionSchema.Move(PositionSchema(5, 5))),
        10,
        0
      )
      MissionConverter
        .toDomain(badSchema)
        .shouldBe(
          Left(
            Validation.MissionValidation(
              Mission.Validation.InvalidPriority(MissionId("M1"), 0)
            )
          )
        )

    "reject TaskSchema.Done as AlreadyCompleted" in:
      MissionConverter
        .toDomain(MissionSchema("M9", TaskSchema.Done, 10))
        .shouldBe(
          Left(
            Validation.MissionValidation(
              Mission.Validation.AlreadyCompleted(MissionId("M9"))
            )
          )
        )
