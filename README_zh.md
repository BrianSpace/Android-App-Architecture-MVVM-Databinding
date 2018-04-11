# 安卓应用架构展示 - 基于数据绑定的MVVM架构 (Java 版本) [[Kotlin版本](../../blob/kotlin/README_zh.md)]  [[English Version](README.md)]
本项目作为安卓应用MVVM架构的例子，展示了如何使用此架构开发一个影视信息网站[The Movie DB](https://www.themoviedb.org/)的客户端。

## 目录
 - [为什么采用MVVM?](#%E4%B8%BA%E4%BB%80%E4%B9%88%E9%87%87%E7%94%A8-mvvm)
 - [应用截屏](#%E5%BA%94%E7%94%A8%E6%88%AA%E5%B1%8F)
 - [应用功能简介](#%E5%BA%94%E7%94%A8%E5%8A%9F%E8%83%BD%E7%AE%80%E4%BB%8B)
 - [应用架构](#%E5%BA%94%E7%94%A8%E6%9E%B6%E6%9E%84)
    - [架构分层](#%E6%9E%B6%E6%9E%84%E5%88%86%E5%B1%82)
    - [主要组件](#%E4%B8%BB%E8%A6%81%E7%BB%84%E4%BB%B6)
    - [应用的关键设计决策](#%E5%BA%94%E7%94%A8%E7%9A%84%E5%85%B3%E9%94%AE%E8%AE%BE%E8%AE%A1%E5%86%B3%E7%AD%96)
    - [模块/主要目录结构（开发视图）](#%E6%A8%A1%E5%9D%97%E4%B8%BB%E8%A6%81%E7%9B%AE%E5%BD%95%E7%BB%93%E6%9E%84%E5%BC%80%E5%8F%91%E8%A7%86%E5%9B%BE)
 - [如何开始](#%E5%A6%82%E4%BD%95%E5%BC%80%E5%A7%8B)
 - [可重用组件](#%E5%8F%AF%E9%87%8D%E7%94%A8%E7%BB%84%E4%BB%B6)
    - [通用功能](#%E9%80%9A%E7%94%A8%E5%8A%9F%E8%83%BD)
    - [数据绑定支持](#%E6%95%B0%E6%8D%AE%E7%BB%91%E5%AE%9A%E6%94%AF%E6%8C%81)
    - [UI层](#ui%E5%B1%82)
 - [外部依赖：Libraries/Frameworks/Widgets](#%E5%A4%96%E9%83%A8%E4%BE%9D%E8%B5%96librariesframeworkswidgets)
 - [代码质量](#%E4%BB%A3%E7%A0%81%E8%B4%A8%E9%87%8F)
 - [注意事项](#%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9)
 - [License](#license)

## 为什么采用 [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel)？
作为客户端应用开发，MVVM是一个比其他的MV*模式（如MVC、MVP）更好的设计模式。 

为什么这么说？简单来讲，View Model是对View的数据和行为的抽象，比起其他的MV*模式有更高的抽象层次。另外，View Model对View进行了解耦，并不对View有任何直接依赖，而是通过数据绑定进行通知。更高的抽象层次和更彻底的解耦会带来更清晰简洁的架构。

任何收益都是有代价的，抽象和解耦也不是没有成本。如果应用的复杂程度达到一定水平，这些初始阶段的投入会很快得到回报，带来开发效率的提高。但对于简单的应用，就是杀鸡用牛刀了。

对这个简单的Demo项目来说，采用MVVM看起来是有一点过度设计了。不过我的主要目的是为了展示如何使用MVVM搭建一个实际的应用，以及如何处理其中面对的问题。而且，如果目标是完成一个像"The Movie DB"客户端这样功能完整的应用，这个项目的框架会是一个好的起点。

## 应用截屏

![Now Playing Page](https://s1.ax1x.com/2018/03/11/9WRzRJ.gif) ![Movie Details Page](https://s1.ax1x.com/2018/03/11/9WW9MR.gif) ![Favorites](https://s1.ax1x.com/2018/03/11/9WWSz9.gif)
## 应用功能简介
本应用有如下功能：
* 主页显示三个Tab：正在上映、收藏、设置。
* 正在上映Tab显示当前正在上映的电影列表。
    * 每个电影显示为网格中的一张海报。图片上有评分的星数。如果电影被收藏，还会有一个心形图标。
    * 列表支持页首下拉刷新，页末上拉加载下一页。
* 收藏Tab显示本地收藏列表，功能和正在上映列表相同。
* 点击电影海报会跳转到电影详情页面，显示额外的详细信息，如宽幅背景图，宣传语以及类似电影列表。
    * 页面中的浮动按钮在加载时会显示加载动画。加载完成后会显示收藏状态按钮，点击按钮可以添加/删除收藏。
    * 上拉可加载类似电影列表的下一页。
* 在设置页面，可以清除应用的缓存（HTTP、图片）以及本地收藏。
## 应用架构
### 架构分层
![](https://s1.ax1x.com/2018/03/11/9WRviF.png)
* 依赖关系
    * 自上而下的单向依赖。下层通过以下形式与上层通讯：
        * View Model通过数据绑定通知UI。
        * Model层通过观察者模式通知View Model层。
        * Repository层则只有通过返回值返回信息给Model层。
    * 依赖关系通过依赖注入 (DI, dependency injection)框架进行注入。View Model以及Model层的类都是可以进行单元测试的。
* View model层不依赖于任何的UI组件，对Android平台的依赖也越小越好。（现有的依赖：Android数据绑定框架、RxAndroid中的AndroidSchedulers以及SparseArray）
* Model层是整个应用的领域模型，应与UI和平台无关。换句话说，就是既不依赖于应用的交互及UI设计，也不依赖于安卓平台。（本项目中依赖了SparseArray，但只是出于性能的需要，如果有必要可随时替换。）
* Repository层是对数据访问的抽象，包括本地数据（Shared Preferences, SQLite数据库）及外部Web APIs。
### 主要组件
![](https://s1.ax1x.com/2018/04/11/CAZ46U.png)

大部分的类名的含义都很明显。不是那么明显的：
* [`TmdbConfig`](app/src/main/java/com/github/brianspace/moviebrowser/models/TmdbConfig.java)：用于处理来自TMDb网站的配置信息。参见<https://developers.themoviedb.org/3/configuration/get-api-configuration>。[`IImageConfig`](app/src/main/java/com/github/brianspace/moviebrowser/models/IImageConfig.java)是供上面的View Model层获取图片路径的接口。
* [`EntityStore`](app/src/main/java/com/github/brianspace/moviebrowser/models/EntityStore.java)是用来保存Model层实体对象（弱）引用的对象存储器。
* [`DataCleaner`](app/src/main/java/com/github/brianspace/moviebrowser/models/DataCleaner.java)用于清除应用的HTTP、图片缓存以及本地的电影收藏数据库。
* [`IConfigStore`](app/src/main/java/com/github/brianspace/moviebrowser/repository/IConfigStore.java)用于存储配置信息。
* [`IFavoriteStore`](app/src/main/java/com/github/brianspace/moviebrowser/repository/IFavoriteStore.java)用于存储用户收藏的电影。
* [`IMovieDbService`](app/src/main/java/com/github/brianspace/moviebrowser/repository/IMovieDbService.java)用于请求"The Movie DB"的Web API。
### 应用的关键设计决策
1. 采用Model-View-ViewModel (MVVM) 结构，利用原生的[安卓数据绑定支持](https://developer.android.google.cn/topic/libraries/data-binding/index.html)。
2. 用依赖注入解耦：使用[Dagger-2](http://google.github.io/dagger/)框架。
3. 异步接口：I/O操作在后台线程进行。采用[RxJava](https://github.com/ReactiveX/RxJava)+[RxAndroid](https://github.com/ReactiveX/RxAndroid)实现。
4. Activity跳转：基于URI，Activity之间无依赖。主要实现见[NavigationHelper](app/src/main/java/com/github/brianspace/moviebrowser/ui/nav/NavigationHelper.java)。
5. 对象生命周期
    * Model层的实体（Entities）由[EntityStore](app/src/main/java/com/github/brianspace/moviebrowser/models/EntityStore.java)维持WeakReference引用，以保证唯一性（每一部电影只对应一个对象实例）并防止内存泄露。这样同一个实体的不同视图就能通过实体发出的变化通知保持同步。
    * View Model层对象和对应的视图有同样的生命周期，和展示的视图是一一对应的关系。如若不然，一方面会给理解对应关系带来困扰，而且生命周期的不一致会带来各种麻烦（相信我，开始的时候View Model也是唯一的）。
### 模块/主要目录结构（开发视图）
作为系统结构的开发视图，清晰的模块划分和目录结构（对应Java的包结构）也是构成清晰架构必不可少的一部分。
* 模块划分
    - app：应用主模块
    - lib-common：通用功能模块
    - lib-databinding：数据绑定支持模块
    - lib-widgets：可重用UI控件模块
* App模块目录结构
    - di：依赖注入组件
        - components: Dagger的[Component](https://google.github.io/dagger/api/latest/dagger/Component.html)和[Subcomponent](https://google.github.io/dagger/subcomponents)
        - modules：Dagger的[Module](https://google.github.io/dagger/api/latest/dagger/Module.html)
        - qualifiers：依赖注入的限定注解（qualifiers）
    - models：Model层类定义
    - repository：数据访问层
        - data：用于反序列化JSON的数据类型定义
        - local：本地存储，包括配置文件（SharedPreferences）和数据库
        - util：工具类
        - web：用于访问TMDb的Web API
    - ui：UI层
        - activity：应用中的Activity
        - databinding：数据绑定所需的BindingAdapter
        - fragment：应用中的Fragment
        - nav：跳转辅助工具类NavigationHelper
        - view：项目相关的UI控件
    - viewmodels：View Model层类定义
* Common模块目录结构
    - objstore：对象池
    - observable：对象及集合的观察者模式实现
    - util：工具类
* Databinding模块目录结构
    - adapter：RecyclerView的adapter，支持绑定到ObservableList。
    - message：基于数据的消息提示机制，以便通过View Model显示消息（现在是Toast）而不用依赖UI组件。
* Widgets模块目录结构
    - behaviors：CoordinatorLayout的behavior
    - utils：工具类。现在有ImageLoader。
    - widgets：项目无关、可重用的UI控件。
## 如何开始
在你想自己编译运行这个应用之前，需要先按照[TMDb的指引](https://developers.themoviedb.org/3/getting-started/introduction)去注册一个开发者帐号并拿到API key，然后把API key接入项目的`gradle.properties`文件中：
```properties
# API Key for the TMDb API
API_KEY="xxxxx"
```
参考：<https://developers.themoviedb.org/3/getting-started/authentication>
## 可重用组件
项目无关的可重用组件放在了单独的module里面：
### 通用功能
* [Observables](lib-common/src/main/java/com/github/brianspace/common/observable)：支持观察者的注册、注销以及事件通知的分发，通过弱引用以避免内存泄露。
* [ObjectStore](lib-common/src/main/java/com/github/brianspace/common/util/ObjectStore.java)以及[ModelObjectStore](lib-common/src/main/java/com/github/brianspace/common/util/ModelObjectStore.java)：线程安全的对象池，保证一个key只对应一个对象。
### 数据绑定支持
* [RecyclerViewDatabindingAdapter](lib-databinding/src/main/java/com/github/brianspace/databinding/adapter/RecyclerViewDatabindingAdapter.java)：RecyclerView的adapter，支持绑定到ObservableList。
* [HeaderedRecyclerViewDatabindingAdapter](lib-databinding/src/main/java/com/github/brianspace/databinding/adapter/HeaderedRecyclerViewDatabindingAdapter.java)：带列表头的RecyclerView的adapter，支持绑定到ObservableList。列表头绑定到元素列表之外的对象上。
### UI层
* [DynamicGridView](lib-widgets/src/main/java/com/github/brianspace/widgets/DynamicGridView.java)：根据预先设置的单元格宽度，能够自动调整列数的（基于RecyclerView的）GridView。 元素之间的间隙自动调整以便均匀分布，列表头部分则可不留间隙占据所有宽度。
* [FixedAspectRatioImage](lib-widgets/src/main/java/com/github/brianspace/widgets/FixedAspectRatioImage.java)：保持预先（通过属性）设置的高宽比的AppCompatImageView。
* [ImageLoader](lib-widgets/src/main/java/com/github/brianspace/utils/ImageLoader.java)：图片加载器（使用Glide）。可根据需要在图片宽度确定后再加载图片。
* [AutoHideWhenScrollDownBehavior](lib-widgets/src/main/java/com/behaviors/AutoHideWhenScrollDownBehavior.java)：CoordinatorLayout的behavior，可让目标控件（如`BottomNavigationView`）在窗口向下滑动的时候自动隐藏。

## 外部依赖：Libraries/Frameworks/Widgets
本项目中展示了如何使用下列常用库、框架以及控件：
* [Dagger-2](http://google.github.io/dagger/)：用于依赖注入，包括如何使用为安卓特制的[AndroidInjector](https://google.github.io/dagger/android.html)来为安卓组件进行依赖注入。基于代码生成而不是反射，非常适合需要进行代码混淆的安卓项目。
* RFP (Reactive Functional Programming)及异步：[RxJava](https://github.com/ReactiveX/RxJava)及[RxAndroid](https://github.com/ReactiveX/RxAndroid)。
* Restful客户端: [Retrofit](https://square.github.io/retrofit/) + [OkHttp](http://square.github.io/okhttp/) + [RxJava](https://github.com/ReactiveX/RxJava) + [GSON](https://github.com/google/gson)。
* ORM框架：[Room](https://developer.android.google.cn/topic/libraries/architecture/room.html)
* UI控件：
    * [CoordinatorLayout](https://developer.android.google.cn/reference/android/support/design/widget/CoordinatorLayout.html) + [AppBarLayout](https://developer.android.google.cn/reference/android/support/design/widget/AppBarLayout.html) + [CollapsingToolbarLayout](https://developer.android.google.cn/reference/android/support/design/widget/CollapsingToolbarLayout.html) + [FloatingActionButton](https://developer.android.google.cn/reference/android/support/design/widget/FloatingActionButton.html)
    * [CoordinatorLayout](https://developer.android.google.cn/reference/android/support/design/widget/CoordinatorLayout.html) + [BottomNavigationView](https://developer.android.google.cn/reference/android/support/design/widget/BottomNavigationView.html)以及[AutoHideWhenScrollDownBehavior](lib-widgets/src/main/java/com/behaviors/AutoHideWhenScrollDownBehavior.java)
    * [ConstraintLayout](https://developer.android.google.cn/reference/android/support/constraint/ConstraintLayout.html)
    * [RecyclerView](https://developer.android.google.cn/reference/android/support/v7/widget/RecyclerView.html)
* 使用[Glide](https://github.com/bumptech/glide)加载图片。
* View binding：[Butterknife](https://github.com/JakeWharton/butterknife)，避免`findViewById`。
* 单元测试框架：
  * [Mockito](https://github.com/mockito/mockito) mock框架
  * [Robolectric](http://robolectric.org/) 单元测试框架，在本地对依赖安卓SDK的类进行单元测试。
* 用浏览器查看应用的SQLite数据库以及SharedPreferences：[Android-Debug-Database](https://github.com/amitshekhariitbhu/Android-Debug-Database)
## 代码质量
为保证代码质量，本项目使用了我的另一个项目（作为submodule）：
* [Android-Quality-Essentials](https://github.com/BrianSpace/Android-Quality-Essentials)，使用CheckStyle、FindBugs、Lint以及PMD进行代码静态检查。

## 注意事项
APK签名的设置来自项目的`gradle.properties`文件。如果你想用自己的签名，需要加入下列属性值：
```properties
# signingConfigs for release build
RELEASE_STORE_FILE=xxx.xxx
RELEASE_STORE_PASSWORD=xxx
RELEASE_KEY_ALIAS=xxx
RELEASE_KEY_PASSWORD=xxx
```

License
=======

  Copyright (C) 2018, Brian He
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

