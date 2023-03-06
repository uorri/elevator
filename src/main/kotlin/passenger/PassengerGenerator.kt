package passenger

import building.Building
import com.github.javafaker.Faker

object PassengerGenerator {
    private val faker = Faker()
    fun create(sourceFloor: Int, targetFloor: Int): Passenger {
        if (sourceFloor < Building.firstFloor || sourceFloor > Building.lastFloor) {
            throw IllegalArgumentException("Invalid source floor: $sourceFloor")
        }
        if (targetFloor < Building.firstFloor || targetFloor > Building.lastFloor) {
            throw IllegalArgumentException("Invalid target floor: $sourceFloor")
        }
        return Passenger(faker.name().firstName(), sourceFloor, targetFloor)
    }

}
