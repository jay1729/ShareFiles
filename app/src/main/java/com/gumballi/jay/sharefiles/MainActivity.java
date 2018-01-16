package com.gumballi.jay.sharefiles;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public Button sendButton,recieveButton;
    public int numbPermissions=7;
    public String[] permissions=new String[numbPermissions];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissions[0]=android.Manifest.permission.ACCESS_NETWORK_STATE;
        permissions[1]=android.Manifest.permission.ACCESS_WIFI_STATE;
        permissions[2]=android.Manifest.permission.CHANGE_WIFI_STATE;
        permissions[3]=android.Manifest.permission.INTERNET;
        permissions[4]=android.Manifest.permission.READ_EXTERNAL_STORAGE;
        permissions[5]=android.Manifest.permission.CHANGE_NETWORK_STATE;
        permissions[6]=android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        sendButton=(Button) findViewById(R.id.sendButton);
        recieveButton=(Button) findViewById(R.id.recieveButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(!checkPermissions()){
                    askPermissions();
                    return;
                }
                Intent intent=new Intent(MainActivity.this,PeersActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        recieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkPermissions()){
                    askPermissions();
                    return;
                }
                Intent intent=new Intent(MainActivity.this,RecieveActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean checkPermissions(){
        for(int i=0;i<numbPermissions;i++){
            if(ContextCompat.checkSelfPermission(this,permissions[i])!=PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    public void askPermissions(){
        AsyncTask task=new AsyncTask(){

            @Override
            protected Object doInBackground(Object[] params) {
                for(int i=0;i<numbPermissions;i++){
                    if(ContextCompat.checkSelfPermission(getApplicationContext(),permissions[i])!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{permissions[i]},i);
                    }
                }
                return null;
            }
        };
        task.execute();
    }

}
