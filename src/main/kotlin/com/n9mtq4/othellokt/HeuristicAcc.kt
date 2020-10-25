package com.n9mtq4.othellokt

/**
 * Created by will on 10/25/20 at 3:10 PM.
 * 
 * A file containing functions for accelerated heuristic evaluation
 * 
 * @author Will "n9Mtq4" Bresnahan
 */

/**
 * Gives each square an index. Exploits symmetries in the 
 * Othello board
 * */
private val GRID_SYMMETRY = arrayOf(
	intArrayOf(0, 1, 2, 3, 3, 2, 1, 0),
	intArrayOf(1, 4, 5, 6, 6, 5, 4, 1),
	intArrayOf(2, 5, 7, 8, 8, 7, 5, 2),
	intArrayOf(3, 6, 8, 9, 9, 8, 6, 3),
	intArrayOf(3, 6, 8, 9, 9, 8, 6, 3),
	intArrayOf(2, 5, 7, 8, 8, 7, 5, 2),
	intArrayOf(1, 4, 5, 6, 6, 5, 4, 1),
	intArrayOf(0, 1, 2, 3, 3, 2, 1, 0)
)

/**
 * Counts how many times each square index is encountered in
 * [GRID_SYMMETRY] / 4.
 * 
 * Note: a constant 4 has been factored out.
 * */
private val GRID_WEIGHTS_COEFFS = intArrayOf(1, 2, 2, 2, 1, 2, 2, 1, 2, 1)

/**
 * Computes the dot product of two arrays
 * 
 * @receiver the first array
 * @param other the second array
 * @return the dot product of the receiver and the other array
 * */
private infix fun IntArray.dotp(other: IntArray): Int {
	var sum = 0
	for (i in this.indices) {
		sum += this[i] * other[i]
	}
	return sum
}

/**
 * Applies the weights (array of 10 values) to the board
 * 
 * @param state the [OthelloState]
 * @param weights an array of 10 weight values
 * @return The evaluation of the board with the weights
 * */
fun applyHeuristicWeights(state: OthelloState, weights: IntArray): Double {
	var boardSum = 0
	
	for (r in 0 until 8) {
		for (c in 0 until 8) {
			boardSum += state.board[r][c] * weights[GRID_SYMMETRY[r][c]]
		}
	}
	
	return boardSum.toDouble() / (4 * (GRID_WEIGHTS_COEFFS dotp weights)).toDouble()
}
