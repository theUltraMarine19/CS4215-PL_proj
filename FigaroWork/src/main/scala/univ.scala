import com.cra.figaro.algorithm._
import com.cra.figaro.language._
import com.cra.figaro.library.compound.If

object univ {
	def main(args : Array[String]){
		val e1 = Flip(0.7)
		val e2 = If(e1, Select(0.2 -> 1, 0.8 -> 2), Select(0.4 -> 2, 0.6-> 3))
		val values = Values()
		println(values(e2))	
	}
		
}
