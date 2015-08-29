
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
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
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

addCommandAlias("journal", "util/runMain com.boldradius.akka_exchange.journal.SharedJournalNodeApp -Dakka.remote.netty.tcp.port=2571 -Dakka.cluster.roles.0=shared-journal")

lazy val frontend = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-http-core-experimental_2.11" % akkaHttpVersion,
      "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaHttpVersion
    )
  ).
  dependsOn(util)

addCommandAlias("fe", "frontend/runMain com.boldradius.akka_exchange.frontend.FrontendNodeApp -Dakka.remote.netty.tcp.port=2551")

addCommandAlias("fe2", "frontend/runMain com.boldradius.akka_exchange.frontend.FrontendNodeApp -Dakka.remote.netty.tcp.port=2561")

lazy val tradeEngine = project.
  settings(commonSettings: _*).
  settings().
  dependsOn(util)

addCommandAlias("te", "tradeEngine/runMain com.boldradius.akka_exchange.trade.engine.TradeEngineNodeApp -Dakka.remote.netty.tcp.port=2552")

addCommandAlias("te2", "tradeEngine/runMain com.boldradius.akka_exchange.trade.engine.TradeEngineNodeApp -Dakka.remote.netty.tcp.port=2562")


lazy val ticker = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion
    )

  ).
  dependsOn(util)

addCommandAlias("tick", "ticker/runMain com.boldradius.akka_exchange.TickerNodeApp -Dakka.remote.netty.tcp.port=2553")

addCommandAlias("tick2", "ticker/runMain com.boldradius.akka_exchange.TickerNodeApp -Dakka.remote.netty.tcp.port=2563")


lazy val securitiesDB = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
    )
    
  ).
  dependsOn(util)

addCommandAlias("sdb", "securitiesDB/runMain com.boldradius.akka_exchange.securities.db.SecuritiesDBNodeApp -Dakka.remote.netty.tcp.port=2554")

addCommandAlias("sdb2", "securitiesDB/runMain com.boldradius.akka_exchange.securities.db.SecuritiesDBNodeApp -Dakka.remote.netty.tcp.port=2564")

lazy val tradeDB = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
    )
    
  ).
  dependsOn(util)

addCommandAlias("tdb", "tradeDB/runMain com.boldradius.akka_exchange.trade.db.TradeDBNodeApp -Dakka.remote.netty.tcp.port=2555")

addCommandAlias("tdb2", "tradeDB/runMain com.boldradius.akka_exchange.trade.db.TradeDBNodeApp -Dakka.remote.netty.tcp.port=2565")


lazy val networkTrade = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-stream-experimental_2.11" % akkaStreamVersion
    )

  ).
  dependsOn(util)

addCommandAlias("net", "networkTrade/runMain com.boldradius.akka_exchange.trade.network.NetworkTradeNodeApp -Dakka.remote.netty.tcp.port=2556")

