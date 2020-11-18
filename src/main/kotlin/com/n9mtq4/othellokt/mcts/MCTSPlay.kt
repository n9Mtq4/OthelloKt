package com.n9mtq4.othellokt.mcts

import clojure.lang.IFn
import com.n9mtq4.othellokt.OthelloState
import com.n9mtq4.othellokt.abBestMove

/**
 * Created by will on 11/17/20 at 3:46 AM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

/**
 * Performs [iterations] iterations of MCTS on [state] to evaluate
 * the current position of the board.
 * 
 * @param state the othello state
 * @param iterations the number of mcts iterations
 * @return a double of the root node's w/n
 * */
fun mctsEval(state: OthelloState, iterations: Int): Double {
	
	val root = MCTSNode(null, state, null, state.current)
	val search = MCTS(state.current, root)
	
	for (i in 0 until iterations) search.mcts()
	
	return root.w / root.n
	
}

/**
 * Plays an othello push gp heuristic against a MCTS.
 * 
 * @param abHeuristic the heuristic for ab
 * @param abPlayer the player for the AB search 1 or -1
 * @param depth the depth of the AB search
 * @param mctsIterations the number of MCTS iterations
 * @return the final board position
 * */
fun mctsPlayGame(abHeuristic: IFn, abPlayer: Int, depth: Int, mctsIterations: Int): OthelloState {
	
	var board = OthelloState()
	var mctsTree = MCTSNode(null, board, null, 1)
	
	while (!board.gameOver()) {
		
		val move = if (board.current == abPlayer) {
			// perform AB search with push heuristic
			abBestMove(abHeuristic, board, depth)
		} else {
			// perform MCTS on the board
			val mctsSearch = MCTS(board.current, mctsTree)
			for (i in 0 until mctsIterations) mctsSearch.mcts()
			mctsTree.bestChild().appliedMove!!
		}
		
		board = board.applyMove(move)
		
		// update the mcts tree
		mctsTree = mctsTree
			.children
			.firstOrNull { it.state == board }
			?: MCTSNode(null, board, null, board.current)
		
	}
	
	return board
	
}