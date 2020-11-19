package com.n9mtq4.othellokt.elo

import clojure.lang.AFn
import clojure.lang.IFn
import com.n9mtq4.othellokt.OthelloState
import com.n9mtq4.othellokt.abBestMove
import com.n9mtq4.othellokt.humanHeuristic
import com.n9mtq4.othellokt.mcts.MCTS
import com.n9mtq4.othellokt.mcts.MCTSNode
import com.n9mtq4.othellokt.mcts.mctsPlayGame
import java.io.File

/**
 * Created by will on 11/18/20 at 11:00 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

fun main() {
	
	avgEloFromFile()
	if (true) return
	
	val abHeuristic = ABHeuristic()
	
	val abPlayer = -1 to Player("AB depth=4")
	
	val itersSpread = 200
	val mctsPlayers = Array(12) { i ->
		(itersSpread * (i + 1)) to Player("MCTS i=${itersSpread * (i + 1)}")
	}
	
	val players = mctsPlayers.toList() + listOf(abPlayer)
	
	println("Starting games...")
	
	val fileOut = File("/tmp/output.txt").printWriter()
	
	(0..20000).toList().parallelStream().forEach { 
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
		val outtxt = "$bd, $wd, $gameResult"
		println(outtxt)
		fileOut.println(outtxt)
		fileOut.flush()
		
		updateElo(bp, wp, gameResult)
		
	}
	
	players.forEach { (d, p) ->
		println("$d\t${p.elo}")
	}
	
}

fun avgEloFromFile() {
	
	val lines = File("games.txt").readLines()
	val ratingsMps = Array(10000) { eloFromFile(lines + lines) }
	val ratings = HashMap<String, Double>()
	
	ratingsMps[0].keys.forEach { key ->
		val s = ratingsMps.map { it[key]!! }.sumByDouble { it.elo }
		ratings[key] = s / ratingsMps.size
	}
	
	ratings
		.toList()
		.sortedBy { it.first.toInt() }
		.forEach { (name, elo) -> 
			println("$name\t$elo")
		}
	
}

fun eloFromFile(lines: List<String>): HashMap<String, Player> {
	
	val ratings = HashMap<String, Player>()
	
	lines
		.filterNot { it.isEmpty() }
		.map { it.split(", ") }
		.shuffled()
		.forEach { (b, w, r) ->
			
			val black = ratings.getOrPut(b) { Player(b) }
			val white = ratings.getOrPut(w) { Player(w) }
			
			updateElo(black, white, r.toInt())
			
		}
	
//	ratings
//		.toList()
//		.sortedBy { it.first.toInt() }
//		.forEach { (name, player) -> 
//			println("$name\t${player.elo}")
//		}
	
	return ratings
	
//	ratings.forEach { name, player -> 
//		println("$name\t${player.elo}")
//	}
	
//	println("done")
	
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
