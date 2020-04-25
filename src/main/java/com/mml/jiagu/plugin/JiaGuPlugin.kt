package com.mml.jiagu.plugin


import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


public class JiaGuPlugin : Plugin<Project> {
    companion object {
        val GROUP = "JiaGu"
        val EXTENSION_NAME = "jiagu"
    }

    private val taskMap = mutableMapOf<String,String>()
    override fun apply(project: Project) {
        val jiaGuPluginExtension = project.extensions
            .create(EXTENSION_NAME, JiaGuPluginExtension::class.java)
        val isResguard = project.plugins.hasPlugin("AndResGuard")
        project.afterEvaluate { project ->
            println("afterEvaluate ${jiaGuPluginExtension.jiaGuToolsPath}")
            val appExtension = project.extensions.getByType(AppExtension::class.java)
            appExtension.applicationVariants.all { applicationVariant ->
                val flavorName= applicationVariant.flavorName
                applicationVariant.outputs.all {
                    val outputFile = it.outputFile
                    //println("outputFile:${outputFile.absolutePath}")
                    val jiaGuName = "jiagu-${it.name}"
                    println("create task:$jiaGuName")
                    val jiaGuTask = project.tasks.create(
                        jiaGuName,
                        JiaGuTask::class.java,
                        outputFile,
                        jiaGuPluginExtension
                    )
                    val buildAndJiaGuName = "build-and-$jiaGuName"
                    println("create task:$buildAndJiaGuName")
                    taskMap[buildAndJiaGuName] = "assemble$flavorName"
                    val buildAndJiaGuTask = project.tasks.create(
                        buildAndJiaGuName,
                        JiaGuTask::class.java,
                        outputFile,
                        jiaGuPluginExtension
                    )
                    buildAndJiaGuTask.doFirst {
                        println("${it.name} doFirst")
                    }
                    buildAndJiaGuTask.doLast {
                        println("${it.name} doLast")
                    }
                    buildAndJiaGuTask.dependsOn("assemble$flavorName")

                }

            }
        }
        project.tasks.whenTaskAdded {task ->
            println("whenTaskAdded:${task.name} dependsOn ${task.dependsOn}")
            val get=taskMap[task.name]
            get?.let {
                println("task ${task.name} dependsOn $it")
                task.dependsOn(it)
            }

        }
    }

}
