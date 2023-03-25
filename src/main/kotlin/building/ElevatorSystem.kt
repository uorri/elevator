package building

import elevator.ElevatorController
import model.Request
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ElevatorSystem {
    private lateinit var elevatorService: ExecutorService
    private var controller = ElevatorController(Building.elevator)

    fun start() {
        elevatorService = Executors.newSingleThreadExecutor(
            Thread.ofVirtual().name("Thread Elevator").factory()
        )
        elevatorService.submit { controller.start() }
    }
    fun shutdown() {
        getStopHistory().clear()
        controller.idle()
        elevatorService.shutdownNow()
    }
    fun request(request: Request) = controller.outsideRequest(request)
    fun insideRequest(number: Int) = controller.insideRequest(number)
    fun getStopHistory() = controller.stopHistory
    fun reset() = controller.reset()

}
