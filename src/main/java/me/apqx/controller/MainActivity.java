package me.apqx.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.apqx.controller.tools.Tools;
import me.apqx.controller.views.ControllerView;
import me.apqx.controller.views.MyGridLayout;
import me.apqx.controller.views.MyRelativeLayout;
import me.apqx.controller.views.OnControllerListener;

/**
 * Created by apqx on 2017/3/3.
 *
 */

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ControllerView controllerView;
    private Connect connect;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private ImageView imageView;
    private MyGridLayout myGridLayout;
    private SharedPreferences sharedPreferences;
    private MyRelativeLayout myRelativeLayout;

    private int velocity,time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        Tools.checkAndGetPermissions(this, Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_COARSE_LOCATION);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter!=null){
            if (!bluetoothAdapter.isEnabled()){
                Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,1);

            }
        }


        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        controllerView=(ControllerView)findViewById(R.id.controllerView);
        controllerView.setOnControllerListener(new MOnControllerListener());
        imageView=(ImageView)findViewById(R.id.imageView_signal);

        handler=new Handler();

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        velocity=Integer.parseInt(sharedPreferences.getString("velocity","0"));
        time=Integer.parseInt(sharedPreferences.getString("time","0"));
//        Log.d("apqx","onCreate "+velocity+" "+time);

        myGridLayout=(MyGridLayout)findViewById(R.id.myGridLayout);
        myGridLayout.setTime(time);
//        Log.d("apqx","main time = "+time);
        myGridLayout.setVelocity(velocity);

        myRelativeLayout=(MyRelativeLayout)findViewById(R.id.myRelativeLayout);
        myRelativeLayout.setOnSendPathDataListener(new MyOnSendPathDataListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connect!=null){
            connect.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        velocity=Integer.parseInt(sharedPreferences.getString("velocity","0"));
        time=Integer.parseInt(sharedPreferences.getString("time","0"));
//        Log.d("apqx","onCreate "+velocity+" "+time);

        myGridLayout.setTime(time);
//        Log.d("apqx","main resume time = "+time);
        myGridLayout.setVelocity(velocity);
    }

    //click this button to search devices,and show a dialog to show the devices
    public void clickSearchDevices(View view){
        if (bluetoothAdapter!=null){
            if (!bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
            }
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("正在搜索设备...");
        View viewDialog=LayoutInflater.from(this).inflate(R.layout.layout_dialog_show_devices,null,false);
        final SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout)viewDialog.findViewById(R.id.swipeRefreshLayout);
        ListView listViewDevices=(ListView)viewDialog.findViewById(R.id.listView_searchDevices);
        builder.setView(viewDialog);
        final AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setRefreshing(true);

        final List<String> listDevices=new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listDevices);
        listViewDevices.setAdapter(arrayAdapter);

        final List<BluetoothDevice> listDeviceObjects=new ArrayList<BluetoothDevice>();
        final BroadcastReceiver receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName()!=null){
                    listDeviceObjects.add(device);
                    listDevices.add(device.getName());
                    Log.d("apqx","found device "+device.getName());
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver,intentFilter);

        bluetoothAdapter.startDiscovery();

        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bluetoothDevice=listDeviceObjects.get(position);
                Log.d("apqx","click device "+bluetoothDevice.getName());

                connect=new Connect(bluetoothDevice,handler,imageView,MainActivity.this);

                dialog.cancel();

            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("apqx","DialogSearch canceled");
                bluetoothAdapter.cancelDiscovery();
                swipeRefreshLayout.setRefreshing(false);
                unregisterReceiver(receiver);
            }
        });

    }

    public void clickToSave(View view){
        myGridLayout.savePath();
    }
    public void clickToClear(View view){
        myGridLayout.clear();
    }
    public void clickToLoad(View view){
        myGridLayout.readPathFromFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_setting:
                startActivity(new Intent(this,SettingActivity.class));
                break;
            case R.id.toolbar_test:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                View view=LayoutInflater.from(this).inflate(R.layout.layout_dialog_test,null,false);
                final EditText editText=(EditText)view.findViewById(R.id.editText_test);
                Button btnSend=(Button)view.findViewById(R.id.btn_send_test);
                builder.setView(view);
                builder.setTitle("输入测试数据");
                AlertDialog dialog=builder.create();
                dialog.show();
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String string=editText.getText().toString();
                        Log.d("apqx","test to send - "+string);
                        if (connect!=null){
                            if (!string.equals("")){
                                connect.sendText(string);
                                editText.setText("");
                                Tools.showToast("发送数据");
                            }else {
                                Tools.showToast("空数据");
                            }
                        }else {
                                Tools.showToast("没有连接");
                        }
                    }
                });
                break;
            case R.id.toolbar_disconnect:
                if (connect!=null){
                    connect.close();
                }
                break;
            default:break;
        }
        return true;
    }

    private class MOnControllerListener implements OnControllerListener{
        //这里应该把数据封装成Node节点，以数组的形式发送，手动模式只是发送JSON节点，一维数组，路径模式发送JSONArray，二维数组
        String string="";
        @Override
        public void up(int velocity) {
            ManualNode node=new ManualNode(velocity);
            node.setUp(ManualNode.YES);
//            string=Tools.getJsonFromNode(node);
            string=Tools.getArrayFromNode(node);
            sendTextThroughConnect(string);
//            sendTextThroughConnect("q");
        }

        @Override
        public void down(int velocity) {
            ManualNode node=new ManualNode(velocity);
            node.setDown(ManualNode.YES);
//            string=Tools.getJsonFromNode(node);
            string=Tools.getArrayFromNode(node);
            sendTextThroughConnect(string);
//            sendTextThroughConnect("z");
        }

        @Override
        public void right(int velocity) {
            ManualNode node=new ManualNode(velocity);
            node.setRight(ManualNode.YES);
//            string=Tools.getJsonFromNode(node);
            string=Tools.getArrayFromNode(node);
            sendTextThroughConnect(string);
//            sendTextThroughConnect("s");
        }

        @Override
        public void left(int velocity) {
            ManualNode node=new ManualNode(velocity);
            node.setLeft(ManualNode.YES);
//            string=Tools.getJsonFromNode(node);
            string=Tools.getArrayFromNode(node);
            sendTextThroughConnect(string);
//            sendTextThroughConnect("w");
        }

        @Override
        public void stop() {
            ManualNode node=new ManualNode(ManualNode.NO_VELOCITY);
            node.setStop(ManualNode.YES);
//            string=Tools.getJsonFromNode(node);
            string=Tools.getArrayFromNode(node);
            sendTextThroughConnect(string);
//            sendTextThroughConnect("x");
        }
    }

    //use this common method to send text through bluetooth connect
    private void sendTextThroughConnect(String string){
        Log.d("apqx","try send = "+string);
        if (connect!=null){
            if (!string.equals("")){
                connect.sendText(string);
                Log.d("apqx","send = "+string);
                Tools.showToast("发送数据");
            }else {
                Tools.showToast("空数据");
            }
        }else {
            Tools.showToast("没有连接");
        }
    }
    class MyOnSendPathDataListener implements MyRelativeLayout.OnSendPathDataListener{
        @Override
        public void send() {
            sendTextThroughConnect(myGridLayout.getPathDataToSend());
        }
    }

}
