package com.n9mtq4.othellokt.elo

import clojure.lang.AFn
import clojure.lang.IFn
import com.n9mtq4.othellokt.OthelloState
import com.n9mtq4.othellokt.abBestMove
import com.n9mtq4.othellokt.humanHeuristic
import com.n9mtq4.othellokt.mcts.MCTS
import com.n9mtq4.othellokt.mcts.MCTSNode
import com.n9mtq4.othellokt.mcts.mctsPlayGame

/**
 * Created by will on 11/18/20 at 11:00 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

fun main() {
	
	val abHeuristic = ABHeuristic()
	
	val abPlayer = -1 to Player("AB depth=4")
	
	val mctsPlayers = Array(16) { i -> (500 * i) to Player("MCTS i=${500 * i}") }
	
	val players = mctsPlayers.toList() + listOf(abPlayer)
	
	println("Starting games...")
	
	(0..500).toList().parallelStream().forEach { 
		val (bd, bp) = players.random()
		val (wd, wp) = players.random()
		
		// don't play a player against itself
		if (bd == wd) return@forEach
		
		val finalState: OthelloState = when {
			bd == -1 -> mctsPlayGame(abHeuristic, 1, 4, wd)
			wd == -1 -> mctsPlayGame(abHeuristic, -1, 4, bd)
			else -> mcts2PlayGame(bd, wd)
		}
		
		val gameResult = finalState.winner()
		println("Result of $bd and $wd is $gameResult")
		
		updateElo(bp, wp, gameResult)
		
	}
	
	players.forEach { println(it) }
	
}

fun mcts2PlayGame(blackMctsIterations: Int, whiteMctsIterations: Int): OthelloState {
	
	var board = OthelloState()
	var blackTree = MCTSNode(null, board, null, 1)
	var whiteTree = MCTSNode(null, board, null, 1)
	
	while (!board.gameOver()) {
		
		val move = if (board.current == 1) {
			val mctsSearch = MCTS(board.current, blackTree)
			for (i in 0 until blackMctsIterations) mctsSearch.mcts()
			blackTree.bestChild().appliedMove!!
		} else {
			val mctsSearch = MCTS(board.current, whiteTree)
			for (i in 0 until whiteMctsIterations) mctsSearch.mcts()
			whiteTree.bestChild().appliedMove!!
		}
		
		board = board.applyMove(move)
		
		// update the mcts tree
		blackTree = blackTree
			.children
			.firstOrNull { it.state == board }
			?: MCTSNode(null, board, null, board.current)
		whiteTree = whiteTree
			.children
			.firstOrNull { it.state == board }
			?: MCTSNode(null, board, null, board.current)
		
	}
	
	return board
	
}


class ABHeuristic : AFn() {
	
	override fun invoke(state: Any): Double {
		return humanHeuristic(state as OthelloState)
	}
	
}
