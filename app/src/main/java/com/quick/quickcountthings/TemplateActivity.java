package com.quick.quickcountthings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TemplateActivity extends AppCompatActivity {
    Context context;
    RecyclerAdapterTemplate adapter;
    LottieAnimationView lav_tmp_dotLoading;
    LinearLayout ll_tmp_content;
    SwipeRefreshLayout srl_tmp_list;
    private RecyclerView rv_maindata;
    private ArrayList<String> listId, listName, listDesc, listImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        context = this;
        AndroidNetworking.initialize(getApplicationContext());
        getSupportActionBar().hide();
        rv_maindata = (RecyclerView) findViewById(R.id.rv_maindata);

        lav_tmp_dotLoading = (LottieAnimationView) findViewById(R.id.lav_tmp_dotLoading);
        ll_tmp_content = (LinearLayout) findViewById(R.id.ll_tmp_content);
        srl_tmp_list = (SwipeRefreshLayout) findViewById(R.id.srl_tmp_list);

        srl_tmp_list.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        initswipeRefresh();
        showAnimation();
        getDataList();
    }

    void initswipeRefresh() {
        srl_tmp_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl_tmp_list.setRefreshing(false);
                TemplateActivity.this.recreate();
            }
        });
    }

    void showAnimation() {
        lav_tmp_dotLoading.setVisibility(View.VISIBLE);
        ll_tmp_content.setVisibility(View.GONE);
    }

    void hideAnimation() {
        lav_tmp_dotLoading.setVisibility(View.GONE);
        ll_tmp_content.setVisibility(View.VISIBLE);
    }

    void initArray() {
        listId = new ArrayList<String>();
        listName = new ArrayList<String>();
        listDesc = new ArrayList<String>();
        listImage = new ArrayList<String>();

        listId.clear();
        listName.clear();
        listDesc.clear();
        listImage.clear();
    }

    public void getDataList() {
        initArray();
        AndroidNetworking.get("http://produksi.quick.com/QuickCountThings/api/template.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                                for (int a = 0; a < response.length(); a++) {
                                    JSONObject c = response.getJSONObject(a);
                                    listId.add(c.getString("id"));
                                    listName.add(c.getString("nama_template"));
                                    listDesc.add(c.getString("deskripsi"));
                                    listImage.add(c.getString("foto_template"));
                                }
                                initRecyclerView();
                                hideAnimation();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Failed!", Snackbar.LENGTH_LONG)
                                .show();
                        hideAnimation();
                    }
                });
    }

    void initRecyclerView() {
        rv_maindata.setHasFixedSize(true);
        adapter = new RecyclerAdapterTemplate(TemplateActivity.this, listId, listName, listDesc, listImage);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TemplateActivity.this);
        rv_maindata.setLayoutManager(layoutManager);
        rv_maindata.setAdapter(adapter);
    }

}
