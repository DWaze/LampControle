package com.dredhat.lhadj.lampcontrole;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class MainActivity extends AppCompatActivity {
    String pickedColor;
    int color;
    ActionBar bar;
    int pos;
    ConstraintLayout mConstraintLayout;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplication();
        Button btnPicker = findViewById(R.id.PickColor);
        Button btnOff = findViewById(R.id.Off);
        Button btnOn = findViewById(R.id.TurnOn);
        final TextView mTemperature = findViewById(R.id.Temperature);
        mConstraintLayout = findViewById(R.id.backgroung);
        pos = 0;

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url ="http://192.168.8.100:8080/relay/On";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Toast.makeText(MainActivity.this, "Relay On!",Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(stringRequest);
            }
        });

        btnPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(MainActivity.this)
                        .setTitle("Choose color")
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                color = selectedColor;
                                pickedColor = Integer.toHexString(selectedColor);
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                String url ="http://192.168.8.100:8080/rgbcolor/ChangeColor/?color="+pickedColor+"&pos="+pos;
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                // Display the first 500 characters of the response string.
                                                mConstraintLayout.setBackgroundColor(color);
                                                pos = 1 ;
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
                                    }
                                });
                                queue.add(stringRequest);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .build()
                        .show();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url ="http://192.168.8.100:8080/rgbcolor/ChangeColor/?color=000000&pos="+pos;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                mConstraintLayout.setBackgroundColor(color);
                                pos = 1 ;
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
                    }
                });
                String url2 ="http://192.168.8.100:8080/relay/Off";
                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Toast.makeText(MainActivity.this, "Relay Off!",Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(stringRequest);
                queue.add(stringRequest2);
            }
        });

        new Thread(new Runnable() {
            public void run() {
                String temp ;
                // a potentially  time consuming task
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url ="http://192.168.8.100:8080/temp/getTemprature/";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                // Display the first 500 characters of the response string.
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        mTemperature.setText(response);
                                    }
                                });
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
                    }
                });
                while(true){
                queue.add(stringRequest);
                }

            }
        }).start();

    }
}
