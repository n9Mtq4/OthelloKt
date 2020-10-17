package com.n9mtq4.othellokt

/**
 * Created by will on 10/17/20 at 4:51 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
data class OthelloMove(val r: Int, val c: Int, val player: Int) {
	
	fun humanString(): String {
		return "${readablePlayer(player)} placing at ($r, $c)"
	}
	
	override fun toString(): String {
		return "($r, $c)"
	}
	
}
