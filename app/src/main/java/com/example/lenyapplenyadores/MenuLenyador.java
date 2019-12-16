package com.example.lenyapplenyadores;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class MenuLenyador extends AppCompatActivity {

    private TableLayout tblTablaPedidos;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_lenyador);


        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        final String correo = prefs.getString("correo", " ");//"No name defined" is the default value.
        final int id = prefs.getInt("id", 0); //0 is the default value.
        final DatePickerDialog[] picker = new DatePickerDialog[1];
        final ArrayList<historialEnvios> listaHistorialEnvios = new ArrayList<historialEnvios>();
        final ArrayList<historialEnvios> listaHistorial = new ArrayList<historialEnvios>();

        tblTablaPedidos = (TableLayout) findViewById(R.id.tblTablaPedidos);
        // btnReporte = (ImageButton) findViewById(R.id.btnReporte);

        final TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        final TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        final TableRow.LayoutParams params3 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        final TableRow row = new TableRow(MenuLenyador.this);
        params1.setMargins(12, 20, 0, 0);
        params2.setMargins(12, 30, 0, 0);
        params3.setMargins(5, 30, 5, 0);

        Thread thread1 = new Thread() {
            @Override
            public void run() {
                final String pedidosObtenidos = obtenerPedidos(id);
                try {
                    JSONObject jsonObjectHistorial = new JSONObject(pedidosObtenidos);
                    JSONArray jsonArrayHistorial = jsonObjectHistorial.getJSONArray("historial");

                    for (int x = 0; x < jsonArrayHistorial.length(); x++) {
                        listaHistorial.add(new historialEnvios(jsonArrayHistorial.getJSONObject(x)));
                    }

                } catch (Exception e) {
                    Log.e("app", "exception", e);
                }

                try {
                    for (int i = 0; i < listaHistorial.size(); i++) {
                        //final String[] nombreLenador = {""};
                        //final String[] apellidoLenador = {""};
                        //final int[] precioUnitario = {0};
                        //final String[] tipoDeLeña = {""};

                        int idDetalle = listaHistorial.get(i).getIdDetalleProducto();
                        int idCliente = listaHistorial.get(i).getIdCliente();
                        String clienteObtenido = obtenerCliente(idCliente);
                        String detalleObtenido = obtenerDetalle(idDetalle);
                        String tipoProductoObtenido = obtenerProducto(idDetalle);

                        JSONObject jsonObjectCliente = new JSONObject(clienteObtenido);
                        JSONObject jsonCliente = jsonObjectCliente.getJSONObject("cliente");
                        final String nombreCliente = jsonCliente.getString("nombre");
                        final String apellidoCliente = jsonCliente.getString("apellido");
                        final String direccionCliente = jsonCliente.getString ("direccion");

                        JSONObject jsonObjectDatos = new JSONObject(detalleObtenido);
                        JSONObject datosJson = jsonObjectDatos.getJSONObject("detalle");
                        int precioUnitario = datosJson.getInt("precio_unitario");
                        final int idProveedor = datosJson.getInt("id_proveedor");
                        final String medida = datosJson.getString("medida");

                        JSONObject jsonObjectTipo = new JSONObject(tipoProductoObtenido);
                        JSONObject tipoJson = jsonObjectTipo.getJSONObject("producto");
                        final String tipoDeLeña = tipoJson.getString("nombre");

                        final int idHistorial = listaHistorial.get(i).getId();
                        final int cantidad = listaHistorial.get(i).getCantidad();
                        final int precioOficial = precioUnitario * cantidad;
                        final String fechaPedido = listaHistorial.get(i).getFecha();
                        final String fechaEnvio = listaHistorial.get(i).getFechaEnvio();
                        final int estado = listaHistorial.get(i).getValidado();
                        final String[] fechaElegida = {""};
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                TableRow row = new TableRow(MenuLenyador.this);
                                TextView lblNombreCliente = new TextView(MenuLenyador.this);
                                TextView lblTipoDeLeña = new TextView(MenuLenyador.this);
                                TextView lblCantidad = new TextView(MenuLenyador.this);
                                TextView lblPrecio = new TextView(MenuLenyador.this);
                                TextView lblFechaPedido = new TextView(MenuLenyador.this);
                                TextView lblFechaEntrega = new TextView(MenuLenyador.this);
                                TextView lblTextoEstado = new TextView(MenuLenyador.this);
                                TextView lblDireccion = new TextView(MenuLenyador.this);
                                Typeface custom_font = ResourcesCompat.getFont(MenuLenyador.this, R.font.roboto);
                                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                                row.setLayoutParams(lp);
                                final Button btnCancelar = new Button(MenuLenyador.this);
                                final Button btnEnviar = new Button(MenuLenyador.this);
                                final Button btnColocarFecha = new Button(MenuLenyador.this);
                                final Button btnPagar = new Button(MenuLenyador.this);
                                btnCancelar.setBackgroundColor(Color.RED);
                                btnCancelar.setText("Cancelar");
                                btnEnviar.setBackgroundColor(Color.BLUE);
                                btnEnviar.setText("Hacer reparto");
                                btnColocarFecha.setBackgroundColor(Color.CYAN);
                                btnColocarFecha.setText("Indique una fecha");
                                btnPagar.setBackgroundColor(Color.BLUE);
                                btnPagar.setText("Pagar");

                                btnCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        RequestQueue queue = Volley.newRequestQueue(MenuLenyador.this);
                                                        String url ="http://fd668ba1.sa.ngrok.io/actualizarValidado.php?idHistorial=" + idHistorial + "&validado="+ 2;


                                                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                                                new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        MenuLenyador.this.recreate();
                                                                    }
                                                                }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(MenuLenyador.this, "Error Volley", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        queue.add(stringRequest);
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:

                                                        break;
                                                }
                                            }
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuLenyador.this);
                                        builder.setMessage("¿Esta seguro que desea cancelar el pedido?").setPositiveButton("Si", dialogClickListener)
                                                .setNegativeButton("No", dialogClickListener).show();

                                    }

                                });

                                btnEnviar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        RequestQueue queue = Volley.newRequestQueue(MenuLenyador.this);
                                                        String url ="http://fd668ba1.sa.ngrok.io/actualizarValidado.php?idHistorial=" + idHistorial + "&validado="+ 4;


                                                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                                                new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        Intent i = new Intent(MenuLenyador.this, MapsActivity.class);
                                                                        i.putExtra("idHistorial", idHistorial);
                                                                        i.putExtra("idproveedor", idProveedor);
                                                                        i.putExtra("direccion", direccionCliente);
                                                                        i.putExtra("precioOficial", precioOficial);
                                                                        i.putExtra("medida", medida);
                                                                        i.putExtra("cantidad", cantidad);
                                                                        i.putExtra("tipoProducto", tipoDeLeña);
                                                                        startActivity(i);
                                                                    }
                                                                }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(MenuLenyador.this, "Error Volley", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        queue.add(stringRequest);
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:

                                                        break;
                                                }
                                            }
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuLenyador.this);
                                        builder.setMessage("¿Esta seguro que desea empezar a enviar el pedido?, esta accion no es reversible").setPositiveButton("Si", dialogClickListener)
                                                .setNegativeButton("No", dialogClickListener).show();

                                    }
                                });

                                btnColocarFecha.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Calendar cldr = Calendar.getInstance();
                                        int day = cldr.get(Calendar.DAY_OF_MONTH);
                                        int month = cldr.get(Calendar.MONTH);
                                        int year = cldr.get(Calendar.YEAR);


                                        picker[0] = new DatePickerDialog(MenuLenyador.this,
                                                new DatePickerDialog.OnDateSetListener() {
                                                    @Override
                                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                        fechaElegida[0] =year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                                                        // Instantiate the RequestQueue.
                                                        RequestQueue queue = Volley.newRequestQueue(MenuLenyador.this);
                                                        String url ="http://fd668ba1.sa.ngrok.io/actualizarFechaEnvio.php?idHistorial=" + idHistorial + "&fechaEnvio="+ fechaElegida[0];


                                                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                                                new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        MenuLenyador.this.recreate();
                                                                    }
                                                                }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(MenuLenyador.this, "Error Volley", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        queue.add(stringRequest);
                                                    }
                                                }, year, month, day);
                                        picker[0].getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                        picker[0].show();

                                    }
                                });

                                btnPagar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(MenuLenyador.this, TrackingRealizado.class);
                                        i.putExtra("idHistorial", idHistorial);
                                        i.putExtra("idproveedor", idProveedor);
                                        i.putExtra("direccion", direccionCliente);
                                        i.putExtra("precioOficial", precioOficial);
                                        i.putExtra("medida", medida);
                                        i.putExtra("cantidad", cantidad);
                                        i.putExtra("tipoProducto", tipoDeLeña);
                                        startActivity(i);
                                    }
                                });

                                lblNombreCliente.setText(nombreCliente + " " + apellidoCliente);
                                lblNombreCliente.setLayoutParams(params2);
                                lblNombreCliente.setTextSize(16);
                                lblNombreCliente.setTextColor(Color.parseColor("#212121"));
                                lblNombreCliente.setTypeface(custom_font);

                                lblTipoDeLeña.setText(tipoDeLeña);
                                lblTipoDeLeña.setLayoutParams(params3);
                                lblTipoDeLeña.setTextSize(16);
                                lblTipoDeLeña.setTextColor(Color.parseColor("#212121"));
                                lblTipoDeLeña.setTypeface(custom_font);

                                lblCantidad.setText(String.valueOf(cantidad));
                                lblCantidad.setLayoutParams(params3);
                                lblCantidad.setTextSize(16);
                                lblCantidad.setTextColor(Color.parseColor("#212121"));
                                lblCantidad.setTypeface(custom_font);

                                lblPrecio.setText(String.valueOf(precioOficial));
                                lblPrecio.setLayoutParams(params3);
                                lblPrecio.setTextSize(16);
                                lblPrecio.setTextColor(Color.parseColor("#212121"));
                                lblPrecio.setTypeface(custom_font);

                                lblFechaPedido.setText(String.valueOf(fechaPedido));
                                lblFechaPedido.setLayoutParams(params3);
                                lblFechaPedido.setTextSize(16);
                                lblFechaPedido.setTextColor(Color.parseColor("#212121"));
                                lblFechaPedido.setTypeface(custom_font);

                                if (fechaEnvio.equals("1753-01-01")) {
                                    lblFechaEntrega.setText("Fecha de entrega aun no estipulada");
                                } else {
                                    lblFechaEntrega.setText(String.valueOf(fechaEnvio));
                                }
                                lblFechaPedido.setLayoutParams(params3);
                                lblFechaPedido.setTextSize(16);
                                lblFechaPedido.setTextColor(Color.parseColor("#212121"));
                                lblFechaPedido.setTypeface(custom_font);

                                if (estado == 1) {
                                    lblTextoEstado.setText("Pedido realizado con exito");
                                } else {
                                    lblTextoEstado.setText("Pedido cancelado");
                                }

                                lblTextoEstado.setLayoutParams(params3);
                                lblTextoEstado.setTextSize(16);
                                lblTextoEstado.setTextColor(Color.parseColor("#212121"));
                                lblTextoEstado.setTypeface(custom_font);

                                lblDireccion.setText(direccionCliente);
                                lblDireccion.setLayoutParams(params3);
                                lblDireccion.setTextSize(16);
                                lblDireccion.setTextColor(Color.parseColor("#212121"));
                                lblDireccion.setTypeface(custom_font);

                                row.addView(lblNombreCliente);
                                row.addView(lblTipoDeLeña);
                                row.addView(lblCantidad);
                                row.addView(lblPrecio);
                                row.addView(lblFechaPedido);
                                row.addView(lblFechaEntrega);
                                row.addView(lblDireccion);
                                if (estado == 3) {
                                    row.addView(btnCancelar);
                                    row.addView(btnColocarFecha);
                                } else if (estado == 6) {
                                    row.addView(btnEnviar);
                                } else if (estado == 4) {
                                    row.addView(btnEnviar);
                                } else if (estado == 7) {
                                    row.addView(btnPagar);
                                }

                                tblTablaPedidos.addView(row, finalI);
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.e("app", "exception", e);
                }

            }
        };
        thread1.start();
    }

    private View.OnClickListener ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int selected_item = (Integer) v.getTag();
        }
    };

    public String obtenerPedidos(int id) {
        String url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resultado = null;

        try {

            url = ("http://fd668ba1.sa.ngrok.io/seleccionarHistorialPorIdUsuario.php?idUsuario=" + id);
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

    public String obtenerCliente(int idCliente) {
        String url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resultado = null;

        try {

            url = ("http://fd668ba1.sa.ngrok.io/obtenerClientePorId.php?idCliente=" + idCliente);
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

    public String obtenerDetalle(int idDetalle) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resultado = null;

        try {

            url = new URL("http://fd668ba1.sa.ngrok.io/obtenerDetallePorId.php?idDetalle=" + idDetalle);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

    public String obtenerProducto(int idDetalle) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resultado = null;

        try {

            url = new URL("http://fd668ba1.sa.ngrok.io/obtenerTipoProductoPorIdDetalle.php?idDetalle=" + idDetalle);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
}
