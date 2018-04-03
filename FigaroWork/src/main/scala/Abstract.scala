import com.cra.figaro.language._
import com.cra.figaro.library.atomic.continuous.Uniform
import com.cra.figaro.library.compound.If
import com.cra.figaro.algorithm.{AbstractionScheme, Abstraction}
import com.cra.figaro.algorithm.factored._

object Abstract {

	def main(args : Array[String]) {
		val flip = Flip(0.5)
		val uniform1 = Uniform(0.0, 1.0)
		val uniform2 = Uniform(1.0, 2.0)
		val chain = If(flip, uniform1, uniform2)
		val apply = Apply(chain, (d: Double) => d + 1.0)
		// apply.addConstraint((d: Double) => d)
		uniform1.addPragma(Abstraction(10))
		uniform2.addPragma(Abstraction(10))
		chain.addPragma(Abstraction(10))
		apply.addPragma(Abstraction(10))
		val ve = VariableElimination(flip)
		ve.start()
		println(ve.probability(flip, true))	

	}
	
}