package com.xfhy.junkcode.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.xfhy.junkcode.ext.AndroidJunkCodeExt
import com.xfhy.junkcode.ext.JunkCodeConfig
import com.xfhy.junkcode.task.AndroidJunkCodeTask
import groovy.lang.Closure
import groovy.util.XmlParser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import java.lang.IllegalArgumentException

/**
 * @author : xfhy
 * Create time : 2021/8/11 7:20 上午
 * Description : 生成垃圾代码的插件
 *
 * 调试技巧: 在AS右侧的Gradle菜单里,找个task,比如Tasks->build->build这个任务,右键debug,即可断点至插件中打了断点的地方.
 *
 */
class GenerateJunkCodePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        /*project.afterEvaluate {
            println("卧槽  Hello world")
        }*/
        val android = project.extensions.getByType(AppExtension::class.java)
            ?: throw IllegalArgumentException("must apply this plugin after 'com.android.application")
        val generateJunkCodeExt = project.extensions.create("androidJunkCode", AndroidJunkCodeExt::class.java)
        project.afterEvaluate {
            android.applicationVariants.all { variant: ApplicationVariant ->
                val variantName = variant.name
                println("变体名称 $variantName")
                //拿到该变体对应的配置
                val junkCodeConfig: Closure<JunkCodeConfig> = generateJunkCodeExt.configMap[variantName] ?: return@all
                val dir = File(project.buildDir, "generated/source/junk/$variantName")
                val resDir = File(dir, "res")
                val javaDir = File(dir, "java")
                val manifestFile = File(dir, "AndroidManifest.xml")
                val packageName = findPackageName(variant)
                println("包名找到了  是$packageName")
                val task = project.task(
                    mapOf(Task.TASK_TYPE to AndroidJunkCodeTask::class.java),
                    "generate${variantName.capitalize()}JunkCode"
                )
                /*task.configure(object :Closure<AndroidJunkCodeTask>(){

                })*/
            }
        }
    }

    /**
     * 从AndroidManifest.xml找到package name
     * @param variant
     * @return
     */
    private fun findPackageName(variant: ApplicationVariant): String {
        var packageName: String? = null
        variant.sourceSets.forEach {
            if (it.manifestFile.exists()) {
                //解析清单文件,找到package
                val parser = XmlParser()
                val node = parser.parse(it.manifestFile)
                packageName = node.attribute("package") as? String
                if (packageName != null) {
                    return@forEach
                }
            }
        }
        return packageName ?: ""
    }
}