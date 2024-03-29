import com.cra.figaro.algorithm.factored._
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.language._
import com.cra.figaro.library.compound._
import com.cra.figaro.library.collection.Container
import com.cra.figaro.library.atomic.discrete._
import com.cra.figaro.util


 // A probabilistc relational model example with multi-valued attributes.
object MutableMovie {
  private val random = com.cra.figaro.util.random

  private class Actor {
    var movies: List[Movie] = List()

    // lazy implies mutable    
    lazy val skillful = Flip(0.1)

    // map is similar to List.map (fun) list in OCaml
    // single field f is converted to a seq of args using f:_*
    // _.quality <--> movies => movies.quality  
    lazy val qualities = Container(movies.map(_.quality):_*) 

    // count the no. of qualities = 'high
    lazy val numGoodMovies = qualities.count(_ == 'high)

    // If (numGoodMovies >= 2, Flip(0.8), Flip(0.1))
    lazy val famous = Chain(numGoodMovies, (n: Int) => if (n >= 2) Flip(0.8) else Flip(0.1))
  }

  private class Movie {
    var actors: List[Actor] = List()

    // create a container of the skills of all the actors
    lazy val skills = Container(actors.map(_.skillful):_*)

    // true if there are any actors whose skillful value is true
    lazy val actorsAllGood = skills.exists(b => b)

    lazy val probLow = Apply(actorsAllGood, (b: Boolean) => if (b) 0.2; else 0.5)

    lazy val probHigh = Apply(actorsAllGood, (b: Boolean) => if (b) 0.5; else 0.2)

    lazy val quality = Select(probLow -> 'low, Constant(0.3) -> 'medium, probHigh -> 'high)
  }

  private class Appearance(actor: Actor, movie: Movie) {
    actor.movies ::= movie    // prepend movie to actor.movies
    movie.actors ::= actor

    // Symbols are string constants
    def probAward(quality: Symbol, famous: Boolean) =
      (quality, famous) match {
        case ('low, false) => 0.001
        case ('low, true) => 0.01
        case ('medium, false) => 0.01
        case ('medium, true) => 0.05
        case ('high, false) => 0.05
        case ('high, true) => 0.2
      }
    lazy val award = SwitchingFlip(Apply(movie.quality, actor.famous, (q: Symbol, f: Boolean) => probAward(q, f)))
  }

  private val numActors = 3
  private val numMovies = 3
  private val numAppearances = 4
  private val actor1 = new Actor
  private val actor2 = new Actor
  private val actor3 = new Actor
  private val movie1 = new Movie
  private val movie2 = new Movie
  private val movie3 = new Movie
  private val appearance1 = new Appearance(actor1, movie1)
  private val appearance2 = new Appearance(actor2, movie2)
  private val appearance3 = new Appearance(actor3, movie2)
  private val appearance4 = new Appearance(actor3, movie3)
  private val otherActors = Array.fill(numActors - 3)(new Actor)
  private val otherMovies = Array.fill(numMovies - 3)(new Movie)
  private val actors = otherActors ++ Array(actor1, actor2, actor3) // ++ command is for append at the beginning of a list
  private val movies = otherMovies ++ Array(movie1, movie2, movie3)
  private val otherAppearances =
    Array.fill(numAppearances - 4)(new Appearance(actors(random.nextInt(numActors)), movies(random.nextInt(numMovies))))
  private val appearances = otherAppearances ++ Array(appearance1, appearance2, appearance3, appearance4)

  // Ensure that exactly one appearance gets an award.
  private def uniqueAwardCondition(awards: List[Boolean]) = awards.count((b: Boolean) => b) == 1
  private val allAwards: Element[List[Boolean]] = Inject(appearances.map(_.award): _*)
  allAwards.setCondition(uniqueAwardCondition)

  // A proposal either proposes to switch the awardee to another awardee or proposes the properties of a movie or
  // an actor.
  private def chooseScheme(): ProposalScheme = {
    DisjointScheme(
      (0.4, () => switchAwards()),
      (0.2, () => ProposalScheme(actors(random.nextInt(numActors)).skillful)),
      (0.2, () => ProposalScheme(movies(random.nextInt(numMovies)).quality)),
      (0.2, () => ProposalScheme(actors(random.nextInt(numActors)).famous)))
  }

  // It's possible that as a result of other attributes changing, an appearance becomes awarded or unawarded.
  // Therefore, we have to take this into account in the proposal scheme.
  
  private def switchAwards(): ProposalScheme = {
    val (awarded, unawarded) = appearances.partition(_.award.value)
    awarded.length match {
      case 1 =>
        val other = unawarded(random.nextInt(numAppearances - 1))
        ProposalScheme(awarded(0).award, other.award)
      case 0 =>
        ProposalScheme(appearances(random.nextInt(numAppearances)).award) // make something unawarded awarded
      case _ =>
        ProposalScheme(awarded(random.nextInt(awarded.length)).award)
    }
  }

  def main(args: Array[String]): Unit = {
    actor3.skillful.observe(true)
    movie2.quality.observe('high)

    // We first make sure the initial state satisfies the unique award condition, and then guide all
    // subsequent proposals to keep that condition.
    appearances.foreach(_.award.randomness = 0.0)
    appearances(random.nextInt(numAppearances)).award.randomness = 1.0
    appearances.foreach(appearance =>
      appearance.award.value = appearance.award.generateValue(appearance.award.randomness))
    allAwards.generate()

    val alg = Importance(1000, appearance1.award, appearance2.award, appearance3.award, appearance4.award)
    util.timed(alg.start(), "Inference")
    alg.stop()
    println(alg.probability(appearance1.award, true))
    println(alg.probability(appearance2.award, true))
    println(alg.probability(appearance3.award, true))
    println(alg.probability(appearance4.award, true))
    alg.kill()
  }
}