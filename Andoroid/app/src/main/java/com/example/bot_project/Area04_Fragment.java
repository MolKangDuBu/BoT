package com.example.bot_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Area04_Fragment extends Fragment {

    public ImageButton iot;
    public TextView Area04_time;
    public TextView Area04_errormessage;
    public ImageView check;
    public Switch onoff;
    private RecyclerView listview;
    private Adapter adapter;
    private ArrayList<String> iotlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View layout =inflater.inflate(R.layout.area04, container, false);
        return layout;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        iot = getView().findViewById(R.id.imb_Area04_iot);
        Area04_time = getView().findViewById(R.id.tv_Area04_time);
        Area04_errormessage = getView().findViewById(R.id.tv_Area04_errormessage);
        check = getView().findViewById(R.id.imv_Area04);
        onoff = getView().findViewById(R.id.switch_Area04);


//        iot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                check.setImageResource(R.drawable.iotact);
//            }
//        });


    }

    private void init(){
        listview = getView().findViewById(R.id.area01_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);
        ListCheck();
    }

    private View.OnClickListener onClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String str = (String) v.getTag();
            StatusCheck();
        }
    };




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
