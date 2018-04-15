import com.cra.figaro.language._
import com.cra.figaro.library.compound.If
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.library.compound._

object figaroScala {

	def main(args:Array[String]) {
		val rainySeason = Flip(0.4)
		val rain = If(rainySeason, Flip(0.9), Flip(0.4))
		val sprinkler = If(rainySeason, Constant(0), Constant(1))
		val wetGrass = CPD(rain, sprinkler,
			(false, 0) -> Flip(0.05),
			(false, 1) -> Flip(0.7),
			(true, 0) -> Flip(0.8),
			(true, 1) -> Flip(0.95)
			)
		val alg = Importance(100, wetGrass)
		alg.start()
		val probWetGrass = alg.probability(wetGrass, true)
		alg.kill
		println(probWetGrass)
	}
}