package com.example.lenyapplenyadores;
/**
 * Esta clase solo tiene los setters and getters de usuario
 */

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Usuario implements Parcelable {
    int id;
    String correo;
    String contrasenya;
    String idRol;
    int activo;

    public Usuario(int id, String correo, String contrasenya, String idRol, int activo, String rememberToken) {
        this.id = id;
        this.correo = correo;
        this.contrasenya = contrasenya;
        this.idRol = idRol;
        this.activo = activo;
    }

    public Usuario() {
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public String getIdRol() {
        return idRol;
    }

    public void setIdRol(String idRol) {
        this.idRol = idRol;
    }

    public static Creator<Usuario> getCREATOR() {
        return CREATOR;
    }

    public Usuario(JSONObject objetoJSON)throws JSONException {
        id = objetoJSON.getInt("id");
        correo =objetoJSON.getString("email");
        contrasenya = objetoJSON.getString("contrasenya");
        idRol = objetoJSON.getString("id_rol");
        activo = objetoJSON.getInt("activo");
    }

    protected Usuario(Parcel in) {
        id = in.readInt();
        correo = in.readString();
        contrasenya = in.readString();
        idRol = in.readString();
        activo = in.readInt();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(correo);
        dest.writeString(contrasenya);
        dest.writeString(idRol);
        dest.writeInt(activo);
    }
}
