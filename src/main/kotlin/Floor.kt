data class Floor(
    val number: Int,
    private val notifier: Notifier
) {
    val buttonUp = object : FloorButton {
        override fun click() {
            ElevatorSystem.request(number, Direction.UP)
        }
    }
    val buttonDown = object : FloorButton {
        override fun click() {
            ElevatorSystem.request(number, Direction.DOWN)
        }
    }

    var isElevatorOnFloorAndDoorsAreOpen = false

    fun getBarrier() = notifier.barriers[number - 1].barrier

}