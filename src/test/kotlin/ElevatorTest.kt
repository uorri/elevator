import building.Building
import building.ElevatorSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import passenger.Passenger
import passenger.PassengerEmulator
import passenger.PassengerGenerator
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ElevatorTest {
    private val defaultScope = CoroutineScope(Dispatchers.Default)
    private lateinit var passengerLatch: CountDownLatch

    @BeforeEach
    fun init() {
        ElevatorSystem.start()
    }

    @AfterEach
    fun shutdown() {
        ElevatorSystem.shutdown()
    }

    @Test
    fun `Creating a passenger on a non-existent floor`() {
        assertThrows<IllegalArgumentException> {
            getPassenger(Building.firstFloor - 1, 5)
        }
        assertThrows<IllegalArgumentException> {
            getPassenger(Building.lastFloor + 1, 5)
        }
    }

    @Test
    fun `Creating a passenger with the target of a non-existent floor`() {
        assertThrows<IllegalArgumentException> {
            getPassenger(1, Building.firstFloor - 1)
        }
        assertThrows<IllegalArgumentException> {
            getPassenger(1, Building.lastFloor + 1)
        }
    }

    @ParameterizedTest
    @CsvSource("1, 10", "7, 2", "3, 9", "25, 4")
    fun `A passenger calls the elevator`(sourceFloor: Int, targetFloor: Int) {
        val passenger = getPassenger(sourceFloor, targetFloor)
        initPassengerLatch(1)
        launchPassengerThread(passenger)
        passengerLatch.await()
        assertTrue { passenger.isArrived() }
    }

    @Test
    fun `A passenger calls the elevator on the first floor and rides to the last`() {
        val passenger = getPassenger(Building.firstFloor, Building.lastFloor)
        initPassengerLatch(1)
        launchPassengerThread(passenger)
        passengerLatch.await()
        assertTrue { passenger.isArrived() }
    }

    @ParameterizedTest
    @CsvSource("3, 7, 8, 2", "1, 10, 4, 5", "4, 25, 3, 11")
    fun `Passengers call the elevator`(
        sourceFloor1: Int, sourceFloor2: Int,
        targetFloor1: Int, targetFloor2: Int
    ) {
        val passenger1 = getPassenger(sourceFloor1, targetFloor1)
        val passenger2 = getPassenger(sourceFloor2, targetFloor2)
        initPassengerLatch(2)
        launchPassengerThread(passenger1)
        launchPassengerThread(passenger2)
        passengerLatch.await()
        assertTrue { passenger1.isArrived() }
        assertTrue { passenger2.isArrived() }
    }

    @Test
    fun `Passengers call the elevator on the same floor`() {
        val sourceFloor = 18
        val passenger1 = getPassenger(sourceFloor, 1)
        val passenger2 = getPassenger(sourceFloor, 5)
        val passenger3 = getPassenger(sourceFloor, 3)
        initPassengerLatch(3)
        launchPassengerThread(passenger1)
        launchPassengerThread(passenger2)
        launchPassengerThread(passenger3)
        passengerLatch.await()
        assertTrue { passenger1.isArrived() }
        assertTrue { passenger2.isArrived() }
        assertTrue { passenger3.isArrived() }
    }

    @Test
    fun `Passengers call the elevator from same floor and ride different directions`() {
        val sourceFloor = 18
        val passenger1 = getPassenger(sourceFloor, 1)
        val passenger2 = getPassenger(sourceFloor, 25)
        initPassengerLatch(2)
        launchPassengerThread(passenger1)
        launchPassengerThread(passenger2)
        passengerLatch.await()
        assertTrue { passenger1.isArrived() }
        assertTrue { passenger2.isArrived() }
    }

    @Test
    fun `Passengers call the elevator from different floors and ride to the same floor`() {
        val targetFloor = 5
        val passenger1 = getPassenger(1, targetFloor)
        val passenger2 = getPassenger(10, targetFloor)
        initPassengerLatch(2)
        launchPassengerThread(passenger1)
        launchPassengerThread(passenger2)
        passengerLatch.await()
        assertTrue { passenger1.isArrived() }
        assertTrue { passenger2.isArrived() }
    }

    @Test
    fun `The elevator picks up passengers who called it during its movement`() {
        ElevatorSystem.reset()
        initPassengerLatch(3)
        launchPassengerThread(getPassenger(1, 15))
        MILLISECONDS.sleep(200)
        launchPassengerThread(getPassenger(10, 4))
        MILLISECONDS.sleep(200)
        launchPassengerThread(getPassenger(4, 12))
        passengerLatch.await()
        assertEquals(listOf(1, 4, 12, 15, 10, 4), ElevatorSystem.getStopHistory())
    }

    @Test
    fun `Passengers call the elevator from the same floor and ride to the same floor`() {
        val sourceFloor = 10
        val targetFloor = 1
        val passenger1 = getPassenger(sourceFloor, targetFloor)
        val passenger2 = getPassenger(sourceFloor, targetFloor)
        val passenger3 = getPassenger(sourceFloor, targetFloor)
        initPassengerLatch(3)
        launchPassengerThread(passenger1)
        launchPassengerThread(passenger2)
        launchPassengerThread(passenger3)
        passengerLatch.await()
        assertTrue { passenger1.isArrived() }
        assertTrue { passenger2.isArrived() }
        assertTrue { passenger3.isArrived() }
    }

    @Test
    fun `Elevator operates during rush hour`() {
        val passengers = mutableListOf<Passenger>()
        repeat(100) {
            passengers.add(getPassenger(
                sourceFloor = Random.nextInt(1, 25),
                targetFloor = Random.nextInt(1, 25))
            )
        }
        initPassengerLatch(passengers.size)
        for (passenger in passengers) {
            launchPassengerThread(passenger)
        }
        passengerLatch.await()
        for (passenger in passengers) {
            assertTrue { passenger.isArrived() }
        }
    }

    private fun getPassenger(sourceFloor: Int, targetFloor: Int) =
        PassengerGenerator.create(sourceFloor, targetFloor)

    private fun launchPassengerThread(passenger: Passenger) {
        defaultScope.launch {
            PassengerEmulator.emulate(passenger)
            passengerLatch.countDown()
        }
    }

    private fun initPassengerLatch(count: Int) {
        passengerLatch = CountDownLatch(count)
    }

    private fun Passenger.isArrived(): Boolean {
        return Building.passengerIsOnTargetFloor(this)
    }


}