package com.n9mtq4.othellokt

import clojure.lang.*
import kotlin.math.sign

/**
 * Created by will on 10/17/20 at 4:50 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

/**
 * A list of directions that a ray can go in
 * */
private val DIRECTIONS = arrayOf(-1, 0, 1)

/**
 * Converts a player integer to a string
 * 
 * @param player the player (-1, 0, 1)
 * @return the string name of the player (white, draw, black)
 * */
internal fun readablePlayer(player: Int) = when(player) {
	-1 -> "white"
	0 -> "draw"
	1 -> "black"
	else -> "ERROR"
}

private val currentKw = RT.keyword(null, "current")
private val moveNumberKw = RT.keyword(null, "move-number")
private val boardKw = RT.keyword(null, "board")

/**
 * An Othello State
 * 
 * @param current the current player (-1, 0, 1)
 * @param moveNumber the move number
 * @param board the board
 * @param generateInitial should we place the initial 4 discs on the board?
 * */
class OthelloState @JvmOverloads constructor(
	var current: Int = 1,
	var moveNumber: Int = 0,
	var board: Array<IntArray> = Array(8) { intArrayOf(0, 0, 0, 0, 0, 0, 0, 0) },
	generateInitial: Boolean = true
) : IRecord, IPersistentMap, ILookup, IObj {
	
	init {
		if (generateInitial) {
			board[3][3] = -1
			board[4][4] = -1
			board[3][4] = 1
			board[4][3] = 1
		}
	}
	
	/**
	 * Determines if the game is over.
	 * 
	 * @return true if the game is over
	 * */
	fun gameOver(): Boolean {
		return !hasMove()
	}
	
	/**
	 * Gets the winner of this game.
	 * The game must be over ([gameOver]) for this to work
	 * 
	 * @return the player that won (-1, 0, 1)
	 * */
	fun winner(): Int {
		assert(gameOver())
		return evaluation().sign
	}
	
	/**
	 * Applies [move] to this state
	 * Does not modify this state
	 * 
	 * @param move the move
	 * @return a new state with [move] applied
	 * */
	fun applyMove(move: OthelloMove): OthelloState {
		
		val newState = copy()
		val (r, c) = move
		assert(r >= 0 && c >= 0 && r < 8 && c < 8 && move.player == current)
		assert(board[r][c] == 0)
		
		// try every direction
		for (dr in DIRECTIONS) {
			for (dc in DIRECTIONS) {
				
				// skip going in no direction
				if (dr == 0 && dc == 0)
					continue
				
				// if we are flanking in this direction, flip all opponents pieces
				if (flanking(r + dr, c + dc, dr, dc, -current, current)) {
					newState.flip(r + dr, c + dc, dr, dc, -current)
				}
				
			}
		}
		
		newState.board[r][c] = move.player
		newState.current = -current
		
		// if no legal moves, switch back to other player
		if (!newState.hasMove()) {
			newState.current = move.player
		}
		
		newState.moveNumber++
		return newState
		
	}
	
	/**
	 * Determines if there is a move for [current] for this state
	 * Equivalent to `state.availableMoves().isEmpty()`, but faster
	 * if you don't need the list of moves.
	 * 
	 * @return true iff [current] has a move to make
	 * */
	private fun hasMove(): Boolean {
		
		for (r in 0 until 8) {
			for (c in 0 until 8) {
				
				// can only play in empty square
				if (board[r][c] != 0)
					continue
				
				// try every direction
				for (dr in DIRECTIONS) {
					for (dc in DIRECTIONS) {
						
						// skip going in no direction
						if (dr == 0 && dc == 0)
							continue
						
						// we detected a move, early exit
						if (flanking(r + dr, c + dc, dr, dc, -current, current))
							return true
						
					}
				}
				
			}
		}
		
		return false
		
	}
	
	/**
	 * Gets a list of moves that are available for [current] in this state
	 * 
	 * @return a list of moves that can be done
	 * */
	fun availableMoves(): List<OthelloMove> {
		
		val moves = mutableListOf<OthelloMove>()
		
		for (r in 0 until 8) {
			for (c in 0 until 8) {
				
				// can only play in empty square
				if (board[r][c] != 0)
					continue
				
				// try every direction
				rowLoop@for (dr in DIRECTIONS) {
					for (dc in DIRECTIONS) {
						
						// skip going in no direction
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
	
	/**
	 * Evaluates the board.
	 * Black's discs - White's discs
	 * 
	 * @return the board evaluation
	 * */
	fun evaluation(): Int {
		// return board.sumBy { it.sum() } // about 20% slower
		var sum = 0
		for (r in 0 until 8) {
			for (c in 0 until 8) {
				sum += board[r][c]
			}
		}
		return sum
	}
	
	/**
	 * Counts the number of discs belonging to a player on the board
	 * 
	 * @param color the color of the player
	 * @return the number of discs in [color]'s color
	 * */
	fun count(color: Int): Int {
		// return board.map { row -> row.count { it == color } }.sum() // about 20% slower
		var sum = 0
		for (r in 0 until 8) {
			for (c in 0 until 8) {
				if (board[r][c] == color) sum++
			}
		}
		return sum
	}
	
	private tailrec fun flip(r: Int, c: Int, dr: Int, dc: Int, color: Int) {
		if (r < 0 || c < 0 || r >= 8 || c >= 8 || board[r][c] != color)
			return
		board[r][c] *= -1
		flip(r + dr, c + dc, dr, dc, color)
	}
	
	private fun flanking(r: Int, c: Int, dr: Int, dc: Int, rowColor: Int, endColor: Int): Boolean {
		return r >= 0 && c >= 0 && r < 8 && c < 8 &&
				board[r][c] == rowColor &&
				flankingHelp(r + dr, c + dc, dr, dc, rowColor, endColor)
	}
	
	private tailrec fun flankingHelp(r: Int, c: Int, dr: Int, dc: Int, rowColor: Int, endColor: Int): Boolean {
		if (r < 0 || r >= 8 || c < 0 || c >= 8 || board[r][c] == 0)
			return false
		if (board[r][c] == endColor)
			return true
		return flankingHelp(r + dr, c + dc, dr, dc, rowColor, endColor)
	}
	
	/**
	 * Converts this state into a string for nice printing
	 * 
	 * @return a string representation
	 * */
	override fun toString(): String {
		var result = "    0   1   2   3   4   5   6   7  \n"
		result += "  +---+---+---+---+---+---+---+---+\n"
		for (r in 0 until 8) {
			result += "$r |"
			for (c in 0 until 8) {
				val rep = when (board[r][c]) {
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
	
	/**
	 * Deep copies this state
	 * 
	 * @return a copy of this state
	 * */
	fun copy(): OthelloState {
		val newBoard = Array(8) { board[it].copyOf() }
		return OthelloState(current, moveNumber, newBoard, false)
	}
	
	/**
	 * Checks if the the [board] is equal to another board
	 * Ignores [current] and [moveNumber]
	 * 
	 * @param other the other
	 * @return true iff other.board == this.board
	 * */
	override fun equals(other: Any?): Boolean {
		if (other == null) return false
		if (other !is OthelloState) return false
		
		return board.contentDeepEquals(other.board)
	}
	
	override fun hashCode(): Int {
		var result = current
		result = 31 * result + moveNumber
		result = 31 * result + board.contentDeepHashCode()
		return result
	}
	
	fun pgetnull(key: Any?): Any? {
		if (key !is Keyword) return null
		return when(key) {
			currentKw -> current
			moveNumberKw -> moveNumber
			boardKw -> board
			else -> return null
		}
	}
	
	fun pgetthrow(key: Any?): Any? {
		return pgetnull(key) ?: throw NoSuchElementException("No key $key in OthelloState")
	}
	
	override fun meta(): IPersistentMap? {
		return null
	}
	
	override fun withMeta(meta: IPersistentMap?): IObj {
		throw UnsupportedOperationException()
	}
	
	override fun seq(): ISeq {
		return ArraySeq.create(
			MapEntry.create(currentKw, current),
			MapEntry.create(moveNumberKw, moveNumber),
			MapEntry.create(boardKw, board)
		)
	}
	
	override fun count(): Int = 3
	
	override fun cons(o: Any?): IPersistentCollection {
		throw UnsupportedOperationException()
	}
	
	override fun empty(): IPersistentCollection {
		throw UnsupportedOperationException()
	}
	
	override fun equiv(o: Any?): Boolean {
		return equals(o)
	}
	
	override fun valAt(key: Any?): Any? {
		return pgetnull(key)
	}
	
	override fun valAt(key: Any?, notFound: Any?): Any? {
		return pgetnull(key) ?: notFound
	}
	
	override fun containsKey(key: Any?): Boolean {
		if (key !is Keyword) return false
		return (key == currentKw) || (key == moveNumberKw) || (key == boardKw)
	}
	
	override fun iterator(): MutableIterator<Any?> {
		return mutableListOf(
			MapEntry.create(currentKw, current),
			MapEntry.create(moveNumberKw, moveNumber),
			MapEntry.create(boardKw, board)
		).iterator()
	}
	
	override fun entryAt(key: Any?): IMapEntry {
		return MapEntry(key, pgetthrow(key))
	}
	
	override fun assoc(key: Any?, `val`: Any?): IPersistentMap {
		throw UnsupportedOperationException()
	}
	
	override fun assocEx(key: Any?, `val`: Any?): IPersistentMap {
		throw UnsupportedOperationException()
	}
	
	override fun without(key: Any?): IPersistentMap {
		throw UnsupportedOperationException()
	}
	
}
