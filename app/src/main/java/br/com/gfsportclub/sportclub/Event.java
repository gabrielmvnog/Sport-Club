package br.com.gfsportclub.sportclub;

/**
 * Created by gabriel on 01/04/18.
 */

public class Event {

    String titulo, imagem, descr, key, data, local;

    public Event(){

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Event(String titulo, String imagem, String descr, String key, String local, String data){
            this.titulo = titulo;
            this.imagem= imagem;
            this.descr = descr;
            this.key = key;
            this.data = data;
            this.local = local;
    }


    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
