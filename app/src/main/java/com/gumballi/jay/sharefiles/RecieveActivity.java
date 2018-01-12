package com.gumballi.jay.sharefiles;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;

public class RecieveActivity extends AppCompatActivity {

    public WifiP2pManager p2pManager;
    public WifiP2pManager.Channel channel;
    public IntentFilter intentFilter;
    public MyBroadcastReciever myBroadcastReciever;
    public static FileServerAsyncTask fileServerAsyncTask;
    public static WifiP2pManager.ConnectionInfoListener infoListener;
    public ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recieve);

        try{
            serverSocket = new ServerSocket(8888);
        }catch (Exception e){
            e.printStackTrace();
        }

        /*if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

        Log.d("Reciever","onCreate");

        p2pManager=(WifiP2pManager) getSystemService(this.WIFI_P2P_SERVICE);
        channel=p2pManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Log.d("Reciever","Channel Disconnected!");
            }
        });
        infoListener=new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                Log.d("Reciever","infoListener");
            }
        };
        myBroadcastReciever=new MyBroadcastReciever(p2pManager,channel,this,null);

        intentFilter=new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        registerReceiver(myBroadcastReciever,intentFilter);

        deletePersistentGroups();
        p2pManager.removeGroup(channel,null);

        final Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
               /* p2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("Reciever","Discovery Success");
                        if(fileServerAsyncTask!=null) return;
                        fileServerAsyncTask=new FileServerAsyncTask(getApplicationContext());
                        fileServerAsyncTask.execute();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d("Reciever","Discovery Failure"+reason);
                        //p2pManager.requestPeers(channel,null);
                    }
                });*/
            }
        });

        p2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("Reciever","Group Created");
                if(fileServerAsyncTask!=null){
                    Log.d("Reciever","Woah!");
                    return;
                }
                fileServerAsyncTask=new FileServerAsyncTask(getApplicationContext());
                //unregisterReceiver(myBroadcastReciever);
                fileServerAsyncTask.execute();
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        fileServerAsyncTask.recieveData();
                    }
                });
                //thread.start();
                //fileServerAsyncTask.execute();
            }

            @Override
            public void onFailure(int reason) {
                Log.d("Reciever","Group not Created"+reason);
            }
        });

        Handler handler1=new Handler();
        handler1.post(new Runnable() {
            @Override
            public void run() {

            }
        });






    }

    public class FileServerAsyncTask extends AsyncTask<Void,Void,Void>{

        private Context context;
        private String fileName;
        //private ServerSocket serverSocket;
        private Socket client;
        private File file;

        public FileServerAsyncTask(Context context) {
            this.context = context;
        }

        public void recieveData(){
            byte buf[]=new byte[1024];
            int len;

            try {
                //serverSocket.bind(new InetSocketAddress(8888));
                //serverSocket.bind(new InetSocketAddress("192.168.49.1",8888));
                Log.d("Reciever","Server Listening");
                Log.d("Reciever Address",serverSocket.getLocalSocketAddress().toString());
                Log.d("Reciever Port",String.valueOf(serverSocket.getLocalPort()));
                //serverSocket.setSoTimeout(1000000);
                client=serverSocket.accept();
                Log.d("Reciever","Server Connected");

                InputStream inputStream1=client.getInputStream();
                ObjectInputStream inputStream=new ObjectInputStream(inputStream1);
                fileName=inputStream.readUTF();
                file=new File(Environment.getExternalStorageDirectory()+"/"+context.getPackageName()+"/"+fileName);
                Log.d("Reciever",file.getPath());
                File dir=file.getParentFile();
                if(!dir.exists()) dir.mkdirs();
                if(file.exists()) file.delete();
                if(file.createNewFile()){
                    Log.d("Reciever","File Created");
                }else Log.d("Reciever","File Not Created");
                OutputStream outputStream=new FileOutputStream(file);
                try{
                    while(((len=inputStream.read(buf))!=-1)){
                        //len=inputStream.read(buf);
                        outputStream.write(buf,0,len);
                        Log.d("Reciever","Writing Data    -"+len);
                    }
                    Log.d("Reciever","Writing Data Final   -"+len);
                }catch (Exception ee){
                    Log.d("Reciever","oops");
                    ee.printStackTrace();
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
                //serverSocket.close();
                //client.close();

            }catch (Exception e){
                e.printStackTrace();
            }



        }

        @Override
        protected Void doInBackground(Void... params) {

            recieveData();
            return null;

            /*byte buf[]=new byte[1024];
            int len;

            try {
                //serverSocket.bind(new InetSocketAddress(8888));
                //serverSocket.bind(new InetSocketAddress("192.168.49.1",8888));
                Log.d("Reciever","Server Listening");
                Log.d("Reciever Address",serverSocket.getLocalSocketAddress().toString());
                Log.d("Reciever Port",String.valueOf(serverSocket.getLocalPort()));
                //serverSocket.setSoTimeout(1000000);
                client=serverSocket.accept();
                Log.d("Reciever","Server Connected");

                InputStream inputStream1=client.getInputStream();
                ObjectInputStream inputStream=new ObjectInputStream(inputStream1);
                fileName=inputStream.readUTF();
                file=new File(Environment.getExternalStorageDirectory()+"/"+context.getPackageName()+"/"+fileName);
                Log.d("Reciever",file.getPath());
                File dir=file.getParentFile();
                if(!dir.exists()) dir.mkdirs();
                if(file.exists()) file.delete();
                if(file.createNewFile()){
                    Log.d("Reciever","File Created");
                }else Log.d("Reciever","File Not Created");
                OutputStream outputStream=new FileOutputStream(file);
               while(inputStream.available()>0){
                   len=inputStream.read(buf);
                   outputStream.write(buf,0,len);
                   Log.d("Reciever","Writing Data");
               }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;*/
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(context,"File Transferred!",Toast.LENGTH_LONG).show();
            Log.d("Reciever","onPostExecute");
            try{
                serverSocket.close();
                client.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(context,"Transfer Cancelled",Toast.LENGTH_LONG).show();
            Log.d("Reciever","Transfer Cancelled");
            try{
                if(client.isConnected()) serverSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy","yup");
        unregisterReceiver(myBroadcastReciever);
        p2pManager.cancelConnect(channel,null);
        p2pManager.stopPeerDiscovery(channel,null);
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            fileServerAsyncTask.cancel(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        //p2pManager.removeGroup(channel,null);
        //deletePersistentGroups();
    }

    private void deletePersistentGroups(){
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(p2pManager, channel, netid, null);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}