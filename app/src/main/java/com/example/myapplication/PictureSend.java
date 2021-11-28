package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.R;
import com.example.myapplication.dao.HttpUtil;
import com.example.myapplication.dao.HttpUtilMultiPart;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class PictureSend extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;
    private File photoFile = null;
    private String server_url = "http://15.165.219.73:2000/api/user/pictureSend";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_picture_send);

        //권한 체크
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener) //PERMISSIONLISTENER 구현
                .setRationaleMessage("카메라 권한이 필요합니다.")//카메라 권한 팝업을 알려줄 때 어떤 메세지를 띄울건지?
                .setDeniedMessage("카메라 권한이 거부되었습니다.")//카메라 권한을 거부하면 나오는 메세지
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);


        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        findViewById(R.id.btn_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(PictureSend.this,Manifest.permission.CAMERA);
                if(permissionCheck == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(PictureSend.this, new String[]{Manifest.permission.CAMERA},0);
                    Toast.makeText(getApplicationContext(), "권한 거부됨",Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(getApplicationContext(), "권한 허용됨",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                        photoFile = null;
                        photoFile = createImageFile();
                        if(photoFile != null){
                            photoUri = FileProvider.getUriForFile(getApplicationContext(),getPackageName(), photoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                            // startActivityForResult 는 다음 인텐트로 화면전환이 일어났을 때 다음 액티비티로부터 값을 다시 가져옴.
                            // startActivityForResult 이것만 쓰면 안되고 밑에 구현해야함.
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){
            if(grantResults[0] == 0){
                Toast.makeText(this,"카메라 권한이 승인됨",Toast.LENGTH_SHORT);
            }else{
                Toast.makeText(this,"카메라 권한이 거부됨",Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;
            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);

            } else {
                exifDegree = 0;
            }
            ((ImageView) findViewById(R.id.iv_result)).setImageBitmap(rotate(bitmap, exifDegree));
        }
    }

    private int exifOrientationToDegrees(int exifOrientation){
        //카메라를 찍을때 90도로 돌아갈때도 있고 그럴때 이미지 회전을 로테이트 시키는 구문.
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);
    }

    private File createImageFile() {
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String timeStamp = LocalDateTime.now().toString();
        //이미지파일 이름을 년월일 시간단위로 생성해서 중복해서 생성되지 않도록!
        String imageFileName = "TEST_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
//            image = File.createTempFile(
//                    imageFileName,
//                    ".png",
//                    storageDir
//            );
            image = new File(storageDir, imageFileName + ".jpg");
            if (!image.exists()) image.createNewFile();

            image.deleteOnExit();
            //jvm이 종료될 때 삭제한다.
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageFilePath = image.getAbsolutePath();
        Toast.makeText(getApplicationContext(), imageFilePath ,Toast.LENGTH_SHORT).show();
        return image;
    }









    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //퍼미션 허용시 일어나는 액션
            Toast.makeText(getApplicationContext(), "권한 허용됨",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            //퍼미션 거부시 일어나는 액션
            Toast.makeText(getApplicationContext(), "권한 거부됨",Toast.LENGTH_SHORT).show();
        }
    };
}
