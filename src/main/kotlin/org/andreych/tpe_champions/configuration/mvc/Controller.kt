package org.andreych.tpe_champions.configuration.mvc

import org.andreych.tpe_champions.estimation.domain.Task
import org.andreych.tpe_champions.estimation.storage.TaskStorageImpl
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class Controller
{
    val taskStorage = TaskStorageImpl

    @RequestMapping("/")
    fun showTaskList(model: Model): String
    {
        fillTaskRows(model)
        return "taskList"
    }

    private fun fillTaskRows(model: Model)
    {
        model.addAttribute("tasks", taskStorage.getAllTasks().sortedByDescending { it.date })
    }

    @RequestMapping("/task/{id}/remove")
    fun removeTask(@PathVariable id: String): String
    {
        taskStorage.removeTask(id)
        return "redirect:/"
    }

    @RequestMapping("/task/{id}/edit")
    fun editTask(model: Model, @PathVariable id: String): String
    {
        if (id != "none")
        {
            with(taskStorage.getTask(id))
            {
                model.addAttribute("name", name)
            }
            model.addAttribute("taskId", id)
        }
        return "taskEdit"
    }

    @RequestMapping("/task/{taskId}/submit")
    fun submitTask(@RequestParam name: String, @PathVariable taskId: String): String
    {
        val task = when (taskId)
        {
            "none" -> Task(name = name).apply { taskStorage.addTask(id, this) }
            else   -> taskStorage.modifyTask(taskId) { _, storedTask ->
                storedTask.copy(name = name)
            }
        }
        return "redirect:/task/${task.id}"
    }

    @RequestMapping("/task/{taskId}")
    fun showTask(model: Model, @PathVariable taskId: String): String
    {
        model.addAttribute("task", taskStorage.getTask(taskId))
        return "task"
    }

    @RequestMapping("/task/{taskId}/performer/{performerId}/edit")
    fun editPerformer(model: Model, @PathVariable taskId: String, @PathVariable performerId: String): String
    {
        if (performerId != "none")
        {
            taskStorage.getTask(taskId).getPerformer(performerId).apply {
                model.addAttribute("name", name)
                model.addAttribute("performerId", id)
            }
        }
        model.addAttribute("taskId", taskId)
        return "performerEdit"
    }

    @RequestMapping("/task/{taskId}/performer/{performerId}/submit")
    fun submitPerformer(@RequestParam name: String,
                        @PathVariable performerId: String,
                        @PathVariable taskId: String): String
    {
        val performer = when (performerId)
        {
            "none" -> taskStorage.addPerformer(name, taskId)
            else   -> taskStorage.modifyPerformer(name, taskId, performerId)
        }
        return "redirect:/task/$taskId/performer/${performer.id}"
    }

    @RequestMapping("/task/{taskId}/performer/{performerId}")
    fun showPerformer(model: Model, @PathVariable taskId: String, @PathVariable performerId: String): String
    {
        model.addAttribute("performer", taskStorage.getTask(taskId).getPerformer(performerId))
        model.addAttribute("task", taskStorage.getTask(taskId))
        return "performer"
    }

    @RequestMapping("/task/{taskId}/performer/{performerId}/remove")
    fun removePerformer(@PathVariable taskId: String, @PathVariable performerId: String): String
    {
        taskStorage.removePerformer(taskId, performerId)
        return "redirect:/task/$taskId"
    }

    @RequestMapping("/task/{taskId}/performer/{performerId}/estimation/{estimationId}/edit")
    fun editEstimation(model: Model,
                       @PathVariable taskId: String,
                       @PathVariable performerId: String,
                       @PathVariable estimationId: String): String
    {
        String(StringBuilder("a"))
        if (estimationId != "none")
        {
            with(taskStorage.getTask(taskId).getPerformer(performerId).getEstimation(estimationId))
            {
                model.addAttribute("partName", partName)
                model.addAttribute("best", best)
                model.addAttribute("worst", worst)
                model.addAttribute("most", mostLikely)
                model.addAttribute("champion", isChampion)
            }
        }
        return "estimationEdit"
    }

    @RequestMapping("/task/{taskId}/performer/{performerId}/estimation/{estimationId}/submit")
    fun submitEstimation(@PathVariable taskId: String,
                         @PathVariable performerId: String,
                         @PathVariable estimationId: String,
                         @RequestParam partName: String,
                         @RequestParam best: Double,
                         @RequestParam worst: Double,
                         @RequestParam most: Double,
                         @RequestParam(required = false) champion: Boolean): String
    {
        when (estimationId)
        {
            "none" -> taskStorage.addEstimation(taskId, performerId, partName, best, worst, most, champion)
            else   -> taskStorage.modifyEstimation(taskId,
                    performerId,
                    estimationId,
                    partName,
                    best,
                    worst,
                    most,
                    champion)
        }
        return "redirect:/task/$taskId/performer/$performerId"
    }

    @RequestMapping("/task/{taskId}/performer/{performerId}/estimation/{estimationId}/remove")
    fun removeEstimation(@PathVariable taskId: String,
                         @PathVariable performerId: String,
                         @PathVariable estimationId: String): String
    {
        taskStorage.removeEstimation(taskId, performerId, estimationId)
        return "redirect:/task/$taskId/performer/$performerId"
    }
}