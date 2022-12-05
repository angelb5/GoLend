package pe.du.pucp.golend.Entity;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class Reservas implements Serializable {

    @Exclude
    private String key;
    private ClienteUser clienteUser;
    private TIUser tiUser;
    private Device device;
    private String motivoReserva;
    private String curso;
    private Integer tiempoReserva;
    private ArrayList<String> Programas;
    private String dni;
    private String otros;
    private transient GeoPoint lugarRecojo;
    private String nombreLugarRecojo;
    private String motivoRechazo;
    private String estado;
    private transient Timestamp horaReserva;
    private transient Timestamp horaFinReserva;
    private transient Timestamp horaRespuesta;

    public Reservas() {
    }

    public Reservas(ClienteUser clienteUser, TIUser tiUser, Device device, String motivoReserva, String curso, Integer tiempoReserva, ArrayList<String> programas, String dni, String otros, GeoPoint lugarRecojo,String nombreLugarRecojo, String motivoRechazo, String estado, Timestamp horaReserva, Timestamp horaFinReserva, Timestamp horaRespuesta) {
        this.clienteUser = clienteUser;
        this.tiUser = tiUser;
        this.device = device;
        this.motivoReserva = motivoReserva;
        this.curso = curso;
        this.tiempoReserva = tiempoReserva;
        Programas = programas;
        this.dni = dni;
        this.otros = otros;
        this.lugarRecojo = lugarRecojo;
        this.nombreLugarRecojo = nombreLugarRecojo;
        this.motivoRechazo = motivoRechazo;
        this.estado = estado;
        this.horaReserva = horaReserva;
        this.horaFinReserva = horaFinReserva;
        this.horaRespuesta = horaRespuesta;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public ClienteUser getClienteUser() {
        return clienteUser;
    }

    public void setClienteUser(ClienteUser clienteUser) {
        this.clienteUser = clienteUser;
    }

    public TIUser getTiUser() {
        return tiUser;
    }

    public void setTiUser(TIUser tiUser) {
        this.tiUser = tiUser;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getMotivoReserva() {
        return motivoReserva;
    }

    public void setMotivoReserva(String motivoReserva) {
        this.motivoReserva = motivoReserva;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Integer getTiempoReserva() {
        return tiempoReserva;
    }

    public void setTiempoReserva(Integer tiempoReserva) {
        this.tiempoReserva = tiempoReserva;
    }

    public ArrayList<String> getProgramas() {
        return Programas;
    }

    public void setProgramas(ArrayList<String> programas) {
        Programas = programas;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getOtros() {
        return otros;
    }

    public void setOtros(String otros) {
        this.otros = otros;
    }

    public GeoPoint getLugarRecojo() {
        return lugarRecojo;
    }

    public void setLugarRecojo(GeoPoint lugarRecojo) {
        this.lugarRecojo = lugarRecojo;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getHoraReserva() {
        return horaReserva;
    }

    public void setHoraReserva(Timestamp horaReservas) {
        this.horaReserva = horaReservas;
    }

    public Timestamp getHoraFinReserva() {
        return horaFinReserva;
    }

    public void setHoraFinReserva(Timestamp horaFinReserva) {
        this.horaFinReserva = horaFinReserva;
    }

    public Timestamp getHoraRespuesta() {
        return horaRespuesta;
    }

    public void setHoraRespuesta(Timestamp horaRespuesta) {
        this.horaRespuesta = horaRespuesta;
    }

    public String getNombreLugarRecojo() {
        return nombreLugarRecojo;
    }

    public void setNombreLugarRecojo(String nombreLugarRecojo) {
        this.nombreLugarRecojo = nombreLugarRecojo;
    }

    public static class ClienteUser implements Serializable {

        private String nombre;
        private String uid;
        private String avatarUrl;
        private String rol;

        public ClienteUser() {
        }

        public ClienteUser(String nombre, String uid, String avatarUrl, String rol) {
            this.nombre = nombre;
            this.uid = uid;
            this.avatarUrl = avatarUrl;
            this.rol = rol;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }
    }

    public static class TIUser implements Serializable {

        private String nombre;
        private String uid;
        private String avatarUrl;

        public TIUser() {
        }

        public TIUser(String nombre, String uid, String avatarUrl) {
            this.nombre = nombre;
            this.uid = uid;
            this.avatarUrl = avatarUrl;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }

    public static class Device implements Serializable {

        private String modelo;
        private String marca;
        private String fotoPrincipal;
        private String categoria;
        private String uid;

        public Device() {
        }

        public Device(String modelo, String marca, String fotoPrincipal, String categoria, String uid) {
            this.modelo = modelo;
            this.marca = marca;
            this.fotoPrincipal = fotoPrincipal;
            this.categoria = categoria;
            this.uid = uid;
        }

        public String getModelo() {
            return modelo;
        }

        public void setModelo(String modelo) {
            this.modelo = modelo;
        }

        public String getMarca() {
            return marca;
        }

        public void setMarca(String marca) {
            this.marca = marca;
        }

        public String getFotoPrincipal() {
            return fotoPrincipal;
        }

        public void setFotoPrincipal(String fotoPrincipal) {
            this.fotoPrincipal = fotoPrincipal;
        }

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }

}
