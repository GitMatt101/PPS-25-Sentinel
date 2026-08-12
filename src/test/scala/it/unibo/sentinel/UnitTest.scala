package it.unibo.sentinel

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.Inspectors
import org.scalatest.Inside
import org.scalatest.OptionValues
import org.scalatest.EitherValues

/** Extend this abstract class to follow the unit test convention for this
  * project.
  */
abstract class UnitTest
    extends AnyWordSpec
    with Matchers
    with Inspectors
    with Inside
    with OptionValues
    with EitherValues
