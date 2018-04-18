import com.cra.figaro.algorithm.factored._
import com.cra.figaro.language._
import com.cra.figaro.library.compound._
object Burglary {
	Universe.createNew()		// create a new universe of RVs
	private val burglary = Flip(0.01)	// RV burglary
	private val earthquake = Flip(0.0001)   	// RV earthquake
	private val alarm = CPD(burglary, earthquake,	// conditional distribution of RV alarm
		(false, false) -> Flip(0.001),				// given burglary and earthquake
		(false, true) -> Flip(0.1),
		(true, false) -> Flip(0.9),
		(true, true) -> Flip(0.99))
	private val johnCalls = CPD(alarm,	// RV denoting the event of John calling given alarm
		false -> Flip(0.01),
		true -> Flip(0.7))
	
	def main(args: Array[String]) {
		johnCalls.observe(true)			// observe that RV johnCalls = true
		val alg = VariableElimination(burglary, earthquake)	// variable elimination elimination everything except burglary, earthquake
		alg.start()
		println("Probability of burglary: " + alg.probability(burglary, true))
		alg.kill
	}
}