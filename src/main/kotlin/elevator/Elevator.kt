package elevator

import passenger.Passenger

abstract class Elevator {
    abstract var position: Int
    abstract val passengers: Collection<Passenger>
    abstract val panel: ElevatorPanel
    abstract fun moveUp()
    abstract fun moveDown()
    abstract fun openDoors()
    abstract fun closeDoors()
    abstract fun add(passenger: Passenger)
    abstract fun remove(passenger: Passenger)
}