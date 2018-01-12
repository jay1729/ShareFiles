package com.gumballi.jay.sharefiles;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by jay on 8/1/18.
 */

public class FileUtils {

    public static String readFile(File file){
        String name="";
        try{
            FileReader fileReader=new FileReader(file);
            BufferedReader reader=new BufferedReader(fileReader);
            name=reader.readLine();
            reader.close();
            fileReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return name;
    }

    public static Uri writeFile(String fileName){
        File file=new File(Environment.getExternalStorageDirectory()+"/"+"FileUtil.txt");
        try{
            if(!file.exists()) file.createNewFile();
            FileWriter fileWriter=new FileWriter(file);
            BufferedWriter writer=new BufferedWriter(fileWriter);
            writer.write(fileName);
            writer.close();
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }



}
