import java.util.concurrent.locks.Lock

class Notifier(private val lock: Lock) {

    val conditions = Array(25) { LazyCondition(lock) }

}