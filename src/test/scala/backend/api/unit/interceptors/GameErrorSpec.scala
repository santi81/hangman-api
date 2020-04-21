package backend.api.unit.interceptors

import backend.api.interceptors.GameError
import org.scalatest.{FlatSpec, Matchers}

class GameErrorSpec extends FlatSpec with Matchers {
  object GameError extends GameError

  it should "validation test in exception messages" in {
    val th1 = intercept[GameError.NonExistentGameException] {
      throw GameError.NonExistentGameException("Game Does not exist")
    }
    th1.getMessage shouldEqual "Game Does not exist"
  }
}
