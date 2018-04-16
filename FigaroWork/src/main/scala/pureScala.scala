import scala.util._

object pureScala {

	def buildModel(iters : Int): Double = { // Test truth values of random variables every iteration using random generator
		if (iters == 0) // Base case
			0.0
		else {
			val prev : Double = buildModel(iters-1)
			val rainySeason: Boolean = if (Random.nextDouble() < 0.4) true else false //rainy season RV, P(Rain)= 0.4
			val rain : Boolean = rainySeason match {	// rain given rainy season random variable
				case true => if (Random.nextDouble() < 0.9) true else false	 // P(rain | rainy season = True) = 0.9
				case false => if (Random.nextDouble() < 0.4) true else false // P(rain | rainy season = False) = 0.4
				}

			val sprinkler : Boolean = rainySeason match {	// sprinkler given rainy season random variable
				case true => false 	// P(sprinkler | rainy season = True) = 0
				case false => true  // P(sprinkler | rainy season = False) = 1
				}

			val wetGrass: Boolean = (rain, sprinkler) match {	// wet grass given rain, sprinkler random variable
				case (false, false) => if (Random.nextDouble() < 0.05) true else false 	// P(WG | rain= F, spr= F) = 0.05
				case (false, true)  => if (Random.nextDouble() < 0.7) true else false   // P(WG | rain= F, spr= T) = 0.7
				case (true, false)  => if (Random.nextDouble() < 0.8) true else false   // P(WG | rain= T, spr= F) = 0.8
				case (true, true)   => if (Random.nextDouble() < 0.95) true else false  // P(WG | rain= T, spr= T) = 0.95
				}    

		if (wetGrass) prev + 1.0 else prev	// count the number of cases where wet grass random variable yields true
		}
	}

	def main(args : Array[String]){
		val probWetGrass = buildModel(100)/100.0   // run 100 iterations of the above sampling algorithm
		println(probWetGrass)
	}
	
}