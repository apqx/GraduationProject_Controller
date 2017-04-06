package me.apqx.controller.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import me.apqx.controller.R;
import me.apqx.controller.tools.Tools;

/**
 * Created by chang on 2016/8/19.
 *
 */
public class ControllerView extends View {
    private Paint paintUp=new Paint();
    private Paint paintDown=new Paint();
    private Paint paintLeft=new Paint();
    private Paint paintRight=new Paint();
    private Paint paintBackGround=new Paint();
    private Paint paintCenterPoint=new Paint();
    private Paint paintLine;
    private Path pathArrow;
    //中心控制点圆心坐标
    private int x;
    private int y;
    //View中心绝对坐标
    private int centerX;
    private int centerY;
    //中心控制圆半径
    private int centerRadius;
    //中心控制圆圆心距原点的最大距离
    private int centerLength;
    //边界圆半径
    private int boardRadius;
    //表示百分比的圆半径
    private int presentRadius;
    private boolean isInit=true;
    //监听器
    private OnControllerListener listener;
    //命令状态
    private int whichIsOn;
    private final int UP=1;
    private final int DOWN=2;
    private final int RIGHT=3;
    private final int LEFT=4;
    private final int STOP=5;
    //当前点击坐标
    private int currentX,currentY;
    //最大速度和最低速度
    private int maxVelocity=10,minVelocity=1;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec),measure(heightMeasureSpec));
    }

    private int measure(int measureSpec){
        int result;
        int specMod=MeasureSpec.getMode(measureSpec);
        int specSize=MeasureSpec.getSize(measureSpec);
        if (specMod==MeasureSpec.EXACTLY){
            result=specSize;
        }else {
            result=400;
            if (specMod==MeasureSpec.AT_MOST){
                result=Math.min(result,specSize);
            }
        }
        return result;
    }

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width=getMeasuredWidth();
        int height=getMeasuredHeight();
        centerX=width/2;
        centerY=height/2;
        //填充View的最大圆半径
        int radius=Math.min(width,height)/2;
        //斜矩形边长
        int length=(int)(radius/Math.sin(Math.PI/4));
        //箭头顶点距坐标轴距离
        int arrowPoint=length*3/8;
        //箭头单边长度
        int arrowLength=length/5;
        //箭头宽度
        int arrowWidth=length/14;
        //控制中心圆半径
        centerRadius=length/7;
        //中心控制圆圆心距原点的最大距离
        centerLength=length/6;
        //圆角矩形的圆角半径
        int roundRectRadius=length/10;
        //View边界圆半径
        boardRadius=Math.min(width/2,height/2);


        paintBackGround.setColor(Color.parseColor("#607d8b"));
        paintBackGround.setStyle(Paint.Style.FILL);
        paintBackGround.setAntiAlias(true);

        canvas.translate(width/2,height/2);
        canvas.save();
        canvas.rotate(45);
//        //画斜圆角矩形
//        canvas.drawRoundRect(new RectF(-length/2,-length/2,length/2,length/2),roundRectRadius,roundRectRadius,backGround);
        //画边界圆
        canvas.drawCircle(0,0,boardRadius,paintBackGround);
        //画表示进度的圆
        paintBackGround.setColor(getContext().getResources().getColor(R.color.colorHighLight));
        canvas.drawCircle(0,0,presentRadius,paintBackGround);

        //画四个指示方向的箭头
        pathArrow=new Path();
        if (whichIsOn==0||whichIsOn==STOP){
            paintUp.setColor(Color.parseColor("#f5f5f5"));
            paintRight.setColor(Color.parseColor("#f5f5f5"));
            paintDown.setColor(Color.parseColor("#f5f5f5"));
            paintLeft.setColor(Color.parseColor("#f5f5f5"));
        }
        //上
        paintUp.setStyle(Paint.Style.STROKE);
        paintUp.setStrokeWidth(arrowWidth);
        pathArrow.moveTo(-arrowPoint,-arrowPoint+arrowLength);
        pathArrow.lineTo(-arrowPoint,-arrowPoint);
        pathArrow.lineTo(-arrowPoint+arrowLength,-arrowPoint);
        canvas.drawPath(pathArrow,paintUp);
        //右
        pathArrow.reset();
        paintRight.setStyle(Paint.Style.STROKE);
        paintRight.setStrokeWidth(arrowWidth);
        pathArrow.moveTo(arrowPoint,-arrowPoint+arrowLength);
        pathArrow.lineTo(arrowPoint,-arrowPoint);
        pathArrow.lineTo(arrowPoint-arrowLength,-arrowPoint);
        canvas.drawPath(pathArrow,paintRight);
        //下
        pathArrow.reset();
        paintDown.setStyle(Paint.Style.STROKE);
        paintDown.setStrokeWidth(arrowWidth);
        pathArrow.moveTo(arrowPoint,arrowPoint-arrowLength);
        pathArrow.lineTo(arrowPoint,arrowPoint);
        pathArrow.lineTo(arrowPoint-arrowLength,arrowPoint);
        canvas.drawPath(pathArrow,paintDown);
        //左
        pathArrow.reset();
        paintLeft.setStyle(Paint.Style.STROKE);
        paintLeft.setStrokeWidth(arrowWidth);
        pathArrow.moveTo(-arrowPoint,arrowPoint-arrowLength);
        pathArrow.lineTo(-arrowPoint,arrowPoint);
        pathArrow.lineTo(-arrowPoint+arrowLength,arrowPoint);
        canvas.drawPath(pathArrow,paintLeft);
        pathArrow.close();
        canvas.restore();

        //画控制圆中心的运动边界圆
        paintLine=new Paint();
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setColor(Color.WHITE);
        paintLine.setAntiAlias(true);
        paintLine.setStrokeWidth(10);
        canvas.drawCircle(0,0,centerLength,paintLine);

        //画中心控制圆
        paintCenterPoint.setAntiAlias(true);
        paintCenterPoint.setColor(Color.parseColor("#78909c"));
        paintCenterPoint.setStyle(Paint.Style.FILL);
        if (isInit){
            canvas.drawCircle(x,y,centerRadius,paintCenterPoint);
        }else {
            canvas.drawCircle(x-width/2,y-height/2,centerRadius,paintCenterPoint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX=(int)event.getX();
        currentY=(int)event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setPoint(currentX,currentY);
                break;
            case MotionEvent.ACTION_MOVE:
                setPoint(currentX,currentY);
                break;
            case MotionEvent.ACTION_UP:
                x=centerX;
                y=centerY;
                presentRadius=0;
                invalidate();
                if (listener!=null){
                    listener.stop();
                    whichIsOn=STOP;
                }
                whichIsOn=0;
                break;
        }
        return true;
    }

    /**
     * 设置表示百分比的圆半径，同时设置对应的速度
     * @param x 触控点的横坐标
     * @param y 触控点的纵坐标
     * @return 返回速度
     */
    private int setPresentRadius(int x,int y){
        int velocity;
        int length=(int)Tools.getDistance(x,y,centerX,centerY);
        if (length<=boardRadius){
            presentRadius=length;
            velocity=(maxVelocity-minVelocity)*(length-centerLength)/(boardRadius-centerLength);
        }else {
            presentRadius=boardRadius;
            velocity=maxVelocity;
        }
        return velocity;
    }

    /**
     * 获取点与圆心连线与横轴的夹角
     * @param centerX 圆心横坐标
     * @param centerY 圆心纵坐标
     * @param startX 当前点横坐标
     * @param startY 当前点纵坐标
     * @return 夹角
     */
    private double getAngle(double centerX,double centerY,double startX,double startY){
        return Math.atan((startY-centerY)/(startX-centerX));
    }

    /**
     * 获取并设置与圈外点相对应的控制圆边界上的点坐标,判断点击的行为
     * @param centerX 圆心横坐标
     * @param centerY 圆心纵坐标
     * @param currentX 当前点横坐标
     * @param currentY 当前点纵坐标
     */
    private void setPointOnCircle(int centerX,int centerY,int currentX,int currentY){
        double tempLength=centerRadius*Math.sqrt(2)/2;
        double angle=getAngle(centerX,centerY,currentX,currentY);
        if (currentX>=centerX){
            x=centerX+(int)(Math.cos(angle)*centerLength);
            y=centerY+(int)(Math.sin(angle)*centerLength);
        }else {
            x=centerX-(int)(Math.cos(angle)*centerLength);
            y=centerY-(int)(Math.sin(angle)*centerLength);
        }
        //判断点击行为
        if (listener!=null){
            if (x>=centerX){
                if (y>=centerY){
                    if (x>=(centerX+tempLength)){
//                        if (whichIsOn!=RIGHT){
                            listener.right(setPresentRadius(currentX,currentY));
                            whichIsOn=RIGHT;
                            paintUp.setColor(Color.parseColor("#f5f5f5"));
                            paintDown.setColor(Color.parseColor("#f5f5f5"));
                            paintLeft.setColor(Color.parseColor("#f5f5f5"));
                            paintRight.setColor(Color.parseColor("#2ecc71"));
//                        }
                    }else {
//                        if (whichIsOn!=DOWN){
                            listener.down(setPresentRadius(currentX,currentY));
                            whichIsOn=DOWN;
                            paintUp.setColor(Color.parseColor("#f5f5f5"));
                            paintDown.setColor(Color.parseColor("#2ecc71"));
                            paintLeft.setColor(Color.parseColor("#f5f5f5"));
                            paintRight.setColor(Color.parseColor("#f5f5f5"));
//                        }
                    }
                }else {
                    if (x>=(centerX+tempLength)){
//                        if (whichIsOn!=RIGHT){
                            listener.right(setPresentRadius(currentX,currentY));
                            whichIsOn=RIGHT;
                            paintUp.setColor(Color.parseColor("#f5f5f5"));
                            paintDown.setColor(Color.parseColor("#f5f5f5"));
                            paintLeft.setColor(Color.parseColor("#f5f5f5"));
                            paintRight.setColor(Color.parseColor("#2ecc71"));
//                        }
                    }else {
//                        if (whichIsOn!=UP){
                            listener.up(setPresentRadius(currentX,currentY));
                            whichIsOn=UP;
                            paintUp.setColor(Color.parseColor("#2ecc71"));
                            paintDown.setColor(Color.parseColor("#f5f5f5"));
                            paintLeft.setColor(Color.parseColor("#f5f5f5"));
                            paintRight.setColor(Color.parseColor("#f5f5f5"));
//                        }
                    }
                }
            }else {
                if (y>=centerY){
                    if (x>=(centerX-tempLength)){
//                        if (whichIsOn!=DOWN){
                            listener.down(setPresentRadius(currentX,currentY));
                            whichIsOn=DOWN;
                            paintUp.setColor(Color.parseColor("#f5f5f5"));
                            paintDown.setColor(Color.parseColor("#2ecc71"));
                            paintLeft.setColor(Color.parseColor("#f5f5f5"));
                            paintRight.setColor(Color.parseColor("#f5f5f5"));
//                        }
                    }else {
//                        if (whichIsOn!=LEFT){
                            listener.left(setPresentRadius(currentX,currentY));
                            whichIsOn=LEFT;
                            paintUp.setColor(Color.parseColor("#f5f5f5"));
                            paintDown.setColor(Color.parseColor("#f5f5f5"));
                            paintLeft.setColor(Color.parseColor("#2ecc71"));
                            paintRight.setColor(Color.parseColor("#f5f5f5"));
//                        }
                    }
                }else {
                    if (x>=(centerX-tempLength)){
//                        if (whichIsOn!=UP){
                            listener.up(setPresentRadius(currentX,currentY));
                            whichIsOn=UP;
                            paintUp.setColor(Color.parseColor("#2ecc71"));
                            paintDown.setColor(Color.parseColor("#f5f5f5"));
                            paintLeft.setColor(Color.parseColor("#f5f5f5"));
                            paintRight.setColor(Color.parseColor("#f5f5f5"));
//                        }
                    }else {
//                        if (whichIsOn!=LEFT){
                            listener.left(setPresentRadius(currentX,currentY));
                            whichIsOn=LEFT;
                            paintUp.setColor(Color.parseColor("#f5f5f5"));
                            paintDown.setColor(Color.parseColor("#f5f5f5"));
                            paintLeft.setColor(Color.parseColor("#2ecc71"));
                            paintRight.setColor(Color.parseColor("#f5f5f5"));
//                        }
                    }
                }
            }
        }
    }

    /**
     * 对外暴露监听器
     * @param listener 监听器
     */
    public void setOnControllerListener(OnControllerListener listener){
        this.listener=listener;
    }

    /**
     * 对外提供模拟设置触摸点方法
     * @param setX 模拟触摸点的横坐标
     * @param setY 模拟触摸点的纵坐标
     */
    public void setPoint(int setX,int setY){
        isInit=false;
        if (Tools.getDistance(centerX,centerY,setX,setY)<=centerLength){
            //如果点在圈内，控制圆应随手指移动
            x=setX;
            y=setY;
            if (listener!=null&&whichIsOn!=STOP){
                listener.stop();
                whichIsOn=STOP;
            }
        }else {
            //如果点在圈外，控制圆应随手指移动，但是不能出圈
            setPointOnCircle(centerX,centerY,setX,setY);
        }
        invalidate();
    }
    //对外提供View中心坐标
    public int getCenterX(){
        return centerX;
    }
    public int getCenterY(){
        return centerY;
    }
    //对外提供设置中心控制圆特殊位置的方法
    public void setUp(){
        setPoint(centerX,centerY-centerLength-10);
    }
    public void setDown(){
        setPoint(centerX,centerY+centerLength+10);
    }
    public void setLeft(){
        setPoint(centerX-centerLength-10,centerY);
    }
    public void setRight(){
        setPoint(centerX+centerLength+10,centerY);
    }
    public void setCenter(){
        setPoint(centerX,centerY);
    }
}
