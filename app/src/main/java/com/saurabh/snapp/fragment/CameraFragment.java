package com.saurabh.snapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.saurabh.snapp.FindUsersActivity;
import com.saurabh.snapp.R;
import com.saurabh.snapp.ShowCaptureActivity;
import com.saurabh.snapp.SplashScreenActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback {

    Camera camera=null;
    Camera.PictureCallback jpegCallBack;
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    final int CAMERA_REQUEST_CODE =1;

    public static CameraFragment newInstance(){
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        mSurfaceView  = view.findViewById(R.id.surfaceView);//Initialising the surface view
        mSurfaceHolder = mSurfaceView.getHolder();//Creating the holder for surface view
        if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);//Requesting for camera permission
        else
        {
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        Button mLogout = view.findViewById(R.id.logout);
        Button mFindUser = view.findViewById(R.id.findUser);
        Button mCapture = view.findViewById(R.id.capture);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        mFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindUsers();
            }
        });
        mCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        jpegCallBack = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                Bitmap rotateBitmap = rotate(decodedBitmap);

                String fileLocation = SaveImageToStorage(rotateBitmap);
                if (fileLocation!=null)
                {
                    Intent intent = new Intent(getActivity(), ShowCaptureActivity.class);
                    startActivity(intent);
                    return;
                }
            }
        };

        return view;
    }

    private String SaveImageToStorage(Bitmap bitmap) {
        String fileName = "imageToSend";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] dataToUpload = baos.toByteArray();
            FileOutputStream fos = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(dataToUpload);
        } catch (Exception e) {
            e.printStackTrace();
            fileName=null;
        }
        return fileName;
    }

    private Bitmap rotate(Bitmap decodeBitmap) {
        int w = decodeBitmap.getWidth();
        int h = decodeBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        return Bitmap.createBitmap(decodeBitmap,0,0,w,h,matrix,true);
    }

    private void FindUsers() {
        Intent intent = new Intent(getContext(), FindUsersActivity.class);
        startActivity(intent);
        return;
    }

    private void captureImage() {
        camera.takePicture(null,null,jpegCallBack);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open();
        }
        catch (Exception e){
            Toast.makeText(getContext(), "Couldn't load camera", Toast.LENGTH_SHORT).show();
        }
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        camera.setDisplayOrientation(90);
        parameters.setPreviewFpsRange(30,30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        bestSize=sizeList.get(0);
        for (int i = 1;i<sizeList.size() ; i++)
            if((sizeList.get(i).width*sizeList.get(i).height)>(bestSize.width*bestSize.height))
                bestSize=sizeList.get(i);
        parameters.setPreviewSize(bestSize.width,bestSize.height);
        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    mSurfaceHolder.addCallback(this);
                    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        getFragmentManager().beginTransaction().detach(this).commitNow();
                        getFragmentManager().beginTransaction().attach(this).commitNow();
                    } else {
                        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
                    }
                }
                else
                    Toast.makeText(getContext(), "Grant camera Permission", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    public void logout()
    {
        FirebaseAuth.getInstance().signOut();//Signing out
        Intent intent = new Intent(getContext(), SplashScreenActivity.class);//Setting up to restart the app
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);//Restarting the app
        return;
    }

}