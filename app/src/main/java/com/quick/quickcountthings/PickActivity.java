package com.quick.quickcountthings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.quick.quickcountthings.view.CanvasDrawActivity;

import java.io.File;

public class PickActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.ImageLoaderDelegate {

    Uri imgUri;
    String id, nama_template, deskripsi, foto_template;
    CardView cv_tmp_card;
    ImageView iv_tmp_image;
    TextView tv_tmp_name, tv_tmp_desc;
    Context context;
    Button btn_change, btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        context = this;
        btn_change = (Button) findViewById(R.id.btn_change);
        btn_start = (Button) findViewById(R.id.btn_start);
        iv_tmp_image = (ImageView) findViewById(R.id.iv_tmp_image);
        tv_tmp_name = (TextView) findViewById(R.id.tv_tmp_name);
        tv_tmp_desc = (TextView) findViewById(R.id.tv_tmp_desc);
        cv_tmp_card = (CardView) findViewById(R.id.cv_tmp_card);

        id = getIntent().getStringExtra("id");
        nama_template = getIntent().getStringExtra("nama_template");
        deskripsi = getIntent().getStringExtra("deskripsi");
        foto_template = getIntent().getStringExtra("foto_template");

        tv_tmp_name.setText(nama_template);
        tv_tmp_desc.setText(deskripsi);

        Glide.with(context)
                .load(foto_template)
                .placeholder(R.drawable.counts)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_tmp_image);

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PickActivity.this, TemplateActivity.class);
                startActivity(intent);
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PickActivity.this, CanvasDrawActivity.class);
                intent.putExtra("obj_type", nama_template);
                startActivity(intent);
            }
        });
    }

    public void pickimg(View view) {
        BSImagePicker pickerDialog = new BSImagePicker.Builder("com.quick.quickcountthings.fileprovider")
                .build();

        pickerDialog.show(getSupportFragmentManager(), "picker");
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        imgUri = uri;
        final File file = FileUtils.getFile(this, imgUri);
        Log.e("cek imgUri", imgUri.toString());
    }

    @Override
    public void loadImage(File imageFile, ImageView ivImage) {

    }
}
