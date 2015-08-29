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

package com.boldradius.akka_exchange.frontend

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.boldradius.akka_exchange.util.ExchangeNodeBootable

object FrontendNodeApp extends ExchangeNodeBootable {

  implicit val materializer = ActorMaterializer()
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

// vim: set ts=2 sw=2 sts=2 et:
