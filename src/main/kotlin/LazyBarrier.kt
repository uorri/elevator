import java.util.concurrent.CyclicBarrier

class LazyBarrier {

    val barrier: CyclicBarrier by lazy {
        CyclicBarrier(1)
    }

}