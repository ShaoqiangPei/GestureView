package com.gestureview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.library.fuction.GestureHelper;
import com.library.fuction.GestureLockView;
import com.library.widget.TitleBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Description:验证密码
 * <p>
 * Author:pei
 * Date: 2019/4/2
 */
public class VerifyGestureActivity extends AppCompatActivity {

    private TitleBar mTitleBar;
    private TextView mTvMessage;
    private GestureLockView mLockView;
    private GestureHelper mGestureHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_gesture);

        initView();
        initData();
        setListener();
    }

    private void initView(){
        mTitleBar=findViewById(R.id.title);
        mTvMessage=findViewById(R.id.tv_message);
        mLockView=findViewById(R.id.gestureLockView);

        mTitleBar.getTvTitle().setVisibility(View.VISIBLE);
        mTitleBar.getTvTitle().setText("验证手势密码");

        mTvMessage.setTextColor(Color.WHITE);
        mTvMessage.setText("验证密码为:0124678");
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

        //验证密码
        mGestureHelper.verifyGesturePwd("0124678", new GestureHelper.OnVerifyListener() {
            @Override
            public void minPointCountError(int pointCount) {
                ToastUtil.shortShow(VerifyGestureActivity.this, "请至少设置" + pointCount + "个点");
            }

            @Override
            public void verifyGesture(boolean access) {
                if(access){
                    ToastUtil.shortShow(VerifyGestureActivity.this, "验证成功！");
                    VerifyGestureActivity.this.finish();
                }else{
                    ToastUtil.shortShow(VerifyGestureActivity.this, "手势密码错误！");
                }
            }
        });
    }

}
