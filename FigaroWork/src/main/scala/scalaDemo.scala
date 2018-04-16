import scala.util._

object scalaDemo{
	def main(args:Array[String]) {
		val f = (x : Int) => x + 1 // function as an object
		val ans = f.apply(2)	   // apply function on an object
		println(ans)
	}
}