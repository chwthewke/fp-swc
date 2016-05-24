import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform._
import com.typesafe.sbteclipse.core.EclipsePlugin._
import scalariform.formatter.preferences._
import scoverage.ScoverageSbtPlugin
import sbtbuildinfo.Plugin._

object FpScalaBuild extends Build {

  object Dependencies {

    val scalatest = "org.scalatest" %% "scalatest" % "3.0.0-RC1" % "test" withSources () withJavadoc ()

    val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.1" % "test" withSources () withJavadoc ()

  }

  override def settings = super.settings :+ ( EclipseKeys.skipParents in ThisBuild := false )

  lazy val fpScalaScalariformSettings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences := defaultPreferences
      .setPreference( AlignSingleLineCaseStatements, true )
      .setPreference( SpaceBeforeColon, true )
      .setPreference( SpaceInsideParentheses, true )    
  )

  lazy val sharedSettings =
    Seq(
      organization := "fr.thomasdufour",
      scalaVersion := "2.11.8")

  lazy val fpScalaSettings = 
    Defaults.coreDefaultSettings ++
    SbtBuildInfo.buildSettings("fr.thomasdufour.fpscala") ++
    SbtEclipse.buildSettings ++
    fpScalaScalariformSettings ++
    sharedSettings ++
    Seq(
      libraryDependencies ++= Seq(
          Dependencies.scalatest,
          Dependencies.scalacheck),
        scalacOptions ++= Seq( "-feature", "-deprecation" ),
        unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil,
        unmanagedSourceDirectories in Test := (scalaSource in Test).value :: Nil
    )

  lazy val fpScala = Project(
    id = "fp-scala",
    base = file( "." ),
    settings = fpScalaSettings ++
      Seq(
        name := "fp-scala",
        mainClass := Some("fr.thomasdufour.fpscala.Main"),
        initialCommands := """|import fr.thomasdufour.fpscala._
                              |import parser.Calc._""".stripMargin
      )
  )
}
