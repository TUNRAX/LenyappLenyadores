package com.example.lenyapplenyadores;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView txtEmail, txtPassword;
    Button btnLogin;

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPassword = (TextView) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        final ArrayList<Usuario> listaUsuario = new ArrayList<Usuario>();

        final boolean[] loginOk = {false};
        final int[] id = {0};
        final String[] correo = {""};

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = txtEmail.getText().toString();
                final String pass = txtPassword.getText().toString();
                int rol = 0;
                //thread rol
                final int[] rolObtenidoLOL = {0};
                Thread thread1 = new Thread() {
                    @Override
                    public void run() {
                        try {
                            final String resultadoCheck = Check(user, pass);
                            JSONObject objetoJSON = new JSONObject();
                            JSONArray arr = new JSONArray(resultadoCheck);
                            JSONObject jObj = arr.getJSONObject(0);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject oneObject = arr.getJSONObject(i);
                                // Pulling items from the array
                                int rolObtenido = oneObject.getInt("id_rol");
                                rolObtenidoLOL[0] = rolObtenido;
                            }

                        } catch (JSONException e) {
                            Log.e("app", "exception", e);
                        }


                    }
                };
                thread1.start();

                //fin thread rol
                while (thread1.getState() != Thread.State.TERMINATED) {
                    rol = rolObtenidoLOL[0];
                }
                if (thread1.getState() != Thread.State.TERMINATED) {
                    rol = rolObtenidoLOL[0];
                }
                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Rellene los campos nesesarios", Toast.LENGTH_SHORT).show();
                } else if (rol == 2) {
                    final JSONObject userJson1 = new JSONObject();
                    try {
                        userJson1.put("user", user);
                        userJson1.put("pass", pass);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String jsonString1 = userJson1.toString();
                    String url1 = "http://84361097.ngrok.io/login.php";
                    try {
                        Back ejec = new Back(new Back.AsyncResponse() {
                            @Override
                            public void processFinish(String jsonResult) {
                                String correoInterno = "";
                                correo[0] = correoInterno;
                                int idUsuario = 0;
                                id[0] = idUsuario;
                                String contrasenya = "";
                                try {
                                    JSONObject jsonObjectUsuario = new JSONObject(jsonResult);
                                    JSONArray jsonArrayUsuario = jsonObjectUsuario.getJSONArray("usuario");
                                    for (int x = 0; x < jsonArrayUsuario.length(); x++) {
                                        listaUsuario.add(new Usuario(jsonArrayUsuario.getJSONObject(x)));
                                    }

                                    for (int i = 0; i < listaUsuario.size(); i++) {
                                        correoInterno = listaUsuario.get(i).getCorreo();
                                        contrasenya = listaUsuario.get(i).getContrasenya();
                                    }
                                    correo[0] = correoInterno;
                                    for (int i = 0; i < listaUsuario.size(); i++) {
                                        idUsuario = listaUsuario.get(i).getId();
                                    }
                                    id[0] = idUsuario;
                                    //comparamos los campos
                                    if (user.equals(correoInterno) && pass.equals(contrasenya)) {
                                        loginOk[0] = true;
                                        if (loginOk[0]) {
                                            try {
                                                try {

                                                    Toast.makeText(getApplicationContext(), "Cargando... por favor espere", Toast.LENGTH_LONG).show();
                                                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                                    editor.putInt("id", id[0]);
                                                    editor.putString("correo", correo[0]);
                                                    editor.apply();
                                                    Intent i = new Intent(getApplicationContext(), MenuLenyador.class);
                                                    startActivity(i);




                                                } catch (Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Error al Insertar Datos, intente nuevamente.", Toast.LENGTH_LONG).show();
                                                    Log.e("app", "exception", e);
                                                }

                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error inesperado.", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Datos incorrectos", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error inesperado, comuniquese con administrador.", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                        ejec.execute(url1, jsonString1);

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al Insertar Datos, intente nuevamente.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Acceso denegado, contactese con el administrador", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String Check(String user, String pass) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resultado = null;

        try {

            url = new URL("http://84361097.ngrok.io/checkearRol.php?correo=" + user
                    + "&contrasenya=" + pass);
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
        }
        return resultado.toString();
    }


    public String Login(String user, String pass) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resultado = null;

        try {

            url = new URL("http://84361097.ngrok.io/checkearRol.php?correo=" + user
                    + "&contrasenya=" + pass);
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
        }
        return resultado.toString();
    }
}

