module Lib
    ( Expr(..)
    ) where

data Op = Plus | Minus | Times | Div | Mod

data Expr a = Const a | Neg (Expr a) | BinOp Op (Expr a) (Expr a)
