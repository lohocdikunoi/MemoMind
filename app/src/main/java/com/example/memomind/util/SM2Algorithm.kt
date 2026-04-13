package com.example.memomind.util

import com.example.memomind.data.local.entity.CardEntity
import kotlin.math.roundToInt

data class SM2Result(
    val repetitions: Int,
    val easeFactor: Double,
    val interval: Int,
    val nextReviewDate: Long,
)

object SM2Algorithm {
    fun calculate(quality: Int, repetitions: Int, easeFactor: Double, interval: Int): SM2Result {
        require(quality in 0..5) { "Quality must be between 0 and 5" }

        var newRepetitions = repetitions
        var newInterval = interval
        var newEaseFactor = easeFactor

        if (quality >= 3) {
            newInterval = when (newRepetitions) {
                0 -> 1
                1 -> 6
                else -> (newInterval * newEaseFactor).roundToInt()
            }
            newRepetitions++
        } else {
            newRepetitions = 0
            newInterval = 1
        }

        newEaseFactor += (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
        if (newEaseFactor < 1.3) newEaseFactor = 1.3

        val nextReviewDate = System.currentTimeMillis() + newInterval * 24L * 60 * 60 * 1000

        return SM2Result(
            repetitions = newRepetitions,
            easeFactor = (newEaseFactor * 100).roundToInt() / 100.0,
            interval = newInterval,
            nextReviewDate = nextReviewDate,
        )
    }

    fun applyToCard(card: CardEntity, quality: Int): CardEntity {
        val result = calculate(quality, card.repetitions, card.easeFactor, card.interval)
        return card.copy(
            repetitions = result.repetitions,
            easeFactor = result.easeFactor,
            interval = result.interval,
            nextReviewDate = result.nextReviewDate,
            lastReviewDate = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
    }
}