package event

data class PassengerListener(override val floorNum: Int, val doOnEvent: () -> Unit) : Listener {
    override fun onEvent() = doOnEvent()
}