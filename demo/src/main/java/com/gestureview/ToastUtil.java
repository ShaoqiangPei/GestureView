package com.gestureview;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Description:
 * <p>
 * Author:pei
 * Date: 2019/4/9
 */
public class ToastUtil {

    public static void shortShow(Context context,String msg){
        if(!TextUtils.isEmpty(msg)){
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        }
    }
}
