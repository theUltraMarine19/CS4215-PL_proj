import com.cra.figaro.algorithm.decision._
import com.cra.figaro.algorithm.factored._
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.language._
import com.cra.figaro.library.compound._
import com.cra.figaro.library.decision._

/**
 * A Single Decision Example, based on the Entrepreneur example from Koller and Friedman, 2009.
 */
object Entrepreneur {
  Universe.createNew()

  val market = Select(0.5 -> 0, 0.3 -> 1, 0.2 -> 2)   // normal RV

  val survey = CPD(market, 0 -> Select(0.6 -> 0, 0.3 -> 1, 0.1 -> 2), // RV with market as parent
    1 -> Select(0.3 -> 0, 0.4 -> 1, 0.3 -> 2),
    2 -> Select(0.1 -> 0, 0.4 -> 1, 0.5 -> 2))

  val found = Decision(survey, List(true, false))

  def valueFcn(f: Boolean, m: Int): Double = {    // value is calculated below using valueFcn
    if (f) {
      m match {
        case 0 => -7.0
        case 1 => 5.0
        case 2 => 20.0
      }
    } else {
      0.0
    }
  }

  val value = Apply(found, market, valueFcn)  // utility variable computes a Double, conditioned upon the decision of found, and curr value of market element 

  def main(args: Array[String]) {

    val alg = DecisionVariableElimination(List(value), found) // use VariableElimination to compute optimal policy for found decision, set optimal policy in found element

    alg.start()

    alg.setPolicy(found)

    println("Optimal Decisions: ")
    println("0 -> " + found.getPolicy(0) + ", (" + alg.getUtility(0, false).norm + " vs " + alg.getUtility(0, true).norm + ")")
    println("1 -> " + found.getPolicy(1) + ", (" + alg.getUtility(1, false).norm + " vs " + alg.getUtility(1, true).norm + ")")
    println("2 -> " + found.getPolicy(2) + ", (" + alg.getUtility(2, false).norm + " vs " + alg.getUtility(2, true).norm + ")")

    alg.kill
  }
}