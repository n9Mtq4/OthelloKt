package com.n9mtq4.othellokt

import clojure.lang.*

/**
 * Created by will on 10/17/20 at 4:51 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

private val rowKw = RT.keyword(null, "row")
private val colKw = RT.keyword(null, "col")
private val playerKw = RT.keyword(null, "player")

/**
 * An Othello Move
 * 
 * @param r the row
 * @param c the column
 * @param player the player making the move
 * */
data class OthelloMove(
	val r: Int,
	val c: Int,
	val player: Int
) : IRecord, IPersistentMap, ILookup, IObj {
	
	/**
	 * Converts to a human readable string
	 * "white placing at (3, 5)"
	 * 
	 * @return the human readable string
	 * */
	fun humanString(): String {
		return "${readablePlayer(player)} placing at ($r, $c)"
	}
	
	/**
	 * Converts to a tuple notation of (r, c)
	 * "(3, 5)"
	 * 
	 * @return a string of (row, col)
	 * */
	override fun toString(): String {
		return "($r, $c)"
	}
	
	fun pgetnull(key: Any?): Any? {
		if (key !is Keyword) return null
		return when(key) {
			rowKw -> r
			colKw -> c
			playerKw -> player
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
			MapEntry.create(rowKw, r),
			MapEntry.create(colKw, c),
			MapEntry.create(playerKw, player)
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
		return (key == rowKw) || (key == colKw) || (key == playerKw)
	}
	
	override fun iterator(): MutableIterator<Any?> {
		return mutableListOf(
			MapEntry.create(rowKw, r),
			MapEntry.create(colKw, c),
			MapEntry.create(playerKw, player)
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
