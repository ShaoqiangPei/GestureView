package com.gestureview;

import android.os.Bundle;
import android.view.View;

import com.library.fuction.GestureHelper;
import com.library.fuction.GestureLockView;
import com.library.widget.TitleBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Description:设置密码
 * <p>
 * Author:pei
 * Date: 2019/4/2
 */
public class SetGestureActivity extends AppCompatActivity {

    private TitleBar mTitleBar;
    private GestureLockView mLockView;
    private GestureHelper mGestureHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_gesture);

        initView();
        initData();
        setListener();
    }

    private void initView(){
        mTitleBar=findViewById(R.id.title);
        mLockView=findViewById(R.id.gestureLockView);

        mTitleBar.getTvTitle().setVisibility(View.VISIBLE);
        mTitleBar.getTvTitle().setText("设置手势密码");
    }

    private void initData(){
        //初始化
        mGestureHelper=new GestureHelper(mLockView);
    }

    private void setListener(){
        //基本配置
//        mGestureHelper.setErrorId(R.drawable.ic_return)//设置错误图标
//                .setNormalId(R.drawable.ic_check)//设置错误图标
//                .setSelectId(R.drawable.ic_about)//设置选中图标
//                .setLineColor(Color.BLUE)//设置连线颜色
//                .setStrokeWidth(5f)//设置连线宽度
//                .setPointCount(5)//设置最少连接点数

        //设置密码
        mGestureHelper.setGesturePwd(new GestureHelper.OnSettingListener() {
                    @Override
                    public void minPointCountError(int pointCount) {
                        ToastUtil.shortShow(SetGestureActivity.this, "请至少设置" + pointCount + "个点");
                    }

                    @Override
                    public void repeadPassword(String msg) {
                        ToastUtil.shortShow(SetGestureActivity.this, "请再设置一次");
                    }

                    @Override
                    public void setGesture(String simplePattern) {
                        if (simplePattern != null) {
                            ToastUtil.shortShow(SetGestureActivity.this, "设置成功!密码为:" + simplePattern);
                            //关闭当前界面
                            SetGestureActivity.this.finish();
                        } else {
                            ToastUtil.shortShow(SetGestureActivity.this, "设置失败，请重试");
                        }
                    }
                });
    }

}
