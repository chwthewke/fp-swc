package fr.thomasdufour.fpscala

object calc {
  sealed trait Expr

  case class Const( a : Int ) extends Expr
  case class Neg( e : Expr ) extends Expr
  case class Add( left : Expr, right : Expr ) extends Expr
  case class Sub( left : Expr, right : Expr ) extends Expr
  case class Mul( left : Expr, right : Expr ) extends Expr
  case class Div( left : Expr, right : Expr ) extends Expr
  case class Mod( left : Expr, right : Expr ) extends Expr
}
