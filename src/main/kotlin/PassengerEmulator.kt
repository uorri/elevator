object PassengerEmulator {

    fun emulate(passenger: Passenger) {
        val sourceFloor = Building.getSourceFloor(passenger)
        passenger.callElevator(sourceFloor)
        while (!sourceFloor.isElevatorOnFloorAndDoorsAreOpen) {
            sourceFloor.getBarrier().await()
        }
        passenger.enterElevator()
        val targetFloor = Building.getTargetFloor(passenger)
        while (!targetFloor.isElevatorOnFloorAndDoorsAreOpen) {
            targetFloor.getBarrier().await()
        }
        passenger.exitElevator(targetFloor)
    }

    private fun Passenger.callElevator(sourceFloor: Floor) {
        if (this.sourceFloorNum < this.targetFloorNum) {
            sourceFloor.buttonUp.click()
        } else {
            sourceFloor.buttonDown.click()
        }
    }

    private fun Passenger.exitElevator(targetFloor: Floor) {
        currentFloor = targetFloor.number
    }

    private fun Passenger.enterElevator() {

    }


}