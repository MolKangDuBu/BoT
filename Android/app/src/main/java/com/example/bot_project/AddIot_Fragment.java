package com.example.bot_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AddIot_Fragment extends Fragment {


    public EditText et_addiot_deivce;
    public EditText et_addiot_area;
    public Button btn_addiot_add;
    public String device;
    public String area;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View layout =inflater.inflate(R.layout.addiot, container, false);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        et_addiot_deivce = getView().findViewById(R.id.et_addiot_device);
        et_addiot_area =getView().findViewById(R.id.et_addiot_area);
        btn_addiot_add = getView().findViewById(R.id.btn_addiot_add);


        btn_addiot_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                device = et_addiot_deivce.getText().toString();
                area = et_addiot_area.getText().toString();
                Addiot();
            }
        });
    }

    private void Addiot() {

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf("http://121.153.155.36:3000/api/addUser"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("volleytest", response.toString());

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
