package com.n9mtq4.othellokt.mcts

import com.n9mtq4.othellokt.OthelloMove
import com.n9mtq4.othellokt.OthelloState
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Created by will on 11/17/20 at 3:36 AM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

/**
 * MCTS Node
 * */
class MCTSNode(
	val parent: MCTSNode?,
	val state: OthelloState,
	val appliedMove: OthelloMove?,
	val player: Int) {
	
	var w = 0.0
	var n = 0.0
	var children = emptyList<MCTSNode>()
	val availableMoves = state.availableMoves()
	var visited = false
	
	val terminal: Boolean
		get() = state.gameOver()
	
	val fullyExpanded: Boolean
		get() {
			if (terminal) return false
			return visited
		}
	
	fun bestChild() = children.maxByOrNull { if (it.n == 0.0) 0.0 else it.w / it.n }!!
	
	fun ucb(playMax: Boolean): Double {
		
		val c = 1.42
		
		if (n == 0.0)
			return 10000.0
		if (parent == null)
			return 1.0
		
//		val maxcoef = if (playMax) 1 else -1
		val q = if (playMax) w / n else 1.0 - (w / n)
		return q + (c * sqrt(ln(parent.n) / n))
		
	}
	
	fun visit() {
		if (visited) return
		children = availableMoves.map { MCTSNode(this, state.applyMove(it), it, -player) }
		visited = true
	}
	
}

class MCTS(val color: Int, val root: MCTSNode) {
	
	/**
	 * @return the move number of the leaf expanded
	 * */
	fun mcts(): Int {
		val leaf = traverse(root)
		leaf.visit()
		for (child in leaf.children) {
			val result = rollout(child)
			backpropagate(child, result, 1.0)
		}
		val result = rollout(leaf)
		backpropagate(leaf, result, 1.0)
		return leaf.state.moveNumber
	}
	
	private fun traverse(node: MCTSNode): MCTSNode {
		var current = node
		while (current.fullyExpanded) {
			
			val currentPlayer = current.player
			val playMax = currentPlayer == color
			
			// no children, so a leaf of the expanded tree
			if (current.children.isEmpty()) return current
			
			// get child with max UCB
			var bestUcb = Double.NEGATIVE_INFINITY
			for (child in current.children) {
				val ucb = child.ucb(playMax)
				if (ucb > bestUcb) {
					bestUcb = ucb
					current = child
				}
			}
			
		}
		return current
	}
	
	private fun rollout(node: MCTSNode): Double {
		var currentState = node.state
		while (!currentState.gameOver()) {
			currentState = currentState.applyMove(currentState.availableMoves().random())
		}
		return when (currentState.winner()) {
			color -> 1.0
			0 -> 0.5
			else -> 0.0
		}
	}
	
	private tailrec fun backpropagate(node: MCTSNode?, dw: Double, dn: Double) {
		if (node == null)
			return
		node.w += dw
		node.n += dn
		backpropagate(node.parent, dw, dn)
	}
	
}
