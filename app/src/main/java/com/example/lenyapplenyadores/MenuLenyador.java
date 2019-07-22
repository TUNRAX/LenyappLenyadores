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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MenuLenyador extends AppCompatActivity {

    private pl.droidsonroids.gif.GifImageView gifImageView;
    private TextView txtBuscar, txtEspere;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_lenyador);


        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        final String correo = prefs.getString("correo", " ");//"No name defined" is the default value.
        final int id = prefs.getInt("id", 0); //0 is the default value.
        final ArrayList<historialEnvios> listaHistorialEnvios = new ArrayList<historialEnvios>();
        final ArrayList<Proveedor> listaProveedor = new ArrayList<Proveedor>();
        gifImageView = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.gifImageView);
        txtBuscar = (TextView) findViewById(R.id.txtBuscar);
        txtBuscar.setVisibility(View.VISIBLE);
        txtEspere = (TextView) findViewById(R.id.txtEspere);
        txtEspere.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        final int[] finalizar = {0};
        final int[] idHistorial = {0};
        final int[] idProveedor = {0};
        final int[] verificado = {0};
        final boolean[] isPaused = {true};
        final int[] newVerificado = {0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                //final String pedidoObtenido = obtenerPedido(id);

                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {

                            RequestQueue queue = Volley.newRequestQueue(MenuLenyador.this);
                            String url = "http://e9eec324.ngrok.io/encontrarPedidos.php?idProv=" + id;


                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String pedidosEncontrados = response;
                                            try {
                                                JSONObject jsonObjectHistorial = new JSONObject(pedidosEncontrados);
                                                JSONObject idHistorialJson = jsonObjectHistorial.getJSONObject("validado");
                                                JSONObject idProveedorJson = jsonObjectHistorial.getJSONObject("proveedor");
                                                listaHistorialEnvios.add(new historialEnvios(idHistorialJson));
                                                listaProveedor.add(new Proveedor(idProveedorJson));


                                            } catch (Exception e) {
                                                Log.e("app", "exception", e);
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MenuLenyador.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });

                            queue.add(stringRequest);

                            for (int i = 0; i < listaHistorialEnvios.size(); i++) {
                                verificado[0] = listaHistorialEnvios.get(i).getValidado();
                                idHistorial[0] = listaHistorialEnvios.get(i).getId();
                            }

                            for (int i = 0; i < listaProveedor.size(); i++) {
                                idProveedor[0] = listaProveedor.get(i).getId();
                            }
                            if (verificado[0] == 3) {
                                isPaused[0] = true;
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                newVerificado[0] = 4;

                                                RequestQueue queue = Volley.newRequestQueue(MenuLenyador.this);
                                                String url = "http://e9eec324.ngrok.io/actualizarValidado.php?validado=" + newVerificado[0] + "&idHistorial=" + idHistorial[0];

                                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                // Display the first 500 characters of the response string.
                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(MenuLenyador.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                queue.add(stringRequest);

                                                //cambiarValidado(idHistorial[0], newVerificado[0]);
                                                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                                                i.putExtra("idHistorial", idHistorial[0]);
                                                i.putExtra("idproveedor", idProveedor[0]);
                                                startActivity(i);
                                                finalizar[0] = 1;
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                newVerificado[0] = 2;
                                                RequestQueue queue1 = Volley.newRequestQueue(MenuLenyador.this);
                                                String url1 = "http://e9eec324.ngrok.io/actualizarValidado.php?validado=" + newVerificado[0] + "&idHistorial=" + idHistorial[0];

                                                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {

                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.d("error volley", String.valueOf(error));
                                                    }
                                                });

                                                queue1.add(stringRequest1);
                                                isPaused[0] = false;
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(MenuLenyador.this);
                                builder.setMessage("Esta disponible para un reparto").setPositiveButton("Si", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();

                            } else {
                                handle.postDelayed(this, 5000);
                            }


                        } catch (Exception e) {
                            Log.e("app", "exception", e);
                        }

                    }

                };
                if (isPaused[0]) {
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


    public String obtenerPedido(int id) {

        String url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resultado = null;

        try {

            url = ("http://e9eec324.ngrok.io/encontrarPedidos.php?idProv=" + id);
            url = url.replaceAll(" ", "%20");
            URL sourceUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) sourceUrl.openConnection();
            respuesta = connection.getResponseCode();

            resultado = new StringBuilder();

            if (respuesta == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((linea = reader.readLine()) != null) {
                    resultado.append(linea);
                }
            }

        } catch (Exception e) {
            Log.e("app", "exception", e);
        }
        return resultado.toString();
    }


    public void cambiarValidado(int idHistorial, int newVerificado) {

        String url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resultado = null;

        try {

            url = ("http://e9eec324.ngrok.io/actualizarValidado.php?validado=" + newVerificado + "&idHistorial=" + idHistorial);
            url = url.replaceAll(" ", "%20");
            URL sourceUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) sourceUrl.openConnection();
            respuesta = connection.getResponseCode();

            resultado = new StringBuilder();

            if (respuesta == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((linea = reader.readLine()) != null) {
                    resultado.append(linea);
                }
            }

        } catch (Exception e) {
            Log.e("app", "exception", e);
        }
    }
}
