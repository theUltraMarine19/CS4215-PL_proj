import com.cra.figaro.language._
import com.cra.figaro.algorithm.factored._

object ExtensionApply {
	class Eq[T](name: Name[Boolean], arg1: Element[T], arg2: Element[T], collection: ElementCollection) 
	extends Apply2(name, arg1, arg2, (t1: T, t2: T) => t1 == t2, collection) 
	{
		override def toString = arg1.toString + " === " + arg2.toString
	}
	object Eq{
		def apply[T1, T2, U](arg1: Element[T1], arg2: Element[T2], fn: (T1, T2) => U)(implicit name: Name[U], collection: ElementCollection) =
			new Apply2(name, arg1, arg2, fn, collection)
	}

	def main(args:Array[String]) {
		val x = Flip(0.5)
		val y = Flip(0.4)
		val z = x === y
		val ve = VariableElimination(z)	// variable elimination to eliminate all other RVs other than flip
		ve.start()
		println(ve.probability(z, true))	// probability that z RV is true

	}
}