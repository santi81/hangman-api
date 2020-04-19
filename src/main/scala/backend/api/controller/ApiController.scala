package backend.api.controller

import backend.api.domain.AccountValidation
import backend.api.interceptors.AccountError
import backend.api.model.{AccountOperation, AccountTransaction, Game, GameStatus, Guess, Hangman, UserGame}
import com.twitter.util.Future

import scala.concurrent.stm._
import scala.collection.mutable.HashMap

class ApiController extends AccountOperation with AccountValidation with AccountError {
  private[this] var accounts: Map[String, Ref[AccountTransaction]] = Map()
  private[this] var gameMap = HashMap[String,UserGame]()
  private[this] val randGenerator = scala.util.Random
  randGenerator.setSeed(1000000)

  override def createGame(): Future[Option[Hangman]] =
    Future {
              // Generate random game id.. use bloom filter to check for existence
              val gameId = randGenerator.nextInt()
              // Number of guesses
              val num_guesses =  6 + scala.util.Random.nextInt(5) // scalastyle:ignore
              var guesses = Guess(num_guesses, num_guesses, 0)
              // Get a random Generated word
              val gameWord = "HANGMAN"
              var gameStatus = GameStatus(gameWord.length, Array.fill(gameWord.length)(' '),
                Array.fill(num_guesses)(' '), false)
              // Store the Game
              val game = Game(gameId.toString, guesses, gameStatus)
              val userGame = UserGame(game, gameWord)
              gameMap.put(gameId.toString, userGame)
              Option(Hangman(gameId))
    }

  override def retrieveGame(gameID: String): Future[Option[Game]] =
    Future{

        val userGame = gameMap.get(gameID).get
        val response = userGame.game
        Option(response)
    }

  // scalastyle:ignore
  override def submitGuess(game_id: Int, guess: Char): Future[Either[AccountFillException, Game]] = Future {
    val userGame = gameMap.get(game_id.toString).get
    val word = userGame.word
    var game = userGame.game

    val game_guess = game.guesses
    game_guess.num_guesses_left -= 1
    game_guess.num_guesses_used +=1

    var game_status = game.status
    var guessedWord = game_status.word
    var wrong_guesses = game_status.wrong_guesses

    val count = word.count(_ == guess)
    if(count > 0)
      {
        var index = -1
        for(i<-1 to count )
        {
          val newIndex = word.indexOf(guess, index + 1)
          guessedWord(newIndex) = guess
          index = newIndex
        }
        if(guessedWord.mkString("") == word) game_status.game_over = true // scalastyle:ignore
        else
            if(game_guess.num_guesses_left == 0) game_status.game_over = true
      }
    else
      {
        var termCond = true
        var index = 0
        while(termCond)
          {
            if(wrong_guesses(index) == ' ')
              {
                wrong_guesses(index) = guess
                termCond = false
              }
            else {
              index += 1
            }
          }
        if(game_guess.num_guesses_left == 0) game_status.game_over = true
      }
      Right(userGame.game)
  }

  override def createAccount(uuid: String, amount: Double): Future[Either[AccountCreateException, AccountTransaction]] = Future {
      if (accounts.get(uuid).isEmpty) {
        val newAccount = Ref(AccountTransaction(uuid, amount = zeroOrGreater(amount)))
        accounts = accounts + (uuid -> newAccount)
        Right(newAccount.single())
      } else {
        Left(AccountCreateException("The account already exists."))
      }
  }

  override def fillAccount(uuid: String, amount: Double): Future[Either[AccountFillException, AccountTransaction]] = Future {
    if (accounts.get(uuid).isEmpty) createAccount(uuid, 0)

    accounts.get(uuid) match {
      case Some(transact) => {
        atomic {implicit tx =>
          transact() = AccountTransaction(transact().uuid, transact().amount + amount)

          displayOperationType(transact().uuid, transact().amount)

          if (amountIsNegative(transact().amount)) {
            transact() = AccountTransaction(transact().uuid, transact().amount - amount)
          }

          Right(transact())
        }
      }
      case _ => Left(AccountFillException("Fill account not found."))
    }
  }
}

object ApiController {
  def apply(): ApiController = new ApiController()
}
