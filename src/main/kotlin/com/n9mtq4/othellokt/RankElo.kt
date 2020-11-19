package com.n9mtq4.othellokt

import clojure.lang.IFn
import com.n9mtq4.othellokt.mcts.mctsPlayGame
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Created by will on 11/19/20 at 4:21 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

/**
 * Pre-computed elo ratings for MCTS
 * with a different number of iterations.
 * */
private val MCTS_RATINGS = mapOf(
	200 to 1329.56757296276,
	400 to 1389.48894810102,
	600 to 1441.90139508945,
	800 to 1480.82886154807,
	1000 to 1486.26490048041,
	1200 to 1500.35976920696,
	1400 to 1536.26175371363,
	1600 to 1540.01831798109,
	1800 to 1541.62670463028,
	2000 to 1563.45497334952,
	2200 to 1559.06419443148,
	2400 to 1579.46482915556
)

/**
 * Get the mcts iterations and rating with the rating
 * closest to the given rating.
 * 
 * @param rating the elo rating to get a close mcts
 * @return a Pair of the mcts iterations and the elo rating
 * */
private fun closestMctsToRating(rating: Double): Pair<Int, Double> {
	
	val mctsIters = MCTS_RATINGS
		.map { (mcts, mrating) -> mcts to (mrating - rating).absoluteValue }
		.minByOrNull { (_, ratingDiff) -> ratingDiff }
		?.first ?: 1200
	
	return mctsIters to (MCTS_RATINGS[mctsIters] ?: 1500.0)
	
}

/**
 * Compute the rating of a heuristic
 * 
 * @param heuristic the clojure heuristic
 * @param rounds the number of rounds to play. Games = 2 * rounds
 * @return the elo rating of the heuristic
 * */
@JvmOverloads
fun computeRating(heuristic: IFn, rounds: Int = 20): Int {
	
	var elo = 1500.0
	var score = 0.0
	var games = 0
	
	repeat(rounds) {
		
		val (mctsIters, mctsElo) = closestMctsToRating(elo)
		
		for (player in arrayOf(-1, 1)) {
			
			val finalBoard = mctsPlayGame(heuristic, player, 3, mctsIters)
			val gameResult = player * finalBoard.winner()
			
			score += when (gameResult) {
				-1 -> mctsElo - 400
				0 -> mctsElo
				1 -> mctsElo + 400
				else -> throw IllegalArgumentException("Invalid game result $gameResult")
			}
			games++
			elo = score / elo
			
		}
		
	}
	
	return elo.roundToInt()
	
}
