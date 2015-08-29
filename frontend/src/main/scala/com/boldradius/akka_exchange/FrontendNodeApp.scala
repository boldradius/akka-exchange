package com.boldradius.akka_exchange

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

object FrontendNodeApp extends App {
  implicit val system = ActorSystem("akka-exchange")
  implicit val materializer = ActorMaterializer()

  val cluster = Cluster(system)
  
  val route =
    path("offers") {
      get {
        complete {
          "Here's some data... or would be if we had data."
        }
      }
    }
  
  Http().bindAndHandle(route, "localhost", 8080)
}
