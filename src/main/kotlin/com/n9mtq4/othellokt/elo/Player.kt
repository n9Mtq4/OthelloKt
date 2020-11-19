package com.n9mtq4.othellokt.elo

import java.lang.IllegalArgumentException
import kotlin.math.pow

/**
 * Created by will on 11/18/20 at 10:31 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

const val K: Int = 32

fun updateElo(black: Player, white: Player, gameResult: Int) {
	
	black.updateElo(white.elo, gameResult)
	white.updateElo(black.elo, -gameResult)
	
}

data class Player(val name: String) {
	
	var elo: Double = 1500.0
	var totalGames: Int = 0
	
	fun updateElo(rb: Double, gameResult: Int) {
		
		val ra = elo
		
		val expected = 10.0.pow((rb - ra) / 400.0)
		
		val actual = when (gameResult) {
			-1 -> 0.0
			0 -> 0.5
			1 -> 1.0
			else -> throw IllegalArgumentException("Invalid game result $gameResult")
		}
		
		val newRating = ra + K * (actual - expected)
		
		totalGames++
		elo = newRating
		
	}
	
}
