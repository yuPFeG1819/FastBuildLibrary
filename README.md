自用的快速开发基础库

用于实验新构想与新技术



## 已依赖库

利用Composing builds方式统一管理配置依赖库

[再见吧 buildSrc, 拥抱 Composing builds 提升 Android 编译速度](https://juejin.cn/post/6844904176250519565)

[除了 buildSrc 还能这样统一配置依赖版本？巧用 includeBuild](https://juejin.cn/post/6844904169833234439)

尽可能少的依赖非官方原生组件

- appcompat 、 constraintLayout 、 RecyclerView 、Jetpack系列

- [Material](https://github.com/material-components/material-components-android/tags)

- [Kotlin](https://github.com/JetBrains/kotlin)、[KotlinCoroutine](https://github.com/Kotlin/kotlinx.coroutines)

- 自用库 ： 

  > [HttpRequestMediator](https://gitee.com/yupfeg/http_request_mediator) 封装网络请求
  > [Logger](https://gitee.com/yupfeg/logger) 日志库
  > [StateLiveDataWrapper](https://gitee.com/yupfeg/state-live-data-wrapper) 单次执行事件LiveData
  > [EasyResultApi](https://gitee.com/yupfeg/easy-result-api) ResultApi,包含权限请求

- [RxJava](https://github.com/ReactiveX/RxJava) TODO ：后续移除
- [Glide](https://github.com/bumptech/glide) 图片加载库
- [Gson](https://github.com/google/gson)
- [SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout) 下拉刷新框架
- [toastUtils](https://github.com/getActivity/ToastUtils) 适配Anroid 11的Toast库
- [MMKV](https://github.com/tencent/mmkv) key-value库
- [TinyPinyin](https://github.com/promeG/TinyPinyin) 拼音库
- [Android-PickerView](https://github.com/Bigkoo/Android-PickerView) 仿IOS的PickerView类，

## MVVM与MVI架构组件封装

- 引入`UseCase`业务用例 `Domain`层,承载业务逻辑功能以及原子化状态。

  > - `UseCase`可添加`Lifecycle`支持，提供给`AutoDispose`与`CoroutineScope`根据生命周期管理任务调度。
  >
  > - 添加`UseCaseTaskScheduler`支持`UseCase`在后台使用。
  >
  > - 如果是MVI架构可尝试只在`UseCase`内进行数据替换。
  >
  > - 添加常用的列表分页加载功能的`UseCase`基类

- `ViewModel`拓展

  > - 自定义`ViewModel`的委托类，利用by关键字获取实例，并且同时为内部持有的`UseCase`对象订阅视图生命周期
  > - 新增`Application`作用域，提供添加允许在全应用作用域的`ViewModel`，用于分发视图信息，统一管理事件分发，避免

- 引入`DataMapper`，隔离数据层与表现层

  > - 利用`DataMapper`映射数据源的数据实体映射为本地数据实体，避免后端数据格式影响视图展示。
  > 
  > - 同时可抽取`UseCase`内的部分数据处理逻辑。 

- Activity、Fragment基类封装

  > 利用拓展函数提取基类功能，尽可能移出`BaseActivity`与`BaseFragment`内逻辑，提高复用性，降低冗余部分

- 



## 图片库封装

沙盒化隔离图片请求库

> 配合kotlin-dsl，抽象图片请求功能接口
>
> TODO :
>
> - 尝试添加`Coil`加载库的支持

## Dialog相关

- Dialog基类封装

  > `BaseDialog`与`BaseBindingDialog`，简化`Dialog`的使用

- DialogFragment基类封装

  > `BaseDialogFragment`与`BaseBindDialogFragment`，简化`DialogFragment`使用

## DataBinding封装

提供DataBinding代理方式，利用by关键字调用DataBinding

> 封装常用的`BindingAdapter`,方便`DataBinding`使用
> TODO : 
>
> 待完善在DataBinding中构建drawable shape的方式

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
  >- 参考[BRV](https://github.com/liangjingkanji/BRV)库，改进视图
  >
  >- 考虑使用`RecyclerView`替代实现三级联动滚轮的实现
  >
  >- [DiffUtils 遇到Kotlin](https://juejin.cn/post/7033206569181544461)
  >
  >  在复制一个List时，是通过浅拷贝的方式，某些情况调用`areContentsTheSame`还是会出现自己比较自己的情况，所以需要使用深拷贝的方式。
  >
  >  参考[Android DiffUtil 封装｜深拷贝](https://juejin.cn/post/6856725337737265166#heading-6)
  >
  >



## 其他杂项

- 桌面消息红点提醒

  > 利用策略模式，根据设备品牌控制桌面红点操作

- WebView缓存池

  > 尝试管理WebView实例，提供kotlin-dsl方式配置webView属性
  > 仅提供预加载，除第一次加载WebView外，后续加载速度很快，且destroy后暂无办法无法复用
  >
  > TODO :
  > - 尝试WebView放入单独进程，需要跨进程通信

- `IdleHandler`延迟启动管理类

- `SpannableStringUtils`封装

  > 利用Kotlin-DSL方式，封装当前添加文本段落的文本样式。
  > 封装思路：[工具类之SpannableStringUtils](https://www.jianshu.com/p/960467ac56c8)

- 应用信息、设备信息、屏幕尺寸 的工具类

- 状态栏与导航栏相关工具

  > 尝试利用`WindowSet`相关API，进行设置沉浸式状态栏
  >
  > TODO : 
  >
  > - 寻找在Android R之后被废弃API的替代方案

- 代码构建`Drawable Shape`

  > TODO ：
  >
  > 有待优化完善，尝试加上kotlin-dsl方式方便配置拓展

- Activity管理栈
- 单位转化拓展

## TODO 

- 继续完善整理常用公共组件封装

- 拼音排序列表自定义视图

- 测试引入Hilt依赖注入

- 尝试实现BottomSheet效果

- 尝试引入或实现本地图片选择库