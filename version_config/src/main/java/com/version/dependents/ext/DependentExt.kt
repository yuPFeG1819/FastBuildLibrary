package com.version.dependents.ext

import org.gradle.api.Action
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 * 依赖的基础拓展方法
 * @author yuPFeG
 * @date 2021/02/04
 */

internal const val api = "api"
internal const val implementation = "implementation"
internal const val testImplementation = "testImplementation"
internal const val androidTestImplementation = "androidTestImplementation"
internal const val kapt = "kapt"

//---------- implementation的依赖方式-------------
/**
 * [DependencyHandler]的拓展函数，添加[implementation]方式的依赖模块
 * * [implementation] 依赖会出现在编译产物中. 但是最终产物中的该依赖不会向外暴露接口.
 * 这种方式可以有效减少 recompile 时所需要编译的模块, 提高编译速度.
 * @param dependencyNotation 依赖的模块描述.
 * 1. 依赖module : project(":xxx")，
 * 2. 依赖文件\文件树 : fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))
 * 3. 远程依赖模块 : module名称 - ”androidx.appcompat:appcompat:1.2.0“
 * */
internal fun DependencyHandler.implementation(dependencyName : Any) =
    add(implementation,dependencyName)

/**
 * [DependencyConstraintHandler]的拓展函数，添加[implementation]方式依赖的约束
 * * 约束默认使用required，仅限制该依赖的最低版本
 * * [implementation] 依赖会出现在编译产物中. 但是最终产物中的该依赖不会向外暴露接口.
 * 这种方式可以有效减少 recompile 时所需要编译的模块, 提高编译速度.
 * @param dependencyName 依赖的模块描述.
 * 1. 依赖module : project(":xxx")，
 * 2. 依赖文件\文件树 : fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))
 * 3. 远程依赖模块 : module名称 - ”androidx.appcompat:appcompat:1.2.0“
 */
internal fun DependencyConstraintHandler.implementation(dependencyName : Any) =
    add(implementation,dependencyName)

/**
 * [DependencyConstraintHandler]的拓展函数，添加[implementation]方式的依赖约束
 * * 约束默认使用required，仅限制该依赖的最低版本
 * * [implementation] 依赖会出现在编译产物中. 但是最终产物中的该依赖不会向外暴露接口.
 * 这种方式可以有效减少 recompile 时所需要编译的模块, 提高编译速度.
 * @param dependencyName 依赖的模块描述.
 * 1. 依赖module : project(":xxx")，
 * 2. 依赖文件\文件树 : fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))
 * 3. 远程依赖模块 : module名称 - ”androidx.appcompat:appcompat:1.2.0“
 * @param constraint 约束条件的闭包[DependencyConstraint]
 */
internal fun DependencyConstraintHandler.implementation(dependencyName: Any,
                                                        constraint : Action<DependencyConstraint>) =
    add(implementation,dependencyName,constraint)

//-------------------api 依赖方式----------------------

/**
 * [DependencyHandler]的拓展函数，添加[api]方式的依赖模块
 * * [api] 依赖会出现在编译产物中. 但是最终产物中的该依赖会向外暴露接口.
 * @param dependencyNotation 依赖的模块描述.
 * 1. 依赖module : project(":xxx")，
 * 2. 依赖文件\文件树 : file("xxx") \ fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))
 * 3. 远程依赖模块 : module名称 - ”androidx.appcompat:appcompat:1.2.0“
 * */
internal fun DependencyHandler.api(dependencyName : Any) = add(api,dependencyName)

/**
 * [DependencyConstraintHandler]的拓展函数，添加[api]方式的依赖模块
 * * [api] 依赖会出现在编译产物中. 但是最终产物中的该依赖会向外暴露接口.
 * @param dependencyNotation 依赖的模块描述.
 * 1. 依赖module : project(":xxx")，
 * 2. 依赖文件\文件树 : file("xxx") \ fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))
 * 3. 远程依赖模块 : module名称 - ”androidx.appcompat:appcompat:1.2.0“
 * */
internal fun DependencyConstraintHandler.api(dependencyName: Any) = add(api,dependencyName)
/**
 * [DependencyConstraintHandler]的拓展方法，添加[api]方式的依赖约束
 * * [api] 依赖会出现在编译产物中. 但是最终产物中的该依赖会向外暴露接口.
 * @param dependencyNotation 依赖的模块描述.
 * 1. 依赖module : project(":xxx")，
 * 2. 依赖文件\文件树 : file("xxx") \ fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))
 * 3. 远程依赖模块 : module名称 - ”androidx.appcompat:appcompat:1.2.0“
 * @param constraint 约束条件的闭包[DependencyConstraint]
 * */
internal fun DependencyConstraintHandler.api(dependencyName: Any,
                                            constraint : Action<DependencyConstraint>) =
    add(api,dependencyName,constraint)

//--------------test 依赖方式-----------------

internal fun DependencyHandler.testImplementation(dependencyName : Any) =
    add(testImplementation,dependencyName)

internal fun DependencyHandler.androidTestImplementation(dependencyName : Any) =
    add(androidTestImplementation,dependencyName)

internal fun DependencyHandler.kapt(dependencyName : Any) = add(kapt,dependencyName)