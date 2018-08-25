package com.jurext.belezenje_storitve.lettherebelight;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
    ToggleButton gumb;
    RelativeLayout main;
    boolean imaFlash=false;
    boolean luckaSveti=false;
    boolean kamera1AliKamera2= false;
    ///////////////////// Camera za starejse telefone
    Camera camera;
    Camera.Parameters parameters;
    //////////////////// Camera za novejse telefone
    CameraManager cameraManager;
    String cameraId;
    ////////////////////
    int verzija=0;
    int verzija2=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gumb=(ToggleButton)findViewById(R.id.toggleButton);
        imaFlash=getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        main=(RelativeLayout)findViewById(R.id.activity_main);
        ///////////////////////
        if (!imaFlash){
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Napaka!!");
            alertDialog.setMessage("Ta naprava ne podpira svetilke");
            alertDialog.setButton("VREDU", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    System.exit(0);
                }
            });
            alertDialog.show();
        }

        if (Preveri_verzijo() == 120){
            GETCamera();
            verzija=120;
        }
        else if (Preveri_verzijo() == 21){
            GETCamera2();
            verzija=21;
        }

        gumb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    main.setBackground(getResources().getDrawable(R.drawable.bulb_satye));
                    if (verzija == 120){
                        vklopiLucko();
                    }
                    else if (verzija == 21){
                        vklopiLucko2();
                    }
                }
                else if(!b){
                    main.setBackground(getResources().getDrawable(R.drawable.bulb_atomic));
                    if (verzija == 120){
                        izklopiLucko();
                    }
                    else if (verzija == 21){
                        izklopiLucko2();
                    }
                }
            }
        });

        if (Preveri_verzijo() == 120){
            Toast.makeText(getBaseContext(), "Verzija je 1-20", Toast.LENGTH_SHORT).show();
        }
        else if(Preveri_verzijo() == 21) {
            Toast.makeText(getBaseContext(), "Verzija je 21", Toast.LENGTH_SHORT).show();
        }

        //GETCamera();
        //GETCamera2();
    }



    ///////////////////////////////////////////////////////////////////////// metode za staro kamero
    private void GETCamera(){
        if (camera==null){
            try {
                camera = Camera.open();
                parameters = camera.getParameters();
            }catch (Exception e){
                ///
            }
        }
    }

    private void vklopiLucko(){
        if (!luckaSveti){
            if (camera == null || parameters == null){
                return;
            }
            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
            luckaSveti=true;
        }
    }

    private void izklopiLucko(){
        if (luckaSveti){
            if (camera == null || parameters == null){
                return;
            }
            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.stopPreview();
            luckaSveti=false;
        }
    }

    private void sprostiKamero(){
        if (camera != null){
            camera.release();
            camera = null;
        }
    }

///////////////////////////////////////////////////////////////////////// metode za novo kamero

    @TargetApi(Build.VERSION_CODES.M)
    private void GETCamera2(){
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        }catch (Exception e){
            //
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void vklopiLucko2(){
        try {
            cameraManager.setTorchMode(cameraId, true);
        }catch (Exception e){
            ///
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void izklopiLucko2(){
            try {
                cameraManager.setTorchMode(cameraId, false);
            }catch (Exception e){
                ///
            }
    }

    ///////////////////////////////////////////////////////////////////////// metoda preveri katera verzija nadroida je na telefonu

    private int Preveri_verzijo(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // android verzija med 1 in 20
            return 120;
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // android verzija 21 ali vec
            return  21;
        }
        return  0;
    }
///////////////////////////////////////////////////////////////////////////// override metode


    @Override
    protected void onStop() {
        super.onStop();
        if (verzija == 120){
            izklopiLucko();
            gumb.setChecked(false);
        }
        else if (verzija == 21){
            izklopiLucko2();
            gumb.setChecked(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (verzija == 120){
            izklopiLucko();
            gumb.setChecked(false);
        }
        else if (verzija == 21){
            izklopiLucko2();
            gumb.setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (verzija == 120){
            izklopiLucko();
            gumb.setChecked(false);
        }
        else if (verzija == 21){
            izklopiLucko2();
            gumb.setChecked(false);
        }
    }



}
