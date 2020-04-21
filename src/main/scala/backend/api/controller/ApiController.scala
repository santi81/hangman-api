package backend.api.controller

import backend.api.interceptors.{ GameError}
import backend.api.model.{Game, GameOperation, GameStatus, Guess, Hangman, UserGame}
import backend.api.util.{Constants, URLFetcher}
import com.twitter.util.Future

import scala.concurrent.stm._
import scala.collection.mutable.HashMap
import scalacache.redis._
import scalacache._
import scalacache.serialization.binary._

class ApiController extends GameOperation with GameError {
  private[this] val gameMap = HashMap[String,UserGame]()
  private[this] val randGenerator = scala.util.Random
  randGenerator.setSeed(1000000)
  //scala-style:ignore
  implicit val redisCache: Cache[UserGame] = RedisCache("redis-cluster", 6379)
  override def createGame(): Future[Option[Hangman]] =
    Future {
              // Generate random game id.. use bloom filter to check for existence
      atomic { implicit tx =>
        var gameId = randGenerator.nextInt()
        if (gameId < 0) gameId = -1 * gameId
        // Number of guesses
        val num_guesses = Constants.numGuesses
        var guesses = Guess(num_guesses, num_guesses, 0)
        // Get a random Generated word
        val gameWord = URLFetcher.fetchRandomWord().toUpperCase
        var gameStatus = GameStatus(gameWord.length, Array.fill(gameWord.length)(' '),
          Array.fill(num_guesses)(' '), false)
        // Store the Game
        val game = Game(gameId.toString, guesses, gameStatus)
        val userGame = UserGame(game, gameWord)
        gameMap.put(gameId.toString, userGame)
        Option(Hangman(gameId))
      }
    }
  override def retrieveGame(gameID: String): Future[Either[NonExistentGameException, Game]] =
    Future{
        val userGameOption = gameMap.get(gameID)
        userGameOption match
        {
          case Some(userGame) =>
            val response = userGame.game
            Right(response)
          case None =>
            Left(NonExistentGameException("Game Does not exist"))
        }
    }
  // scalastyle:ignore
  override def submitGuess(game_id: Int, guess: Char): Future[Either[NonExistentGameException, Game]] = Future {
    val userGameOption = gameMap.get(game_id.toString)
    userGameOption match
    {
      case Some(userGame) =>
        // scalastyle:ignore
        atomic {implicit tx =>
          val word = userGame.word; val game = userGame.game; val game_guess = game.guesses
          val game_status = game.status; val guessedWord = game_status.word
          val wrong_guesses = game_status.wrong_guesses; val count = word.count(_ == guess.toUpper)
          if(count > 0) {
            var index = -1
            for(i<-1 to count ) {
              val newIndex = word.indexOf(guess.toUpper, index + 1)
              guessedWord(newIndex) = guess.toUpper
              index = newIndex }
            if(guessedWord.mkString("") == word) game_status.game_over = true}
          else {
            game_guess.num_guesses_left -= 1
            game_guess.num_guesses_used +=1
            var termCond = true
            var index = 0
            while(termCond) {
              if(wrong_guesses(index) == ' ') {
                wrong_guesses(index) = guess.toUpper
                termCond = false
              }
              else {
                index += 1
              }}
            if(game_guess.num_guesses_left == 0) {
              game_status.game_over = true
              game_status.word = word.toCharArray}}
          Right(userGame.game)}
      case None =>
        Left(NonExistentGameException("Game Does not exist"))
    }
  }
}
object ApiController {
  def apply(): ApiController = new ApiController()
}
