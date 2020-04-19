package backend.api


import backend.api.model.SubmitGuessRequest
import io.circe.generic.auto._
import io.finch.{Endpoint, _}
import io.finch.circe._
import io.finch.syntax._

object Routes {

  import backend.api.controller.ApiController
  import backend.api.model.{AccountFillRequest, AccountTransaction, Game, GameCreateRequest, Hangman}
  private lazy val apiController = ApiController()


  final val createGame: Endpoint[Option[Hangman]] =
    post("createGame" :: jsonBody[GameCreateRequest]) { req: GameCreateRequest =>
      for {
        r <- apiController.createGame()
      } yield Ok(r)
    }

  final val retrieveGame: Endpoint[Option[Game]] =
    get("retrieveGame" :: path[String]) {req: String =>
      for {
        r <- apiController.retrieveGame(req)
      } yield Ok(r)
    }

  final val submitGuess: Endpoint[Game] =
    post("submitGuess" :: jsonBody[SubmitGuessRequest]) { req: SubmitGuessRequest =>
      for {
        r <- apiController.submitGuess(req.game_id, req.guess)
      } yield r match {
        case Right(a) => Ok(a)
        case Left(m) => BadRequest(m)
      }
    }

  final val fillAccount: Endpoint[AccountTransaction] =
    post("account" :: jsonBody[AccountFillRequest]) {req: AccountFillRequest =>
      for {
        r <- apiController.fillAccount(req.uuid, req.amount)
      } yield r match {
        case Right(a) => Ok(a)
        case Left(m) => BadRequest(m)
      }
    }
}
