package fr.thomasdufour.fpscala

import org.scalacheck.Arbitrary._
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.Matchers
import org.scalatest.WordSpec

class ParserSpec extends WordSpec with Matchers with GeneratorDrivenPropertyChecks with TypeCheckedTripleEquals {

  import parser.Calc._
  import calc.integral._

  "An expression parser" when {
    "parsing a single-digit number" should {
      "return its numeric value" in {

        expr.parse( "1" ) should ===( Right( Const( BigInt( 1 ) ) ) )

      }
    }

    "parsing a complex expression" should {
      "return its syntax tree" in {

        expr.parse( "1 + (3 + 7) * 2" ) should ===( Right(
          Op( Plus,
            Const( BigInt( 1 ) ),
            Op( Times,
              Op( Plus,
                Const( BigInt( 3 ) ),
                Const( BigInt( 7 ) ) ),
              Const( BigInt( 2 ) ) ) )
        ) )

      }
    }
  }

}
