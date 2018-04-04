import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.language._
import com.cra.figaro.library.compound.^^


// A Markov logic example.
object AnnealingSmokers {
  private class Person {
    val smokes = Flip(0.6)
  }

  private val alice, bob, clara = new Person
  private val friends = List((alice, bob), (bob, clara))
  clara.smokes.observe(true)

  private def smokingInfluence(pair: (Boolean, Boolean)) =
    if (pair._1 == pair._2) 3.0; else 1.0

  for { (p1, p2) <- friends } {
    ^^(p1.smokes, p2.smokes).setConstraint(smokingInfluence)
  }

  def main(args: Array[String]) {
    val alg = MetropolisHastingsAnnealer(ProposalScheme.default, Schedule.default(3.0))
    alg.start()
    Thread.sleep(1000)
    alg.stop()
    println("Most likely state of Alice: " + alg.mostLikelyValue(alice.smokes))
    println("Most likely state of Bob: " + alg.mostLikelyValue(bob.smokes))
    alg.kill
  }
}