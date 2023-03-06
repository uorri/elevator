package elevator

import org.tinylog.Logger
import passenger.Passenger
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit.MILLISECONDS

class ElevatorImpl : Elevator() {
    override var position = 1
    override val passengers = CopyOnWriteArrayList<Passenger>()
    override val panel: ElevatorPanel = ElevatorPanel(25)

    override fun moveUp() {
        MILLISECONDS.sleep(100)
        position++
        Logger.info { "The elevator moved up to: $position floor." }
    }

    override fun moveDown() {
        MILLISECONDS.sleep(100)
        position--
        Logger.info { "The elevator moved down to: $position floor." }
    }

    override fun openDoors() {
        Logger.info { "The doors have opened" }
    }

    override fun closeDoors() {
        Logger.info { "Doors closed" }
    }

    override fun add(passenger: Passenger) {
        passengers.add(passenger)
    }

    override fun remove(passenger: Passenger) {
        passengers.remove(passenger)
    }

    fun contains(passenger: Passenger) = passengers.contains(passenger)


}