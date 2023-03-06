package event

import java.util.concurrent.CopyOnWriteArrayList

object EventStorage {
    private val listeners = CopyOnWriteArrayList<Listener>()

    fun listen(listener: Listener): Boolean {
        return listeners.add(listener)
    }

    fun fire(floorNum: Int) {
        notify(floorNum)
    }

    private fun notify(floorNum: Int) {
        listeners.filter { it.floorNum == floorNum }.forEach { it.onEvent() }
        forget(floorNum)
    }

    private fun forget(floorNum: Int) {
        listeners.removeIf { it.floorNum == floorNum }
    }

}