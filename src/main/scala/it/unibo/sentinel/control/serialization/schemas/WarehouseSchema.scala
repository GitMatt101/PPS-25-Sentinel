package it.unibo.sentinel.control.serialization.schemas

case class WarehouseSchema(
  width: Int,
  height: Int,
  tiles: Seq[(PositionSchema, TileSchema)]
)
