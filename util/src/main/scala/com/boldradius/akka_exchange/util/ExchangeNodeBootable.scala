/**
 * Copyright © 2015, BoldRadius Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.boldradius.akka_exchange.util

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster

import scala.collection.breakOut


abstract class ExchangeNodeBootable extends App {
  import net.ceedubs.ficus.Ficus._
  fetchSystemProperties(args)


  implicit val system = ActorSystem("akka-exchange")

  implicit val config = system.settings.config

  val cluster = Cluster(system)

  println("[Starting up with Seed Nodes]: " +
    config.getStringList("akka.cluster.seed-nodes"))


  /**
   * Sets us up so any startup args are merged as system properties
   */
  def fetchSystemProperties(args: Array[String]) {
    val Property = """(\S+)=(\S+)""".r

    val options: Map[String, String] = if (args.length > 0)
      args.collect {
        case Property(key, value) => key -> value
      }(breakOut)
    else
      Map.empty[String, String]

    for ((key, value) <- options if key.startsWith("-D"))
      System.setProperty(key.substring(2), value)

  }
}
