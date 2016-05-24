package fr.thomasdufour.fpscala

object Main {
  def main( args : Array[String] ) = {
    if ( args.isEmpty )
      println( "Usage: calc expression" )
    else
      parser.Calc.expr.parse( args( 0 ) ).right.map( calc.integral.eval[BigInt] _ ) match {
        case Right( result ) => println( result )
        case Left( error )   => println( error )
      }
  }
}
