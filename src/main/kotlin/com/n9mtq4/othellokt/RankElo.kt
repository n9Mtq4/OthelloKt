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
	20 to 1012.20463383862,
	40 to 1127.87709178272,
	60 to 1205.27736724821,
	80 to 1234.17603308075,
	100 to 1292.82881041256,
	120 to 1329.06287362451,
	140 to 1340.83270239907,
	160 to 1343.72336356304,
	180 to 1360.22856273615,
	200 to 1383.9571067387,
	220 to 1407.64918873246,
	240 to 1417.50449599938,
	400 to 1450.58257608757,
	600 to 1494.31028676273,
	800 to 1513.66172891599,
	1000 to 1532.63098289929,
	1200 to 1571.37667093234,
	1400 to 1582.29204351634,
	1600 to 1590.61891969016,
	1800 to 1582.90821004184,
	2000 to 1625.53533054869,
	2200 to 1620.59333453342,
	2400 to 1630.09425690247,
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
fun computeRating(heuristic: IFn, depth: Int = 3, rounds: Int = 200): Int {
	
	var score = 0.0
	var games = 0
	var elo = 1500.0
	
	for (i in 0 until rounds) {
		
		val (iterations, mctsElo) = closestMctsToRating(elo)
		val blackBoard = mctsPlayGame(heuristic, 1, depth, iterations)
		val whiteBoard = mctsPlayGame(heuristic, -1, depth, iterations)
		
		println("-1, $iterations, ${blackBoard.winner()}")
		println("$iterations, -1, ${whiteBoard.winner()}")
		
		score += when (blackBoard.winner()) {
			-1 -> mctsElo - 400
			0 -> mctsElo
			1 -> mctsElo + 400
			else -> throw IllegalArgumentException("Invalid game result")
		}
		score += when (whiteBoard.winner()) {
			1 -> mctsElo - 400
			0 -> mctsElo
			-1 -> mctsElo + 400
			else -> throw IllegalArgumentException("Invalid game result")
		}
		
		games += 2
		
		elo = score / games
		
	}
	
	return elo.roundToInt()
	
}
