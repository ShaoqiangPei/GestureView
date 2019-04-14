package com.library.fuction;

import android.os.Handler;
import android.view.ViewGroup;

import java.util.List;

/**
 * Description:手势帮助类
 * <p>
 * Author:pei
 * Date: 2019/4/8
 */
public class GestureHelper {

    /**默认设置连接点数**/
    private static final int DEFAULT_POINT_COUNT=4;

    private String mLastGesturePwd;//上次设置密码
    private int mCount;//重复设置次数
    private int mPointCount;//设置连接点数
    private GestureLockView mLockView;

    public GestureHelper(GestureLockView lockView){
        this.mLockView=lockView;
    }


    /**设置连线颜色**/
    public GestureHelper setLineColor(int lineColor){
        mLockView.setLineColor(lineColor);
        return GestureHelper.this;
    }

    /**设置连线线宽**/
    public GestureHelper setStrokeWidth(float width){
        mLockView.setStrokeWidth(width);
        return GestureHelper.this;
    }

    /**设置正常情况下资源id**/
    public GestureHelper setNormalId(int drawableId){
        mLockView.setNormalId(drawableId);
        return GestureHelper.this;
    }

    /**设置选中情况下资源id**/
    public GestureHelper setSelectId(int drawableId){
        mLockView.setSelectId(drawableId);
        return GestureHelper.this;
    }

    /**设置错误情况下资源id**/
    public GestureHelper setErrorId(int drawableId){
        mLockView.setErrorId(drawableId);
        return GestureHelper.this;
    }


    /**设置至少连接的点数**/
    public GestureHelper setPointCount(int pointCount){
        if(pointCount<=0){
            throw new SecurityException("====pointCount 请输入大于零的值=======");
        }
        mPointCount=pointCount;
        return GestureHelper.this;
    }

    /**设置LockView尺寸**/
    public void setLockViewSize(int dp){
        ViewGroup.LayoutParams params= mLockView.getLayoutParams();
        params.height= dp;
        params.width= dp;
        mLockView.setLayoutParams(params);
    }

    /**设置手势密码**/
    public void setGesturePwd(OnSettingListener onSettingListener){
        mLockView.setOnDrawFinishedListener(new GestureLockView.OnDrawFinishedListener() {
            @Override
            public boolean OnDrawFinished(List<Integer> passList) {
                mPointCount=getPointCount();
                if (passList.size() < mPointCount) {
                    //至少连接mPointCount个点，请重试
                    onSettingListener.minPointCountError(mPointCount);
                    delayClearLockView();
                    //错误的时候return false
                    return false;
                }
                StringBuilder sb = new StringBuilder();
                for (Integer i : passList) {
                    sb.append(i);
                }
                String simplePattern = sb.toString();
                mCount++;
                if (mCount == 1) {
                    mLastGesturePwd = simplePattern;
                    //请再设置一次
                    onSettingListener.repeadPassword("请再设置一次");
                    delayClearLockView();
                    //没有错误的时候return true
                    return true;
                } else {
                    if (simplePattern.equals(mLastGesturePwd)) {
                        mCount = 0;
                        mLastGesturePwd = null;
                        //设置成功(存储密码，关闭当前activity)
                        onSettingListener.setGesture(simplePattern);
                    } else {
                        //两次手势密码设置不一致，请重新设置
                        onSettingListener.setGesture(null);
                        mCount = 0;
                        mLastGesturePwd = null;
                        delayClearLockView();
                        //错误的时候return false
                        return false;
                    }
                }
                //没有错误的时候return true
                return true;
            }
        });
    }

    /**验证手势密码**/
    public void verifyGesturePwd(String password,OnVerifyListener onVerifyListener){
        mLockView.setOnDrawFinishedListener(new GestureLockView.OnDrawFinishedListener() {
            @Override
            public boolean OnDrawFinished(List<Integer> passList) {
                mPointCount=getPointCount();
                if (passList.size() < mPointCount) {
                    //至少连接mPointCount个点，请重试
                    onVerifyListener.minPointCountError(mPointCount);
                    delayClearLockView();
                    return false;
                }
                StringBuilder sb = new StringBuilder();
                for (Integer i : passList) {
                    sb.append(i);
                }
                String simplePattern = sb.toString();
                if (!simplePattern.equals(password)) {//手势错误
                    //手势密码错误
                    onVerifyListener.verifyGesture(false);
                    delayClearLockView();
                    return false;
                }
                //手势密码正确
                onVerifyListener.verifyGesture(true);
                delayClearLockView();
                return true;
            }
        });
    }

    /**清除密码**/
    private void delayClearLockView() {
        new Handler().postDelayed(() -> mLockView.resetPoints(), 600);
    }

    private int getPointCount(){
        return mPointCount>0?mPointCount:DEFAULT_POINT_COUNT;
    }

    /**设置密码的监听**/
    public interface OnSettingListener{
        //至少连接mPointCount个点
        void minPointCountError(int pointCount);
        //请再设置一次
        void repeadPassword(String msg);
        //设置成功/失败---成功时返回密码，失败时返回null
        void setGesture(String simplePattern);
    }

    /**验证密码的监听**/
    public interface OnVerifyListener{
        //至少连接mPointCount个点
        void minPointCountError(int pointCount);
        //手势密码是否正确(正确返回true,错误返回false)
        void verifyGesture(boolean access);
    }

}
