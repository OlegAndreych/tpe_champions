package org.andreych.tpe_champions.estimation

import org.andreych.tpe_champions.estimation.domain.PartPointsEstimation
import org.andreych.tpe_champions.estimation.domain.Performer
import org.andreych.tpe_champions.estimation.domain.Task

interface ChampionsEstimator
{
    fun estimate(task: Task): Task
}

/**
 * Calculates "Champions estimation" for a task.
 * Created by oleg on 04.06.16.
 */
object ChampionsEstimatorImpl : ChampionsEstimator
{
    /**
     * Let's calculate estimation numbers for a single performer.
     */
    private fun calculatePerformersEstimation(performer: Performer): Performer
    {
        val calculatedEstimations = performer.estimations.values.map { it.id to calculateEstimationsEstimation(it) }.toMap()
        val champs = calculatedEstimations.values.filter { it.isChampion }.map { it.estimation }
        val perfEstimation = scopeChampionsEstimation(calculatedEstimations.size, champs)
        return performer.copy(estimation = perfEstimation, estimations = calculatedEstimations)
    }

    /**
     * Let's three-point-estimate [PartPointsEstimation].
     */
    private fun calculateEstimationsEstimation(it: PartPointsEstimation): PartPointsEstimation
            = it.copy(estimation = threePointEstimate(it.best, it.worst, it.mostLikely))

    /**
     * Let's estimate numbers for a [Task].
     */
    override fun estimate(task: Task): Task
    {
        val calculatedTeam = task.team.map { calculatePerformersEstimation(it.value) }
        val taskEstimation = calculatedTeam.map { it.estimation }.average()
        return task.copy(estimation = taskEstimation, team = calculatedTeam.map { it.id to it }.toMap())
    }

    /**
     * Usual three point estimation function for a [PartPointsEstimation].
     */
    private fun threePointEstimate(best: Double, worst: Double, mostLikely: Double): Double
            = (best + 4 * mostLikely + worst) / 6

    /**
     * Estimation for toughest ones we have.
     */
    private fun scopeChampionsEstimation(totalParts: Int, championEstimates: Collection<Double>): Double
    {
        when (championEstimates.size)
        {
            0    -> return 0.0
            else -> return 0.54 * (totalParts / championEstimates.size) * championEstimates.sum()
        }
    }
}