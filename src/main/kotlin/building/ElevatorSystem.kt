package building

import elevator.ElevatorController
import model.Request
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ElevatorSystem {
    private lateinit var elevatorService: ExecutorService
    private var controller: ElevatorController = ElevatorController(Building.elevator)

    fun start() {
        val factory = Thread.ofVirtual().name("Thread Elevator").factory()
        elevatorService = Executors.newSingleThreadExecutor(factory)
        elevatorService.submit { controller.start() }
    }

    fun shutdown() {
        controller.stopHistory.clear()
        elevatorService.shutdownNow()
    }
    fun request(request: Request) = controller.outsideRequest(request)
    fun insideRequest(number: Int) = controller.insideRequest(number)
    fun getStopHistory() = controller.stopHistory
    fun reset() = controller.reset()

}
