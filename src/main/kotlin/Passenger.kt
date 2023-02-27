data class Passenger(
    val name: String,
    val sourceFloorNum: Int,
    val targetFloorNum: Int
) {
    var currentFloor = sourceFloorNum
}
