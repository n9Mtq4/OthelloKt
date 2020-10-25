package com.n9mtq4.othellokt

import clojure.lang.IFn
import kotlin.math.max
import kotlin.math.min

/**
 * Created by will on 10/20/20 at 12:35 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

/**
 * A list of handcrafted grid weights to evaluate an Othello board
 * */
private val GRID_WEIGHTS = arrayOf(
	intArrayOf(120, -20, 20,  5,   5,   20,  -20, 120),
	intArrayOf(-20, -40, -5,  -5,  -5,  -5,  -40, -20),
	intArrayOf(20,  -5,  15,  3,   3,   15,  -5,  20),
	intArrayOf(5,   -5,  3,   3,   3,   3,   -5,  5),
	intArrayOf(5,   -5,  3,   3,   3,   3,   -5,  5),
	intArrayOf(20,  -5,  15,  3,   3,   15,  -5,  20),
	intArrayOf(-20, -40, -5,  -5,  -5,  -5,  -40, -20),
	intArrayOf(120, -20, 20,  5,   5,   20,  -20, 120)
)

//val GRID_WEIGHTS_SUM = GRID_WEIGHTS.sumBy { it.sum() }

/**
 * Precomputed sum of [GRID_WEIGHTS]
 * const for compiler inlining
 * */
private const val GRID_WEIGHTS_SUM = 336

/**
 * Precomputed board score weight
 * const for compiler inlining
 * */
private const val BOARD_SCORE_WEIGHT = 0.8 / GRID_WEIGHTS_SUM

/**
 * Precomputed move score weight
 * const for compiler inlining
 * */
private const val MOVE_SCORE_WEIGHT = 0.4 / 10.0

/**
 * A handcrafted othello heuristic
 * 
 * @param state the [OthelloState]
 * @return a double of the board evaluation. Positive is good for the current player
 * */
fun humanHeuristic(state: OthelloState): Double {
	if (state.gameOver())
		return 1000000.0 * state.winner()
	var boardSum = 0.0
	for (r in 0 until 8) {
		for (c in 0 until 8) {
			boardSum += state.board[r][c] * GRID_WEIGHTS[r][c]
		}
	}
	val possibleMoves = state.current * state.availableMoves().size
	return BOARD_SCORE_WEIGHT * boardSum + MOVE_SCORE_WEIGHT * possibleMoves
}

/**
 * An implementation of minimax with alpha beta pruning.
 * 
 * @param heuristic A clojure function that returns a double
 * @param state the [OthelloState]
 * @param depth the depth of the search
 * @param playMax Should we try to maximize the score?
 * @param alpha the alpha pruning parameter
 * @param beta the beta pruning parameter
 * */
@JvmOverloads
fun alphaBetaKtCljFunc(
	heuristic: IFn,
	state: OthelloState,
	depth: Int,
	playMax: Boolean,
	alpha: Double = Double.NEGATIVE_INFINITY,
	beta: Double = Double.POSITIVE_INFINITY
): Double {
	
	// depth exceeded or game won. Use the heuristic
	if (depth <= 0 || state.gameOver())
		return heuristic(state) as Double
	
	if (playMax) {
		
		var value = Double.NEGATIVE_INFINITY
		var a = alpha
		
		for (move in state.availableMoves()) {
			
			val child = state.applyMove(move)
			
			// recursively evaluation position & update alpha
			value = max(value, alphaBetaKtCljFunc(heuristic, child, depth - 1, !playMax, a, beta))
			a = max(value, a)
			
			// prune if possible
			if (a >= beta) break
			
		}
		
		return value
		
	} else {
		
		var value = Double.POSITIVE_INFINITY
		var b = beta
		
		for (move in state.availableMoves()) {
			
			val child = state.applyMove(move)
			
			// recursively evaluation position & update beta
			value = min(value, alphaBetaKtCljFunc(heuristic, child, depth - 1, !playMax, alpha, b))
			b = min(value, b)
			
			// prune if possible
			if (alpha >= b) break
			
		}
		
		return value
		
	}
	
}
