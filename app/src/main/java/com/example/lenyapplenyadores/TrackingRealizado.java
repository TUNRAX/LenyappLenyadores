package com.example.lenyapplenyadores;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrackingRealizado extends AppCompatActivity {
    private TextView lblPago;
    private Button btnConfirmar;

    int idHistorial = 0;
    int idProveedor = 0;
    final Handler handler = new Handler();

    final ArrayList<historialEnvios> listaHistorialEnvios = new ArrayList<historialEnvios>();
    final ArrayList<Proveedor> listaProveedor = new ArrayList<Proveedor>();
    final int[] finalizar = {0};
    final int[] verificado = {0};
    int tipoDePago = 0;
    int pago = 0;
    final boolean[] isPaused = {true};
    final int[] newVerificado = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_realizado);
        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        String correo = prefs.getString("correo", " ");//"No name defined" is the default value.
        final int id = prefs.getInt("id", 0); //0 is the default value.
        lblPago = (TextView) findViewById(R.id.lblPago);
        btnConfirmar = (Button) findViewById(R.id.btnConfirmar);
        lblPago.setVisibility(View.GONE);
        btnConfirmar.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        idHistorial = bundle.getInt("idHistorial");
        idProveedor = bundle.getInt("idproveedor");


    new Thread(new Runnable() {
        @Override
        public void run() {
            //final String pedidoObtenido = obtenerPedido(id);

            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try{
                    RequestQueue queue = Volley.newRequestQueue(TrackingRealizado.this);
                    String url ="http://865e33a1.sa.ngrok.io/seleccionarHistorial.php?idHistorial="+ idHistorial;


                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    String historialEncontrados = response;
                                    try {
                                        JSONObject jsonObjectHistorial = new JSONObject(historialEncontrados);
                                        JSONArray jsonArrayHistorial = jsonObjectHistorial.getJSONArray("historial");
                                        for (int x = 0; x < jsonArrayHistorial.length(); x++) {
                                            listaHistorialEnvios.add(new historialEnvios(jsonArrayHistorial.getJSONObject(x)));
                                        }





                                    } catch (Exception e) {
                                        Log.e("app", "exception", e);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(TrackingRealizado.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    queue.add(stringRequest);


                    for (int i = 0; i < listaHistorialEnvios.size(); i++) {
                        verificado[0] = listaHistorialEnvios.get(i).getValidado();
                        pago = listaHistorialEnvios.get(i).getPagado();
                        tipoDePago = listaHistorialEnvios.get(i).getTipoDeCompraId();
                    }


                        if ((verificado[0] == 1 && pago == 2 && tipoDePago == 2) || (verificado[0] == 1 && pago == 3 && tipoDePago == 2)) {
                            isPaused[0] = true;
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            int pago = 1;
                                            RequestQueue queue = Volley.newRequestQueue(TrackingRealizado.this);
                                            String url ="http://865e33a1.sa.ngrok.io/actualizarPago.php?idHistorial="+ idHistorial + "&pago="+ pago;

                                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {


                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toast.makeText(TrackingRealizado.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            queue.add(stringRequest);
                                            Toast.makeText(TrackingRealizado.this, "Pago Realizado", Toast.LENGTH_SHORT).show();
                                            //Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                                            //i.putExtra("idHistorial", idHistorial[0]);
                                            //i.putExtra("idproveedor", idProveedor[0]);
                                            //startActivity(i);
                                            finalizar[0] = 1;
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            int pagoo = 3;
                                            RequestQueue queue1 = Volley.newRequestQueue(TrackingRealizado.this);
                                            String url1 ="http://865e33a1.sa.ngrok.io/actualizarPago.php?idHistorial="+ idHistorial + "&pago="+ pagoo;

                                            StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {


                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toast.makeText(TrackingRealizado.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            queue1.add(stringRequest1);
                                            Toast.makeText(TrackingRealizado.this, "Insista", Toast.LENGTH_SHORT).show();

                                            isPaused[0] = false;
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(TrackingRealizado.this);
                            builder.setMessage("¿Recibio el pago?").setPositiveButton("Si", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();

                        } else if(verificado[0] == 1 && tipoDePago == 1) {
                            if(pago == 1){
                                Toast.makeText(TrackingRealizado.this, "Pago realizado por tarjeta", Toast.LENGTH_SHORT).show();
                                finalizar[0] = 1;
                            }else{
                                handle.postDelayed(this, 5000);
                            }

                        }else{
                            handle.postDelayed(this, 5000);
                        }


                    } catch (Exception e) {
                        Log.e("app", "exception", e);
                    }

                }

            };
            if(isPaused[0]) {
                handler.removeCallbacks(runnable);
            } else {
                handler.postDelayed(runnable, 5000);
            }
            if (finalizar[0] == 1) {
                handler.removeCallbacks(runnable);
            } else {
                handler.postDelayed(runnable, 5000);
            }
        }
    }).start();
}
    @SuppressLint("HandlerLeak")
    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
