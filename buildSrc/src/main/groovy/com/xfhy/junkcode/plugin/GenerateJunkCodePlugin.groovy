package com.xfhy.junkcode.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.xfhy.junkcode.ext.JunkCodeConfig
import com.xfhy.junkcode.ext.AndroidJunkCodeExt
import org.gradle.api.Plugin
import org.gradle.api.Project

class GenerateJunkCodePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def android = project.extensions.getByType(AppExtension)
        if (!android) {
            throw IllegalArgumentException("must apply this plugin after 'com.android.application'")
        }
        def generateJunkCodeExt = project.extensions.create("androidJunkCode", AndroidJunkCodeExt)
        project.afterEvaluate {
            android.applicationVariants.all { variant ->
                def variantName = variant.name
                Closure<JunkCodeConfig> junkCodeConfig = generateJunkCodeExt.configMap[variantName]
                if (junkCodeConfig) {
                    def dir = new File(project.buildDir, "generated/source/junk/$variantName")
                    def resDir = new File(dir, "res")
                    def javaDir = new File(dir, "java")
                    def manifestFile = new File(dir, "AndroidManifest.xml")
                    String packageName = findPackageName(variant)
                    def generateJunkCodeTask = project.task("generate${variantName.capitalize()}JunkCode", type: AndroidJunkCodeTask) {
                        junkCodeConfig.delegate = config
                        junkCodeConfig.resolveStrategy = DELEGATE_FIRST
                        junkCodeConfig.call()
                        manifestPackageName = packageName
                        outDir = dir
                    }
                }
            }
        }
    }


    /**
     * 从AndroidManifest.xml找到package name
     * @param variant
     * @return
     */
    static String findPackageName(ApplicationVariant variant) {
        String packageName = null
        for (int i = 0; i < variant.sourceSets.size(); i++) {
            def sourceSet = variant.sourceSets[i]
            if (sourceSet.manifestFile.exists()) {
                def parser = new XmlParser()
                Node node = parser.parse(sourceSet.manifestFile)
                packageName = node.attribute("package")
                if (packageName != null) {
                    break
                }
            }
        }
        return packageName
    }
}