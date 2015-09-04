# akka-exchange

<small>Copyright© 2015 BoldRadius Solutions</small>

A demonstration multi-node Clustered Akka application including Spray, Sharding, Data Duplication (aka Replication), and Akka TCP / Streams.

This document outlines the design and usage of the project.

  * [System Design](#system-design)
    - [Architecture Overview](#architecture)
  * [Vagrant / Docker Setup](#vagrant-and-docker-setup)

## System Design

The design of the system is to look sort of like a real live online trading exchange. Users are able to trade via both a Web interface; for larger customers such as banks a TCP/IP trade protocol is also provided.

Two types of security are tradeable:
  - Stocks 
    * Identifier: [Ticker Symbol](https://en.wikipedia.org/wiki/Ticker_symbol)
    * Exchange Type: i.e., NYSE, NASDAQ, et. al.
  - Bonds 
    * Identifier: [CUSIP](https://en.wikipedia.org/wiki/CUSIP)
    * Bond Type: i.e., Municipal, Corporate, Treasury, etc

### Architecture
The overall architecture is outlined roughly in the following diagram. Specific detail is found within.
![](design/akka-exchange.png)

The system utilizes Akka clustering, and is broken into several components. Each of the cluster nodes, uses several plugins which demonstrate various Akka Clustering features.

  - [Frontend Node(s)](#frontend): The Rest API [akka-http / seed node(s)]
  - [Trade Ticker Node](#trading-users): Replicated Feed of completed trades/current prices. Retrieves Trade notifications from the Event Bus. Saves the data via Akka Persistence.
  - [Trade Engine Node](#trade-engine): A Cluster Singleton which proxies the transactions for requested trades. Makes sure there's only ever one instance so that it can gate trades on a single item.
  - [Trading Users DB Node(s)](#trading-users): Sharded Database of active users and their portfolios (all the bids and offers they have on particular stocks & bonds). Utilizes Sharding & Persistence.
  - [Securities DB Node (s)](#securities): Database of known Securities, i.e. Stocks & Bonds. Utilizes Sharding & Data Distribution (aka Replication). 
  - [Network Trade API Node(s)](#network-trade-api): Network API for Trading, using a Binary Protocol. Utilizes Akka IO and Akka Streams for the sample client.



##### Notes
  - *TODO: Figure out where trade transactions are handled. Probably a FSM fired up per trade request?*
  - *TODO: `AtLeastOnceDelivery` to ensure a crash can recover a trade? Trade timeouts probably won't tolerate this but need an excuse to demo it.*

#### Frontend
The **frontend** of the system is a node running [akka-http](http://doc.akka.io/docs/akka-stream-and-http-experimental/1.0/scala/http/)<sup>1</sup> ,  exposing a REST API for web users to trade, browse offerings, etc. This node (or nodes, if you choose to run multiple to load balance, etc) also acts as seed(s) for the cluster. With the default run scripts, the first **frontend** node will start on port `2551`.

<small>1</small><sub>(note that performance is still not optimised for akka-http, but will be in future releases. Alternately we could use the highly performant Spray, upon which akka-http is based.)</sub>


#### Trade Ticker
A persistent node of Trade Data. This provides a Feed of completed trades/current bids & asks (offers). 

Utilizes [Akka Sharding](http://doc.akka.io/docs/akka/2.4.0-RC1/scala/cluster-sharding.html) to spread out data & reduce single node resource usage. Additionally, uses [Akka Persistence](http://doc.akka.io/docs/akka/2.4.0-RC1/scala/persistence.html) to ensure tolerance to crash/failure/actor migration without losing Trade data.


#### Trade Engine

A Cluster Singleton which proxies the transactions for requested trades. Makes sure there's only ever one instance so that it can gate trades on a single item.

Also determines if the system is up & available (beyond 'appropriate nodes up') by checking that all required components are in a 'tradeable' state.

#### Trading Users 
Sharded Database of active users and their portfolios. The Portfolio represents all of a User's stock/bond bids & offers active in the system. Sharded for resource balancing, and Persistent, so it is tolerant to crash/shutdown/actor migration. 

Utilizes [Akka Sharding](http://doc.akka.io/docs/akka/2.4.0-RC1/scala/cluster-sharding.html) to spread out data & reduce single node resource usage. Additionally, uses [Akka Persistence](http://doc.akka.io/docs/akka/2.4.0-RC1/scala/persistence.html) to ensure tolerance to crash/failure/actor migration without losing User data.

  - Each Trading User has a sub-actor for their portfolio, with a subactor for each security type. So, each user will have a child Portfolio Actor, each of which has child actors for Stock Portfolio and Bond Portfolio. *(TODO: maybe should be a trader has a StockPortfolio and BondPortfolio?)*

#### Securities 

Ephemeral (but Replicated) Database of known Securities, i.e. Stocks & Bonds. This is all of the Securities active in the system, with information on every bid offer, and trade that has occurred. This data is initialized cleanly from the Trader DB each time the node restarts, to ensure it is fresh at all times.

Utilizes [Akka's Data Distribution](http://doc.akka.io/docs/akka/2.4.0-RC1/scala/distributed-data.html) module for Replication, so that there's a "Primary" instance of the Securities DB actor, with additional nodes acting as "Secondaries" consuming data. If the Primary node crashes/is shut down, the Secondary node takes over with the replicated data. 

#### Network Trade API 

Network based Trade System (TCP/IP with custom Binary Protocol) for emulating non-web users, such as large banks or high frequency traders. 

This utilizes [Akka I/O](http://doc.akka.io/docs/akka/2.4.0-RC1/scala/io.html) to handle the TCP/IP and serializing/deserializing of the messages. Interfaces to the system through the same Actor messaging as the REST API.

## Vagrant and Docker Setup

In addition to being a demonstration of Akka Clustering, this project serves as a Docker demonstration. For now, the Docker configuration has been focused on running within a Vagrant host, but each module/node of the project has a full Docker config that can standalone.

### Setting Up Vagrant

This Vagrant is setup to initialize a VirtualBox Linux host running Docker, in which each container is setup. The configuration for the VirtualBox host is in `Vagrantfile.host`; you could easily change that to spin up say, an AWS Server instead of VirtualBox. The actual Docker hosts are all configured in the primary `Vagrantfile`.

Best behavior of the Vagrant setup will require some Vagrant plugins:

```bash
vagrant plugin install vagrant-timezone # Set a timezone other than UTC on the host, for simplifying log reading
```

Parallel execution of vagrant (The default) causes some fun race conditions due to container linking. ` VAGRANT_NO_PARALLEL=yes vagrant up` seems to be the best way to bring up all of the configured nodes.  
