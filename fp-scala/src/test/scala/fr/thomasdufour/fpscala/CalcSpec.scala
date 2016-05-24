package fr.thomasdufour.fpscala

import org.scalacheck.Arbitrary._
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.Matchers
import org.scalatest.WordSpec

class CalcSpec extends WordSpec with Matchers with GeneratorDrivenPropertyChecks with TypeCheckedTripleEquals {

  import calc.integral._

  "A Calculator" when {
    "evaluating a Const" should {
      "return its value" in {

        forAll { ( x : BigInt ) =>

          eval( Const( x ) ) should ===( x )

        }

      }
    }

    "evaluating an Op(Plus, ...)" should {
      "return the sum of its operands" in {

        forAll { ( x : BigInt, y : BigInt ) =>
          eval( Op( Plus, Const( x ), Const( y ) ) ) should ===( x + y )
        }

      }
    }
  }

}
