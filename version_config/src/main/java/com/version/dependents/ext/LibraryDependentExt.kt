package com.version.dependents.ext

import org.gradle.api.Project

/**
 * Library模块的基础预设依赖配置
 * @author yuPFeG
 * @date 2021/02/04
 */
/**
 * [Project]的拓展函数，添加Library模块的基础预设依赖配置
 */
fun Project.setLibraryModuleCommDependency(){
    dependencies.apply {
        api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
        //添加测试库依赖
        addTestingDependency()
    }
}