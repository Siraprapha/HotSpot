package com.example.ink.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CallFromUser extends Fragment {

    private static final String CAMERA_IMAGES_DIR = "TestPictures";
    String userChoosenTask;
    ImageView img;
    Button b;

    public CallFromUser(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_fromuser, container, false);
        Intent intent = getActivity().getIntent();

        img = rootview.findViewById(R.id.ivImage);
        b = rootview.findViewById(R.id.btnSelectPhoto);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        return rootview;
    }

    public void selectImage(View view) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(getActivity().getApplicationContext());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
        Toast.makeText(getActivity().getApplicationContext(), "Button Clicked!", Toast.LENGTH_SHORT).show();
    }

    private void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }
    private void galleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                    AlertDialog.Builder d = new AlertDialog.Builder(getActivity().getApplicationContext());
                    d.setTitle("Add Photo!");
                    d.setMessage("Could not open camera or Gallery. Please check permission.");
                    d.show();
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1)
                onSelectFromGalleryResult(data);
            else if (requestCode == 0)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        img.setImageBitmap(bm);
        //img.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.id.ivImage, 100, 100));
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        //saveImage(thumbnail);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"inkie.jpg");
        if (!destination.mkdirs()) {
            Log.e("image", "Directory not created");
        }
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            Log.d("image", "file not found" + destination, e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("image", "Error writing " + destination, e);
            e.printStackTrace();
        }
        img.setImageBitmap(thumbnail);

    }

    public void saveImage(Bitmap bitmap){
        // Save image to gallery
        String savedImageURL = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),bitmap,"","");
        Toast.makeText(getActivity().getApplicationContext(),"Image Saved at : "+savedImageURL,Toast.LENGTH_LONG).show();
        Log.d("image", "saveImage: "+savedImageURL);
    }

    //decode bitmap
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public void createFolder(){
        // here PictureFolder is the folder name you can change it offcourse
        String RootDir = Environment.getExternalStorageDirectory()
                + File.separator + "PictureFolder";
        File RootFile = new File(RootDir);
        RootFile.mkdir();
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("image", "Directory not created");
        }
        return file;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("Check", "onActivityCreated");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("Check", "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Check", "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Check", "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("Check", "onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("Check", "onDetach");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Check", "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Check", "onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("Check", "onStart");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i("Check", "onStop");
    }

}
