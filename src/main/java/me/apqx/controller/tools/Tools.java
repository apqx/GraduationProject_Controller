package me.apqx.controller.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import me.apqx.controller.ManualNode;
import me.apqx.controller.MyApplication;
import me.apqx.controller.Node;
import me.apqx.controller.R;

/**
 * Created by apqx on 2017/3/3.
 * Tools for this app
 */

public class Tools {
    private static Toast toast=Toast.makeText(MyApplication.getContext(),"",Toast.LENGTH_SHORT);
    public static void showToast(String string){
        toast.setText(string);
        toast.show();
    }

    public static void checkAndGetPermissions(Activity activity, String... permissions){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            for (String permission:permissions){
                if (ContextCompat.checkSelfPermission(activity,permission)== PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(activity,new String[]{permission},1);
                }
            }
        }
    }

    //判断时间是否大于指定的时间
    public static boolean isOverTime(int time1,int time2,int time){
        if (time2>=time1&&time2<60){
            if ((time2-time1)<time){
                return false;
            }else {
                return true;
            }
        }else {
            if ((time2+60-time1)<time){
                return false;
            }else {
                return true;
            }
        }
    }
    private static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    //获取两点间的距离
    public static double getDistance(int startX,int startY,int endX,int endY){
        return Math.hypot(endX-startX,endY-startY);
    }
    /**
     * 将px转换为dp或dip
     */
    public static int pxToDp(Context context, float pxValue){
        final float scale=context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale+0.5f);
    }
    /**
     * 将dp或dip转换为px
     */
    public static int dpToPx(Context context,float dpValue){
        final float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
    /**
     * 将px转换为sp
     */
    public static int pxToSp(Context context,float pxValue){
        final float fontScale=context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue/fontScale+0.5f);
    }
    /**
     * 将sp转换为px
     */
    public static int spToPx(Context context,float dpValue){
        final float fontScale=context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(dpValue*fontScale+0.5f);
    }

    /**
     * 路径模式下， 将包含路径点的List封装成JsonArray字符串
     * @param nodeList
     * @return
     */
    public static String getJsonFromNodeList(List<Node> nodeList){
        JSONArray jsonArray=new JSONArray();
        JSONObject jsonNode=null;
        try{
            for (Node node:nodeList){
                jsonNode=new JSONObject();
                jsonNode.put("velocity",node.getVelocity());
                jsonNode.put("time",node.getTime());
                jsonNode.put("degree",node.getDegree());
                jsonNode.put("index",node.getIndexOfGrid());
                jsonNode.put("unload",node.getUnload());
                jsonArray.put(jsonNode);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    /**
     * 手动模式下，将命令节点封装成Json字符串
     * @param node
     * @return
     */
    public static String getJsonFromNode(ManualNode node){
        JSONObject jsonObject=null;
        try {
            jsonObject=new JSONObject();
            jsonObject.put("up",node.getUp());
            jsonObject.put("down",node.getDown());
            jsonObject.put("left",node.getLeft());
            jsonObject.put("right",node.getRight());
            jsonObject.put("stop",node.getStop());
            jsonObject.put("velocity",node.getVelocity());
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 路径模式下，从Json数据中读取各节点的信息，封装成List
     * @param json
     * @return
     */
    public static List<Node> getNodeListFromJson(String json){
        List<Node> nodeList=new LinkedList<Node>();
        try {
            JSONArray jsonArray=new JSONArray(json);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=(JSONObject)jsonArray.get(i);
                Node node=new Node(jsonObject.getInt("velocity"),jsonObject.getInt("time"),jsonObject.getInt("degree"),jsonObject.getInt("index"),jsonObject.getInt("unload"));
                nodeList.add(node);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return nodeList;
    }

    public static String getFile(File file){
        BufferedReader bufferedReader=null;
        StringBuilder stringBuilder=new StringBuilder();
        try {
            bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String string;
            while ((string=bufferedReader.readLine())!=null){
                stringBuilder.append(string);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (bufferedReader!=null){
                    bufferedReader.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    public static void saveFile(final String string,final File file){
        new Thread(new Runnable() {
            @Override
            public void run() {
                PrintStream printStream=null;
                try {
                    printStream=new PrintStream(new FileOutputStream(file));
                    printStream.println(string);
                    printStream.flush();
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    if (printStream!=null){
                        printStream.close();
                    }
                }
            }
        }).start();
    }

}