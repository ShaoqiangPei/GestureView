
### GestureView使用说明
[![](https://jitpack.io/v/ShaoqiangPei/GestureView.svg)](https://jitpack.io/#ShaoqiangPei/GestureView)
#### 简介
这是一个手势密码使用功能的封装库，主要用来设置手势密码和手势密码验证,调用简单方便。

#### 一. 库引用配置
在你项目中的project对应的build.gradle中添加如下配置：
```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
然后在你app_module对应的build.gradle中添加引用
```
	dependencies {
	        implementation 'com.github.ShaoqiangPei:GestureView:Tag'
	}
```
Tag为引用库版本，如我要引用1.0.0版本，可以这样引用：
```
	dependencies {
	        implementation 'com.github.ShaoqiangPei:GestureView:1.0.0'
	}
```
#### 二.具体使用
##### 2.1 在布局中引用GestureLockView控件
xml 中引用示例如下：
```
    <com.library.fuction.GestureLockView
        android:id="@+id/gestureLockView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```
##### 2.2 方法介绍
手势密码库的一系列方法都封装到 GestureHelper 类中,下面对GestureHelper中一系列方法的使用做一个简单介绍
###### 2.2.1 GestureHelper初始化
GestureHelper初始化需要传一个GestureLockView对象，如下
```
public GestureHelper(GestureLockView lockView)
```
###### 2.2.2 设置连线颜色
```
setLineColor(int lineColor)
```
###### 2.2.3 设置正常情况下资源id
```
setNormalId(int drawableId)
```
###### 2.2.4 设置选中情况下资源id
```
setSelectId(int drawableId)
```
###### 2.2.5 设置错误情况下资源id
```
setErrorId(int drawableId)
```
###### 2.2.6 设置至少连接的点数
```
setPointCount(int pointCount)
```
###### 2.2.7 设置GestureLockView尺寸
```
setLockViewSize(int dp)
```
###### 2.2.8 设置手势密码
```
setGesturePwd(OnSettingListener onSettingListener)
```
###### 2.2.9 验证手势密码
其中 password 为传入的要验证的密码
``` 
verifyGesturePwd(String password,OnVerifyListener onVerifyListener)
```
#### 三.使用示例
##### 3.1 需要在xml布局中引入GestureLockView控件
```
    <com.library.fuction.GestureLockView
        android:id="@+id/gestureLockView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```
##### 3.2 在activity中初始化 GestureLockView 和 GestureHelper
```
    @BindView(R.id.gestureLockView)
    GestureLockView mLockView;
    
    private GestureHelper mGestureHelper;
```
```
    mGestureHelper=new GestureHelper(mLockView);
```
##### 3.3 在activity中对GestureLockView进行基本设置
这里我们可以全部设置，也可以根据具体情况做部分设置或不设置,以下给一个设置的示例：
```
        //基本配置
        mGestureHelper.setErrorId(R.drawable.ic_return)//设置错误图标
           .setNormalId(R.drawable.ic_check)//设置错误图标
           .setSelectId(R.drawable.ic_about)//设置选中图标
           .setLineColor(Color.BLUE)//设置连线颜色
           .setStrokeWidth(5f)//设置连线宽度
           .setPointCount(5)//设置最少连接点数
```
##### 3.4 在activity中对GestureLockView设置背景
如果你想给GestureLockView设置一个背景或背景色，你可以直接在GestureLockView上设置，类似这样
```
        //设置GestureLockView背景色为黑色
        mLockView.setBackgroundColor(Color.BLACK);
```
##### 3.4 在activity中对GestureLockView设置尺寸
一般的GestureLockView在界面上会显示成正方形,如果你想将 GestureLockView 的宽高设置成手机竖屏时的宽高并与手机宽度保持些许间距，你可以这样设置
```
        //设置mLockView大小
        mGestureHelper.setLockViewSize(ScreenUtil.getWidth()-ScreenUtil.dp2px(20,mContext));
```
##### 3.4 GestureLockView设置手势密码
```
        //设置密码监听
        mGestureHelper.setGesturePwd(new GestureHelper.OnSettingListener() {
            @Override
            public void minPointCountError(int pointCount) {
                ToastUtil.shortShow("请至少设置" + pointCount + "个点");
            }

            @Override
            public void repeadPassword(String msg) {
                ToastUtil.shortShow("请再设置一次");
            }

            @Override
            public void setGesture(String simplePattern) {
                if (simplePattern != null) {
                    ToastUtil.shortShow("设置成功!密码为:" + simplePattern);
                    //这里你可以保存密码
                    //......
                    //关闭当前界面
                    //......
                } else {
                    ToastUtil.shortShow("设置失败，请重试");
                }
            }
        });
```
以上需要注意的是，当 simplePattern 返回 null时表示设置手势密码失败,不为 null 的时候，表示设置手势密码成功,
你可以在设置密码成功的时候做保存密码和关闭当前设置密码界面的操作。
##### 3.4 GestureLockView验证手势密码密码
```
       //验证手势密码的监听
        mGestureHelper.verifyGesturePwd(PASS_WORD, new GestureHelper.OnVerifyListener() {
            @Override
            public void minPointCountError(int pointCount) {
                ToastUtil.shortShow("请至少设置" + pointCount + "个点");
            }

            @Override
            public void verifyGesture(boolean access) {
                if(access){
                    ToastUtil.shortShow("验证成功");
                    //关闭当前界面
                    //......
                }else{
                    ToastUtil.shortShow("手势密码错误！");
                }
            }
        });
```
验证手势密码时需要注意的是，这里的 PASS_WORD 是你需要验证的手势密码，当access返回false时表示验证失败，
access返回true时表示验证成功.验证成功的时候，你可以做关闭当前手势验证界面的操作。








