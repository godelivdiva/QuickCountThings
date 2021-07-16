package com.quick.quickcountthings.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.quick.quickcountthings.FileUtils;
import com.quick.quickcountthings.R;
import com.quick.quickcountthings.model.Shape;
import com.quick.quickcountthings.presenter.CanvasPresenter;
import com.quick.quickcountthings.presenter.StatsPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by T0015
 */
public class CanvasDrawActivity extends AppCompatActivity
        implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate{

    private static final String TAG = CanvasDrawActivity.class.getSimpleName();
    private CustomView canvas = null;
    CanvasPresenter canvasPresenter;
    private int maxY ; // average screen height
    private int maxX ; //average screen height
    Uri image_uri;
    SweetAlertDialog sweetAlertDialog;
    Bitmap bmp;
    RadioGroup rg_edit;
    RadioButton rb_plus, rb_minus;
    public static Boolean plus = false;
    public static Boolean minus = false;
    String obj_type;
    Button btn_save_img;

    public static HashMap<Shape.Type, Integer> myDataset;
    public  static StatsPresenter statsPresenter;
    public static TextView tv_hasil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_draw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.canvas = (CustomView) this.findViewById(R.id.canvasDrawView);
        AndroidNetworking.initialize(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Apply for multiple permissions together
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
        }

        obj_type = getIntent().getStringExtra("obj_type");

        rg_edit = (RadioGroup) findViewById(R.id.rg_edit);
        rb_plus = (RadioButton) findViewById(R.id.rb_plus);
        rb_minus = (RadioButton) findViewById(R.id.rb_minus);
        tv_hasil = (TextView) findViewById(R.id.tv_hasil);
        btn_save_img = (Button) findViewById(R.id.btn_save_img);

        canvasPresenter = new CanvasPresenter(canvas, this);
        setupActionButtons();
        getCanvasWidthAndHeight();

        btn_save_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog = new SweetAlertDialog(CanvasDrawActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#B99FEB"));
                sweetAlertDialog.setTitleText("\nLoading . . .");
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = takeScreenshot();
                        saveImage(bitmap);
                    }
                },1000);
            }
        });
//        rg_edit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                switch (i) {
//                    case R.id.rb_plus:
//                        plus = true;
//                        minus = false;
//                        break;
//                    case R.id.rb_minus:
//                        plus = false;
//                        minus =  true;
//                        break;
//                    default:
//                        plus = false;
//                        minus = false;
//                        break;
//                }
//            }
//        });

        rb_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rb_plus.isChecked()) {
                    if (!plus) {
                        rb_plus.setChecked(true);
                        rb_minus.setChecked(false);
                        plus = true;
                        minus = false;
                    } else {
                        plus = false;
//                        rb_plus.setChecked(false);
//                        rb_minus.setChecked(false);
                        rg_edit.clearCheck();
                    }
                }
            }
        });


        rb_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rb_minus.isChecked()) {
                    if (!minus) {
                        rb_minus.setChecked(true);
                        rb_plus.setChecked(false);
                        minus = true;
                        plus = false;
                    } else {
                        minus = false;
//                        rb_minus.setChecked(false);
//                        rb_plus.setChecked(false);
                        rg_edit.clearCheck();
                    }
                }
            }
        });

        statsPresenter = new StatsPresenter();

        setHasil();

    }

    public Bitmap takeScreenshot() {
//        View rootView = findViewById(android.R.id.content).getRootView();
        CustomView view = (CustomView) this.findViewById(R.id.canvasDrawView);
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }


    private void saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/CountThings");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String currentDate = new SimpleDateFormat("ddMMyy", Locale.getDefault()).format(new Date());
        myDataset = (HashMap<Shape.Type, Integer>) statsPresenter.getCountByGroup();
        String hasil = myDataset.get(Shape.Type.CIRCLE).toString();

        String fname = "CountThings"+ currentDate +"_"+hasil+".jpg";
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

            sweetAlertDialog.dismissWithAnimation();
        }
    }

    public static void setHasil(){
        myDataset = (HashMap<Shape.Type, Integer>) statsPresenter.getCountByGroup();
        String stats = " Hasil : " + myDataset.get(Shape.Type.CIRCLE);
        if(myDataset.get(Shape.Type.CIRCLE)==null){
            tv_hasil.setText(" Hasil : 0 ");
        } else {
            tv_hasil.setText(stats);
        }
    }

    private void setupActionButtons() {
        CardView fabAdd = (CardView) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.quick.quickcountthings.fileprovider")
                        .build();

                pickerDialog.show(getSupportFragmentManager(), "picker");
            }
        });

        CardView fabUndo = (CardView) findViewById(R.id.fabUndo);
        fabUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasPresenter.undo();
            }
        });

        CardView fabCount = (CardView) findViewById(R.id.fabCount);
        fabCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstFun();
            }
        });
    }

    void firstFun(){
        sweetAlertDialog = new SweetAlertDialog(CanvasDrawActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#B99FEB"));
        sweetAlertDialog.setTitleText("\nLoading . . .");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                helper.delete();
                postData();
            }
        },1000);
    }

    void postData(){
        final File file = FileUtils.getFile(this, image_uri);
        Log.e("file ", String.valueOf(file));

        AndroidNetworking.upload("http://192.168.168.159:5000/v1/materials_detection")
                .addMultipartFile("image",file)
                .addMultipartParameter("obj_type",obj_type)
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

                        canvasPresenter.setMaxX(canvas.getWidth());
                        canvasPresenter.setMaxY(canvas.getHeight());

//                        bmp = BitmapFactory.decodeFile(file.getPath()).copy(Bitmap.Config.ARGB_8888, true);

//                        getCanvasWidthAndHeight2();
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

//                                Float x = Float.parseFloat(c.getString("xcenter"));
//                                Float y = Float.parseFloat(c.getString("ycenter"));
                                int x = c.getInt("xcenter");
                                int y = c.getInt("ycenter");
                                int width = c.getInt("width");
                                Float w = Float.parseFloat(c.getString("width"));
                                Float r = Float.parseFloat(c.getString("height"));
                                Float ts = r/4;
                                Float radius = Math.min(w/2,r/2);

                                canvasPresenter.addShapeCircle(Shape.Type.CIRCLE, x, y, width/2);

                            } catch (JSONException e) {
                                // Do something to recover ... or kill the app.
                            }
                        }
//                        canvas.setRotation(getPhotoOrientation(file.getPath()));
//                        canvas.setImageBitmap(bmp);
//                        canvas.invalidate();

                        sweetAlertDialog.dismissWithAnimation();
                        setHasil();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("Error :", "error");
                        sweetAlertDialog.dismissWithAnimation();
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Gagal menghitung objek!", Snackbar.LENGTH_LONG)
                                .show();
                        setHasil();
                    }
                });
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

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        statsPresenter.deleteAllByShape(Shape.Type.CIRCLE);
        setHasil();
        image_uri = uri;
        Log.e("cek imgUri", image_uri.toString());
        final File file = FileUtils.getFile(this, image_uri);
        canvasPresenter.imageUri = image_uri;
//        try {
//            Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(image_uri));
//            canvas.setImageBitmap(bitmap);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        canvas.setImageURI(image_uri);
    }

    @Override
    public void loadImage(File imageFile, ImageView ivImage) {
        statsPresenter.deleteAllByShape(Shape.Type.CIRCLE);
        Glide.with(CanvasDrawActivity.this).load(imageFile.getPath()).centerCrop().into(ivImage);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_canvas_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_stats) {
            startStatsView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startStatsView() {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }

    private void getCanvasWidthAndHeight() {
        ViewTreeObserver viewTreeObserver = canvas.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    canvas.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    maxY = canvas.getHeight();
                    maxX = canvas.getWidth();
                    //Reduce radius so that shape isn't left incomplete at the edge
                    canvasPresenter.setMaxX(maxX);
                    int bottomButtonHeight = 100;
                    canvasPresenter.setMaxY(maxY);
                    removeOnGlobalLayoutListener(canvas, this);
                    Log.d(TAG, " Screen max x= " + maxX + " maxy = " + maxY);
                }
            });
        }
    }

    private void getCanvasWidthAndHeight2() {
        ViewTreeObserver viewTreeObserver = canvas.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    canvas.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    maxY = canvas.getHeight();
                    maxX = canvas.getWidth();
                    //Reduce radius so that shape isn't left incomplete at the edge
                    canvasPresenter.setMaxX(maxX);
                    int bottomButtonHeight = 100;
                    canvasPresenter.setMaxY(maxY);
                    removeOnGlobalLayoutListener(canvas, this);
                    Log.d(TAG, " Screen max x= " + maxX + " maxy = " + maxY);
                }
            });

//            canvas.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//
////                    Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(),
////                            R.drawable.background_image);
//
//                    float imageRatio = (float) bmp.getWidth() / (float) bmp.getHeight();
//
//                    int imageViewWidth = canvas.getWidth();
//                    int imageRealHeight = (int) (imageViewWidth / imageRatio);
//
//                    Bitmap imageToShow = Bitmap.createScaledBitmap(bmp, imageViewWidth, imageRealHeight, true);
//                    canvas.setImageBitmap(imageToShow);
//
//                }
//            });
        }
    }

    public static Boolean getPlus(){
        return plus;
    }

    public static Boolean getMinus(){
        return minus;
    }

    /*Since global layout listener is called multiple times, remove it once we get the screen width and height
     */
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }
}
