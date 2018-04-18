import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.language._
import com.cra.figaro.library.atomic._
import com.cra.figaro.library.compound._
import scala.util._


object Battle {
	class Battalion {
		val mission = Select(0.3 -> 'attack, 0.7-> 'defend)
		val underAttack = Flip(0.2)
		var batteries: List[Battery] = Nil // mutable
		// lazy val battStatus = batteries.map{ case b => b.statusOk }
		// def chooseNextMission(status: List[Boolean]) =
		// 	if (status contains false) 'attack; else mission
		// lazy val nextMission = Chain(battStatus, chooseNextMission)
	} // 
	class Battery(inBattalion: Battalion) { // subclass
		inBattalion.batteries = this :: inBattalion.batteries
		val hit = If(inBattalion.underAttack, Flip(0.6), Flip(0.01))
	val statusOk = If(hit, Flip(0.2), Flip(0.9))
	}
	def main(args:Array[String]) {
		val battalion1 = new Battalion
		val battery1 = new Battery(battalion1)
		val battery2 = new Battery(battalion1)
		// battalion1.nextMission	
}

}