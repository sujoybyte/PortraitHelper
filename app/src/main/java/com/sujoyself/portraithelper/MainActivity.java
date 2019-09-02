package com.sujoyself.portraithelper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;

public class MainActivity extends Activity {

    private int STORAGE_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button patchButton = findViewById(R.id.button1) ;
        patchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    moveFile();

                } else {
                    requestStoragePermission();
                }
            }
        });
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Storage Permission Needed")
                    .setMessage("This permission is needed for patching that is organizing the portrait images. ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void moveFile() {

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

            if (cameraFolder == null) {
                for (int i = 0; i < cameraFolderList.length; i++) {
                    File[] cameraFolderIMGList = cameraFolderList[i].listFiles(new FilenameFilter() {
                        public boolean accept(File cameraFolder, String name) {
                            return name.toLowerCase().endsWith(".jpg");
                        }
                    });

                    for (int m = 0; m < cameraFolderIMGList.length; m++) {
                        String jpgFile = cameraFolderIMGList[m].toString().substring(51);

                        //getting image location from portrait folder
                        File newLocation = new File(cameraFolderIMGList[m].toString().substring(0, 31) + jpgFile);

                        //moving image from portrait folder to the camera folder
                        cameraFolderIMGList[m].renameTo(newLocation);
                    }
                }
            }
            else {
                //delete the empty portrait folders
                for (int d = 0; d < cameraFolderList.length; d++) {
                    cameraFolderList[d].delete();
                }
            }

            Toast.makeText(this, "PORTRAIT PATCHED SUCCESSFULLY :)", Toast.LENGTH_SHORT).show();
        }

        catch (Exception e)
        {
            Toast.makeText(this, "PATCHING FAILED :( !!", Toast.LENGTH_SHORT).show();
        }
    }

}

