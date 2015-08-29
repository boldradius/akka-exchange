package com.boldradius.akka_exchange.util

import akka.actor.{Props, ActorSystem}
import akka.cluster.Cluster
import com.boldradius.akka_exchange.journal.{SharedJournalFinder, SharedJournal}

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
trait ExchangeNodeBootable {
  val findJournal: Boolean = true
  implicit val system = ActorSystem("akka-exchange")

  val cluster = Cluster(system)


  val persistentJournal = if (findJournal) {
    println("Finding Shared Journal.")

    system.actorOf(
      Props(
        classOf[SharedJournalFinder],
        SharedJournalFinder.name
      )
    )
  } else system.deadLetters


}
