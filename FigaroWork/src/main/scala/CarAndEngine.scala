import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.algorithm.factored._
import com.cra.figaro.language._
import com.cra.figaro.library.compound._
import com.cra.figaro.library.atomic.discrete.Uniform
import com.cra.figaro.library.atomic.continuous.Normal

 // A probabilistic relational model example with reference uncertainty.
 // Element Collection is the current universe
object CarAndEngine {
  abstract class Engine extends ElementCollection { // Engine is an instance of ElementCollection
    val power: Element[Symbol]
  }

  private class V8 extends Engine {
    val power: Element[Symbol] = Select(0.8 -> 'high, 0.2 -> 'medium)("power", this) // name "power" as the instance of this power being created
  }

  private class V6 extends Engine {
    val power: Element[Symbol] = Select(0.2 -> 'high, 0.5 -> 'medium, 0.3 -> 'low)("power", this)
  }

  private object MySuperEngine extends V8 {
    override val power: Element[Symbol] = Constant('high)("power", this)
  }

  class Car extends ElementCollection {
    val engine = Uniform[Engine](new V8, new V6, MySuperEngine)("engine", this)

    val speed = CPD(
      get[Symbol]("engine.power"),
      'high -> Constant(90.0),
      'medium -> Constant(80.0),
      'low -> Constant(70.0))
  }

  def main(args: Array[String]) {
    val car = new Car
    val alg = VariableElimination(car.speed)
    alg.start()
    alg.stop()
    println(alg.expectation(car.speed)(d => d))
    alg.kill()
  }
}