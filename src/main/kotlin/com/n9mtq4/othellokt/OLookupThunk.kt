package com.n9mtq4.othellokt

import clojure.lang.ILookupThunk
import clojure.lang.IObj
import clojure.lang.IPersistentMap
import clojure.lang.Keyword

/**
 * Created by will on 10/18/20 at 11:14 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class OLookupThunk(private val key: Keyword?, private val elem: Any?) : ILookupThunk, IObj {
	
	override fun get(target: Any?): Any? {
		return elem
	}
	
	override fun meta(): IPersistentMap {
		throw UnsupportedOperationException()
	}
	
	override fun withMeta(meta: IPersistentMap?): IObj {
		throw UnsupportedOperationException()
	}
	
}
