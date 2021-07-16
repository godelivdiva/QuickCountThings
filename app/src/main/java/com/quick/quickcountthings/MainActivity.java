package com.quick.quickcountthings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.facebook.stetho.Stetho;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.snackbar.Snackbar;
import com.quick.quickcountthings.view.CanvasDrawActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate, View.OnTouchListener {

    PhotoView iv_gambar;
    TextView tv_hasil;
    SweetAlertDialog sweetAlertDialog;
    Button btn_save_img, btn_reset_count;

    Uri image_uri;
    Bitmap bmp;
    ChipCloud chipCloud;
    private String array_type[];
    String param_type;
    Canvas canvas;
    Paint paint, pain;
    dbHelp helper;
    float downx = 0,downy = 0,upx = 0,upy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        AndroidNetworking.initialize(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Stetho.initializeWithDefaults(this);

        helper = new dbHelp(this);
        helper.delete();

        iv_gambar = findViewById(R.id.iv_gambar);
        tv_hasil = findViewById(R.id.tv_hasil);
        btn_save_img =  findViewById(R.id.btn_save_img);
        btn_reset_count =  findViewById(R.id.btn_reset_count);

        chipCloud = (ChipCloud) findViewById(R.id.cc_type);

        array_type = new String[2];
        array_type[0] = "circle";
        array_type[1] = "rectangle";

//        iv_gambar.setZoomable(false);
        iv_gambar.setOnTouchListener(this);

        new ChipCloud.Configure()
                .chipCloud(chipCloud)
                .selectedColor(Color.parseColor("#B99FEB"))
                .selectedFontColor(Color.parseColor("#ffffff"))
                .deselectedColor(Color.parseColor("#e1e1e1"))
                .deselectedFontColor(Color.parseColor("#333333"))
                .selectTransitionMS(500)
                .deselectTransitionMS(250)
                .labels(array_type)
                .mode(ChipCloud.Mode.SINGLE)
                .allCaps(false)
                .gravity(ChipCloud.Gravity.LEFT)
                .textSize(getResources().getDimensionPixelSize(R.dimen.default_textsize))
                .verticalSpacing(getResources().getDimensionPixelSize(R.dimen.vertical_spacing))
                .minHorizontalSpacing(getResources().getDimensionPixelSize(R.dimen.min_horizontal_spacing))
                .chipListener(new ChipListener() {
                    @Override
                    public void chipSelected(int index) {
                        //...
                        param_type = array_type[index];
                        Log.e("cek type", param_type);


                    }
                    @Override
                    public void chipDeselected(int index) {
                        //...
                    }
                })
                .build();

        btn_save_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#B99FEB"));
                sweetAlertDialog.setTitleText("\nLoading . . .");
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saveImage(bmp);
                    }
                },1000);

            }
        });

        btn_reset_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Paint clearPaint = new Paint();
//                clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
//                canvas.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), clearPaint);
//                iv_gambar.setImageURI(image_uri);
//                tv_hasil.setText("");
//                tv_hasil.setVisibility(View.GONE);
                Intent i = new Intent(getApplicationContext(), CanvasDrawActivity.class);
                i .putExtra("image_uri", image_uri.toString());
                startActivity(i);
            }
        });

    }

    public void pilih(View view) {
        iv_gambar.setRotation(0);
        BSImagePicker pickerDialog = new BSImagePicker.Builder("com.quick.quickcountthings.fileprovider")
                .build();

        pickerDialog.show(getSupportFragmentManager(), "picker");
    }

    public void hitung(View view) {
        Log.e("cek", "klik hitung");
        if(image_uri == null){
            Snackbar.make(getWindow().getDecorView().getRootView(), "Pilih / ambil foto terlebih dahulu!", Snackbar.LENGTH_LONG)
                    .show();
        } else if(param_type == null) {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Pilih type objek terlebih dahulu!", Snackbar.LENGTH_LONG)
                    .show();
        } else {
            firstFun();

        }
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        image_uri = uri;
        final File file = FileUtils.getFile(this, image_uri);

        Glide.with(MainActivity.this).load(getRightAngleImage(file.getPath())).into(iv_gambar);

        iv_gambar.setImageURI(image_uri);
        tv_hasil.setVisibility(View.GONE);
        tv_hasil.setText("");
        btn_save_img.setVisibility(View.GONE);
        btn_reset_count.setVisibility(View.GONE);
    }

    @Override
    public void loadImage(File imageFile, ImageView ivImage) {
        Glide.with(MainActivity.this).load(getRightAngleImage(imageFile.getPath())).into(ivImage);
    }

    void firstFun(){
        sweetAlertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#B99FEB"));
        sweetAlertDialog.setTitleText("\nLoading . . .");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                helper.delete();
                postData();
            }
        },1000);
    }

    void postData(){
        final File file = FileUtils.getFile(this, image_uri);
        Log.e("file ", String.valueOf(file));

        AndroidNetworking.upload("http://192.168.168.159:5000/v1/materials_detection")
                .addMultipartFile("image",file)
                .addMultipartParameter("obj_type",param_type)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.e("onProgress :", "onProgress");
                    }
                })
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        Log.e("Data :", String.valueOf(response));
                        Log.e("size", String.valueOf(response.length()));

                        bmp = BitmapFactory.decodeFile(file.getPath()).copy(Bitmap.Config.ARGB_8888, true);
                        paint = new Paint();
                        paint.setColor(getResources().getColor(R.color.warning));
                        paint.setTextAlign(Paint.Align.CENTER);
                        canvas = new Canvas(bmp); //This sentence is incorrect

                        pain = new Paint();
                        pain.setColor(getResources().getColor(R.color.warning));
                        pain.setStyle(Paint.Style.STROKE);
                        pain.setTextAlign(Paint.Align.CENTER);
                        pain.setStrokeWidth(10f);

                        for (int j = 0; j < response.length(); j++) {
                            try {
                                JSONObject c = response.getJSONObject(j);

                                ContentValues values = new ContentValues();
                                values.put("xcenter", c.getString("xcenter"));
                                values.put("ycenter", c.getString("ycenter"));
                                values.put("width", c.getString("width"));
                                values.put("height", c.getString("height"));
                                values.put("idx", c.getString("idx"));
                                values.put("flag", "Y");
                                helper.insert(values);

                                Float x = Float.parseFloat(c.getString("xcenter"));
                                Float y = Float.parseFloat(c.getString("ycenter"));
                                Float w = Float.parseFloat(c.getString("width"));
                                Float r = Float.parseFloat(c.getString("height"));
                                Float radius = Math.min(w/2,r/2);

                                canvas.drawCircle(x, y, w/2 , pain);
//                                canvas.drawText(String.valueOf(j+1), x, y, paint);
                                paint.setTextSize(42f);
//                                canvas.drawText(c.getString("idx"), x, y, paint);

                            } catch (JSONException e) {
                                // Do something to recover ... or kill the app.
                            }
                        }
                        iv_gambar.setRotation(getPhotoOrientation(file.getPath()));
                        iv_gambar.setImageBitmap(bmp);
                        tv_hasil.setVisibility(View.VISIBLE);
                        tv_hasil.setText("Hasil : "+response.length());
                        btn_save_img.setVisibility(View.VISIBLE);
                        btn_reset_count.setVisibility(View.VISIBLE);

                        sweetAlertDialog.dismissWithAnimation();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("Error :", "error");
                        sweetAlertDialog.dismissWithAnimation();
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Gagal menghitung objek!", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/ACountThings");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String currentDate = new SimpleDateFormat("ddMMyy HH:mm:ss", Locale.getDefault()).format(new Date());

        String fname = "ACountThings-"+ currentDate +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            sweetAlertDialog.dismissWithAnimation();
            Snackbar.make(getWindow().getDecorView().getRootView(), "Gambar tersimpan", Snackbar.LENGTH_LONG)
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {
            ExifInterface exif  = null;
            try {
                exif = new ExifInterface(imagePath);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 90;
                    break;
                default:
                    rotate = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    private String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree,photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }



    private String rotateImage(int degree, String imagePath){

        if(degree<=0){
            return imagePath;
        }
        try{
            Bitmap b= BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if(b.getWidth()>b.getHeight()){
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
        return imagePath;
    }

    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.e("cek", "action down");
                downx = event.getX();
                downy = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("cek", "action move");
                upx = event.getX();
                upy = event.getY();
//                canvas.drawLine(downx, downy, upx, upy, paint);
//                iv_gambar.invalidate();
//                downx = upx;
//                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                Log.e("cek", "action up");
                upx = event.getX();
                upy = event.getY();
//                canvas.drawCircle(upx, upy, upx/2, pain);
//                iv_gambar.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

}
