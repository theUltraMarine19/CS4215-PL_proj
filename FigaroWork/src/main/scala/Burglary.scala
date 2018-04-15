import com.cra.figaro.algorithm.factored._
import com.cra.figaro.language._
import com.cra.figaro.library.compound._
object Burglary {
	Universe.createNew()
	private val burglary = Flip(0.01)
	private val earthquake = Flip(0.0001)
	private val alarm = CPD(burglary, earthquake,
		(false, false) -> Flip(0.001),
		(false, true) -> Flip(0.1),
		(true, false) -> Flip(0.9),
		(true, true) -> Flip(0.99))
	private val johnCalls = CPD(alarm,
		false -> Flip(0.01),
		true -> Flip(0.7))
	
	def main(args: Array[String]) {
		johnCalls.observe(true)
		val alg = VariableElimination(burglary, earthquake)
		alg.start()
		println("Probability of burglary: " + alg.probability(burglary, true))
		alg.kill
	}
}