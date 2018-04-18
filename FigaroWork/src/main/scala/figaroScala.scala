import com.cra.figaro.language._
import com.cra.figaro.algorithm.factored._
import com.cra.figaro.library.compound.If
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.library.compound._
import com.cra.figaro.algorithm.learning._
import com.cra.figaro.library.atomic.continuous._
import com.cra.figaro.patterns.learning._

class Tests(val parameters: ParameterCollection) {
  val trials = for (f <- 0 until 73) yield {  // for loop from 1 to 73
    Flip(parameters.get("isWet"))   // declare Flip variable using "fairness" reference object
  }
}

object figaroScala {
	Universe.createNew();


	val data = Seq(	// sequence of observations for grass being wet or dry
		'F', 'F', 'F', 'T', 'F', 'F', 'T', 'F', 'T', 'F', 'F', 'T', 'F',
	    'T', 'F', 'F', 'T', 'F', 'T', 'T', 'F', 'T', 'F', 'F', 'T', 'F', 'F', 'F', 'T',
	    'T', 'F', 'F', 'T', 'F', 'T', 'F', 'T', 'T', 'F', 'T', 'F', 'F', 'F', 'F', 'F',
	    'F', 'F', 'F', 'F', 'T', 'F', 'T', 'F', 'F', 'T', 'F', 'F', 'F', 'F', 'F',
	    'F', 'T', 'F', 'F', 'F', 'T', 'F', 'F', 'F', 'F', 'F', 'F', 'F',
	    )

	def main(args:Array[String]) {
		val params = ModelParameters()	// parameters for modelling via prior distribution
		val isWet = Beta(2.0, 2.0)("isWet", params)	// beta distribution as prior for Flip
		val model = new Tests(params.priorParameters)	// initialize with the prior parameters
		data zip model.trials foreach {                   // set the isWet values from the observed trials
	      (datum: (Char, Flip)) => if (datum._1 == 'F') datum._2.observe(false) else datum._2.observe(true)
	    }
	    val numberOfEMIterations = 10
	    val numberOfBPIterations = 10
	    val learningAlgorithm = EMWithBP(10, 10, params) // EM algorithm with Belief Propagation for inference
	    learningAlgorithm.start
	    println("The probability of the grass being wet from data is : ")
	    // val wetness = params.posteriorParameters.get("isWet")
	    println(params.posteriorParameters.get("isWet")) 		// posterior parameters refers to the MAP values
		learningAlgorithm.stop
	    learningAlgorithm.kill
	    
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
		println("Given the CPD, probability of wet grass is : ")
		println(probWetGrass)
		alg.kill
		// val oddsOfWetGrass = wetness.toDouble/(1.toDouble - wetness)
		wetGrass.setConstraint((b:Boolean) => if (b) 3.0; else 7.0) // add constraint on the wet Grass RV
		val alg1 = VariableElimination(rain)	// variable elimiation for queries on rain
		alg1.start
		val probRain = alg1.probability(rain, true)
		alg1.kill
		println("Prob. of rain")
		println(probRain)
	}
}