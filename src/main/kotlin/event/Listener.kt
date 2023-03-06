package event

import java.util.EventListener

interface Listener : EventListener {
    val floorNum: Int
    fun onEvent()
}