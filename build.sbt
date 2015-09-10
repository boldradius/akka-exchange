
name := "akka-exchange"
 
val akkaVersion        = "2.4.0-RC1"
val akkaStreamVersion  = "1.0"
val akkaHttpVersion    = "1.0"
val sigarLoaderVersion = "1.6.6-rev002"
val logbackVersion     = "1.1.3"
val projectVersion     = "0.1-SNAPSHOT"
val squantsVersion     = "0.5.3"


lazy val commonSettings = Seq( 
  organization := "com.boldradius",
  version := projectVersion,
  scalaVersion := "2.11.7",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "io.kamon" % "sigar-loader" % sigarLoaderVersion,
    "com.squants" %% "squants" % squantsVersion,
    // for the saner groovy config of Logback
    "org.codehaus.groovy" % "groovy" % "2.4.3",
    "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
    "net.ceedubs" %% "ficus" % "1.1.2"
  ),
  fork in (Test, run) := true,
  // Runs OpenJDK 8. Official docker image, should be safe to use.
  dockerBaseImage := "java:8-jdk",
  dockerUpdateLatest := true,
  dockerExposedVolumes := Seq("/opt/docker/logs"),
  // todo: probably change me later when we have a non-snap version?
  version in Docker := "latest",
  // todo - change me once we figure out port(s)?
  dockerExposedPorts := Seq(2551)
)

lazy val root = (project in file(".")).
  aggregate(util, journal, frontend, 
            tradeEngine, ticker, securitiesDB,
            traderDB, networkTrade).
  settings(commonSettings: _*).
  settings(
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
  ).
  dependsOn("root")

lazy val journal = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-journal",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      "org.iq80.leveldb" % "leveldb" % "0.7",
      "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
    ),
    bashScriptExtraDefines ++=  IO.readLines(file(".") / "src" / "main" / "resources" / "docker-detect.sh")
  ).
  dependsOn(util).
  enablePlugins(JavaServerAppPackaging, DockerPlugin)



addCommandAlias("package-journal", "journal/universal:packageBin")

addCommandAlias("dockerize-journal", "journal/docker:publishLocal")

// Doesn't work right due to JNI issues; you need to generate a package and run from there :(
addCommandAlias("journal", "journal/runMain com.boldradius.akka_exchange.journal.SharedJournalNodeApp -Dakka.remote.netty.tcp.port=2571 -Dakka.cluster.roles.0=shared-journal")

lazy val frontend = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-frontend",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-http-core-experimental_2.11" % akkaHttpVersion,
      "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaHttpVersion
    ),
    bashScriptExtraDefines ++=  IO.readLines(file(".") / "src" / "main" / "resources" / "docker-detect.sh"),
    // todo - change me once we figure out port(s)?
    dockerExposedPorts ++= Seq(8080)
  ).
  dependsOn(util).
  enablePlugins(JavaServerAppPackaging, DockerPlugin)

addCommandAlias("dockerize-journal", "frontend/docker:publishLocal")

addCommandAlias("frontend", "frontend/runMain com.boldradius.akka_exchange.frontend.FrontendNodeApp -Dakka.remote.netty.tcp.port=2551")

addCommandAlias("frontend2", "frontend/runMain com.boldradius.akka_exchange.frontend.FrontendNodeApp -Dakka.remote.netty.tcp.port=2561")

lazy val tradeEngine = project.in(file("trade-engine")).
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-trade-engine",
    bashScriptExtraDefines ++=  IO.readLines(file(".") / "src" / "main" / "resources" / "docker-detect.sh")
  ).
  dependsOn(util).
  enablePlugins(JavaServerAppPackaging, DockerPlugin)

addCommandAlias("trade-engine", "tradeEngine/runMain com.boldradius.akka_exchange.trade.engine.TradeEngineNodeApp -Dakka.remote.netty.tcp.port=2552")

addCommandAlias("trade-engine2", "tradeEngine/runMain com.boldradius.akka_exchange.trade.engine.TradeEngineNodeApp -Dakka.remote.netty.tcp.port=2562")


lazy val ticker = project.
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-ticker",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
    ),  
    bashScriptExtraDefines ++=  IO.readLines(file(".") / "src" / "main" / "resources" / "docker-detect.sh")
  ).
  dependsOn(util, journal).
  enablePlugins(JavaServerAppPackaging, DockerPlugin)

addCommandAlias("ticker", "ticker/runMain com.boldradius.akka_exchange.TickerNodeApp -Dakka.remote.netty.tcp.port=2553")

addCommandAlias("ticker2", "ticker/runMain com.boldradius.akka_exchange.TickerNodeApp -Dakka.remote.netty.tcp.port=2563")


lazy val securitiesDB = (project in file("securities-db")).
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-securities-db",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion
    ),
    bashScriptExtraDefines ++=  IO.readLines(file(".") / "src" / "main" / "resources" / "docker-detect.sh")
  ).
  dependsOn(util, journal).
  enablePlugins(JavaServerAppPackaging, DockerPlugin)

addCommandAlias("securities-db", "securitiesDB/runMain com.boldradius.akka_exchange.securities.db.SecuritiesDBNodeApp -Dakka.remote.netty.tcp.port=2554")

addCommandAlias("securities-db2", "securitiesDB/runMain com.boldradius.akka_exchange.securities.db.SecuritiesDBNodeApp -Dakka.remote.netty.tcp.port=2564")

lazy val traderDB = (project in file("trader-db")).
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-trader-db",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
    ),
    bashScriptExtraDefines ++=  IO.readLines(file(".") / "src" / "main" / "resources" / "docker-detect.sh")
  ).
  dependsOn(util, journal).
  enablePlugins(JavaServerAppPackaging, DockerPlugin)

addCommandAlias("trader-db", "traderDB/runMain com.boldradius.akka_exchange.trade.db.TraderDBNodeApp -Dakka.remote.netty.tcp.port=2555")

addCommandAlias("trader-db2", "traderDB/runMain com.boldradius.akka_exchange.trade.db.TraderDBNodeApp -Dakka.remote.netty.tcp.port=2565")


lazy val networkTrade = (project in file("network-trade")).
  settings(commonSettings: _*).
  settings(
    name := "akka-exchange-network-trade",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-stream-experimental_2.11" % akkaStreamVersion
    ),
    bashScriptExtraDefines ++=  IO.readLines(file(".") / "src" / "main" / "resources" / "docker-detect.sh")
  ).
  dependsOn(util, journal).
  enablePlugins(JavaServerAppPackaging, DockerPlugin)

addCommandAlias("network-trade", "networkTrade/runMain com.boldradius.akka_exchange.trade.network.NetworkTradeNodeApp -Dakka.remote.netty.tcp.port=2556")

