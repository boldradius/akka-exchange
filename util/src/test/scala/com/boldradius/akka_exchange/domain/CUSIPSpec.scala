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

import org.scalatest._

class CUSIPSpec extends WordSpec with ShouldMatchers {
  val Netflix2025CorporateBond = "U74079AE1"

  // we really don't need cusip validation. wire this in later for fun.
  "A Valid CUSIP" should {
    "Validate" in {
      CUSIP.validate(Netflix2025CorporateBond) shouldBe true
    }
  }


}
