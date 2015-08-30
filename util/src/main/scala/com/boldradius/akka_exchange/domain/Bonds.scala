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

sealed abstract class BondType(val description: String) extends AnyVal

case object TreasuryBill extends BondType("U.S. Treasury Bill")

case object CorporateBond extends BondType("Corporate Bond")

case object MunicipalBond extends BondType("Municipal Bond")

import scala.collection.JavaConversions._

object CUSIP {


  val ValidLetters = Set('A', 'B', 'C', 'D', 'E', 'F', 'G',
                         'H', 'I', 'J', 'K', 'L', 'M', 'N',
                         'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                         'V', 'W', 'X', 'Y', 'Z')

  val ValidSymbols = Set('*', '@', '#')

  /**
   * @param cusip
   * @return
   */

  def validate(cusip: String): Boolean = {
    // require checkdigit for now
    if (cusip.length != 9)
      false
    else {
      var sum = 0
      for (n <- 0 to 7) {
        val v = charCode(cusip.charAt(n))

        sum = sum + (v / 10) + (v % 10)

        def charCode(c: Char): Int = {
          val x =
            if (c >= 0 && c <= 9)
              c
            else if (ValidLetters.contains(c))
              letterOrdinal(c) + 9
            else if (c == '*')
              36
            else if (c == '@')
              37
            else if (c == '#')
              38
            else
              Integer.MAX_VALUE // should cause a calc fail

          if (n % 2 == 0) x * 2 else x
        }



      }

    }
  }

  def letterOrdinal(c: Char): Int = c - 65 + 1
}
