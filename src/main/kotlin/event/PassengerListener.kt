package event

data class PassengerListener(override val floorNum: Int, val f: () -> Unit) : Listener {
    override fun onEvent() = f()
}