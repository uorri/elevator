package elevator

import event.EventStorage
import building.Building
import model.Request
import org.tinylog.Logger
import type.Direction.DOWN
import type.Direction.UP
import elevator.State.*
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ElevatorController(private val elevator: Elevator) {
    private val lock = ReentrantLock()
    private val isInsideRequestCondition = lock.newCondition()
    private val isOutsideRequestCondition = lock.newCondition()
    private val requestController = RequestController()
    private var state = IDLE
    private var direction = UP
    private var target = 0

    fun start() {
        while (state == IDLE) lock.withLock { isOutsideRequestCondition.await() }
        while (state != IDLE) run()
    }

    private fun run() {
        val position = elevator.position
        when {
            position < target -> elevator.moveUp()
            position > target -> elevator.moveDown()
            else -> stop(position)
        }
    }

    private fun stop(position: Int) {
        makeStopSettings(position)
        elevator.openDoors()
        notifyPassengers(position)
        waitInsideRequest()
        elevator.closeDoors()
        if (state == RUNNING) { updateTarget() }
    }

    private fun makeStopSettings(position: Int) {
        changeStateTo(WAITING)
        requestController.remove(position)
        if (isEdgeFloor()) { reverse() }
        stopHistory.add(position)
    }

    private fun waitInsideRequest() = lock.withLock {
        while (state == WAITING) {
            if (isInsideRequestCondition.await(100, MILLISECONDS)) {
                Logger.info { "An inside request has been received." }
            } else {
                Logger.info { "Timeout expired." }
                changeStateTo(if (requestController.noRequests()) IDLE else RUNNING)
            }
        }
    }

    private fun updateTarget() {
        target = getNextTarget() ?: run {
            reverse()
            getNextTarget()!!
        }
    }

    fun outsideRequest(request: Request) {
        val isNewRequest = requestController.add(request)
        when {
            isNewRequest && state == IDLE -> setInitialSettings(request.floor)
            isNewRequest && needPickUp(request) -> changeTarget()
        }
    }

    private fun changeTarget() {
        requestController.getPickUpTarget(elevator.position, direction)?.let { target = it }
    }

    private fun setInitialSettings(target: Int) {
        this.target = target
        this.direction = getDirection(target)
        notifyElevatorOutside()
    }

    private fun needPickUp(request: Request): Boolean {
        val reqDirection = request.direction
        val reqFloor = request.floor
        return when {
            reqDirection != direction -> false
            reqDirection == DOWN && elevator.position >= reqFloor -> true
            reqDirection == UP && elevator.position <= reqFloor -> true
            else -> false
        }
    }

    fun insideRequest(number: Int) {
        requestController.add(Request(number, getDirection(number)))
        notifyElevatorInside()
    }

    private fun notifyElevatorOutside() {
        if (lock.tryLock(100, MILLISECONDS)) {
            try {
                state = RUNNING
                isOutsideRequestCondition.signalAll()
            } finally {
                lock.unlock()
            }
        }
    }

    private fun notifyElevatorInside() {
        if (lock.tryLock(100, MILLISECONDS)) {
            try {
                state = RUNNING
                isInsideRequestCondition.signalAll()
            } finally {
                lock.unlock()
            }
        }
    }

    private fun changeStateTo(state: State) {
        this.state = state
        Logger.info { "Elevator $state now." }
    }

    private fun getDirection(target: Int) = if (elevator.position < target) UP else DOWN

    private fun notifyPassengers(position: Int) = EventStorage.fire(position)

    private fun getNextTarget() = requestController.getNextTarget(elevator.position, direction)

    private fun isEdgeFloor(): Boolean {
        return elevator.position == Building.firstFloor || elevator.position == Building.lastFloor
    }

    private fun reverse() {
        direction = if (direction == UP) DOWN else UP
    }

    fun reset() {
        stopHistory.clear()
        target = 0
        elevator.position = 1
    }

    fun idle() {
        changeStateTo(IDLE)
    }

    val stopHistory = mutableListOf<Int>()

}
