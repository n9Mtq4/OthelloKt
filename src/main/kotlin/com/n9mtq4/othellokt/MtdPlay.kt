package com.n9mtq4.othellokt

import clojure.lang.IFn

/**
 * Created by will on 11/15/20 at 11:46 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

/**
 * Gets the best possible move for a given board using alpha beta
 *
 * @param heuristic the heuristic to pass to alpha beta
 * @param state the board position to evaluate
 * @param depth the depth to run alpha beta
 * @return the best move determined by alpha beta using [heuristic]
 * */
fun mtdBestMove(heuristic: IFn, state: OthelloState, depth: Int, f: Double = 0.0): Pair<Double, OthelloMove> {
	
	// set up moves and player
	val availableMoves = state.availableMoves()
	val rev = state.current == 1
	
	// find the best move
	var bestMove = availableMoves[0]
	var bestMoveScore = Double.NEGATIVE_INFINITY
	
	for (move in availableMoves) {
		
		val score = state.current * mtdf(heuristic, state.applyMove(move), depth - 1, !rev, f)
		
		if (score > bestMoveScore) {
			bestMoveScore = score
			bestMove = move
		}
		
	}
	
	return bestMoveScore to bestMove
	
}

/**
 * Plays a game of Othello against two clojure heuristics. Uses
 * alpha beta with a depth of [depth]
 *
 * @param black the black clojure heuristic
 * @param white the white clojure heuristic
 * @param depth the alpha beta depth
 * @return the winner of the game
 * */
fun mtdPlayGame(black: IFn, white: IFn, depth: Int): Int {
	
	var board = OthelloState()
	
	// store previous board evaluations to improve the speed of the next evaluation
	var blackGuess = 0.0
	var whiteGuess = 0.0
	
	while (!board.gameOver()) {
		
		if (board.current == 1) {
			val (f, move) = mtdBestMove(black, board, depth, blackGuess)
			blackGuess = f
			board = board.applyMove(move)
		} else {
			val (f, move) = mtdBestMove(white, board, depth, whiteGuess)
			whiteGuess = f
			board = board.applyMove(move)
		}
		
	}
	
	return board.winner()
	
}
