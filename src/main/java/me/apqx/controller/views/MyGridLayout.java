package me.apqx.controller.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.apqx.controller.Node;
import me.apqx.controller.R;
import me.apqx.controller.tools.Tools;

/**
 * Created by apqx on 2017/3/17.
 *
 */

public class MyGridLayout extends View {

    private int gridCount,itemWidth,itemRadius;
    private Paint paintItem,paintLine;
    private int x,y;
    private boolean shouldDrawLine;
    private Path path;
    private int lastIndex,upIndex,downIndex,rightIndex,leftIndex;
    private List<Node> nodeList;
    //degree默认为0，即朝向正前方，向左180度为负值，向右180度为正值
    private int time,velocity,degree;
    private int unload=Node.NO_UNLOAD;
    private int[][] vector;

    public MyGridLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyGridLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init( @Nullable AttributeSet attrs){
        TypedArray typedArray=getContext().obtainStyledAttributes(attrs,R.styleable.MyGridLayout);
        gridCount=typedArray.getInt(R.styleable.MyGridLayout_gridCount,7);
        paintItem=new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLine=new Paint();
        path=new Path();
        nodeList=new LinkedList<Node>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec),measure(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        itemWidth=getMeasuredWidth()/gridCount;
        itemRadius=itemWidth/3;
        paintItem.setStyle(Paint.Style.FILL);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        paintLine.setStrokeWidth(Tools.dpToPx(getContext(),10));
        paintLine.setStrokeJoin(Paint.Join.ROUND);
        paintLine.setColor(getContext().getResources().getColor(R.color.colorAccent));


        for (int i=0;i<gridCount*gridCount;i++){
            int[] center=getCenter(i);
            paintItem.setColor(getContext().getResources().getColor(R.color.colorPrimary));
            canvas.drawCircle(center[0],center[1],itemRadius,paintItem);
            paintItem.setColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
            canvas.drawCircle(center[0],center[1],itemRadius- Tools.dpToPx(getContext(),5),paintItem);
        }


        canvas.drawPath(path,paintLine);

        //高亮绘制路径起点
        if (nodeList.size()>0){
            paintItem.setColor(getContext().getResources().getColor(R.color.colorHighLight));
            int[] center=getCenter(nodeList.get(0).getIndexOfGrid());
            canvas.drawCircle(center[0],center[1],itemRadius- Tools.dpToPx(getContext(),5),paintItem);
            if (nodeList.size()>1){
                int[] secondCenter=getCenter(nodeList.get(1).getIndexOfGrid());
                paintLine.setColor(getContext().getResources().getColor(R.color.colorHighLight));
                canvas.drawLine(center[0],center[1],secondCenter[0],secondCenter[1],paintLine);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x=(int)event.getX();
        y=(int)event.getY();
        int index=whichPoint(x,y);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                shouldDrawLine=false;
                if (index!=-1){
                    Node existNode=null;
                    if (nodeList.size()==0){
                        int[] center=getCenter(index);
                        path.moveTo(center[0],center[1]);
                        path.lineTo(x,y);
                        lastIndex=index;
                        shouldDrawLine=true;
//                        Log.d("apqx","node time = "+time);
                        nodeList.add(new Node(velocity,time,degree,index,unload));
                    }else if ((existNode=isNodeExist(index))!=null){
                        //高亮改点及路径，弹窗输入设置项
//                        Log.d("apqx","highLight "+index);
//                        Log.d("apqx",nodeList.toString());
                        highLightAndDialog(existNode);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (shouldDrawLine) {
                    if (index!=-1&&index!=lastIndex){
                        int[] center=getCenter(index);
                        int[] lastCenter=getCenter(lastIndex);
                        path.setLastPoint(lastCenter[0],lastCenter[1]);
                        path.lineTo(center[0],center[1]);
                        path.lineTo(x,y);
                        lastIndex=index;
                        nodeList.add(new Node(velocity,time,degree,index,unload));
                    }else {
                        path.setLastPoint(x,y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (nodeList.size()<2){
                    path.reset();
                    nodeList.clear();
                }else {
                    int[] center=getCenter(lastIndex);
                    path.setLastPoint(center[0],center[1]);
                    if (nodeList.size()>1&&shouldDrawLine){
                        calculateDegree();
                        calculateTime();
                        setLastNode();
                    }
                }
//                Log.d("apqx",nodeList.toString());
                break;
        }
        if (shouldDrawLine){
            invalidate();
        }

        return true;
    }


    private int measure(int measureSpace){
        int specMode=MeasureSpec.getMode(measureSpace);
        int specSize=MeasureSpec.getSize(measureSpace);
        int size=1080;
        if (specMode==MeasureSpec.EXACTLY){
            size=specSize;
        }else {
            if (specMode==MeasureSpec.AT_MOST){
                size=Math.min(size,specSize);
            }
        }
        return size;
    }

    private int[] getCenter(int index){
        int[] center=new int[2];
        if (index>=0&&index<gridCount*gridCount){
            //x
            center[0]=index%gridCount*itemWidth+itemWidth/2;
            //y
            center[1]=index/gridCount*itemWidth+itemWidth/2;

        }
        return center;
    }
    private int whichPoint(int x,int y){
        int point=-1;
        int column=x/itemWidth;
        int row=y/itemWidth;
        int index=row*gridCount+column;
        int[] center=getCenter(index);
        if (Tools.getDistance(x,y,center[0],center[1])<itemRadius){
            point=index;
        }
        return point;
    }
    private void resetAvaliableIndex(int index){
        if (index%gridCount==0){
            leftIndex=-1;
        }else {
            leftIndex=index-1;
        }
        if (index%gridCount==gridCount-1){
            rightIndex=-1;
        }else {
            rightIndex=index+1;
        }
        if (index/gridCount==0){
            upIndex=-1;
        }else {
            upIndex=index-gridCount;
        }
        if (index/gridCount==gridCount-1){
            downIndex=-1;
        }else {
            downIndex=index+gridCount;
        }
    }

    public void setVelocity(int velocity){
        this.velocity=velocity;
    }

    public void setTime(int time){
        this.time=time;
//        Log.d("apqx","grid time = "+this.time);
    }
    public void clear(){
        path.reset();
        nodeList.clear();
        invalidate();

    }
    private void setPath(String jsonPath){
        nodeList=Tools.getNodeListFromJson(jsonPath);
//        Log.d("apqx","nodeList = "+nodeList);
        if (nodeList.size()>1){
            path.reset();
            for (int i=0;i<nodeList.size();i++){
                int[] xy=getCenter(nodeList.get(i).getIndexOfGrid());
                if (i==0){
                    path.moveTo(xy[0],xy[1]);
                }else {
                    path.lineTo(xy[0],xy[1]);
                }
            }
            shouldDrawLine=true;
            invalidate();
        }
    }
    public void readPathFromFile(){
        final File dir=getContext().getExternalFilesDir(null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        final AlertDialog dialog=builder.setTitle("选择文件")
                .create();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_file_list,null);
        dialog.setView(view);
        dialog.show();
        ListView listView=(ListView)view.findViewById(R.id.listView_listFile);
        List<String> list=new ArrayList<String>();
        Collections.addAll(list,dir.list());
        ArrayAdapter adapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final File file=new File(dir,parent.getItemAtPosition(position).toString());
//                Log.d("apqx","click "+file.getName());
                if (file.exists()){
//                    Log.d("apqx","file exist");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String jsonPath=Tools.getFile(file);
//                            Log.d("apqx",jsonPath);
                            MyGridLayout.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    setPath(jsonPath);
                                }
                            });
                        }
                    }).start();
                    dialog.cancel();
                }
            }
        });
    }
    public void savePath(){
        if (nodeList.size()!=0){

            final File dir=getContext().getExternalFilesDir(null);
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            final AlertDialog dialog=builder.setTitle("输入文件名")
                    .create();
            View view=LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_save_file,null);
            dialog.setView(view);
            dialog.show();
            final EditText editTextSaveFile=(EditText)view.findViewById(R.id.editText_saveFile);
            Button btnSaveFile=(Button)view.findViewById(R.id.btn_saveFile);
            Button btnSaveFileCancel=(Button)view.findViewById(R.id.btn_saveFile_cancel);
            btnSaveFileCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            btnSaveFile.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name=editTextSaveFile.getText().toString();
                    if (name.equals("")){

                    }else {
                        File file=new File(dir,name);
                        Tools.saveFile(Tools.getJsonFromNodeList(nodeList),file);
                        dialog.cancel();
                    }
                }
            });
        }
    }

    private Node isNodeExist(int index){
        Node returnNode=null;
        for (Node node:nodeList){
            if (node.getIndexOfGrid()==index){
                returnNode=node;
                break;
            }
        }


        return returnNode;
    }

    private void highLightAndDialog(final Node node){
        int indexOfList=nodeList.indexOf(node);
//        Log.d("apqx",indexOfList+"");
        if (indexOfList!=nodeList.size()-1){
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            AlertDialog dialog=builder.setTitle("设置属性")
                    .create();
            View view= LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_node,null);
            dialog.setView(view);
            final EditText editTextVelocity=(EditText)view.findViewById(R.id.editText_velocity);
            final EditText editTextTime=(EditText)view.findViewById(R.id.editText_time);
            final EditText editTextDegree=(EditText)view.findViewById(R.id.editText_degree);
            final Switch switchUnload=(Switch)view.findViewById(R.id.switch_unload);
            editTextTime.setText(String.valueOf(node.getTime()));
            editTextVelocity.setText(String.valueOf(node.getVelocity()));
            editTextDegree.setText(String.valueOf(node.getDegree()));
            if (node.getUnload()==Node.DO_UNLOAD){
                switchUnload.setChecked(true);
            }else {
                switchUnload.setChecked(false);
            }
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
//                    Log.d("apqx","cancel");
                    node.setVelocity(Integer.parseInt(editTextVelocity.getText().toString()));
                    node.setTime(Integer.parseInt(editTextTime.getText().toString()));
                    node.setDegree(Integer.parseInt(editTextDegree.getText().toString()));
                    if (switchUnload.isChecked()){
                        node.setUnload(Node.DO_UNLOAD);
                    }else {
                        node.setUnload(Node.NO_UNLOAD);
                    }
                }
            });
        }
    }

    private void calculateTime(){
        for (int i=0;i<nodeList.size()-1;i++){
            int[] currentXy=getCenter(nodeList.get(i).getIndexOfGrid());
            int[] nextXy=getCenter(nodeList.get(i+1).getIndexOfGrid());
            int nodeTime=1+(int)(((float)time/itemWidth)*Tools.getDistance(currentXy[0],currentXy[1],nextXy[0],nextXy[1]));
            nodeList.get(i).setTime(nodeTime);
        }
    }
    private void calculateDegree(){
//        Log.d("apqx","calculateDegree");
        //二维数组保存向量
        vector=new int[nodeList.size()][2];
        //获取所有向量，第一个向量是机器人面向的方向的单位向量，因为手机总是朝向机器人面向的方向，所以是(0,-1)
        for (int i=0;i<nodeList.size();i++){
            if (i==0){
                vector[i]=new int[]{0,-1};
            }else {
                vector[i]=getVector(getCenter(nodeList.get(i-1).getIndexOfGrid()),getCenter(nodeList.get(i).getIndexOfGrid()));
            }
        }

        for (int i=0;i<nodeList.size()-1;i++){
            int nodeDegree=getDegreeFromVector(vector[i],vector[i+1]);
//            Log.d("apqx","nodeDegree= "+nodeDegree);

            nodeList.get(i).setDegree(nodeDegree);
        }
    }
    private int[] getVector(int[] first,int[] last){
        int[] vector=new int[2];
        vector[0]=last[0]-first[0];
        vector[1]=last[1]-first[1];
        return vector;
    }

    /**
     * 用向量的数量积计算夹角大小，用向量积来判断夹角的正负
     *
     */
    private int getDegreeFromVector(int[] first,int[] last){
        int degree=(int)(Math.acos(((first[0]*last[0]+first[1]*last[1])/(getVectorMod(first)*getVectorMod(last))))*180/Math.PI);
        double product=getVectorProduct(first,last);
        if (product<0){
            degree=-degree;
        }
        return degree;
    }
    private double getVectorMod(int[] xy){
        double mod=Math.sqrt(xy[0]*xy[0]+xy[1]*xy[1]);
        return mod;
    }
    //向量积
    private int getVectorProduct(int[] first,int[] last){
        return first[0]*last[1]-last[0]*first[1];
    }
    //设置最后一个节点的数据
    private void setLastNode(){
        Node lastNode=nodeList.get(nodeList.size()-1);
        if (lastNode.getIndexOfGrid()!=nodeList.get(0).getIndexOfGrid()){
            lastNode.setDegree(0);
        }else {
            doIfLoop(lastNode);
        }
        lastNode.setTime(0);
        lastNode.setVelocity(0);
    }
    //如果最后一个节点和第一个节点重合，应正确设置最后一个节点的转向角
    private void doIfLoop(Node lastNode){
        lastNode.setDegree(getDegreeFromVector(vector[vector.length-1],vector[1]));
    }

    public String getPathDataToSend(){
        Log.d("apqx","getPathDataToSend");
        return Tools.getJsonFromNodeList(nodeList);
    }

}
