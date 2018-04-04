import com.cra.figaro.language._
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.library.compound.If

object testImp {
	def main(args : Array[String]){
		val e1 = Select(0.25 -> 0.3, 0.25 -> 0.5, 0.25 -> 0.7, 0.25 -> 0.9)
		val e2 = Flip(e1)
		val e3 = If(e2, Select(0.3 -> 1, 0.7 -> 2), Constant(2))
		e3.setCondition((i: Int) => i == 2)
		val imp = Importance(10000, e2)
		imp.start()
		println(imp.probability(e2, true))
		imp.kill()		
	}
}
