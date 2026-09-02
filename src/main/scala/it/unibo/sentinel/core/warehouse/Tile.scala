package it.unibo.sentinel.core.warehouse

import it.unibo.sentinel.core.simulation.Tick

/** Represents a tile in the warehouse.
  */
sealed trait Tile

object Tile:

  /** Validation error generated when creating a [[Tile]].
    */
  enum Validation:
    /** The cost of the tile is negative.
      */
    case NegativeCost(cost: Tick)

  /** Represents a floor tile.
    */
  case class Floor(cost: Tick = Tick.unit) extends Tile
