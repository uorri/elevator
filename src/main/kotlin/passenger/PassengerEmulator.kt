package passenger

import event.PassengerListener
import building.Building
import org.tinylog.Logger
import java.util.concurrent.CyclicBarrier

object PassengerEmulator {

    fun emulate(passenger: Passenger) {
        val barrier = CyclicBarrier(2)
        Building.addPassenger(passenger)
        with(passenger) {
            callElevator(barrier)
            waitElevatorArrive(barrier)
            enterElevator()
            clickTargetButton(barrier)
            waitElevatorArrive(barrier)
            exitElevator()
        }
    }

    private fun Passenger.waitElevatorArrive(barrier: CyclicBarrier) {
        Logger.info { "$name is waiting." }
        barrier.await()
    }

    private fun Passenger.callElevator(barrier: CyclicBarrier) {
        val listener = getPassengerListener(sourceFloorNum, barrier)
        Building.callElevator(this, listener)
    }

    private fun Passenger.exitElevator() {
        Building.movePassengerFromElevatorToFloor(this)
    }

    private fun Passenger.enterElevator() {
        Building.movePassengerFromFloorToElevator(this)
    }

    private fun Passenger.clickTargetButton(barrier: CyclicBarrier) {
        val listener = getPassengerListener(targetFloorNum, barrier)
        Building.clickInsideElevator(this, listener)
    }

    private fun getPassengerListener(floorNum: Int, barrier: CyclicBarrier): PassengerListener {
        val listener = PassengerListener(floorNum) {
            barrier.await()
            barrier.reset()
        }
        return listener
    }
}


