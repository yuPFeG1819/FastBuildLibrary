自用的快速开发基础库

用于实验新构想与新技术



## 已依赖库

利用`Composing builds`方式统一管理配置依赖库

[再见吧 buildSrc, 拥抱 Composing builds 提升 Android 编译速度](https://juejin.cn/post/6844904176250519565)

[除了 buildSrc 还能这样统一配置依赖版本？巧用 includeBuild](https://juejin.cn/post/6844904169833234439)

尽可能少的依赖非官方原生组件

- appcompat 、 constraintLayout 、 RecyclerView 、Jetpack系列

- [Material](https://github.com/material-components/material-components-android/tags)

- [Kotlin](https://github.com/JetBrains/kotlin)、[KotlinCoroutine](https://github.com/Kotlin/kotlinx.coroutines)

- 自用库 ： 

  > [HttpRequestMediator](https://gitee.com/yupfeg/http_request_mediator) Retrofit网络请求封装
  > [Logger](https://gitee.com/yupfeg/logger) 日志库
  > [StateLiveDataWrapper](https://gitee.com/yupfeg/state-live-data-wrapper) 单次执行事件LiveData
  > [EasyResultApi](https://gitee.com/yupfeg/easy-result-api) ResultApi,包含权限请求
  >
  > [StartTaskDispatcher](https://gitee.com/yupfeg/StartTaskDispatcher) 启动任务调度器，进行启动优化
  > [ExecutorProvider](https://gitee.com/yupfeg/ExecutorProvider) 线程池库，收敛项目线程应用
  > [CodeDrawableDsl](https://gitee.com/yupfeg/CodeDrawableDsl) 动态构建Drawable的库
  > 

- ~~[RxJava](https://github.com/ReactiveX/RxJava)~~ 已移除依赖
- [Glide](https://github.com/bumptech/glide) 图片加载库
- [Gson](https://github.com/google/gson)
- [SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout) 下拉刷新框架
- [toastUtils](https://github.com/getActivity/ToastUtils) 适配Anroid 11的Toast库
- [MMKV](https://github.com/tencent/mmkv) key-value库
- [TinyPinyin](https://github.com/promeG/TinyPinyin) 拼音库
- [Android-PickerView](https://github.com/Bigkoo/Android-PickerView) 仿IOS的PickerView类，

## MVVM与MVI架构组件封装

- 引入`UseCase`业务用例作为 `Domain`层,承载业务逻辑功能以及原子化状态。

  > - `UseCase`内提供给`CoroutineScope`根据生命周期管理任务调度。
  >
  > - 整合提供`UseCaseQueue`，在`ViewModel`与其他非UI场景的业务用例维护逻辑，在时机合适时取消业务用例
  >
  > - 如果是MVI架构可尝试只在`UseCase`内进行业务逻辑处理，数据状态交由外部`ViewModel`进行统一管理。
  >
  > - 提供常用**列表分页加载**功能的`UseCase`

- `ViewModel`拓展

  > - 新增`Application`作用域的`ViewModelStore`，提供添加允许在全应用作用域的`ViewModel`，用于分发视图信息，统一管理跨页面事件分发，避免不可控的全局事件分发

- 提供`DataMapper`，隔离数据层与表现层

  > - 利用`DataMapper`映射数据源的数据实体映射为本地数据实体，避免后端数据格式影响视图展示。
  > 
  > - 同时可抽取`UseCase`内的部分数据处理逻辑。 

- Activity、Fragment基类封装

  > 利用拓展函数提取基类功能，尽可能移出`BaseActivity`与`BaseFragment`内逻辑，提高复用性，降低冗余部分

- 提供`ListDataFilter`的列表数据过滤类

  > 用于以数据驱动UI修改列表，提供`RecyclerView`数据展示，在原始业务数据上进行过滤处理。
  >
  > 默认提供进行分页加载的数据过滤类，在原有数据基础上额外增加**加载更多**、**无数据页**、**分页错误**等数据。

## 图片库封装

沙盒化隔离图片请求库

> 以kotlin-dsl设置图片请求参数，抽象图片请求功能接口
>

## Dialog相关

- Dialog基类封装

  > `BaseDialog`与`BaseBindingDialog`，简化`Dialog`的使用

- DialogFragment基类封装

  > `BaseDialogFragment`与`BaseBindDialogFragment`，简化`DialogFragment`使用

## DataBinding封装

提供DataBinding代理方式，利用by关键字调用DataBinding

> 封装常用的`BindingAdapter`,方便`DataBinding`使用
>
> - 底部导航`BottomNabigationView`的DataBinding拓展
> - `ImageView`的DataBinding拓展
> - `RecyclerView`的DataBinding拓展
> - `TextView`的DataBinding拓展
> - `View`类的DataBinding拓展，其中包含`WindowInset`相关属性
> - `ViewPager2`的DataBinding拓展

## RecyclerView封装

- 利用策略模式封装`RecyclerView.Adapter`，简化多类型item的实现成本，更贴近于纯粹的`View`。

  >外部统一使用`RecyclerListAdapter`，不影响外部`RecyclerView`正常使用，侵入性较低。
  >
  >将`Adapter`与具体业务逻辑数据类型解耦，仅根据列表item的数据类型，将具体显示逻辑交由委托类处理
  >
  >参考[官方方案](https://github.com/google/iosched/blob/89df01ebc19d9a46495baac4690c2ebfa74946dc/mobile/src/main/java/com/google/samples/apps/iosched/ui/feed/FeedAdapter.kt)
  >
  >参考[掘金上的策略模式方案](https://juejin.cn/post/6876967151975006221)
  >
  >TODO ：
  >
  >- 后续尝试添加更多自定义LayoutManager的拓展实现
  >
  >- 参考[BRV](https://github.com/liangjingkanji/BRV)库，以DSL方式改进视图配置
  >
  >- 考虑使用`RecyclerView`的自定义Layout替代实现三级联动滚轮的实现
  >
  >- [DiffUtils 遇到Kotlin](https://juejin.cn/post/7033206569181544461)
  >
  >  在复制一个List时，是通过浅拷贝的方式，某些情况调用`areContentsTheSame`还是会出现自己比较自己的情况，所以需要使用深拷贝的方式。
  >
  >  参考[Android DiffUtil 封装｜深拷贝](https://juejin.cn/post/6856725337737265166#heading-6)
  >
  >

## WindowInset相关

对`WindowInsetCompat`中可兼容Api 30以下的常用功能进行封装

- 软键盘、状态栏、底部导航栏相关

  > 只能在视图树构建后才能获取到，相比反射获取高度更为健壮

- 提供`Activity`与`View`拓展函数，利用`WindowInsetsControllerCompat`快捷控制软键盘

- 沉浸式状态栏与导航栏支持（官方称 **边到边适配**）

  > 1. 封装`WindowCompat.setDecorFitsSystemWindows`新版本的延伸内容到系统栏(向下兼容，替代systemUiVisibility)
  >
  > 2. 提供`View`拓展函数，延伸视图内容到系统栏（增加视图高度），需要先调用`WindowCompat.setDecorFitsSystemWindows(window,false)`允许将视图内容延伸到状态栏后才能生效。

- 简化`WindowInsets`动画执行回调，默认实现视图跟随软键盘移动，避免遮挡底部视图

## RxJava支持

抽取`RxJava3`依赖到其他module

- `AutoUseCase`，提供`ViewModel`设置`LifecycleScopeProvider`，便于`UseCase`内部使用`RxJava`数据流的自动管理订阅生命周期
- `AutoDisposeViewModel`，便于`ViewModel`内部使用`AutoDispose`自动管理`RxJava`数据流。
- 封装`RxJava3`延迟重试`retryWhen`操作符
- 封装`RxJava3`的轮询操作符`retryWhen`与`repeatWhen`操作符利用`compose`合并
- 简化`RxJava3`的数据流线程调度操作符使用

## Key-Value本地缓存

利用代理模式，配合`by`关键字，将对象的赋值、取值操作委托到对本地key-value缓存的操作

## 其他杂项

- 整合应用进程全局功能，抽离移除`Application`的基类，避免继承的耦合

  > 参考`Jetpack ProcessLifecycleOwner`的思路，通过`Application.registerActivityLifecycleCallbacks`订阅所有`Activity`的生命周期变化。
  >
  > 1. 对外提供`LifecycleOwner`，内部创建`LifecycleRegistry`，用于监听在前台视图启动后，分发应用生命周期，
  >
  > - onCreate只会分发一次，在第一次应用启动时分发
  > - 在第一个Activity进入前台时，依次分发onStart、onResume
  > - 在最后一个Activbity进入onPause时，启动延迟任务，延迟足够长时间，以过滤屏幕旋转重建的情况
  > - 然后分发onPause、onStop事件，确定应用进入后台。
  >
  > 外部可以利用这个全局`LifecycleOwner`的`Lifecycle`，方便的监听应用进入前后台。
  >
  > 2. 提供`ActivityStack`栈，管理所有启动的Activity，在onCreate时推入栈，在onDestory时移出栈
  >
  >    方便进行对于视图的管理
  >
  > 3. 提供Application范围的协程作用域，以替代`GlobalScope`。
  >
  > 4. 提供Application范围的`ViewModelStroe`，方便利用在全`Application`范围的共享ViewModel，跨页面传递分发信息

- 自定义View
  - 未读消息红点标记视图 `BadgeView`
  - 九宫格布局 `NineGridLayout`
  - 弧形进度条 `ArcProgressBar`
  
- 桌面消息红点提醒

  > 利用类的多态，（静态策略）模式，根据设备品牌控制桌面红点操作

- WebView缓存池

  > 尝试管理WebView实例，提供kotlin-dsl方式配置webView属性
  > 仅提供预加载功能，除第一次加载WebView外，后续加载速度很快，且destroy后暂无办法复用
  >
  > TODO :
  > - 尝试WebView放入单独进程，需要跨进程通信
  > - 尝试增加`okhttp`代理`WebView`加载资源，提供外部设置的`OkHttpClient`实例，进行构建网络请求

- `SpannableStringUtils`封装

  > 利用Kotlin-DSL方式，设置当前添加文本段落的文本样式。
  > 封装思路：[工具类之SpannableStringUtils](https://www.jianshu.com/p/960467ac56c8)

- 应用信息、设备信息 的工具类 `DevicesTools`

- 屏幕尺寸相关函数 `ScreenExt`

- Activity管理栈`ActivityStackHelper`

  > 管理所有`Activity`，更多是提供跨`Activity`关闭等操作

- 单位转化拓展 px->dp , dp->px

- `Double`的高精度运算操作

  > 提供`Double`的拓展函数，利用`BigDecimal`进行高精度运算，保留指定的小数点后的位数

- 提供`File`本地文件相关工具类

- 提供本地`Uri`相关函数，兼容到Android 10以上的沙盒系统、兼容Android 7.0的FileProvider

- 金钱价格的`EditText`输入过滤类`AmountInputFilter`，只能输入到小数点后2位

- 日期与时间相关工具类`DateTimeTools`

- 本地联系人相关工具类`ContactsTools`

## TODO 

- 尝试添加`Coil`加载库的支持

- 拼音排序列表自定义视图

- 测试引入Hilt依赖注入

- 尝试实现BottomSheet效果

- 尝试引入或实现本地图片选择库

- 实现NestedScrolling机制的嵌套滑动RecyclerView
  
  > 参考思路[也许是最贴近京东首页体验的嵌套滑动吸顶效果](http://solart.cc/2020/07/17/nested_ceiling_effect/)
  > 
  
- 考虑RecyclerView内部嵌入RefreshLayout下拉刷新机制

- ~~尝试使用Kotlin-dsl方式方便配置拓展，代码构建`Drawable Shape`，避免项目内存在过多的`Drawable`文件~~(已完成)

- ~~完善在`DataBinding`中构建代码`drawable shape`的方式~~ (已抽取到单独项目库依赖[CodeDrawableDsl](https://gitee.com/yupfeg/CodeDrawableDsl))