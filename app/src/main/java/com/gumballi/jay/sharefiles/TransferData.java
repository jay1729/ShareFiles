package com.gumballi.jay.sharefiles;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by jay on 5/1/18.
 */

public class TransferData extends AsyncTask<Void,Void,Void>{
    public Context context;
    public Uri uri;
    public InetAddress serverAddress;
    public String fileName;

    public TransferData(Context context, Uri uri,String fileName, InetAddress serverAddress){
        this.context=context;
        this.uri=uri;
        this.fileName=fileName;
        this.serverAddress=serverAddress;
        Toast.makeText(context,"Transfer Started",Toast.LENGTH_SHORT).show();
       // Toast.makeText(context,(new InetSocketAddress(port)).toString(),Toast.LENGTH_LONG).show();
    }

    public void sendData(Context context,Uri uri){

        int len;
        byte buf[]  = new byte[1024];

        Log.d("Data Transfer","Transfer Starter");
        //Log.d("Data Transfer IP",(new InetSocketAddress(port)).toString());

        Socket socket=new Socket();

        try{
            socket.bind(null);
            //socket.bind(new InetSocketAddress(8888));
            Log.d("Client Address",socket.getLocalSocketAddress().toString());

            socket.connect(new InetSocketAddress(serverAddress,8888));
            Log.d("Client","Client Connected");

            //OutputStream outputStream = socket.getOutputStream();
            //ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
            OutputStream outputStream=socket.getOutputStream();
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream=cr.openInputStream(uri);
            objectOutputStream.writeUTF(fileName);
            while ((len = inputStream.read(buf)) != -1) {
                objectOutputStream.write(buf,0,len);
                Log.d("Sender","Writing Data");
            }
            //objectOutputStream.write(-1);
            inputStream.close();
            objectOutputStream.close();
            socket.close();

        }catch (Exception e){
            //Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
            Log.d("Data Transfer",e.toString());
            e.printStackTrace();
        }

        finally {
            if(socket!=null){
                if(socket.isConnected()){
                    try{
                        socket.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    protected Void doInBackground(Void... params) {
        sendData(context,uri);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context, "Data Transferred!", Toast.LENGTH_SHORT).show();
        Log.d("Sender","Finished!");
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d("Sender","Cancelled!");
    }
}
