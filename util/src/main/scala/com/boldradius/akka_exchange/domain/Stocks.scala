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
package com.boldradius.akka_exchange.domain

sealed abstract class StockExchange(val name: String)

case object NYSE extends StockExchange("New York Stock Exchange")

case object NASDAQ extends StockExchange("NASDAQ")

case object LSE extends StockExchange("London Stock Exchange")

sealed abstract class StockSymbol extends AnyVal {
  type Exchange <: StockExchange

  val symbol: String
  def exchange: Exchange
}

case class NYSESymbol(symbol: String) extends StockSymbol {
  type Exchange = NYSE.type
  def exchange = NYSE
}

case class NASDAQSymbol(symbol: String) extends StockSymbol {
  type Exchange = NASDAQ.type
  def exchange = NASDAQ
}

case class LSESymbol(symbol: String) extends StockSymbol {
  type Exchange = LSE.type
  def exchange = LSE
}

