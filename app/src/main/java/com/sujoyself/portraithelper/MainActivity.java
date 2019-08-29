package com.sujoyself.portraithelper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void moveFile(View view) {

        try {
            String storage = String.valueOf(Environment.getExternalStorageDirectory());  //to get the main phone storage location
            File cameraFolder = new File(storage + "/DCIM/Camera");  //the camera clicked photos location

            File[] cameraFolderList = cameraFolder.listFiles(new FilenameFilter()
            {
                //listing the portrait folders in an array
                public boolean accept(File cameraFolder, String name)
                {
                    return name.toUpperCase().startsWith("IMG") && !name.toLowerCase().endsWith(".jpg");
                }
            });

            for (int i = 0; i < cameraFolderList.length; i++)
            {
                File[] cameraFolderIMGList = cameraFolderList[i].listFiles(new FilenameFilter()
                {
                    public boolean accept(File cameraFolder, String name)
                    {
                        return name.toLowerCase().endsWith(".jpg");
                    }
                });

                for (int m = 0; m < cameraFolderIMGList.length; m++)
                {
                    String jpgFile = cameraFolderIMGList[m].toString().substring(51);

                    //getting image location from portrait folder
                    File newLocation = new File(cameraFolderIMGList[m].toString().substring(0, 31) + jpgFile);

                    //moving image from portrait folder to the camera folder
                    cameraFolderIMGList[m].renameTo(newLocation);
                }
            }
            for (int d = 0;d < cameraFolderList.length;d++)
            {
                //delete the empty portrait folders
                cameraFolderList[d].delete();
            }
            
            Toast.makeText(this, "PORTRAIT PATCHED SUCCESSFULLY :)", Toast.LENGTH_SHORT).show();
        }

        catch (Exception e)
        {
            Toast.makeText(this, "PATCHING FAILED :( !!", Toast.LENGTH_SHORT).show();
        }
    }

}

