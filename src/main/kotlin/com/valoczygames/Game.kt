import java.lang.NumberFormatException
import java.util.*

fun main() {
    val game = Game(10)
    game.start()
}

enum class Parity {
    EVEN, ODD
}

data class ParityBet(val bet: Int, val parity: Parity)

class Player(private val scanner: Scanner, val name: String, initialBallCount: Int) {
    var ballCount: Int = initialBallCount

    fun win(bet: Int) {
        ballCount += bet
    }

    fun lose(bet: Int) {
        ballCount -= bet
    }

    fun readHideNumber(): Int {
        println("$name! Please decide how many balls to hide in your palm.")
        return readNumber()
    }

    fun guessParityAndReadBet(): ParityBet {
        val parity = guessParity()
        val bet = readBetNumber()
        return ParityBet(bet, parity)
    }

    private fun readBetNumber(): Int {
        println("$name! Please decide how many balls to bet along your guess.")
        return readNumber()
    }

    private fun readNumber(): Int {
        println("You currently have $ballCount balls.")
        try {
            val n = String(System.console()?.readPassword() ?: "null".toCharArray())
            val number = if (n == "null") {
                scanner.nextInt()
            } else {
                Integer.parseInt(n)
            }

            if (number < 1 || number > ballCount) {
                println("$name! Number must be > 0 and <= $ballCount. Try again.")
                return readNumber()
            }
            return number
        } catch (e: Exception) {
            when(e) {
                is InputMismatchException, is NumberFormatException -> {
                    println("$name! ${scanner.nextLine()} was not an Integer number. Try again.")
                    return readNumber()
                }
                else -> throw e
            }
        }
    }

    private fun guessParity(): Parity {
        try {
            println("$name! Guess the parity of the number that the other player is hiding.")
            println("Enter 0 if even, 1 if odd.")
            val number = scanner.nextInt()
            if (number != 0 && number != 1) {
                println("$name! Number must be 0 or 1. Try again.")
                return guessParity()
            }
            return if (number == 0) Parity.EVEN else Parity.ODD
        } catch (e: InputMismatchException) {
            println("$name! ${scanner.nextLine()} was not an Integer number. Try again.")
            return guessParity()
        }
    }
}

class Game(initialBallCount: Int) {
    private val scanner = Scanner(System.`in`)
    private val player1: Player
    private val player2: Player
    private var actualPlayer1: Player
    private var actualPlayer2: Player
    private var turn = 1

    init {
        println("Specify name of player 1 and hit enter.")
        player1 = Player(scanner, scanner.nextLine(), initialBallCount)
        actualPlayer1 = player1
        println("Specify name of player 2 and hit enter.")
        player2 = Player(scanner, scanner.nextLine(), initialBallCount)
        actualPlayer2 = player2
    }

    fun start() {
        while (player1.ballCount != 0 && player2.ballCount != 0) {
            println("---------------- Turn $turn! ----------------")
            val hiddenNumber = actualPlayer1.readHideNumber()
            val guess = actualPlayer2.guessParityAndReadBet()
            val bet = Math.min(hiddenNumber, guess.bet)

            decideRoundOutcome(guess, hiddenNumber, bet)
            switchPlayers()
        }

        printWinner()
    }

    private fun decideRoundOutcome(guess: ParityBet, hiddenNumber: Int, bet: Int) {
        if (guess.parity == Parity.EVEN) {
            if (hiddenNumber % 2 == 0) {
                player2WonRound(bet)
            } else {
                player1WonRound(bet)
            }
        } else {
            if (hiddenNumber % 2 == 1) {
                player2WonRound(bet)
            } else {
                player1WonRound(bet)
            }
        }
        println()
    }

    private fun player1WonRound(bet: Int) {
        println("${actualPlayer1.name} won turn $turn and receives $bet balls from ${actualPlayer2.name}.")
        actualPlayer1.win(bet)
        actualPlayer2.lose(bet)
    }

    private fun player2WonRound(bet: Int) {
        println("${actualPlayer2.name} won turn $turn and receives $bet balls from ${actualPlayer1.name}.")
        actualPlayer1.lose(bet)
        actualPlayer2.win(bet)
    }

    private fun switchPlayers() {
        val tempPlayer = actualPlayer1
        actualPlayer1 = actualPlayer2
        actualPlayer2 = tempPlayer
        turn++
    }

    private fun printWinner() {
        if (player1.ballCount != 0) {
            println("The winner is ${player1.name}!")
        } else {
            println("The winner is ${player2.name}!")
        }
        scanner.close()
    }
}