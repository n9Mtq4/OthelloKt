package com.n9mtq4.othellokt

import clojure.lang.IFn
import kotlin.math.max
import kotlin.math.min

/**
 * Created by will on 10/20/20 at 12:35 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
val GRID_WEIGHTS = arrayOf(
	intArrayOf(120, -20, 20, 5, 5, 20, -20, 120),
	intArrayOf(-20, -40, -5, -5, -5, -5, -40, -20),
	intArrayOf(20, -5, 15, 3, 3, 15, -5, 20),
	intArrayOf(5, -5, 3, 3, 3, 3, -5, 5),
	intArrayOf(5, -5, 3, 3, 3, 3, -5, 5),
	intArrayOf(20, -5, 15, 3, 3, 15, -5, 20),
	intArrayOf(-20, -40, -5, -5, -5, -5, -40, -20),
	intArrayOf(120, -20, 20, 5, 5, 20, -20, 120)
)

val GRID_WEIGHTS_SUM = GRID_WEIGHTS.sumBy { it.sum() }

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
	return 0.8 * (boardSum / GRID_WEIGHTS_SUM) + 0.4 * (possibleMoves.toDouble() / 10.0)
}

@JvmOverloads
fun alphaBetaHandCraftedKt(
	state: OthelloState,
	depth: Int,
	playMax: Boolean,
	alpha: Double = Double.MIN_VALUE,
	beta: Double = Double.MAX_VALUE
): Double {
	
	if (depth <= 0 || state.gameOver())
		return humanHeuristic(state)
	
	var a = alpha
	var b = beta
	
	if (playMax) {
		var value = Double.MIN_VALUE
		for (move in state.availableMoves()) {
			val child = state.applyMove(move)
			value = max(value, alphaBetaHandCraftedKt(child, depth - 1, !playMax, a, b))
			a = max(value, a)
			if (a >= b) return value
		}
		return value
	} else {
		var value = Double.MAX_VALUE
		for (move in state.availableMoves()) {
			val child = state.applyMove(move)
			value = min(value, alphaBetaHandCraftedKt(child, depth - 1, !playMax, a, b))
			b = min(value, b)
			if (a >= b) return value
		}
		return value
	}
	
}


@JvmOverloads
fun alphaBetaKtCljFunc(
	heuristic: IFn,
	state: OthelloState,
	depth: Int,
	playMax: Boolean,
	alpha: Double = Double.MIN_VALUE,
	beta: Double = Double.MAX_VALUE
): Double {
	
	if (depth <= 0 || state.gameOver())
		return heuristic(state) as Double
	
	var a = alpha
	var b = beta
	
	if (playMax) {
		var value = Double.MIN_VALUE
		for (move in state.availableMoves()) {
			val child = state.applyMove(move)
			value = max(value, alphaBetaKtCljFunc(heuristic, child, depth - 1, !playMax, a, b))
			a = max(value, a)
			if (a >= b) return value
		}
		return value
	} else {
		var value = Double.MAX_VALUE
		for (move in state.availableMoves()) {
			val child = state.applyMove(move)
			value = min(value, alphaBetaKtCljFunc(heuristic, child, depth - 1, !playMax, a, b))
			b = min(value, b)
			if (a >= b) return value
		}
		return value
	}
	
}
