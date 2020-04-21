package backend.api.model

import com.twitter.util.Future

final case class SubmitGuessRequest(game_id: Int, guess: Char)
final case class Guess(var num_guesses_left: Int, var num_total_guesses: Int, var num_guesses_used:Int)
final case class GameStatus(var total_chars: Int, var word: Array[Char], var wrong_guesses:Array[Char],
                            var game_over:Boolean)
final case class Game(var game_id: String, var guesses: Guess, var status: GameStatus)
final case class UserGame(var game:Game,var word:String)
final case class Hangman(game_id: Int)
final case class GameCreateRequest()

trait GameOperation {
  def createGame: Future[Option[Any]]
  def retrieveGame(gameID: String): Future[Either[Exception, Any]]
  def submitGuess(game_id: Int, guess: Char): Future[Either[Exception, Any]]
}


