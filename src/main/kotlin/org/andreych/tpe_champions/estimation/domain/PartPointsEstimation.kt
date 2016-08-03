package org.andreych.tpe_champions.estimation.domain

import org.andreych.tpe_champions.estimation.exceptions.ValidationEstimationException
import java.util.*

/**
 * Estimation data of a task's part.
 * Created by oleg on 04.06.16.
 */
data class PartPointsEstimation(val id: String = UUID.randomUUID().toString(),
                                val partName: String,
                                val estimation: Double = 0.0,
                                val best: Double,
                                val worst: Double,
                                val mostLikely: Double,
                                val isChampion: Boolean)
{
    init
    {
        when
        {
            best < 0 || worst < 0 || mostLikely < 0 ->
                throw ValidationEstimationException(message = "All estimates should be positive or zero.")
            best > worst                            ->
                throw ValidationEstimationException(message = "Best estimation should be less than worst one.")
            best > mostLikely                       ->
                throw ValidationEstimationException(message = "Best estimation should be less than most likely one.")
            mostLikely > worst                      ->
                throw ValidationEstimationException(message = "Most likely estimation should be less than worst one.")
        }
    }
}
