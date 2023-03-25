package emulator

import event.PassengerListener
import building.Building
import building.Building.moveFromElevatorToFloor
import building.Building.moveFromFloorToElevator
import org.tinylog.Logger
import passenger.Passenger
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

    private fun Passenger.exitElevator() = moveFromElevatorToFloor()

    private fun Passenger.enterElevator() = moveFromFloorToElevator()

    private fun Passenger.clickTargetButton(barrier: CyclicBarrier) {
        val listener = getPassengerListener(targetFloorNum, barrier)
        Building.clickInsideElevator(this, listener)
    }

    private fun getPassengerListener(floorNum: Int, barrier: CyclicBarrier) = PassengerListener(floorNum) {
        barrier.await()
        barrier.reset()
    }
}


