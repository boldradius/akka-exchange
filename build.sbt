
name := "akka-exchange"
 
organization := "com.boldradius"

version := "0.1-SNAPSHOT"
 
 
val akkaVersion        = "2.4.0-RC1"
val akkaStreamVersion  = "1.0"
val akkaHttpVersion    = "1.0"
val sigarLoaderVersion = "1.6.6-rev002"
val logbackVersion     = "1.1.3"


lazy val commonSettings = Seq( 
  scalaVersion := "2.11.7",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "io.kamon" % "sigar-loader" % sigarLoaderVersion,
    // for the saner groovy config of Logback
    "org.codehaus.groovy" % "groovy" % "2.4.3"

  )
)

lazy val util = project.
  settings(commonSettings: _*).
  settings(
    scalacOptions := Seq(
      "-encoding",
      "UTF-8",
      "-target:jvm-1.8",
      "-deprecation",
      "-language:postfixOps"
    )
  )

lazy val tradeEngine = project.
  settings(commonSettings: _*).
  settings().
  dependsOn(util)


lazy val frontend = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-http-core-experimental_2.11" % akkaHttpVersion,
      "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaHttpVersion
    )
  ).
  dependsOn(util)

lazy val networkTrade = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-stream-experimental_2.11" % akkaStreamVersion
    )
    
  ).
  dependsOn(util)



lazy val securitiesDB = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion
    )
    
  ).
  dependsOn(util)

lazy val tradeDB = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion
    )
    
  ).
  dependsOn(util)


lazy val ticker = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion
    )
    
  ).
  dependsOn(util)


