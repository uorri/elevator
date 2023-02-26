import kotlin.random.Random

object Elevator {
    private var position = Random.nextInt(1, 25)
    fun moveUp() {
        Thread.sleep(1)
        position++
    }

    fun moveDown() {
        Thread.sleep(1)
        position--
    }

}