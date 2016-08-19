package com.example.jungmo.opentsdbtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity  implements Button.OnClickListener {

    Button btn_send;
    TextView tv_status;
    EditText et_value;
    JSONObject obj, obj2;
    String serverURL = "htpp://localhost:14242"; //Server address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_send = (Button) findViewById(R.id.send);
        et_value = (EditText) findViewById(R.id.value);
        tv_status = (TextView) findViewById(R.id.status);

        btn_send.setOnClickListener(this);



    }

    public String sendJSON(String jsonMSG, String serverURL){


        OutputStream os = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        HttpURLConnection conn = null;
        String response = "";
        URL url = null;
        try {
            url = new URL(serverURL);
            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5*1000);
            conn.setReadTimeout(5*1000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Cache-control", "no-cache");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            os = conn.getOutputStream();
            os.write(jsonMSG.getBytes());
            os.flush();
            int responseCode = conn.getResponseCode();
            Log.v("dd", String.valueOf(responseCode));

            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1){
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();
                response = new String(byteData);

                Log.v("dd", "DATA response = " + response);

            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public void onClick(View view) {
        if(view == btn_send) {

            obj = new JSONObject();
            obj2 = new JSONObject();
            try {
                obj.put("metric", "android.test");
                obj.put("timestamp", System.currentTimeMillis() / 1000L);
                obj.put("value", et_value.getText());
                obj2.put("host", "test");
                obj2.put("cpu", 0);
                obj.put("tags", obj2);
                Log.v("dd", obj.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast toast = Toast.makeText(getApplicationContext(), "SEND", Toast.LENGTH_SHORT);
            toast.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendJSON(obj.toString(),serverURL);

                }
            }).start();
        }
    }

}

