# TestPullToScale
这是一个支持ListView和ScrollView上推时，先缩放headerView，然后再响应listview和ScrollView的滑动操作；
下拉时则先响应listView和ScrollView的滑动操作再放大headerView;

注意：
由于当ContentView布局复杂时，改变HeadView的高度时，ContentView的高度也会变化，进而导致contentView重绘，
使得滑动过程及动画卡顿严重，所以请设置setContentViewMaxHeight(int height)和setAllowResetContentViewMaxHeight(
boolean allowResetContentViewMaxHeight)两个方法；
组件将会在用户第一次触摸时，为ContentView设置最大值，进而避免滑动过程中ContentView重绘的问题；

setContentViewMaxHeight(int height)是为ContentView指定ContentView的最大高度，
	如果不指定，将自动已当前ContentView的高度 + headerView的最大高度 - headerView的最小高度为ContentView的最大高度；
setAllowResetContentViewMaxHeight(
			boolean allowResetContentViewMaxHeight)设置标记是否允许在第一次触摸时设置ContentView的最大高度，默认为true允许；

*新增缩放过程中阴影遮罩动画；即在缩放HeaderView时headerView上方会有阴影从浅到深逐渐变化；
 使用方法：调用该组件的setCoverViewId(int id)方法，设置HeaderView中用于遮罩的View的id给该组件，
 则该组件会在缩放过程中自动改变该遮罩View的透明度；

拓展：
	如果需要拓展别的组件，请务必重写setContentViewMaxHeight()虚函数，实现为contentView设置一个显示时的最大高度；
	逻辑应该如下：
	如果用户指定了大于等于0的ContentViewMaxHeight，则以此值为准设置给ContentView；
	否则将自动以当前ContentView的高度 + headerView的最大高度 - headerView的最小高度为ContentView的最大高度；
	代码实现参考PullToScaleListView和PullToScaleScrollView中的setContentViewMaxHeight()方法；
	
			
效果截图：
![image1](https://github.com/ZhangSir/TestPullToScale/blob/master/Screenshot_2015-11-09-15-35-38.jpeg)
![image2](https://github.com/ZhangSir/TestPullToScale/blob/master/Screenshot_2015-11-09-15-35-46.jpeg)
