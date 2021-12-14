自用的快速开发基础库

用于实验新构想与新技术

-  对于`MVVM`的架构模式实现的基类封装
> 1. 引入`UseCase`业务用例,承载业务逻辑功能以及原子化状态
> `UseCase`添加`Lifecycle`支持，提供给`AutoDispose`与`CoroutineScope`根据生命周期管理任务调度。
> 添加`UseCaseTaskScheduler`支持`UseCase`在后台使用。
> 
> 2. 引入`DataMapper`映射数据源的数据实体映射为本地数据实体，避免后端数据格式影响视图展示。

- `SpannableStringBuilder`使用封装
> 利用Kotlin-DSL方式，封装当前添加文本段落的文本样式。
> 封装思路：[工具类之SpannableStringUtils](https://www.jianshu.com/p/960467ac56c8)
> 

- 桌面消息红点提醒
> 利用策略模式，根据设备品牌控制桌面红点操作

- 封装常用的`BindingAdapter`,方便`DataBinding`使用
TODO 待完善对于在DataBinding中构建drawable shape的方式
- 