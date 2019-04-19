package com.library.fuction;

import android.app.Service;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.library.R;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created pei
 */
public class GestureLockView extends View {

    private Context context;
    private Paint paint;
    private Paint linePaint;
    private Bitmap bitmapPointError;
    private Bitmap bitmapPointNormal;
    private Bitmap bitmapPointPress;

    private int bitmapR;
    private boolean pointInited = false;
    private int width;
    private int height;
    private Point[][] points;
    private ArrayList<Point> pointList;
    private ArrayList<Integer> passList;

    /**
     * 用于标识当前是否正在绘制
     */
    private boolean isDraw = false;
    private float mouseX, mouseY;
    private OnDrawFinishedListener listener;

    public GestureLockView(Context context) {
        super(context);
        this.context=context;
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(context, attrs);
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init(context, attrs);
    }

    /**设置线颜色**/
    public void setLineColor(int lineColor){
        if(linePaint!=null){
            linePaint.setColor(lineColor);
        }
    }

    /**设置线宽**/
    public void setStrokeWidth(float width){
        if(linePaint!=null){
            linePaint.setStrokeWidth(width);
        }
    }

    /**设置正常情况下资源id**/
    public void setNormalId(int drawableId){
        bitmapPointNormal = BitmapFactory.decodeResource(getResources(),drawableId);
    }

    /**设置选中情况下资源id**/
    public void setSelectId(int drawableId){
        bitmapPointPress = BitmapFactory.decodeResource(getResources(),drawableId);
    }

    /**设置错误情况下资源id**/
    public void setErrorId(int drawableId){
        bitmapPointError = BitmapFactory.decodeResource(getResources(),drawableId);
    }

    private void init(Context context, AttributeSet attrs) {
        setBackgroundColor(Color.TRANSPARENT);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GestureLockView);
        int normal = a.getResourceId(R.styleable.GestureLockView_bitmapNormal, R.mipmap.normal_gesture);
        int select = a.getResourceId(R.styleable.GestureLockView_bitmapSelect, R.mipmap.select_gesture);
        int error = a.getResourceId(R.styleable.GestureLockView_bitmapError, R.mipmap.error_gesture);
        int lineColor = a.getColor(R.styleable.GestureLockView_lineColor, 0xFF773355);
        float strokeWidth = a.getDimension(R.styleable.GestureLockView_strokeWidth, 8);
        a.recycle();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(strokeWidth);
        //设置画线时的PorterDuffMode,这里的效果是让线画出来时位于点的图层的下面(即相交处点覆盖线)
        linePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

        //获得三种状态对应的位图
        bitmapPointError = BitmapFactory.decodeResource(getResources(),error);
        bitmapPointNormal = BitmapFactory.decodeResource(getResources(),normal);
        bitmapPointPress =getBitmap(context)!=null?getBitmap(context):BitmapFactory.decodeResource(getResources(),select);

        //获得位图半径
        bitmapR = bitmapPointError.getHeight() / 2;
        //初始化9个点
        points = new Point[3][3];
        pointList = new ArrayList<>();
        passList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        width = measureWidth - getPaddingLeft() - getPaddingRight();
        height = measureHeight - getPaddingTop() - getPaddingBottom();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!pointInited){
            //如果未对bitmap的坐标进行过初始化，则初始化
            initPoints();
        }
        canvas.drawBitmap(drawView(), 0, 0, null);
    }

    private Bitmap drawView() {
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        drawPoints(canvas);

        if(pointList.size() > 0){
            Point pointStart = pointList.get(0);
            for(int i = 1; i < pointList.size(); i++){
                Point pointEnd = pointList.get(i);
                drawLine(canvas, pointStart, pointEnd);
                pointStart = pointEnd;
            }
            if(isDraw){
                drawLine(canvas, pointStart, new Point(mouseX, mouseY));
            }
        }
        return target;
    }

    /**
     * 画相邻点直接的连线
     * @param canvas
     * @param pointStart 起始点
     * @param pointEnd 终止点
     */
    private void drawLine(Canvas canvas, Point pointStart, Point pointEnd) {
        if(pointStart.state == Point.STATE_PRESS || pointStart.state == Point.STATE_ERROR)
            //如果当前状态为按下或错误时，画点之间的连线
            canvas.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, linePaint);
    }

    /**
     * 画点
     * @param canvas
     */
    private void drawPoints(Canvas canvas) {
        Bitmap bitmap;
        for (Point[] point : points) {
            for (Point aPoint : point) {
                //根据不同的状态绘制不同的点
                if (aPoint.state == Point.STATE_NORMAL) {
                    bitmap = bitmapPointNormal;
                } else if (aPoint.state == Point.STATE_PRESS) {
                    bitmap = bitmapPointPress;
                } else {
                    bitmap = bitmapPointError;
                }
                canvas.drawBitmap(bitmap, aPoint.x - bitmapR, aPoint.y - bitmapR, paint);
            }
        }

    }

    private void initPoints() {
        //计算x方向和y方向相邻bitmap之间的间距
        int spaceX = (width - bitmapR * 6) / 2;
        int spaceY = (height - bitmapR * 6) / 2;

        points[0][0] = new Point(bitmapR, bitmapR);
        points[0][1] = new Point(bitmapR * 3 + spaceX, bitmapR);
        points[0][2] = new Point(bitmapR * 5 + spaceX * 2, bitmapR);

        points[1][0] = new Point(bitmapR, bitmapR * 3 + spaceY);
        points[1][1] = new Point(bitmapR * 3 + spaceX, bitmapR * 3 + spaceY);
        points[1][2] = new Point(bitmapR * 5 + spaceX * 2, bitmapR * 3 + spaceY);

        points[2][0] = new Point(bitmapR, bitmapR * 5 + spaceY * 2);
        points[2][1] = new Point(bitmapR * 3 + spaceX, bitmapR * 5 + spaceY * 2);
        points[2][2] = new Point(bitmapR * 5 + spaceX * 2, bitmapR * 5 + spaceY * 2);

        pointInited = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        int[] ij;
        int i, j;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                resetPoints();
                ij = getSelectPoint();
                if(null != ij){
                    isDraw = true;
                    i = ij[0];
                    j = ij[1];
                    points[i][j].state = Point.STATE_PRESS;
                    pointList.add(points[i][j]);
                    passList.add(i * 3 + j);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isDraw){
                    ij = getSelectPoint();
                    if(null != ij){
                        i = ij[0];
                        j = ij[1];
                        if(!pointList.contains(points[i][j])){
                            //不允许重复画同一个点
                            points[i][j].state = Point.STATE_PRESS;
                            pointList.add(points[i][j]);
                            passList.add(i * 3 + j);
                            startVibrate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean valid = false;
                if(null != listener && isDraw){
                    //回调接口，判断此次绘制是否成功
                    valid = listener.OnDrawFinished(passList);
                }
                if(!valid){
                    //如果不成功，将所有已连线的点的状态置为Error
                    for(Point point : pointList){
                        point.state = Point.STATE_ERROR;
                    }
                }
                isDraw = false;
                break;
        }
        postInvalidate();
        return true;
    }

    /**
     * 判断当前被选中的是哪个点
     * @return
     */
    private int[] getSelectPoint(){
        Point pointMouse = new Point(mouseX, mouseY);
        for(int i = 0; i < points.length; i++){
            for(int j = 0; j < points[i].length; j++){
                //遍历9个点，如果手指按下的位置距离点的圆心小于半径长度时，认为当前点被选中
                float distance = points[i][j].distance(pointMouse);
                if(distance < bitmapR){
                    int[] result = new int[2];
                    result[0] = i;
                    result[1] = j;
                    return result;
                }
            }
        }
        return null;
    }

    public void resetPoints(){
        pointList.clear();
        passList.clear();

        if(points==null){
            points = new Point[3][3];
            initPoints();
        }
        for (Point[] point : points) {
            for (Point aPoint : point) {
                aPoint.state = Point.STATE_NORMAL;
            }
        }
        postInvalidate();
    }

    private void startVibrate() {
        Vibrator vib = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(200);
    }

    private  Bitmap getMatrixBitmap(Bitmap bm){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) 2) / 3;
        float scaleHeight = ((float) 2) / 3;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,true);
        return newbm;
    }

    /**
     * 获取本应用图标 bitmap
     * @param context
     */
    public synchronized Bitmap getBitmap(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
        Bitmap bm = null;
        try {
            BitmapDrawable bd = (BitmapDrawable) d;
            bm = bd.getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    public interface OnDrawFinishedListener {
        boolean OnDrawFinished(List<Integer> passList);
    }

    public void setOnDrawFinishedListener(OnDrawFinishedListener listener) {
        this.listener = listener;
    }
}
