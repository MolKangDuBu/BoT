package com.example.bot_project;

import android.app.Notification;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    public TextView Area01_working;
    public TextView Area01_update;
    public TextView Area01_errormessage;
    public ImageView check;
    public Switch onoff;
    private RecyclerView listview;
    private Adapter adapter;
    private ArrayList<String> iotlist;
    private Check Network;
    public Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View layout =inflater.inflate(R.layout.area01, container, false);
        return layout;
    }


    public void onStart() {

        super.onStart();
        Network = new Check();
        handler =null;
        Area01_working = getView().findViewById(R.id.tv_Area01_working);
        Area01_update = getView().findViewById(R.id.tv_Area01_updatetime);
        Area01_errormessage = getView().findViewById(R.id.tv_Area01_errormessage);
        check = getView().findViewById(R.id.imv_Area01_check);
        onoff = getView().findViewById(R.id.switch_Area01);
        init();



    }


    private void init() {

        listview = getView().findViewById(R.id.area01_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);
        handler = new Handler();
//       Network.FindArea(listview,"area01", getContext());

       Thread t =  new Thread(new Runnable() {
            @Override
            public void run() {

                    Network.FindArea(listview, "area01", getContext());


                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            iotlist = Network.getIotlist();

                            Adapter adapter = new Adapter(getContext(), iotlist);
                            listview.setAdapter(adapter);
                            ListDecoration listdeco = new ListDecoration();
                            listview.addItemDecoration(listdeco);

                            adapter.setOnClickListener(new Adapter.itemClick() {
                                @Override
                                public void onItemClickListener(View v, int position, String iotname) {
                                    if (iotname.equals("lamp")) {
                                        check.setImageResource(R.drawable.iotact);
                                    } else if (iotname.equals("gas")) {
                                        check.setImageResource(R.drawable.gas);

                                    } else if (iotname.equals("tmp")) {
                                        check.setImageResource(R.drawable.thermometer);
                                    }
                                }
                            });
                        }
                    });


            }
        });
       t.start();

    }




}
