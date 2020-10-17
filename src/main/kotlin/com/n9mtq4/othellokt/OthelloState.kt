package com.n9mtq4.othellokt

import kotlin.math.sign

/**
 * Created by will on 10/17/20 at 4:50 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
private val DIRECTIONS = arrayOf(-1, 0, 1)

fun readablePlayer(player: Int) = when(player) {
	-1 -> "white"
	0 -> "draw"
	1 -> "black"
	else -> "ERROR"
}

class OthelloState(
	var current: Int = 1,
	var moveNumber: Int = 0,
	var board: Array<IntArray> = Array(8) { intArrayOf(0, 0, 0, 0, 0, 0, 0, 0) },
	generateInitial: Boolean = true) {
	
	init {
		if (generateInitial) {
			board[3][3] = -1
			board[4][4] = -1
			board[3][4] = 1
			board[4][3] = 1
		}
	}
	
	fun gameOver(): Boolean {
		return availableMoves().isEmpty()
	}
	
	fun winner(): Int {
		assert(gameOver())
		return evaluation().sign
	}
	
	fun copy(): OthelloState {
		val newBoard = Array(8) { board[it].copyOf() }
		return OthelloState(current, moveNumber, newBoard, false)
	}
	
	fun applyMove(move: OthelloMove): OthelloState {
		
		val newState = copy()
		val (r, c) = move
		assert(r >= 0 && c >= 0 && r < 8 && c < 8 && move.player == current)
		assert(board[r][c] == 0)
		
		for (dr in DIRECTIONS) {
			for (dc in DIRECTIONS) {
				
				if (dr == 0 && dc == 0) continue
				
				if (flanking(r + dr, c + dc, dr, dc, -current, current)) {
					newState.flip(r + dr, c + dc, dr, dc, -current)
				}
				
			}
		}
		
		newState.board[r][c] = move.player
		newState.current = -current
		
		// if no legal moves, switch back to other player
		if (newState.availableMoves().isEmpty()) {
			newState.current = move.player
		}
		
		newState.moveNumber++
		return newState
		
	}
	
	tailrec fun flip(r: Int, c: Int, dr: Int, dc: Int, color: Int) {
		if (r < 0 || c < 0 || r >= 8 || c >= 8 || board[r][c] != color)
			return
		board[r][c] *= -1
		flip(r + dr, c + dc, dr, dc, color)
	}
	
	fun availableMoves(): List<OthelloMove> {
		
		val moves = mutableListOf<OthelloMove>()
		
		for (r in 0 until 8) {
			for (c in 0 until 8) {
				
				// can only play in empty square
				if (board[r][c] != 0)
					continue
				
				rowLoop@for (dr in DIRECTIONS) {
					for (dc in DIRECTIONS) {
						
						if (dr == 0 && dc == 0)
							continue
						
						if (flanking(r + dr, c + dc, dr, dc, -current, current)) {
							moves.add(OthelloMove(r, c, current))
							break@rowLoop
						}
						
					}
				}
				
			}
		}
		
		return moves
		
	}
	
	fun evaluation(): Int {
		return board.sumBy { it.sum() }
	}
	
	fun count(color: Int): Int {
		return board.map { row -> row.count { it == color } }.sum()
	}
	
	fun flanking(r: Int, c: Int, dr: Int, dc: Int, rowColor: Int, endColor: Int): Boolean {
		return r >= 0 && c >= 0 && r < 8 && c < 8 &&
				board[r][c] == rowColor &&
				flankingHelp(r + dr, c + dc, dr, dc, rowColor, endColor)
	}
	
	tailrec fun flankingHelp(r: Int, c: Int, dr: Int, dc: Int, rowColor: Int, endColor: Int): Boolean {
		if (r < 0 || r >= 8 || c < 0 || c >= 8 || board[r][c] == 0)
			return false
		if (board[r][c] == endColor)
			return true
		return flankingHelp(r + dr, c + dc, dr, dc, rowColor, endColor)
	}
	
	override fun toString(): String {
		var result = "    0   1   2   3   4   5   6   7  \n"
		result += "  +---+---+---+---+---+---+---+---+\n"
		for (r in 0 until 8) {
			result += "$r |"
			for (c in 0 until 8) {
				val player = board[r][c]
				val rep = when (player) {
					0 -> " "
					1 -> "X"
					-1 -> "O"
					else -> "E"
				}
				result += " $rep |"
			}
			result += "\n"
			result += "  +---+---+---+---+---+---+---+---+\n"
		}
		result += "============== STATUS ==============\n"
		result += "current player: ${readablePlayer(current)}\n"
		result += "black (X): ${count(1)}\nwhite (O): ${count(-1)}"
		return result
	}
	
	override fun equals(other: Any?): Boolean {
		if (other == null) return false
		if (other !is OthelloState) return false
		
		return board.contentDeepEquals(other.board)
		
	}
	
}
