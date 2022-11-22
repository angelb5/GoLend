package pe.du.pucp.golend.Entity;

import com.google.firebase.firestore.Exclude;

public class User {
    @Exclude
    private String uid;
    private String nombre;
    private String correo;
    private String codigo;
    private String rol;
    private String permisos;
    private String avatarUrl;

    public User() {
    }

    public User(String nombre, String correo, String codigo, String rol, String avatarUrl, String permisos) {
        this.nombre = nombre;
        this.correo = correo;
        this.codigo = codigo;
        this.rol = rol;
        this.permisos = permisos;
        this.avatarUrl = avatarUrl;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String persmisos) {
        this.permisos = persmisos;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
