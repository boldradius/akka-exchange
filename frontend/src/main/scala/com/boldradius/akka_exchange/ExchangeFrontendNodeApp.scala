package com.boldradius.akka_exchange

import akka.actor.ActorSystem
import akka.cluster.Cluster

object ExchangeFrontendNodeApp {
  implicit system = ActorSystem("akka-exchange")

  val cluster = Cluster(system)

}
