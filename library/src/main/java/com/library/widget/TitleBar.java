package com.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.library.R;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Description:标题栏
 * <p>
 * Author:pei
 * Date: 2019/3/21
 */
public class TitleBar extends ConstraintLayout {

    private View mLayoutView;
    private Context mContext;

    private ImageView mImvLeft;
    private ImageView mImvRight;
    private TextView mTvLeft;
    private TextView mTvRight;
    private TextView mTvTitle;

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLayoutView = LayoutInflater.from(context).inflate(R.layout.title_bar_layout, this);
        this.mContext = context;

        initView();
        initViewGone();
    }

    /**用于初始化控件的**/
    private <T> T getView(int rId) {
        View view = TitleBar.this.findViewById(rId);
        return (T) view;
    }

    private void initView(){
        mImvLeft=getView(R.id.imv_left);
        mImvRight=getView(R.id.imv_right);
        mTvLeft=getView(R.id.tv_left);
        mTvRight=getView(R.id.tv_right);
        mTvTitle=getView(R.id.tv_title);
    }

    private void initViewGone(){
        mImvLeft.setVisibility(View.GONE);
        mImvRight.setVisibility(View.GONE);
        mTvLeft.setVisibility(View.GONE);
        mTvRight.setVisibility(View.GONE);
        mTvTitle.setVisibility(View.GONE);

    }

    public ImageView getImvLeft() {
        return mImvLeft;
    }


    public ImageView getImvRight() {
        return mImvRight;
    }

    public TextView getTvLeft() {
        return mTvLeft;
    }

    public TextView getTvRight() {
        return mTvRight;
    }

    public TextView getTvTitle() {
        return mTvTitle;
    }


}