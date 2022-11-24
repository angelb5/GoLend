package pe.du.pucp.golend.Entity;

import com.google.firebase.firestore.Exclude;

import java.util.List;
import java.util.Locale;

public class Device {
    @Exclude
    private String key;
    private String modelo;
    private String marca;
    private String categoria;
    private String descripcion;
    private String accesorios;
    private List<String> fotosUrl;
    private int stock;
    private List<String> searchKeywords;
    private String searchCategoria;
    private String searchMarca; //estará en lowercase

    public Device() {
    }

    public Device(String modelo, String marca, String categoria, String descripcion, String accesorios, List<String> fotosUrl, int stock, List<String> searchKeywords, String searchCategoria) {
        this.modelo = modelo;
        this.marca = marca;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.accesorios = accesorios;
        this.fotosUrl = fotosUrl;
        this.stock = stock;
        this.searchKeywords = searchKeywords;
        this.searchCategoria = searchCategoria;
        searchMarca = marca.toLowerCase(Locale.ROOT);
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAccesorios() {
        return accesorios;
    }

    public void setAccesorios(String accesorios) {
        this.accesorios = accesorios;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public List<String> getFotosUrl() {
        return fotosUrl;
    }

    public void setFotosUrl(List<String> fotosUrl) {
        this.fotosUrl = fotosUrl;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(List<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public String getSearchCategoria() {
        return searchCategoria;
    }

    public void setSearchCategoria(String searchCategoria) {
        this.searchCategoria = searchCategoria;
    }

    public String getSearchMarca() {
        return searchMarca;
    }

    public void setSearchMarca(String searchMarca) {
        this.searchMarca = searchMarca;
    }
}
