package org.andreych.tpe_champions.estimation.storage

import com.github.benmanes.caffeine.cache.Caffeine
import org.andreych.tpe_champions.estimation.ChampionsEstimator
import org.andreych.tpe_champions.estimation.ChampionsEstimatorImpl
import org.andreych.tpe_champions.estimation.domain.PartPointsEstimation
import org.andreych.tpe_champions.estimation.domain.Performer
import org.andreych.tpe_champions.estimation.domain.Task
import org.andreych.tpe_champions.estimation.exceptions.NoTaskException

interface TaskStorage
{
    fun modifyTask(id: String, remapping: (id: String, inn: Task) -> Task): Task
    fun removeTask(id: String)
    fun getTask(id: String): Task?
    fun getAllTasks(): Collection<Task>
    fun addTask(id: String, task: Task): Task
    fun addPerformer(name: String, taskId: String): Performer
    fun modifyPerformer(name: String, taskId: String, performerId: String): Performer
    fun removePerformer(taskId: String, performerId: String)
    fun modifyEstimation(taskId: String,
                         performerId: String,
                         estimationId: String,
                         partName: String,
                         best: Double,
                         worst: Double,
                         most: Double,
                         champion: Boolean)

    fun addEstimation(taskId: String,
                      performerId: String,
                      partName: String,
                      best: Double,
                      worst: Double,
                      most: Double,
                      champion: Boolean)

    fun removeEstimation(taskId: String, performerId: String, estimationId: String)
}

/**
 * Stores calculated tasks by their taskId.
 * Created by oleg on 21.06.16.
 */
object TaskStorageImpl : TaskStorage
{
    private val storage = Caffeine.newBuilder().maximumSize(10000).build<String, Task>()
    private val estimator: ChampionsEstimator = ChampionsEstimatorImpl

    override fun getAllTasks(): Collection<Task>
    {
        return storage.asMap().values
    }

    override fun removeTask(id: String)
    {
        storage.invalidate(id)
    }

    override fun getTask(id: String): Task
    {
        return storage.getIfPresent(id) ?: throw NoTaskException("There's no task with $id identifier.")
    }

    override fun modifyTask(id: String, remapping: (id: String, inn: Task) -> Task): Task
    {
        return storage.asMap().computeIfPresent(id,
                { id, task ->
                    estimator.estimate(remapping(id,
                            task))
                }) ?: throw NoTaskException("There's no task with $id identifier.")
    }

    override fun addTask(id: String, task: Task): Task
    {
        val estimatedTask = estimator.estimate(task)
        storage.put(id, estimatedTask)
        return estimatedTask
    }

    override fun addPerformer(name: String, taskId: String): Performer
    {
        val performer = Performer(name = name)
        modifyTask(taskId, { id, task ->
            val newTeam = task.team.plus(performer.id to performer)
            task.copy(team = newTeam)
        })
        return performer
    }

    override fun modifyPerformer(name: String, taskId: String, performerId: String): Performer
    {
        val task = modifyTask(taskId, { id, task ->
            val performer = task.getPerformer(performerId)
            val newTeam = task.team.plus(performer.id to performer.copy(name = name))
            task.copy(team = newTeam)
        })
        return task.getPerformer(performerId)
    }

    override fun removePerformer(taskId: String, performerId: String)
    {
        modifyTask(taskId, { id, task ->
            val newTeam = task.team.filter { !it.key.equals(performerId) }
            task.copy(team = newTeam)
        })
    }

    override fun modifyEstimation(taskId: String,
                                  performerId: String,
                                  estimationId: String,
                                  partName: String,
                                  best: Double,
                                  worst: Double,
                                  most: Double,
                                  champion: Boolean)
    {
        modifyTask(taskId, { id, task ->
            val performer = task.getPerformer(performerId)
            val estimation = performer.getEstimation(estimationId)
            val modifiedEst = estimation.copy(partName = partName,
                    best = best,
                    worst = worst,
                    mostLikely = most,
                    isChampion = champion)
            val modifiedPerf = performer.copy(estimations = performer.estimations.plus(modifiedEst.id to modifiedEst))
            task.copy(team = task.team.plus(modifiedPerf.id to modifiedPerf))
        })
    }

    override fun addEstimation(taskId: String,
                               performerId: String,
                               partName: String,
                               best: Double,
                               worst: Double,
                               most: Double,
                               champion: Boolean)
    {
        modifyTask(taskId, { id, task ->
            val performer = task.getPerformer(performerId)
            val newEst = PartPointsEstimation(partName = partName,
                    worst = worst,
                    isChampion = champion,
                    mostLikely = most,
                    best = best)
            val newEstimations = performer.estimations.plus(newEst.id to newEst)
            val modifiedPerf = performer.copy(estimations = newEstimations)
            task.copy(team = task.team.plus(modifiedPerf.id to modifiedPerf))
        })
    }

    override fun removeEstimation(taskId: String, performerId: String, estimationId: String)
    {
        modifyTask(taskId, { id, task ->
            val performer = task.getPerformer(performerId)
            val filteredEstimations = performer.estimations.filter { it.key != estimationId }
            val modifiedPerf = performer.copy(estimations = filteredEstimations)
            val modifiedTeam = task.team.plus(modifiedPerf.id to modifiedPerf)
            task.copy(team = modifiedTeam)
        })
    }
}