package me.apqx.controller.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.apqx.controller.R;

/**
 * Created by apqx on 2017/3/17.
 */

public class MyRelativeLayout extends RelativeLayout implements View.OnClickListener{
    private ImageView imageViewSignal;
    private RelativeLayout relativeLayoutRout;
    private LinearLayout linearLayoutStartRout;
    private RelativeLayout relativeLayoutControlPanel;
    private int heightOfRoutBar,topOfControlPanelUp,topOfControlPanelDown,controlPanelHeight;
    private boolean animateToTop,animateBasedVelocity;
    private Button btnExpand,btnSend;
    private TextView textViewExpand;
    private MyGridLayout myGridLayout;
    private OnSendPathDataListener onSendPathDataListener;

    private int y,lastY,offsetY;

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean returnValue=false;
        if (relativeLayoutControlPanel==null||imageViewSignal==null){
            relativeLayoutControlPanel=(RelativeLayout)findViewById(R.id.relativeLayout_controlPanel);
            imageViewSignal=(ImageView)findViewById(R.id.imageView_signal);
        }
        if (linearLayoutStartRout==null){
            linearLayoutStartRout=(LinearLayout)findViewById(R.id.linearLayout_start_roat);
            btnExpand=(Button)linearLayoutStartRout.findViewById(R.id.btn_expand);
            btnSend=(Button)linearLayoutStartRout.findViewById(R.id.btn_do_rout);
            btnExpand.setOnClickListener(this);
            btnSend.setOnClickListener(this);
        }
        controlPanelHeight=relativeLayoutControlPanel.getMeasuredHeight();
        heightOfRoutBar=linearLayoutStartRout.getMeasuredHeight();
        topOfControlPanelUp=0;
        topOfControlPanelDown=controlPanelHeight-heightOfRoutBar;
        int dragAreaTop=(int)relativeLayoutControlPanel.getY();
        int dragAreaBottom=linearLayoutStartRout.getMeasuredHeight()+dragAreaTop;
        int dragAreaRight=(int)btnExpand.getX();
        int dragAreaLeft=btnExpand.getMeasuredWidth();
//        Log.d("apqx",dragAreaTop+" < "+ev.getY()+" < "+dragAreaBottom);
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (relativeLayoutControlPanel.getY()==topOfControlPanelDown){
                    if (ev.getY()>dragAreaTop&&ev.getY()<dragAreaBottom&&ev.getX()<dragAreaRight&&ev.getX()>dragAreaLeft){
                        returnValue=true;
                    }
                }else if (ev.getY()>dragAreaTop&&ev.getY()<dragAreaBottom){
                    returnValue=true;
                }
                break;
        }
        return returnValue;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (relativeLayoutRout==null){
            relativeLayoutRout=(RelativeLayout)findViewById(R.id.relativeLayout_rout);
        }
        if (relativeLayoutControlPanel==null){
            relativeLayoutControlPanel=(RelativeLayout)findViewById(R.id.relativeLayout_controlPanel);
        }
        if (linearLayoutStartRout==null){
            linearLayoutStartRout=(LinearLayout)findViewById(R.id.linearLayout_start_roat);
            btnExpand=(Button)linearLayoutStartRout.findViewById(R.id.btn_expand);
        }
        if (myGridLayout==null){
            myGridLayout=(MyGridLayout)findViewById(R.id.myGridLayout);
        }
        controlPanelHeight=relativeLayoutControlPanel.getMeasuredHeight();
        heightOfRoutBar=linearLayoutStartRout.getMeasuredHeight();
        topOfControlPanelUp=0;
        topOfControlPanelDown=controlPanelHeight-heightOfRoutBar;
        y=(int)event.getRawY();
        VelocityTracker velocityTracker=VelocityTracker.obtain();
        velocityTracker.addMovement(event);
        velocityTracker.computeCurrentVelocity(1000);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastY=y;
                break;
            case MotionEvent.ACTION_MOVE:

                offsetY=y-lastY;
                if (relativeLayoutControlPanel.getY()>=0&&relativeLayoutControlPanel.getY()<=topOfControlPanelDown){
                    if (relativeLayoutControlPanel.getY()==0&&offsetY>0){
                        relativeLayoutControlPanel.offsetTopAndBottom(offsetY);
                    }else if (relativeLayoutControlPanel.getY()==topOfControlPanelDown&&offsetY<0){
                        relativeLayoutControlPanel.offsetTopAndBottom(offsetY);
                    }else if (relativeLayoutControlPanel.getY()>0&&relativeLayoutControlPanel.getY()<topOfControlPanelDown){
                        relativeLayoutControlPanel.offsetTopAndBottom(offsetY);
                    }

                    if (velocityTracker.getYVelocity()>3000){
                        animateBasedVelocity=true;
                        animateToTop=false;
                    }else if (velocityTracker.getYVelocity()<-3000){
                        animateToTop=true;
                        animateBasedVelocity=true;
                    }

                    linearLayoutStartRout.setVisibility(VISIBLE);
                    getPercentAndSetAlpha();
                }

                lastY=y;
                break;
            case MotionEvent.ACTION_UP:
                if (animateBasedVelocity){
                    if (!animateToTop){
                        animateToBottom();
                    }else {
                        animateToTop();
                    }
                }else if (relativeLayoutControlPanel.getY()>=controlPanelHeight/2){
                    animateToBottom();
                }else {
                    animateToTop();
                }
                velocityTracker.clear();
                velocityTracker.recycle();
                animateToTop=false;
                animateBasedVelocity=false;
                break;
        }
        return true;
    }

    private void animateToBottom(){
        ObjectAnimator animator=ObjectAnimator.ofFloat(relativeLayoutControlPanel,"y",topOfControlPanelDown);
        animator.setDuration(250);
        animator.start();
        animator.addUpdateListener(new MyUpdateListener());
    }
    private void animateToTop(){
        ObjectAnimator animator=ObjectAnimator.ofFloat(relativeLayoutControlPanel,"y",0);
        animator.setDuration(250);
        animator.start();
        animator.addUpdateListener(new MyUpdateListener());

    }
    private void getPercentAndSetAlpha(){
        float percent=relativeLayoutControlPanel.getY()/controlPanelHeight;
        linearLayoutStartRout.setAlpha(percent);
    }
    private class MyUpdateListener implements ValueAnimator.AnimatorUpdateListener{
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            getPercentAndSetAlpha();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_do_rout:
                if (onSendPathDataListener!=null){
                    onSendPathDataListener.send();
                }
                break;
            case R.id.btn_expand:
                animateToTop();
                break;
        }
    }

    public void setOnSendPathDataListener(OnSendPathDataListener onSendPathDataListener){
        this.onSendPathDataListener=onSendPathDataListener;
    }

    public interface OnSendPathDataListener{
        void send();
    }
}
