package nl.bigdatarepublic.common

import nl.bigdatarepublic.common.LogFutureImplicit._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LogFutureImplicitSpec extends FlatSpec with Matchers with ScalaFutures {

  // ---------------------------------------------------------------------------
  // logSuccess
  // ---------------------------------------------------------------------------

  "logSuccess" should "invoke the callback with the result when the Future succeeds" in {
    var captured: Option[Int] = None
    val future = Future.successful(42).logSuccess { v => captured = Some(v) }

    whenReady(future) { result =>
      captured shouldBe Some(42)
    }
  }

  it should "return the original value unchanged after calling the callback" in {
    val future = Future.successful(99).logSuccess { _ => () }

    whenReady(future) { result =>
      result shouldBe 99
    }
  }

  it should "not invoke the callback when the Future fails" in {
    var called = false
    val future = Future.failed[Int](new RuntimeException("boom")).logSuccess { _ => called = true }

    whenReady(future.failed) { _ =>
      called shouldBe false
    }
  }

  it should "propagate the original failure without modification" in {
    val error = new RuntimeException("original error")
    val future = Future.failed[Int](error).logSuccess { _ => () }

    whenReady(future.failed) { ex =>
      ex shouldBe error
    }
  }

  // ---------------------------------------------------------------------------
  // logFailure
  // ---------------------------------------------------------------------------

  "logFailure" should "invoke the callback with the exception when the Future fails" in {
    val error = new RuntimeException("something went wrong")
    var captured: Option[Throwable] = None
    val future = Future.failed[Int](error).logFailure { e => captured = Some(e) }

    whenReady(future.failed) { _ =>
      captured shouldBe Some(error)
    }
  }

  it should "re-throw the original exception (not swallow it)" in {
    val error = new IllegalStateException("bad state")
    val future = Future.failed[Int](error).logFailure { _ => () }

    whenReady(future.failed) { ex =>
      ex shouldBe error
    }
  }

  it should "not invoke the callback when the Future succeeds" in {
    var called = false
    val future = Future.successful(1).logFailure { _ => called = true }

    whenReady(future) { _ =>
      called shouldBe false
    }
  }

  it should "return the original value unchanged when the Future succeeds" in {
    val future = Future.successful("hello").logFailure { _ => () }

    whenReady(future) { result =>
      result shouldBe "hello"
    }
  }
}
