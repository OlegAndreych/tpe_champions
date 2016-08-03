package org.andreych.tpe_champions.estimation.domain

import org.andreych.tpe_champions.estimation.exceptions.NoEstimationException
import org.andreych.tpe_champions.estimation.exceptions.NoPerformerException
import java.util.*

/**
 * Overall task we're trying to estimate.
 */
data class Task(val id: String = UUID.randomUUID().toString(),
                val name: String = "",
                val estimation: Double = 0.0,
                val date: Date = Date(),
                val team: Map<String, Performer> = emptyMap())
{
    fun getPerformer(id: String): Performer
    {
        return team[id] ?: throw  NoPerformerException("There's no performer with $id identifier.")
    }
}

/**
 * One of performers of a task.
 */
data class Performer(val id: String = UUID.randomUUID().toString(),
                     val name: String = "",
                     val estimation: Double = 0.0,
                     val estimations: Map<String, PartPointsEstimation> = emptyMap())
{
    fun getEstimation(id: String): PartPointsEstimation
    {
        return estimations[id] ?: throw  NoEstimationException("There's no estimation with $id identifier.")
    }
}