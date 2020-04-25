package com.mml.jiagu.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.*
import javax.inject.Inject

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/4/24 18:30
 * Description: This is JiaGuTask
 * Package: com.mml.jiagu.plugin
 * Project: DeskTopPhotoView
 */
//使用kotlin 需要声明类open
open class JiaGuTask() : DefaultTask() {
    lateinit var apk: File
    lateinit var jiaGuPluginExtension: JiaGuPluginExtension

    @Inject
    constructor(apk: File, jiaGuPluginExtension: JiaGuPluginExtension) : this() {
        group = JiaGuPlugin.GROUP
        description = " 360 jiagu"
        this.apk = apk
        this.jiaGuPluginExtension = jiaGuPluginExtension
    }

    @TaskAction
    fun begin() {
        println("login 360 account")
        project.exec { execSpec ->
            execSpec.commandLine(
                "java", "-jar", jiaGuPluginExtension.jiaGuToolsPath,
                "-login", jiaGuPluginExtension.username, jiaGuPluginExtension.password
            )
        }
        println("import sign")
        project.exec { execSpec ->
            execSpec.commandLine(
                "java",
                "-jar",
                jiaGuPluginExtension.jiaGuToolsPath,
                "-importsign",
                jiaGuPluginExtension.keyStoreFile,
                jiaGuPluginExtension.keyStorePassword
                ,
                jiaGuPluginExtension.keyAlias,
                jiaGuPluginExtension.keyAliasPassword
            )
        }
        jiaGuPluginExtension.channelFile?.let {
            println("import mulpkg")
            project.exec { execSpec ->
                execSpec.commandLine(
                    "java", "-jar", jiaGuPluginExtension.jiaGuToolsPath,
                    "-importmulpkg", it
                )
            }
        }
        jiaGuPluginExtension.config?.let {
            println("config ")
            project.exec { execSpec ->
                execSpec.commandLine(
                    "java", "-jar", jiaGuPluginExtension.jiaGuToolsPath,
                    "--config", it
                )
            }
        }
        println("origin apk.absolutePath:${apk.absolutePath}")
        println("source apk.absolutePath:${apk.parent}")
        project.exec { execSpec ->
            execSpec.commandLine(
                "java", "-jar", jiaGuPluginExtension.jiaGuToolsPath,
                "-jiagu",
                jiaGuPluginExtension.inputFile ?: apk.absolutePath,
                jiaGuPluginExtension.outputFile ?: apk.parent, "-autosign",
                "${jiaGuPluginExtension.channelFile?.let { "-automulpkg" }}"
            )
        }
    }

}