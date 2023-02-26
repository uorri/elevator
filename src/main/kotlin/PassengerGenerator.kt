object PassengerGenerator {
    fun getPassenger(name: String, sourceFloor: Int, targetFloor: Int): Passenger {
        if (name.isBlank() || name.isEmpty()) {
            throw IllegalArgumentException("Invalid name: $name")
        }
        if (sourceFloor < Building.firstFloor || sourceFloor > Building.lastFloor) {
            throw IllegalArgumentException("Invalid source floor: $sourceFloor")
        }
        if (targetFloor < Building.firstFloor || targetFloor > Building.lastFloor) {
            throw IllegalArgumentException("Invalid target floor: $sourceFloor")
        }
        return Passenger(name, sourceFloor, targetFloor)
    }

}
