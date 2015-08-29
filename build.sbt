
name := "akka-exchange"
 
val akkaVersion        = "2.4.0-RC1"
val akkaStreamVersion  = "1.0"
val akkaHttpVersion    = "1.0"
val sigarLoaderVersion = "1.6.6-rev002"
val logbackVersion     = "1.1.3"


lazy val commonSettings = Seq( 
  organization := "com.boldradius",
  version := "0.1-SNAPSHOT",
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

  ),
  fork in (Test, run) := true

)


lazy val util = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-util",
    scalacOptions := Seq(
      "-encoding",
      "UTF-8",
      "-target:jvm-1.8",
      "-deprecation",
      "-language:postfixOps"
    ),
    fork in (Test, run) := true
  )

lazy val journal = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-journal",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      "org.iq80.leveldb" % "leveldb" % "0.7",
      "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
    )
  ).dependsOn(util).enablePlugins(JavaServerAppPackaging)

addCommandAlias("package-journal", "journal/universal:packageBin")

// Doesn't work right due to JNI issues; you need to generate a package and run from there :(
addCommandAlias("journal", "journal/runMain com.boldradius.akka_exchange.journal.SharedJournalNodeApp -Dakka.remote.netty.tcp.port=2571 -Dakka.cluster.roles.0=shared-journal")

lazy val frontend = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-frontend",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-http-core-experimental_2.11" % akkaHttpVersion,
      "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaHttpVersion
    )
  ).dependsOn(util).enablePlugins(JavaServerAppPackaging)

addCommandAlias("frontend", "frontend/runMain com.boldradius.akka_exchange.frontend.FrontendNodeApp -Dakka.remote.netty.tcp.port=2551")

addCommandAlias("frontend2", "frontend/runMain com.boldradius.akka_exchange.frontend.FrontendNodeApp -Dakka.remote.netty.tcp.port=2561")

lazy val tradeEngine = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-tradeEngine"
  ).dependsOn(util).enablePlugins(JavaServerAppPackaging)

addCommandAlias("trade-engine", "tradeEngine/runMain com.boldradius.akka_exchange.trade.engine.TradeEngineNodeApp -Dakka.remote.netty.tcp.port=2552")

addCommandAlias("trade-engine2", "tradeEngine/runMain com.boldradius.akka_exchange.trade.engine.TradeEngineNodeApp -Dakka.remote.netty.tcp.port=2562")


lazy val ticker = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-ticker",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion
    )

  ).dependsOn(util).enablePlugins(JavaServerAppPackaging)

addCommandAlias("ticker", "ticker/runMain com.boldradius.akka_exchange.TickerNodeApp -Dakka.remote.netty.tcp.port=2553")

addCommandAlias("ticker2", "ticker/runMain com.boldradius.akka_exchange.TickerNodeApp -Dakka.remote.netty.tcp.port=2563")


lazy val securitiesDB = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-securitiesDB",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
    )
    
  ).dependsOn(util, journal).enablePlugins(JavaServerAppPackaging)

addCommandAlias("securities-db", "securitiesDB/runMain com.boldradius.akka_exchange.securities.db.SecuritiesDBNodeApp -Dakka.remote.netty.tcp.port=2554")

addCommandAlias("securities-db2", "securitiesDB/runMain com.boldradius.akka_exchange.securities.db.SecuritiesDBNodeApp -Dakka.remote.netty.tcp.port=2564")

lazy val traderDB = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-traderDB",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
    )
    
  ).dependsOn(util, journal).enablePlugins(JavaServerAppPackaging)

addCommandAlias("trader-db", "traderDB/runMain com.boldradius.akka_exchange.trade.db.TraderDBNodeApp -Dakka.remote.netty.tcp.port=2555")

addCommandAlias("trader-db2", "traderDB/runMain com.boldradius.akka_exchange.trade.db.TraderDBNodeApp -Dakka.remote.netty.tcp.port=2565")


lazy val networkTrade = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-networkTrade",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-stream-experimental_2.11" % akkaStreamVersion
    )

  ).dependsOn(util).enablePlugins(JavaServerAppPackaging)

addCommandAlias("network-trade", "networkTrade/runMain com.boldradius.akka_exchange.trade.network.NetworkTradeNodeApp -Dakka.remote.netty.tcp.port=2556")

