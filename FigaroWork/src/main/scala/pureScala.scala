import scala.util._

object pureScala {

	def buildModel(iters : Int): Double = {
		if (iters == 0) 
			0.0
		else {
			val prev : Double = buildModel(iters-1)
			val rainySeason: Boolean = if (Random.nextDouble() < 0.4) true else false
			val rain : Boolean = rainySeason match {
				case true => if (Random.nextDouble() < 0.9) true else false
				case false => if (Random.nextDouble() < 0.4) true else false
				}

			val sprinkler : Boolean = rainySeason match {
				case true => false
				case false => true
				}

			val wetGrass: Boolean = (rain, sprinkler) match {
				case (false, false) => if (Random.nextDouble() < 0.05) true else false
				case (false, true)  => if (Random.nextDouble() < 0.7) true else false
				case (true, false)  => if (Random.nextDouble() < 0.8) true else false
				case (true, true)   => if (Random.nextDouble() < 0.95) true else false
				}    

		if (wetGrass) prev + 1.0 else prev
		}
	}

	def main(args : Array[String]){
		val probWetGrass = buildModel(100)/100.0
		println(probWetGrass)
	}
	
}