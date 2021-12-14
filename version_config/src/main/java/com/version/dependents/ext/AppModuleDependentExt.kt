package com.version.dependents.ext

import org.gradle.api.Project

/**
 * 针对App 模块的依赖相关拓展方法
 * @author yuPFeG
 * @date 2021/02/04
 */
/**
 * [Project]的拓展函数，设置app模块的预设依赖配置
 * */
internal fun Project.setAppModuleBaseDependents(){
    //添加预设依赖
    dependencies.apply {
        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
        //依赖基础库
        implementation(project(":lib_base"))
        //添加测试库依赖
        addTestingDependency()
    }
}

