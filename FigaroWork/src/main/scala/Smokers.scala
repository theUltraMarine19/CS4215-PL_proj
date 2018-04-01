package com.cra.figaro.example

import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.language._
import com.cra.figaro.library.compound.^^

// A Markov logic example.

object Smokers {
  private class Person {
    val smokes = Flip(0.6)  // 60% prob that this person smokes
  }

  private val alice, bob, clara = new Person  // create three new persons
  private val friends = List((alice, bob), (bob, clara))  // create a network of friends (Markov network)
  clara.smokes.observe(true)    // One of the friends in the group smokes

  private def smokingInfluence(pair: (Boolean, Boolean)) =
    if (pair._1 == pair._2) 3.0; else 1.0     // similar to a constraint that two friends having similar smoking habits 3 times more likely than having different smoking habits 

  for { (p1, p2) <- friends } {
    ^^(p1.smokes, p2.smokes).setConstraint(smokingInfluence)  // create tuples of smoking habits of two friends and set this above constraint
  }

  def main(args: Array[String]) {
    val alg = MetropolisHastings(20000, ProposalScheme.default, alice.smokes)
    alg.start()
    println("Probability of Alice smoking: " + alg.probability(alice.smokes, true))
    alg.kill
  }
}