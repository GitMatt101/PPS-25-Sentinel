package it.unibo.sentinel.boundary.serialization

import it.unibo.sentinel.boundary.serialization.Codec.Validation
import it.unibo.sentinel.boundary.serialization.schemas.*
import it.unibo.sentinel.boundary.serialization.converters.*
import it.unibo.sentinel.core.warehouse.{Warehouse, Position, Tile}
import it.unibo.sentinel.core.item.Item
import it.unibo.sentinel.core.mission.{Mission, Task, Action}

/** Converts between domain models and their corresponding serializable schemas.
  *
  * @tparam Model
  *   The domain model type.
  * @tparam ModelSchema
  *   The DTO/schema type used for serialization.
  */
trait Converter[Model, ModelSchema]:

  /** Converts a domain model to its schema representation.
    */
  def toSchema(model: Model): ModelSchema

  /** Reconstructs a domain model from its schema.
    */
  def toDomain(schema: ModelSchema): Either[Validation, Model]

object Converter:

  given Converter[Action, ActionSchema] = ActionConverter
  given Converter[Item, ItemSchema] = ItemConverter
  given Converter[Mission, MissionSchema] = MissionConverter
  given Converter[Position, PositionSchema] = PositionConverter
  given Converter[Task, TaskSchema] = TaskConverter
  given Converter[Tile, TileSchema] = TileConverter
  given Converter[Warehouse, WarehouseSchema] = WarehouseConverter
