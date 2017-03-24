package me.apqx.controller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;

import me.apqx.controller.tools.Tools;

/**
 * Created by apqx on 2017/3/3.
 * Deal with the most of the connect work
 */

public class Connect {
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket socket;
    private PrintStream printStream;
    private Handler handler;
    private ImageView imageView;
    private Activity activity;
    private static final String MY_UUID="00001101-0000-1000-8000-00805F9B34FB";
    public Connect(BluetoothDevice bluetoothDevice, Handler handler, ImageView imageView, Activity activity){
        this.bluetoothDevice=bluetoothDevice;
        this.handler=handler;
        this.imageView=imageView;
        this.activity=activity;
        new Thread(new ConnectThread(),"Thread-connect").start();

    }
    private class ConnectThread implements Runnable{
        @Override
        public void run() {
            try {
                socket=bluetoothDevice.createRfcommSocketToServiceRecord(java.util.UUID.fromString(MY_UUID));
                socket.connect();
                printStream=new PrintStream(socket.getOutputStream());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Tools.showToast("连接成功");
                        imageView.setBackgroundColor(activity.getResources().getColor(R.color.colorConnect));
                    }
                });
            }catch (IOException e){
                e.printStackTrace();
                Log.d("apqx","Failed to get socket");
            }
        }
    }

    public void sendText(String text){
        if (printStream!=null&&socket.isConnected()){
            Log.d("apqx","send - "+text);
            printStream.println(text);
        }else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Tools.showToast("没有连接");
                }
            });
        }

    }

    public void close(){
        imageView.setBackgroundColor(activity.getResources().getColor(R.color.colorDisconnect));
        try {
            if (socket.isConnected()){
                if (printStream!=null){
                    printStream.close();
                    Log.d("apqx","printStream close");
                }
                if (socket!=null){
                    Log.d("apqx","socket close");
                    socket.close();
                }

            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
