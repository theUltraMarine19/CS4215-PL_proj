import com.cra.figaro.language._
import com.cra.figaro.algorithm.factored._
import com.cra.figaro.algorithm.factored.beliefpropagation._
import com.cra.figaro.library.compound.If

object testBP{
	def main(args:Array[String]){
		val e1 = Select(0.25 -> 0.3, 0.25 -> 0.5, 0.25 -> 0.7, 0.25 -> 0.9)
		val e2 = Flip(e1)
		val e3 = If(e2, Select(0.3 -> 1, 0.7 -> 2), Constant(2))
		e3.setCondition((i: Int) => i == 2)
		val bp = BeliefPropagation(100, e2)
		bp.start()
		println(bp.probability(e2, true))
		bp.kill	
	}
}