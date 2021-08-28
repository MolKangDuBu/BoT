
package com.example.bot_project;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import androidx.appcompat.app.AppCompatActivity;

        import com.android.volley.AuthFailureError;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;

        import java.util.HashMap;
        import java.util.Map;

public class MainActivity extends AppCompatActivity {//Login

    public EditText et_login_ID;
    public EditText et_login_PW;
    public Button btn_login_login;
    public TextView tv_login_signup;
    public TextView tv_login_forgotpw;
    public String ID;
    public String PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Intent intent1 = new Intent(this, loading.class);
        startActivity(intent1);

        et_login_ID = findViewById(R.id.et_login_ID);
        et_login_PW = findViewById(R.id.et_login_pw);
        btn_login_login = findViewById(R.id.btn_login_login);
        Intent intent = new Intent(this, Main_Activity.class);
        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID= et_login_ID.getText().toString();
                PW = et_login_PW.getText().toString();
                startActivity(intent);
                //Login();
            }
        });


    }
    private void Login() {

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
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(stringRequest);

    }



}
