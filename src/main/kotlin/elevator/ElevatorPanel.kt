package elevator

import model.Button
import building.ElevatorSystem

class ElevatorPanel(numberOfButtons: Int) {
    val buttons: Array<Button>
    init {
        var i = 0
        buttons = Array(numberOfButtons) {
            object : ElevatorButton(++i) {
                override fun click() {
                    ElevatorSystem.insideRequest(number)
                }
            }
        }
    }

}
