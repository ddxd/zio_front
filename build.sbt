
val FS2Version        = "1.0.4"
val Http4sVersion     = "0.21.0-SNAPSHOT"
val CirceVersion      = "0.12.0-M2"
val CirceFS2Version   = "0.11.0"
val JawnFS2Version    = "0.14.2"
val LogbackVersion    = "1.2.3"
val ScalaLogVersion   = "3.9.2"
val PureConfigVersion = "0.11.0"
val ZioVersion        = "1.0-RC5"
val ScalaTestVersion  = "3.0.5"
val DoobieVersion     = "0.7.0"
val H2Version         = "1.4.199"
val FlywayVersion     = "5.2.4"

val KindProjVersion = "0.9.10"
val BetterMonadicForVersion = "0.3.0"
resolvers += Resolver.sonatypeRepo("snapshots")

lazy val root = (project in file("."))
  .settings(
    organization := "CloverGroup",
    name := "front",
    version := "0.1.0",
    scalaVersion := "2.12.8",
    maxErrors := 5,
    libraryDependencies ++= Seq(
      "co.fs2"                      %% "fs2-core"                   % FS2Version,
      "org.http4s"                  %% "http4s-blaze-server"        % Http4sVersion,
      "org.http4s"                  %% "http4s-blaze-client"        % Http4sVersion,
      "org.http4s"                  %% "http4s-circe"               % Http4sVersion,
      "org.http4s"                  %% "http4s-dsl"                 % Http4sVersion,

      "io.circe"                    %% "circe-generic"              % CirceVersion,
      "io.circe"                    %% "circe-literal"              % CirceVersion,
      //"org.http4s"                  %% "jawn-fs2"                   % JawnFS2Version,
      //"org.typelevel"               %% "jawn-ast"                   % JawnFS2Version,


      "org.tpolecat"                %% "doobie-core"                % DoobieVersion,
      "org.tpolecat"                %% "doobie-h2"                  % DoobieVersion,
      "org.tpolecat"                %% "doobie-hikari"              % DoobieVersion,

      "com.h2database"              %  "h2"                         % H2Version,
      "org.flywaydb"                %  "flyway-core"                % FlywayVersion,

      "ch.qos.logback"              % "logback-classic"             % LogbackVersion, 
      "com.typesafe.scala-logging"  %% "scala-logging"              % ScalaLogVersion,

      "org.scalactic"               %% "scalactic"                  % ScalaTestVersion,
      "org.scalatest"               %% "scalatest"                  % ScalaTestVersion,

      "com.github.pureconfig"       %% "pureconfig"                 % PureConfigVersion,
      "com.github.pureconfig"       %% "pureconfig-cats-effect"     % PureConfigVersion, 

      "org.scalaz"                  %% "scalaz-zio"                 % ZioVersion,
      "org.scalaz"                  %% "scalaz-zio-interop-cats"    % ZioVersion,

      //compilerPlugin("org.spire-math" %% "kind-projector"           % KindProjVersion),
      //compilerPlugin("com.olegpy"     %% "better-monadic-for"       % BetterMonadicForVersion)
    )
  )

 scalacOptions := Seq(
      "-feature",
      "-deprecation",
      "-explaintypes",
      "-unchecked",
      "-Xfuture",
      "-encoding", "UTF-8",
      "-language:higherKinds",
      "-language:existentials",
      "-Ypartial-unification",
      //"-Xfatal-warnings",
      "-Xlint:-infer-any,_",
      "-Ywarn-value-discard",
      "-Ywarn-numeric-widen",
      "-Ywarn-extra-implicit",
      //"-Ywarn-unused:_",
      "-Ywarn-inaccessible",
      "-Ywarn-nullary-override",
      "-Ywarn-nullary-unit",
      "-opt:l:inline"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
