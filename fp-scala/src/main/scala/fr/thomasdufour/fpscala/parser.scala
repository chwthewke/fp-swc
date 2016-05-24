package fr.thomasdufour.fpscala

object parser {

  object Calc extends Parsers {

    import calc.integral._

    def digit : Parser[Int] =
      char.filter( _.isDigit, "Expected digit" ).map( _.toInt - '0'.toInt )

    def number : Parser[BigInt] =
      digit.many1.map( _.foldLeft( 0 )( _ * 10 + _ ) )

    def operator( c : Char, op : BinOp ) : Parser[BinOp] =
      char.filter( _ == c, s"Expected $c" ).map( _ => op )

    val opPlus = operator( '+', Plus )

    val opMinus = operator( '-', Minus )

    val opTimes = operator( '*', Times )

    val opDivide = operator( '/', Divide )

    val opModulo = operator( '%', Modulo )

    def assocLeft[A]( binOp : Parser[BinOp], subExpr : Parser[Expr[A]] ) : Parser[Expr[A]] = {
      def combine : ( Expr[A], List[( BinOp, Expr[A] )] ) => Expr[A] = {
        case ( expr, Nil )                    => expr
        case ( expr, ( op1, expr1 ) :: tail ) => combine( Op( op1, expr, expr1 ), tail )
      }

      val rightOps : Parser[List[( BinOp, Expr[A] )]] =
        binOp.map2( subExpr )( ( _, _ ) ).many

      subExpr.map2( rightOps )( combine )
    }

    def expr : Parser[Expr[BigInt]] =
      assocLeft( opPlus.orElse( opMinus ), term )

    def term : Parser[Expr[BigInt]] =
      assocLeft( opTimes.orElse( opDivide ).orElse( opModulo ), factor )

    def factor : Parser[Expr[BigInt]] =
      signed( number.map( Const( _ ) ) ).orElse( parExpr )

    def parExpr : Parser[Expr[BigInt]] =
      signed(
        char.filter( _ == '(', "Expected (" )
          .map2( expr )( ( _, e ) => e )
          .map2( char.filter( _ == ')', "Expected )" ) )( ( e, _ ) => e ) )

    def unaryPlus : Parser[Expr[BigInt] => Expr[BigInt]] =
      opPlus.map( _ => identity _ )

    def unaryMinus : Parser[Expr[BigInt] => Expr[BigInt]] =
      opMinus.map( _ => Neg( _ ) )

    def unarySign : Parser[Expr[BigInt] => Expr[BigInt]] =
      unaryPlus.orElse( unaryMinus )

    def signed( subExpr : Parser[Expr[BigInt]] ) : Parser[Expr[BigInt]] =
      unarySign.map2( signed( subExpr ) )( _.apply( _ ) ).orElse( subExpr )

  }

  trait Parsers {
    def char : Parser[Char] = Parser {
      case "" => Left( "Empty string" )
      case s  => Right( ( s.substring( 1 ), s.charAt( 0 ) ) )
    }

    def succeed[A]( a : A ) : Parser[A] = Parser { s => Right( ( s, a ) ) }

    def fail[A]( err : String ) : Parser[A] = Parser { _ => Left( err ) }
  }

  object Parsers extends Parsers

  case class Parser[+A]( runParser : String => Either[String, ( String, A )] ) {

    import Parsers._

    def flatMap[B]( f : A => Parser[B] ) : Parser[B] = Parser {
      s =>
        runParser( s ) match {
          case Left( err )               => Left( err )
          case Right( ( remainder, a ) ) => f( a ).runParser( remainder )
        }
    }

    def map[B]( f : A => B ) : Parser[B] = flatMap( a => succeed( f( a ) ) )

    def map2[B, C]( second : => Parser[B] )( f : ( A, B ) => C ) : Parser[C] =
      flatMap( a => second.map( b => f( a, b ) ) )

    def many : Parser[List[A]] = Parser {
      s =>
        runParser( s ) match {
          case Left( err )               => Right( ( s, Nil ) )
          case Right( ( remainder, a ) ) => many.map( a :: _ ).runParser( remainder )
        }
    }

    def many1 : Parser[List[A]] = map2( many )( _ :: _ )

    def filter( f : A => Boolean, err : => String = "filter" ) : Parser[A] =
      flatMap { a =>
        if ( f( a ) ) succeed( a ) else fail( err )
      }

    def orElse[B >: A]( p : Parser[B] ) : Parser[B] = Parser {
      s =>
        runParser( s ) match {
          case Left( _ ) => p.runParser( s )
          case result    => result
        }
    }

    def total : Parser[A] = Parser {
      s =>
        runParser( s ) match {
          case ok @ Right( ( "", _ ) ) => ok
          case Right( ( r, _ ) )       => Left( s"Leftover input $r" )
          case ko                      => ko
        }
    }

    def parse( input : String ) : Either[String, A] =
      total.runParser( input.replaceAll( "\\s", "" ) ).right.map( _._2 )
  }
}
