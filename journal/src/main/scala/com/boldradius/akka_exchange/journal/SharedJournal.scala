/**
 * Copyright © 2015, BoldRadius Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boldradius.akka_exchange.journal

import akka.actor._
import akka.cluster.ClusterEvent._
import akka.cluster._
import akka.persistence.journal.leveldb.{ SharedLeveldbJournal, SharedLeveldbStore }
import com.boldradius.akka_exchange.util.ExchangeNodeBootable

import scala.concurrent.duration._

object SharedJournal {

  val name: String =
    "shared-journal"

  def pathFor(address: Address): ActorPath =
    RootActorPath(address) / "user" / name
}

/**
 * This is a demonstration DB, for use in a sample cluster.
 *
 * In the real world you should be using a persistence plugin for
 * an external datastore such as MySQL, Casssandra, or Kafka
 */
object SharedJournalNodeApp extends ExchangeNodeBootable {

  val sharedJournal =
    system.actorOf(
      Props(new SharedLeveldbStore),
      SharedJournal.name
    )
  SharedLeveldbJournal.setStore(sharedJournal, system)

}

object SharedJournalFinder {

  val name: String =
    "shared-journal-finder"

  def props: Props =
    Props(new SharedJournalFinder)
}

/**
 * TODO: Integrate in the ExchangeNodeBootable
 *
 * All nodes will need to start a copy of this at boot,
 * it listens to the cluster to find the node running the Shared Journal,
 * to allow persistence to work.
 *
 * TODO: This should probably stay running, and listen for termination of the journal,
 * sending an exception to actorsystem as a result.
 */
class SharedJournalFinder extends Actor with ActorLogging {
  log.info("Searching for Shared Journal...")

  override def preStart(): Unit =
    Cluster(context.system).subscribe(self, InitialStateAsEvents, classOf[MemberUp])

  override def receive: Receive =
    waiting

  private def waiting: Receive = {
    case MemberUp(member) if member hasRole SharedJournal.name =>
      log.info(
        "Discovered a node with the Shared Journal Role ({}). " +
          "Checking for Journal Actor.", SharedJournal.name
      )
      onSharedJournalMemberUp(member)
  }

  private def becomeSearching(): Unit = {
    context.setReceiveTimeout(10 seconds)
    context.become(searching)
  }

  private def searching: Receive = {
    case ActorIdentity(_, Some(sharedJournal)) =>
      SharedLeveldbJournal.setStore(sharedJournal, context.system)
      log.info("Located and initialized Shared Journal at {}", sharedJournal)
      context.stop(self)
    case ActorIdentity(_, None) =>
      log.error("Failed to locate Shared Journal. Did you start the node?")
      context.stop(self)
      throw new LinkageError("Unable to locate required Shared Journal in the cluster. This node cannot operate safely.")
    case ReceiveTimeout =>
      log.error("Timed out during search for Shared Journal. Did you start the node?")
      context.stop(self)
      throw new LinkageError("Unable to locate required Shared Journal in the cluster. This node cannot operate safely.")
  }

  private def onSharedJournalMemberUp(member: Member): Unit = {
    val sharedJournal = context.actorSelection(
      SharedJournal.pathFor(member.address)
    )
    sharedJournal ! Identify(None)
    becomeSearching()
  }
}

// vim: set ts=2 sw=2 sts=2 et:
