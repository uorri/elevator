import com.github.javafaker.Faker
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.test.assertTrue

class ElevatorTest {
    private val executorService = Executors.newVirtualThreadPerTaskExecutor()
    private val faker = Faker()
    private val passengerName = faker.name().firstName()
    private val passengerName2 = faker.name().firstName()

    @BeforeEach
    fun init() {
        ElevatorSystem.start()
    }

    @AfterEach
    fun shutdown() {
        ElevatorSystem.shutdown()
    }

    @Test
    fun invalidNameTest() {
        assertThrows<IllegalArgumentException> {
            PassengerGenerator.getPassenger("", 1, 10)
        }
        assertThrows<IllegalArgumentException> {
            PassengerGenerator.getPassenger(" ", 1, 4)
        }
    }

    @Test
    fun invalidSourceFloor() {
        assertThrows<IllegalArgumentException> {
            PassengerGenerator.getPassenger(passengerName, Building.firstFloor - 1, 5)
        }
        assertThrows<IllegalArgumentException> {
            PassengerGenerator.getPassenger(passengerName, Building.lastFloor + 1, 5)
        }
    }

    @Test
    fun invalidTargetFloor() {
        assertThrows<IllegalArgumentException> {
            PassengerGenerator.getPassenger(passengerName, 1, Building.firstFloor - 1)
        }
        assertThrows<IllegalArgumentException> {
            PassengerGenerator.getPassenger(passengerName, 1, Building.lastFloor + 1)
        }
    }

    @Test
    fun onePassengerTest() {
        val passenger = PassengerGenerator.getPassenger(passengerName, 3, 8)
        executorService.submit { PassengerEmulator.emulate(passenger) }.get()
        assertTrue { passenger.sourceFloorNum == passenger.targetFloorNum }
    }

    @Test
    fun onePassengerEdgeFloorsTest() {
        val passenger = PassengerGenerator.getPassenger(
            passengerName, Building.firstFloor, Building.lastFloor
        )
        executorService.submit { PassengerEmulator.emulate(passenger) }.get()
        assertTrue { passenger.sourceFloorNum == passenger.targetFloorNum }
    }

    @Test
    fun twoPassengersTest() {
        val passenger1 = PassengerGenerator.getPassenger(passengerName, 3,  8)
        val passenger2 = PassengerGenerator.getPassenger(passengerName2, 7, 2)
        val countDownLatch = CountDownLatch(2)
        executorService.submit {
            PassengerEmulator.emulate(passenger1)
            countDownLatch.countDown()
        }
        executorService.submit {
            PassengerEmulator.emulate(passenger2)
            countDownLatch.countDown()
        }
        countDownLatch.await()
        assertTrue { passenger1.sourceFloorNum == passenger1.targetFloorNum }
        assertTrue { passenger2.sourceFloorNum == passenger2.targetFloorNum }
    }

    @Test
    fun sameSourceFloorSameDirectionTest() {
        val sourceFloor = 18
        val passenger1 = PassengerGenerator.getPassenger(passengerName, sourceFloor, 1)
        val passenger2 = PassengerGenerator.getPassenger(passengerName2, sourceFloor,5)
        val countDownLatch = CountDownLatch(2)
        executorService.submit {
            PassengerEmulator.emulate(passenger1)
            countDownLatch.countDown()
        }
        executorService.submit {
            PassengerEmulator.emulate(passenger2)
            countDownLatch.countDown()
        }
        countDownLatch.await()
        assertTrue { passenger1.sourceFloorNum == passenger1.targetFloorNum }
        assertTrue { passenger2.sourceFloorNum == passenger2.targetFloorNum }
    }

    @Test
    fun sameSourceFloorDifferentDirectionTest() {
        val sourceFloor = 18
        val passenger1 = PassengerGenerator.getPassenger(passengerName, sourceFloor, 1)
        val passenger2 = PassengerGenerator.getPassenger(passengerName2, sourceFloor, 25)
        val countDownLatch = CountDownLatch(2)
        executorService.submit {
            PassengerEmulator.emulate(passenger1)
            countDownLatch.countDown()
        }
        executorService.submit {
            PassengerEmulator.emulate(passenger2)
            countDownLatch.countDown()
        }
        countDownLatch.await()
        assertTrue { passenger1.sourceFloorNum == passenger1.targetFloorNum }
        assertTrue { passenger2.sourceFloorNum == passenger2.targetFloorNum }
    }

    @Test
    fun sameTargetFloorTest() {
        val targetFloor = 5
        val passenger1 = PassengerGenerator.getPassenger(passengerName, 1, targetFloor)
        val passenger2 = PassengerGenerator.getPassenger(passengerName2, 10, targetFloor)
        val countDownLatch = CountDownLatch(2)
        executorService.submit {
            PassengerEmulator.emulate(passenger1)
            countDownLatch.countDown()
        }
        executorService.submit {
            PassengerEmulator.emulate(passenger2)
            countDownLatch.countDown()
        }
        countDownLatch.await()
        assertTrue { passenger1.sourceFloorNum == passenger1.targetFloorNum }
        assertTrue { passenger2.sourceFloorNum == passenger2.targetFloorNum }
    }


    @Test
    fun sameSourceFloorAndSameTargetFloorTest() {
        val sourceFloor = 10
        val targetFloor = 1
        val passenger1 = PassengerGenerator.getPassenger(passengerName, sourceFloor, targetFloor)
        val passenger2 = PassengerGenerator.getPassenger(passengerName2, sourceFloor, targetFloor)
        val countDownLatch = CountDownLatch(2)
        executorService.submit {
            PassengerEmulator.emulate(passenger1)
            countDownLatch.countDown()
        }
        executorService.submit {
            PassengerEmulator.emulate(passenger2)
            countDownLatch.countDown()
        }
        countDownLatch.await()
        assertTrue { passenger1.sourceFloorNum == passenger1.targetFloorNum }
        assertTrue { passenger2.sourceFloorNum == passenger2.targetFloorNum }
    }

    @Test
    fun manyPassengersTest() {
        val passengers = mutableListOf<Passenger>()
        repeat(100) {
            val passenger = PassengerGenerator.getPassenger(
                name = faker.name().firstName(),
                sourceFloor = Random.nextInt(1, 25),
                targetFloor = Random.nextInt(1, 25)
            )
            passengers.add(passenger)
        }
        val countDownLatch = CountDownLatch(passengers.size)
        for (passenger in passengers) {
            executorService.submit {
                PassengerEmulator.emulate(passenger)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        for (passenger in passengers) {
            assertTrue { passenger.sourceFloorNum == passenger.targetFloorNum }
        }

    }

}