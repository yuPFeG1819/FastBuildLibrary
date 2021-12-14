package com.version.dependents.ext

import com.version.dependents.*
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 * Gradle依赖管理相关的拓展方法文件
 * @author yuPFeG
 * @date 2021/02/02
 */

/**
 * [DependencyHandler]的拓展函数，添加公用的测试依赖
 */
internal fun DependencyHandler.addTestingDependency(){
    testImplementation (Testing.testJunit)
    androidTestImplementation (Testing.extJunit)
    androidTestImplementation (Testing.espressoCore)
}

/**
 * [Project]的拓展函数，设置基础库module的预设依赖配置
 * * 打印依赖树的命令 gradlew :app:dependencies ，其中:app为特定module名称
 * *
 * * [DependencyHandler官方文档](https://docs.gradle.org/6.5/javadoc/org/gradle/api/artifacts/dsl/DependencyHandler.html)
 * */
@Deprecated("目前无法在Gradle插件内区分是否为基础库类型，暂时废弃，改为原始方式")
internal fun Project.addBaseLibraryModuleDependency(){
    //添加预设依赖
    dependencies.apply {
        api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
        //添加测试库依赖
        addTestingDependency()

        //---------------kotlin-----------------
        api (ThirdParty.Kotlin.stdlib)
        api (ThirdParty.Kotlin.coroutinesAndroid)
        //------------官方依赖库--------------
        api (AndroidX.appcompat)
        //AppCompat已包含fragment的依赖，这里另外依赖带ktx后缀的库
        api (AndroidX.Fragment.fragmentKtx)
        //fragment已包含activity的依赖，这里另外依赖带ktx后缀的库
        api (AndroidX.activityKtx)
        api (Google.material)
        api (AndroidX.recyclerView)
        api (AndroidX.viewpager2)
        api (AndroidX.cardView)
        //约束布局
        api (AndroidX.constraintLayout)
        //官方提供的流式布局库
        api (Google.flexbox)

        api (AndroidX.multiDex)
        //---------------JetPack--------------------
        api (AndroidX.coreKtx)
        api (AndroidX.Lifecycle.viewModelKtx)
        api (AndroidX.Lifecycle.commonJava8)
        api (AndroidX.Lifecycle.runtimeKtx)
        api (AndroidX.Lifecycle.liveDataKtx)
        api (AndroidX.Lifecycle.viewModelSavedState)
        api (AndroidX.Lifecycle.service)
        api (AndroidX.Lifecycle.process)

        //---------------Navigation库------------------
        api (AndroidX.Navigation.fragmentKtx)
        api (AndroidX.Navigation.uiKtx)
        //-------------JetPack_Room数据库---------------------
        api (AndroidX.Room.runtime)
        api (AndroidX.Room.roomKtx)
        api (AndroidX.Room.testing)
        //多模块项目，必须在用到database的位置添加上room的kapt
        kapt (AndroidX.Room.compiler)

        //-------------------------RxJava3库---------------------
        api (RxJava.rxJava3)
        //添加依赖约束（约束依赖使用的最低版本，在module中直接依赖只能比约束内的版本高）
//        this.constraints.apply {
//            //约束RxJava3的版本
//            api(RxJava.rxJava3){constraints->
//                //通过strictly强制约束依赖版本，不能约束直接依赖的版本
//                constraints.version { versionConstraint->
//                    versionConstraint.strictly(RxJava.rxJava3Version)
//                }
//            }
//        }
        api (RxJava.rxAndroid3)
        api (RxJava.rxKotlin3)
        api (RxJava.rxRelay3)

        //rxJava的权限请求库
        api (RxJava.RxPermission.rxJava3)
        //--------------AutoDispose库----------------
        //管理rxJava内存泄漏问题(0.8.0以上需要AndroidX支持)
        api (RxJava.AutoDispose.rxJava3)
        api (RxJava.AutoDispose.rxJava3Lifecycle)
        //--------------RxBinding库-------------
        //基础库，内部已包含RxJava3依赖，防止版本混乱，还是在外面引用
        api (RxJava.RxBinding.core4)
        api (RxJava.RxBinding.appcompat4)

        //----------------OkHttp3相关库-------------------
        //内部已经依赖了okIo
//        constraints {
//            it.api(Network.okHttp3){constraints->
//                //通过strictly强制约束依赖版本，不能约束直接依赖的版本
//                constraints.version { versionConstraint->
//                    versionConstraint.strictly(Network.okhttpVersion)
//                }
//            }
//        }
        api (Network.okHttp3)
        //GSON解析库
        api (Google.gson)
        //fastJson是引用.android包，体积更小
//        api (Network.fastJsonAndroid)
        //==============Retrofit网络库===================
        api (Network.retrofit2)
        //支持RxJava3 CallBack处理
        api (Network.retrofitRxJava3Adapter)
        //支持解析成标准字符串
        api (Network.retrofitScalars2)
        //支持GSON解析
        api (Network.retrofitGson2)

        //----------------Glide图片加载框架-----------------
        api (ThirdParty.Glide.glide)
        // Glide 的注解和注解解析器
        api (ThirdParty.Glide.glideAnnotations)
        kapt (ThirdParty.Glide.compiler)
        //Glide对OkHttp3的支持
        api (ThirdParty.Glide.okHttp)

        //---------------其他杂项----------------
        //智能下拉刷新库
        api (ThirdParty.SmartRefreshLayout.base)
        //中文拼音转化核心库
        api (ThirdParty.TinPinYin.base)
    }
}