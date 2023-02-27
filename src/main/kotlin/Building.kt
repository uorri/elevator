object Building {
    const val lastFloor: Int = 25
    const val firstFloor: Int = 1

    private val floors = initFloors()
    private val notifier = Notifier()

    private fun initFloors(): Array<Floor> {
        var i = 1
        return Array(25) { Floor(i++, notifier) }
    }

    fun getSourceFloor(passenger: Passenger) = getFloor(passenger.sourceFloorNum)

    fun getTargetFloor(passenger: Passenger) = getFloor(passenger.targetFloorNum)

    private fun getFloor(number: Int) = floors[number - 1]


}