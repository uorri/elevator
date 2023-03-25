package floor

import model.Button
import building.ElevatorSystem
import model.Request
import passenger.Passenger
import type.Direction
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class Floor(val number: Int) {
    val buttonUp = object : Button {
        override fun click() = ElevatorSystem.request(Request(number, Direction.UP))
    }
    val buttonDown = object : Button {
        override fun click() = ElevatorSystem.request(Request(number, Direction.DOWN))
    }

    private val passengers = CopyOnWriteArrayList<Passenger>()
    private val lock = ReentrantLock()

    fun addPassenger(passenger: Passenger) = lock.withLock { passengers.add(passenger) }

    fun removePassenger(passenger: Passenger) = lock.withLock { passengers.remove(passenger) }

    fun containsPassenger(passenger: Passenger) = lock.withLock { passengers.contains(passenger) }

}