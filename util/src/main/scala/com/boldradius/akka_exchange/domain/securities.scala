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

package com.boldradius.akka_exchange.domain


sealed trait Security {
  type SymbolType <: SecuritySymbol
  val symbol: SymbolType
}

case class Stock(symbol: StockSymbol) extends Security {
  type SymbolType = StockSymbol
}

sealed abstract class Bond(symbol: CUSIP) extends Security {
  type SymbolType = CUSIP
}

trait SecuritySymbol

object CUSIP {

  def validate(cusip: String): Boolean =
    letterOrdinal(cusip.last) == calculateCheckDigit(cusip)

  def calculateCheckDigit(cusip: String): Int = {
    // accept with or without checkdigit
    var sum = 0
    for (n <- 0 to 6) {
      def charCode(c: Char): Int = {
        val x =
          letterOrdinal(c)

        if (n % 2 == 1) x * 2 else x
      }

      val v = charCode(cusip.charAt(n))

      if (v > 9) {
        val div = v / 10
        val mod = v % 10

        sum += mod + div
      } else
        sum += v

    }

    val mod = (10 - (sum % 10)) % 10
    mod
  }

  def letterOrdinal(c: Char): Int =
    if (c >= '0' && c <= '9') // digits 0-9
      c - '0'
    else
      c - 'A' + 10
}

case class CUSIP(cusip: String) extends SecuritySymbol {
  require(CUSIP.validate(cusip), "Invalid CUSIP: Check Digit Doesn't Match.")
}

sealed abstract class StockSymbol(val symbol: String) extends SecuritySymbol {
  type Exchange <: StockExchange

  def exchange: Exchange
}

case class NYSESymbol(sym: String) extends StockSymbol(sym) {
  type Exchange = NYSE.type
  def exchange = NYSE
}

case class NASDAQSymbol(sym: String) extends StockSymbol(sym) {
  type Exchange = NASDAQ.type
  def exchange = NASDAQ
}

case class LSESymbol(sym: String) extends StockSymbol(sym) {
  type Exchange = LSE.type
  def exchange = LSE
}


