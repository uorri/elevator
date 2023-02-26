import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ElevatorTest {

    @BeforeEach
    fun init() {
        ElevatorSystem.start()
    }

    @AfterEach
    fun shutdown() {
        ElevatorSystem.shutdown()
    }

    @Test
    fun testElevator() {
        val passenger = PassengerGenerator.getPassenger(name = "Michael", floor = 1, target = 8)
        val thread = Thread { passenger.emulate() }
        thread.start()
        thread.join()
        assertTrue { passenger.floor == passenger.target }
    }

}