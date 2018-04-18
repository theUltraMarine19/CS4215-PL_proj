import com.cra.figaro.language._
import com.cra.figaro.library.atomic.continuous.Uniform
import com.cra.figaro.library.compound.If
import com.cra.figaro.algorithm.{AbstractionScheme, Abstraction}
import com.cra.figaro.algorithm.factored._

object Abstract {

	def main(args : Array[String]) {
		val flip = Flip(0.5)		// Flip UnRV
		val uniform1 = Uniform(0.0, 1.0)	// Uniform RV 
		val uniform2 = Uniform(1.0, 2.0)	// Uniform RV
		val chain = If(flip, uniform1, uniform2) // conditional distribution based on flip
		val apply = Apply(chain, (d: Double) => d + 1.0)	// apply function to increase the value obtained by 1.0
		apply.addConstraint((d: Double) => d)	// add Constraint for value to be non-zero
		uniform1.addPragma(Abstraction(10))		// (since continuous distributions, pragma provides abstractions to 
		uniform2.addPragma(Abstraction(10))		// choose a set of abstract points from the given set of concrete points
		chain.addPragma(Abstraction(10))		// of given size)
		apply.addPragma(Abstraction(10))
		val ve = VariableElimination(flip)	// variable elimination to eliminate all other RVs other than flip
		ve.start()
		println(ve.probability(flip, true))	// probability that flip RV is true
		
	}
	
}