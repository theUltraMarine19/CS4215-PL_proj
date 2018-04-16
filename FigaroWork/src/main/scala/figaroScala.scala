import com.cra.figaro.language._
import com.cra.figaro.library.compound.If
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.library.compound._

object figaroScala {

	def main(args:Array[String]) {
		val rainySeason = Flip(0.4)						  // rainy season RV, P(rain)=0.4
		val rain = If(rainySeason, Flip(0.9), Flip(0.4))  // rain RV, P(rain | RS = T)= 0.9, P(rain | RS = F)= 0.4
		val sprinkler = If(rainySeason, false, true)	  //sprinkler RV, P(spr | RS = T)= 0, P(spr | RS = F)= 1
		val wetGrass = CPD(rain, sprinkler,				  // specify conditional distribution of wet grass RV
			(false, false) -> Flip(0.05),				  // P(Wet Grass | rain = F, sprinkler = 0) = 0.05
			(false, true) -> Flip(0.7),					  // P(Wet Grass | rain = F, sprinkler = 1) = 0.7
			(true, false) -> Flip(0.8),					  // P(Wet Grass | rain = T, sprinkler = 0) = 0.8
			(true, true) -> Flip(0.95)					  // P(Wet Grass | rain = T, sprinkler = 1) = 0.95
			)
		val alg = Importance(100, wetGrass)				  // Importance sampling algorithm for 100 iterations
		alg.start()
		val probWetGrass = alg.probability(wetGrass, true)
		alg.kill
		println(probWetGrass)
	}
}