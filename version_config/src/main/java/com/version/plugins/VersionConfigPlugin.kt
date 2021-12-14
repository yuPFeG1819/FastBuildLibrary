package com.version.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 依赖管理与公共预设gradle配置的插件
 * @author yuPFeG
 * @date 2020/06/11
 */
@Suppress("unused")
class VersionConfigPlugin : Plugin<Project>{

    override fun apply(project : Project) {
        println("test Version config plugin start")
//        val dslConfig = project.extensions.create("dslConfig",ModuleDslConfig::class.java)
//        println("custom version config plugin get dslConfig : ${dslConfig.configBaseLib}")
//        project.commConfig()
    }

//    private fun Project.commConfig(){
////        val dslConfigBean = project.extensions.findByName("dslConfig") as ModuleDslConfig
////        println("custom plugin AppExtension : ${dslConfigBean.configBaseLib}")
//        this.plugins.whenPluginAdded {plugins->
//            //根据module类型，设置各自gradle预设配置
//            when(plugins){
//                //com.android.application 应用启动模块插件
//                is AppPlugin ->{
//                    println("App plugin add base config to ${plugins.javaClass}")
//                    //配置公共插件
//                    addCommonPlugin()
//                    //设置app模块的公共android拓展配置
//                    applyAppModuleConfig()
//                    //设置app模块预设的依赖
//                    setAppModuleBaseDependents()
//                }
//                //com.android.library lib模块插件
//                is LibraryPlugin ->{
//                    println("Library plugin add base config to ${plugins.javaClass}")
//                    //配置公共插件
//                    addCommonPlugin()
//                    //设置library模块的公共android拓展配置
//                    applyLibraryModuleConfig()
//                    //设置library模块的预设依赖
//                    setLibraryModuleCommDependency()
//                }
//            }
//        }
//    }
//
//    /**
//     * 配置公用的插件
//     */
//    private fun Project.addCommonPlugin() {
//        plugins.apply {
//            /*
//             * 相当于
//             * apply plugin: “kotlin-android”
//             * //开启使用@Parcelize注解实现Parcelize
//             * apply plugin: ”kotlin-parcelize“
//             * //kotlin替代注解
//             * apply plugin: "kotlin-kapt"
//             */
//            apply("kotlin-android")
//            apply("kotlin-parcelize")
//            apply("kotlin-kapt")
//        }
//    }
//
//    /**
//     * [Project]拓展函数，应用app模块的android{}拓展预设配置
//     */
//    private fun Project.applyAppModuleConfig(){
//        this.extensions.getByType(AppExtension::class.java).apply {
//            println("App module add extension config to ${AppExtension::class.java}")
//            defaultConfig.applicationId = AppBuildConfig.applicationId
//            //应用基础配置
//            applyBaseConfig()
//        }
//        //配置基础task
//        this.tasks.applyCommTaskConfig()
//    }
//
//    /**
//     * [Project]拓展函数，应用library模块的android{}拓展预设配置
//     */
//    private fun Project.applyLibraryModuleConfig() {
//        this.extensions.getByType(LibraryExtension::class.java).apply {
//            println("Library module add extension config to ${LibraryExtension::class.java}")
//            //设置子模块的混淆规则文件
//            defaultConfig.consumerProguardFiles("consumer-rules.pro")
//            //应用基础配置
//            applyBaseConfig()
//        }
//        //配置基础task
//        this.tasks.applyCommTaskConfig()
//    }
//
//    /**
//     * [BaseExtension]的拓展函数，Extension的通用预设配置
//     * */
//    private fun BaseExtension.applyBaseConfig() {
//        compileSdkVersion(AppBuildConfig.compileSdkVersion)
//        buildToolsVersion(AppBuildConfig.buildToolsVersion)
//
//        defaultConfig.apply{
//            minSdkVersion(AppBuildConfig.minSdkVersion)
//            targetSdkVersion(AppBuildConfig.targetSdkVersion)
//            versionCode = AppBuildConfig.versionCode
//            versionName = AppBuildConfig.versionName
//            testInstrumentationRunner = AppBuildConfig.testRunner
//            //开启分包
//            multiDexEnabled = true
//            buildConfigField("String","TEST_FIELD","\"testField\"")
//        }
//
//        buildTypes.apply{
//            getByName("debug"){buildType->
//                //关闭混淆
//                buildType.isMinifyEnabled = false
//                buildType.buildConfigField("String","DEBUG_TEST_FIELD","\"debug_test_field\"")
//            }
//
//        }
//
//        //设置编译基于java8
//        compileOptions.apply {
//            sourceCompatibility = JavaVersion.VERSION_1_8
//            targetCompatibility = JavaVersion.VERSION_1_8
//        }
//
//    }
//
//    /**设置基础task配置*/
//    private fun TaskContainer.applyCommTaskConfig(){
//        this.withType(KotlinCompile::class.java){compile->
//            /**
//             * 相当于
//             * //kotlin编译使用jvm1.8特性
//             * kotlinOptions {
//             *   jvmTarget = '1.8'
//             * }
//             * */
//            compile.kotlinOptions {
//                jvmTarget = "1.8"
//            }
//        }
//    }
}