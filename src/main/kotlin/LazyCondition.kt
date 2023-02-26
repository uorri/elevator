import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock

class LazyCondition(private val lock: Lock) {

    val condition: Condition by lazy {
        lock.newCondition()
    }

}