package com.example.bot_project;

import android.app.Notification;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Area01_Fragment extends Fragment {


//    public ImageButton iot;
//    public ImageButton gas;
//    public ImageButton thermometer;
    public TextView Area01_time;
    public TextView Area01_errormessage;
    public ImageView check;
    public Switch onoff;
    private RecyclerView listview;
    private Adapter adapter;
    private ArrayList<String> iotlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View layout =inflater.inflate(R.layout.area01, container, false);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
//        iot = getView().findViewById(R.id.imb_Area01_iot);
//        gas = getView().findViewById(R.id.imbtn_Area01_gas);
//        thermometer = getView().findViewById(R.id.imb_Area01_thermometer);
        Area01_time = getView().findViewById(R.id.tv_Area01_time);
        Area01_errormessage = getView().findViewById(R.id.tv_Area01_errormessage);

        check = getView().findViewById(R.id.imv_Area01_check);
        onoff = getView().findViewById(R.id.switch_Area01);



        adapter.setOnClickListener(new Adapter.itemClick() {
            @Override
            public void onItemClickListener(View v, int position,String iotname) {
                if(iotname.equals("0")){
                    check.setImageResource(R.drawable.iotact);
                    StatusCheck();
                }else if(iotname.equals("1")){
                    check.setImageResource(R.drawable.gas);
                    StatusCheck();
                }
            }
        });
//        iot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                check.setImageResource(R.drawable.iotact);
//
//            }
//        });
//
//        gas.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                check.setImageResource(R.drawable.gas);
//
//            }
//        });
//
//        thermometer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                check.setImageResource(R.drawable.thermometer);
//
//            }
//        });


    }
    private void init(){
        listview = getView().findViewById(R.id.area01_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);
        iotlist = new ArrayList<>();
        iotlist.add("0");
        iotlist.add("1");
        adapter = new Adapter(getContext(), iotlist);
        listview.setAdapter(adapter);
        ListDecoration listdeco = new ListDecoration();
        listview.addItemDecoration(listdeco);
        //   ListCheck();


    }



    private void ListCheck() {//IOT장비 확인

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf("http://121.153.155.36:3000/api/addUser"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("volleytest", response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            iotlist = new ArrayList<>();
                            int size = jsonArray.length();
                            for(int i =0; i<size;i++){
                                JSONObject row = jsonArray.getJSONObject(i);
                                iotlist.add(row.getString("iot"));
                            }
                            adapter = new Adapter(getContext(), iotlist);
                            listview.setAdapter(adapter);
                            ListDecoration listdeco = new ListDecoration();
                            listview.addItemDecoration(listdeco);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("volleytest", "error : "+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id","왜");
                params.put("pw","치킨");
                params.put("name","사");
                params.put("mail","안");
                params.put("phone","줘");
                params.put("team","줘");

                Log.d("volleytest", "ok");
                return params;
            }
        };



        stringRequest.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(stringRequest);

    }


    private void StatusCheck() {

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf("http://121.153.155.36:3000/api/addUser"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("volleytest", response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            iotlist = new ArrayList<>();
                            int size = jsonArray.length();
                            for(int i =0; i<size;i++){
                                JSONObject row = jsonArray.getJSONObject(i);
                                iotlist.add(row.getString("iot"));
                            }
                            adapter = new Adapter(getContext(), iotlist);
                            listview.setAdapter(adapter);
                            ListDecoration listdeco = new ListDecoration();
                            listview.addItemDecoration(listdeco);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("volleytest", "error : "+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id","왜");
                params.put("pw","치킨");
                params.put("name","사");
                params.put("mail","안");
                params.put("phone","줘");
                params.put("team","줘");

                Log.d("volleytest", "ok");
                return params;
            }
        };



        stringRequest.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(stringRequest);

    }

}
