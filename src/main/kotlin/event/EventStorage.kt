package event

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object EventStorage {
    private val listeners = CopyOnWriteArrayList<Listener>()
    private val lock = ReentrantLock()

    fun listen(listener: Listener) = lock.withLock { listeners.add(listener) }

    fun fire(floorNum: Int) = lock.withLock { notify(floorNum) }

    private fun notify(floorNum: Int) {
        listeners.filter { it.floorNum == floorNum }.forEach { it.onEvent() }
        forget(floorNum)
    }

    private fun forget(floorNum: Int) = listeners.removeIf { it.floorNum == floorNum }

}