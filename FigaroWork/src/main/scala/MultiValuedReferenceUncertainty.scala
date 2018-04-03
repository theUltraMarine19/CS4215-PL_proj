package com.cra.figaro.example

import com.cra.figaro.algorithm.factored._
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.language._
import com.cra.figaro.library.compound._
import com.cra.figaro.util.MultiSet

// A simple model example with multi-valued reference uncertainty and aggregates.

object MultiValuedReferenceUncertainty {
  class Component extends ElementCollection {
    val f = Select(0.2 -> 2, 0.3 -> 3, 0.5 -> 5)("f", this)
  }

  val specialComponent1 = new Component
  val specialComponent2 = new Component

  val makeComponent = () => Select(0.1 -> specialComponent1, 0.2 -> specialComponent2, 0.7 -> new Component)

  class Container extends ElementCollection {
    val components = MakeList(Select(0.5 -> 1, 0.5 -> 2), makeComponent)("components", this) // will contain 1 element with 0.5 prob. and 2 elements with 0.5 prob.

    val sum = getAggregate((xs: MultiSet[Int]) => (0 /: xs)(_ + _))("components.f") // _ + _ <==> (fun x y -> x + y)
                                                                                    // function should be folded through xs, starting from 0. Similar to List.fold_left of Ocaml
  }

  def main(args: Array[String]): Unit = {
    val c = new Container
    val alg = Importance(100000, c.sum)
    alg.start()
    println(alg.distribution(c.sum).toList)
    alg.kill
  }
}