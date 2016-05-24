package fr.thomasdufour.fpscala

object calc {

  object int {

    sealed trait Expr

    case class Const( a : Int ) extends Expr
    case class Neg( e : Expr ) extends Expr
    case class Add( left : Expr, right : Expr ) extends Expr
    case class Sub( left : Expr, right : Expr ) extends Expr
    case class Mul( left : Expr, right : Expr ) extends Expr
    case class Div( left : Expr, right : Expr ) extends Expr
    case class Mod( left : Expr, right : Expr ) extends Expr

    def eval( e : Expr ) : Int = e match {
      case Const( x )  => x
      case Neg( x )    => -eval( x )
      case Add( l, r ) => eval( l ) + eval( r )
      case Sub( l, r ) => eval( l ) - eval( r )
      case Mul( l, r ) => eval( l ) * eval( r )
      case Div( l, r ) => eval( l ) / eval( r )
      case Mod( l, r ) => eval( l ) % eval( r )
    }
  }

  object double {

    sealed trait Expr

    case class Const( a : Double ) extends Expr
    case class Neg( e : Expr ) extends Expr
    case class Add( left : Expr, right : Expr ) extends Expr
    case class Sub( left : Expr, right : Expr ) extends Expr
    case class Mul( left : Expr, right : Expr ) extends Expr
    case class Div( left : Expr, right : Expr ) extends Expr

    def eval( e : Expr ) : Double = e match {
      case Const( x )  => x
      case Neg( x )    => -eval( x )
      case Add( l, r ) => eval( l ) + eval( r )
      case Sub( l, r ) => eval( l ) - eval( r )
      case Mul( l, r ) => eval( l ) * eval( r )
      case Div( l, r ) => eval( l ) % eval( r )
    }
  }

  class numeric[A]( implicit N : Integral[A] ) {

    import N._

    sealed trait Expr

    case class Const( a : A ) extends Expr
    case class Neg( e : Expr ) extends Expr
    case class Add( left : Expr, right : Expr ) extends Expr
    case class Sub( left : Expr, right : Expr ) extends Expr
    case class Mul( left : Expr, right : Expr ) extends Expr
    case class Div( left : Expr, right : Expr ) extends Expr
    case class Mod( left : Expr, right : Expr ) extends Expr

    def eval( e : Expr ) : A = e match {
      case Const( x )  => x
      case Neg( x )    => -eval( x )
      case Add( l, r ) => eval( l ) + eval( r )
      case Sub( l, r ) => eval( l ) - eval( r )
      case Mul( l, r ) => eval( l ) * eval( r )
      case Div( l, r ) => eval( l ) / eval( r )
      case Mod( l, r ) => eval( l ) % eval( r )
    }
  }

  val int2 = new numeric[Int]

  val bigint = new numeric[BigInt]

  val double2 = new numeric[Double]()( Numeric.DoubleAsIfIntegral )

  object adhoc {

    sealed trait Expr[A]

    case class Const[A]( a : A ) extends Expr[A]
    case class Neg[A]( e : Expr[A] )( implicit val N : Numeric[A] ) extends Expr[A]
    case class Add[A]( left : Expr[A], right : Expr[A] )( implicit val N : Numeric[A] ) extends Expr[A]
    case class Sub[A]( left : Expr[A], right : Expr[A] )( implicit val N : Numeric[A] ) extends Expr[A]
    case class Mul[A]( left : Expr[A], right : Expr[A] )( implicit val N : Numeric[A] ) extends Expr[A]
    case class Div[A]( left : Expr[A], right : Expr[A] )( implicit val F : Fractional[A] ) extends Expr[A]
    case class Idiv[A : Integral]( left : Expr[A], right : Expr[A] )( implicit val I : Integral[A] ) extends Expr[A]
    case class Mod[A : Integral]( left : Expr[A], right : Expr[A] )( implicit val I : Integral[A] ) extends Expr[A]

    def eval[A]( expr : Expr[A] ) : A = expr match {
      case Const( x )        => x
      case ex @ Neg( e )     => ex.N.negate( eval( e ) )
      case ex @ Add( e, f )  => ex.N.plus( eval( e ), eval( f ) )
      case ex @ Sub( e, f )  => ex.N.minus( eval( e ), eval( f ) )
      case ex @ Mul( e, f )  => ex.N.times( eval( e ), eval( f ) )
      case ex @ Div( e, f )  => ex.F.div( eval( e ), eval( f ) )
      case ex @ Idiv( e, f ) => ex.I.quot( eval( e ), eval( f ) )
      case ex @ Mod( e, f )  => ex.I.rem( eval( e ), eval( f ) )
    }

  }

  object integral {
    sealed trait BinOp
    case object Plus extends BinOp
    case object Minus extends BinOp
    case object Times extends BinOp
    case object Divide extends BinOp
    case object Modulo extends BinOp

    sealed trait Expr[A]

    case class Const[A]( a : A ) extends Expr[A]
    case class Neg[A]( e : Expr[A] ) extends Expr[A]
    case class Op[A]( o : BinOp, left : Expr[A], right : Expr[A] ) extends Expr[A]

    def eval[A]( e : Expr[A] )( implicit I : Integral[A] ) : A = {
      import I._

      e match {
        case Const( x )         => x
        case Neg( x )           => -eval( x )
        case Op( Plus, l, r )   => eval( l ) + eval( r )
        case Op( Minus, l, r )  => eval( l ) - eval( r )
        case Op( Times, l, r )  => eval( l ) * eval( r )
        case Op( Divide, l, r ) => eval( l ) / eval( r )
        case Op( Modulo, l, r ) => eval( l ) % eval( r )
      }
    }
  }

}
