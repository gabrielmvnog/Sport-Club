package br.com.gfsportclub.sportclub;

/**
 * Created by gabriel on 01/04/18.
 */

public class Event {

    String titulo, imagem, descr, key;

    public Event(){

    }

    public Event(String titulo,String imagem,String descr, String key){
            this.titulo = titulo;
            this.imagem= imagem;
            this.descr = descr;
            this.key = key;
    }

    public String getTitle() {
        return titulo;
    }

    public String getImage() {
        return imagem;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void setImage(String imagem) {
        this.imagem = imagem;
    }

    public void setTitle(String titulo) {
        this.titulo = titulo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
