package com.gumballi.jay.sharefiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jay on 5/1/18.
 */

public class ChooseFile {

    public static final int FILE_TRANSFER_CODE=69;

    public static void fileChooser(Activity activity){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try{
            activity.startActivityForResult(Intent.createChooser(intent,"Choose File to Transfer"),FILE_TRANSFER_CODE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
