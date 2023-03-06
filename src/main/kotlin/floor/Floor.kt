package floor

import model.Button
import building.ElevatorSystem
import model.Request
import passenger.Passenger
import type.Direction
import java.util.concurrent.CopyOnWriteArrayList

data class Floor(val number: Int) {
    val buttonUp = object : Button {
        override fun click() {
            ElevatorSystem.request(Request(number, Direction.UP))
        }
    }
    val buttonDown = object : Button {
        override fun click() {
            ElevatorSystem.request(Request(number, Direction.DOWN))
        }
    }

    private val passengers = CopyOnWriteArrayList<Passenger>()

    fun addPassenger(passenger: Passenger) {
        passengers.add(passenger)
    }

    fun removePassenger(passenger: Passenger) {
        passengers.remove(passenger)
    }

    fun containsPassenger(passenger: Passenger) = passengers.contains(passenger)

}