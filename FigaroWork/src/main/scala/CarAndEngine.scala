import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.algorithm.factored._
import com.cra.figaro.language._
import com.cra.figaro.library.compound._
import com.cra.figaro.library.atomic.discrete.Uniform
import com.cra.figaro.library.atomic.continuous.Normal
import com.cra.figaro.algorithm.factored.beliefpropagation._

 // A probabilistic relational model example with reference uncertainty.
 // Element Collection is the current universe
object CarAndEngine {
  abstract class Engine extends ElementCollection { // Engine is an instance of ElementCollection
    val power: Element[Symbol]
  }
  // the three subclasses of engine
  // name "power" as the instance of this power being created
  private class V8 extends Engine {		
    val power: Element[Symbol] = Select(0.8 -> 'high, 0.2 -> 'medium)("power", this) 
  }                                                                                  
  // "power" will now be used to reference
  private class V6 extends Engine {
    val power: Element[Symbol] = Select(0.2 -> 'high, 0.5 -> 'medium, 0.3 -> 'low)("power", this)
  }

  private object CustomEngine extends V8 {
    override val power: Element[Symbol] = Constant('high)("power", this)
  }
  // objects of each engine type
  private val nissanV6 = new V6
  private val nissanV8 = new V8
  private val nissanCustom = CustomEngine
  private val allEngines = List((nissanV8, nissanV6), (nissanV8, nissanCustom)) // engines that share manufacturer bias

  private def manfBias(pair: (Symbol, Symbol)) =
    if (pair._1 == pair._2) 2.0; else 1.0	// engines of the same power are 2 times more likely than different

  for { (e1, e2) <- allEngines } {	// create tuples of powers of engines and set this above constraint
    ^^(e1.power, e2.power).setConstraint(manfBias)  
  }

  // given that, you do not observe any low pwer nissan V8 engine
  nissanV8.power.setCondition((s : Symbol) => s == 'low | s == 'high)

  // declare a new class car
  class Car extends ElementCollection {
    val engine = Uniform[Engine](new V8, new V6, CustomEngine)("engine", this) // V6, V8, custom engine are equally probable

    val speed = CPD(		// RV speed given, the power of the engine obtained via reference
      get[Symbol]("engine.power"),
      'high -> Constant(90.0),
      'medium -> Constant(80.0),
      'low -> Constant(70.0))
  }

  def main(args: Array[String]) {
  	val alg = MetropolisHastings(20000, ProposalScheme.default, nissanV6.power)	// MetropolisHastings to gauge power of V6 engine
    // val alg = BeliefPropagation(20000, nissanV6.power)
    alg.start()
    println("Probability of high grade V6: " + alg.probability(nissanV6.power, (s : Symbol) => s == 'high))
    alg.stop()
    alg.kill
    val car = new Car
    val alg1 = VariableElimination(car.speed) 
    alg1.start()
    println("Expected speed of an engine : " + alg1.expectation(car.speed)(d => d)) // expected value of the speed for any engine
    alg1.kill()
  }
}