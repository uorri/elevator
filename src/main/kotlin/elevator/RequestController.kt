package elevator

import model.Request
import type.Direction
import java.util.*

class RequestController {
    private val requestsDown: NavigableSet<Int> = Collections.synchronizedNavigableSet(TreeSet())
    private val requestsUp: NavigableSet<Int> = Collections.synchronizedNavigableSet(TreeSet())

    fun add(request: Request): Boolean {
        val isNewRequest = if (request.direction == Direction.UP) {
            requestsUp.add(request.floor)
        } else {
            requestsDown.add(request.floor)
        }
        return isNewRequest
    }

    fun getPickUpTarget(position: Int, direction: Direction): Int? {
        return if (direction == Direction.DOWN) {
            requestsDown.floor(position)
        } else {
            requestsUp.ceiling(position)
        }
    }

    fun remove(position: Int) {
        requestsUp.remove(position)
        requestsDown.remove(position)
    }

    fun getNextTarget(position: Int, direction: Direction): Int? {
        return if (direction == Direction.UP) {
            requestsUp.ceiling(position) ?: requestsUp.floor(position)
        } else {
            requestsDown.floor(position) ?: requestsDown.ceiling(position)
        }
    }

    fun noRequests() = requestsUp.isEmpty() && requestsDown.isEmpty()
}