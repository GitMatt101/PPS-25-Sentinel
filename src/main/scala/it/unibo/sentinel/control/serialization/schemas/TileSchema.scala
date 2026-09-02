package it.unibo.sentinel.control.serialization.schemas

import it.unibo.sentinel.control.serialization.Schema
import it.unibo.sentinel.core.warehouse.Tile
import it.unibo.sentinel.control.serialization.Codec.Validation
import it.unibo.sentinel.core.simulation.Tick

enum TileSchema extends Schema[Tile]:
  override type Self = TileSchema

  case Floor(cost: Int)

  override def validated: Either[Validation, TileSchema] = this match
    case Floor(cost) if cost < 0 =>
      Left(Validation.TileValidation(Tile.Validation.NegativeCost(Tick(cost))))
    case _ => Right(this)
