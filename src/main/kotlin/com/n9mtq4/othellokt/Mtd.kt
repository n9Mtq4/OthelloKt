package com.n9mtq4.othellokt

import clojure.lang.IFn
import kotlin.math.max
import kotlin.math.min

/**
 * Created by will on 11/4/20 at 11:22 PM.
 * 
 * An implementation of https://people.csail.mit.edu/plaat/mtdf.html
 * 
 * @author Will "n9Mtq4" Bresnahan
 */

@JvmOverloads
fun mtdf(heuristic: IFn, state: OthelloState, depth: Int, playMax: Boolean, f: Double = 0.0): Double {
	
	var g = f
	var upperbound = Double.POSITIVE_INFINITY
	var lowerbound = Double.NEGATIVE_INFINITY
	
	val table = HashMap<OthelloState, Pair<Double, Double>>()
	
	do {
		
		val beta = if (g == lowerbound) g + 1 else g
		
		g = alphaBetaKtWithMemoryFljFunc(table, heuristic, state, depth, playMax, beta - 1, beta)
		
		if (g < beta) {
			upperbound = g
		} else {
			lowerbound = g
		}
		
	} while (lowerbound < upperbound)
	
	return g
	
}

fun alphaBetaKtWithMemoryFljFunc(
	table: HashMap<OthelloState, Pair<Double, Double>>,
	heuristic: IFn,
	state: OthelloState,
	depth: Int,
	playMax: Boolean,
	_alpha: Double,
	_beta: Double
): Double {
	
	var alpha = _alpha
	var beta = _beta
	var g: Double
	
	// lookup
	val lookup = table[state]
	var nlower = Double.NEGATIVE_INFINITY
	var nupper = Double.POSITIVE_INFINITY
	
	if (lookup != null) {
		
		nlower = lookup.first
		nupper = lookup.second
		if (nlower >= beta) return nlower
		if (nupper <= alpha) return nupper
		
		alpha = max(alpha, nlower)
		beta = min(beta, nupper)
		
	}
	
	// depth exceeded or game won. Use the heuristic
	if (depth <= 0 || state.gameOver()) {
		
		g = heuristic(state) as Double
		
	} else if (playMax) {
		
		g = Double.NEGATIVE_INFINITY
		var a = alpha
		
		for (move in state.availableMoves()) {
			
			val child = state.applyMove(move)
			
			// recursively evaluation position & update alpha
			g = max(g, alphaBetaKtWithMemoryFljFunc(table, heuristic, child, depth - 1, !playMax, a, beta))
			a = max(a, g)
			
			// prune if possible
			if (g >= beta) break
			
		}
		
	} else {
		
		g = Double.POSITIVE_INFINITY
		var b = beta
		
		for (move in state.availableMoves()) {
			
			val child = state.applyMove(move)
			
			// recursively evaluation position & update beta
			g = min(g, alphaBetaKtWithMemoryFljFunc(table, heuristic, child, depth - 1, !playMax, alpha, b))
			b = min(b, g)
			
			// prune if possible
			if (g <= alpha) break
			
		}
		
	}
	
	if (g <= alpha) {
		nupper = g
	}
	
	if (g > alpha && g < beta) {
		nlower = g
		nupper = g
	}
	
	if (g >= beta) {
		nlower = g
	}
	
	table[state] = nlower to nupper
	
	return g
	
}
