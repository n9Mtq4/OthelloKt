package com.n9mtq4.othellokt.elo

import java.lang.IllegalArgumentException
import kotlin.math.pow

/**
 * Created by will on 11/18/20 at 10:31 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

const val K: Int = 2

fun updateElo(black: Player, white: Player, gameResult: Int) {
	
	black.updateElo(white.elo, gameResult)
	white.updateElo(black.elo, -gameResult)
	
}

data class Player(val name: String) {
	
	var score: Double = 0.0
	var elo: Double = 1500.0
	var totalGames: Int = 0
	
	fun updateElo(rb: Double, gameResult: Int) {
		
		score += when (gameResult) {
			-1 -> rb - 400
			0 -> rb
			1 -> rb + 400
			else -> throw IllegalArgumentException("Invalid game result $gameResult")
		}
		
		totalGames++
		elo = score / totalGames
		
	}
	
}
