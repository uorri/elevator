package building

import event.EventStorage
import event.PassengerListener
import elevator.ElevatorImpl
import floor.Floor
import org.tinylog.Logger
import passenger.Passenger

object Building {
    const val lastFloor: Int = 25
    const val firstFloor: Int = 1
    private val floors = initFloors()
    val elevator = ElevatorImpl()

    private fun initFloors(): Array<Floor> {
        var i = 1
        return Array(lastFloor) { Floor(i++) }
    }

    fun clickInsideElevator(passenger: Passenger, listener: PassengerListener): Boolean {
        if (!elevator.contains(passenger)) { return false }
        EventStorage.listen(listener)
        elevator.panel.buttons[passenger.targetFloorNum - 1].click()
        Logger.info { "${passenger.name} pressed the ${passenger.targetFloorNum} button in the elevator." }
        return true
    }

    fun addPassenger(passenger: Passenger) {
        getFloor(passenger.sourceFloorNum).addPassenger(passenger)
    }

    fun callElevator(passenger: Passenger, listener: PassengerListener) {
        Logger.info { "${passenger.name} calls the elevator on the ${passenger.sourceFloorNum} floor." }
        EventStorage.listen(listener)
        val floor = getFloor(passenger.sourceFloorNum)
        if (passenger.sourceFloorNum < passenger.targetFloorNum) {
            floor.buttonUp.click()
        } else {
            floor.buttonDown.click()
        }
    }

    fun movePassengerFromFloorToElevator(passenger: Passenger): Boolean {
        if (elevator.position != passenger.sourceFloorNum) { return false }
        getFloor(passenger.sourceFloorNum).removePassenger(passenger)
        elevator.add(passenger)
        Logger.info { "${passenger.name} entered the elevator." }
        return true
    }

    fun movePassengerFromElevatorToFloor(passenger: Passenger): Boolean {
        if (elevator.position != passenger.targetFloorNum) { return false }
        elevator.remove(passenger)
        getFloor(passenger.targetFloorNum).addPassenger(passenger)
        Logger.info { "${passenger.name} got off the elevator." }
        return true
    }

    private fun getFloor(number: Int) = floors[number - 1]

    fun passengerIsOnTargetFloor(passenger: Passenger): Boolean {
        val floors = floors.asSequence().filter { it.containsPassenger(passenger) }
        if (floors.count() != 1) return false
        return floors.first().number == passenger.targetFloorNum
    }
}