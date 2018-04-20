import com.cra.figaro.language._
import com.cra.figaro.library.atomic.continuous._
import com.cra.figaro.util._
import scala.util._
import com.cra.figaro.algorithm.factored._

object Extension {
	class MaxValue (val numTries : Int, val upperBound : Int, name : Name[Int], collection : ElementCollection) 
	extends Element[Int](name, collection) with Atomic[Int]
	{
		def density(d: Int) = if (d >= 0 && d < upperBound) 1.0/upperBound; else 0.0
		// Randomness is now a list of integers
		type Randomness = List[Int]
		// generate a list of random numbers between 0, upperBound
		// whose length is specified by numTries
		def generateRandomness(): List[Int] = {
			println(List.tabulate(numTries) (i => Random.nextInt(upperBound)))
			List.tabulate(numTries) (i => Random.nextInt(upperBound))
		}
		// get the maximum element of that list
		def generateValue(r: List[Int]) = {
			println(r.max)
			r.max
		}
		// def args: List[Element[_]]
		// final def generate(): Unit = {
		//     // if (!setFlag) { // Make sure we do not generate this element if we have already set its value
		//       args.foreach(arg => if (arg.value == null) arg.generate()) // make sure arguments have a valid value
		//       randomness = generateRandomness()
		//       value = generateValue(randomness)
		//     // }
		// }
		override def toString = "Max(" + numTries + ", " + upperBound + ")"
	}

	object Max extends Creatable {
		def apply(numTries: Int, upperBound: Int)(implicit name: Name[Int], collection: ElementCollection) =
    		new MaxValue(numTries, upperBound, name, collection)


		type ResultType = Int

		def create(args: List[Element[_]]) = apply(args(0).asInstanceOf[Int], args(1).asInstanceOf[Int])
	}

	def main(args:Array[String]) {

		val univ = Universe.createNew()
		val x = Max(20, 100)
		val y = Uniform(0, 100);
		val ve = VariableElimination(x)	// variable elimination to eliminate all other RVs other than flip
		ve.start()
		println(ve.probability(x, (x: Int) => x < 90))	// probability that x RV is true
	}	
}

