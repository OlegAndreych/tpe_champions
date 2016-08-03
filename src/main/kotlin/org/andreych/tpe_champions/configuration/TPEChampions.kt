package org.andreych.tpe_champions.configuration

import org.andreych.tpe_champions.estimation.domain.PartPointsEstimation
import org.andreych.tpe_champions.estimation.domain.Performer
import org.andreych.tpe_champions.estimation.domain.Task
import org.andreych.tpe_champions.estimation.storage.TaskStorageImpl
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.annotation.PostConstruct

@Configuration
@ComponentScan
@EnableAutoConfiguration
open class TPEChampions
{
    /**
     * Fills app with sample data.
     */
    @PostConstruct
    fun init()
    {
        val date1 = Date(1)
        val date2 = Date(2)
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val id3 = UUID.randomUUID().toString()
        val tasks = listOf(

                Task(id = UUID.randomUUID().toString(),
                        name = "Task",
                        date = date2,
                        team = mutableMapOf(id1 to Performer(id1,
                                "perfer",
                                estimations = mutableMapOf("123" to
                                        PartPointsEstimation(id = "123", partName = "some work",
                                                best = 1.0,
                                                worst = 3.0,
                                                mostLikely = 2.0,
                                                isChampion = true))))),
                Task(id = UUID.randomUUID().toString(),
                        name = "Task2",
                        date = date1,
                        team = mutableMapOf(id2 to Performer(id2,
                                "perfer",
                                estimations = mutableMapOf("321" to PartPointsEstimation(id = "321",
                                        partName = "some work",
                                        best = 1.0,
                                        worst = 3.0,
                                        mostLikely = 2.0,
                                        isChampion = true))), id3 to
                                Performer(id3,
                                        "perfur",
                                        estimations = mutableMapOf("213" to PartPointsEstimation(id = "213",
                                                partName = "some work",
                                                best = 1.0,
                                                worst = 3.0,
                                                mostLikely = 2.0,
                                                isChampion = true))))
                )
        )
        tasks.forEach { task ->
            TaskStorageImpl.addTask(task.id, task)
        }
    }
}

fun main(args: Array<String>)
{
    SpringApplicationBuilder().sources(TPEChampions::class.java).run(*args)
}