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

data class OthelloMove(
	val r: Int, val c: Int, val player: Int
) : IPersistentMap, ILookup, IKeywordLookup, IObj {
	
	fun humanString(): String {
		return "${readablePlayer(player)} placing at ($r, $c)"
	}
	
	override fun toString(): String {
		return "($r, $c)"
	}
	
	fun pgetnull(key: Any?): Object? {
		if (key !is Keyword) return null
		return when(key) {
			rowKw -> r as Object
			colKw -> c as Object
			playerKw -> player as Object
			else -> return null
		}
	}
	
	fun pgetthrow(key: Any?): Object? {
		return pgetnull(key) ?: throw NoSuchElementException("No key $key in OthelloState")
	}
	
	override fun meta(): IPersistentMap? {
		return null
	}
	
	override fun withMeta(meta: IPersistentMap?): IObj {
		throw UnsupportedOperationException()
	}
	
	override fun getLookupThunk(k: Keyword): ILookupThunk {
		return OLookupThunk(k, pgetnull(k))
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
