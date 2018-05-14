package id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset;

/**
 * Created by adhi on 09/05/18.
 */

public class UserController {

    private String id_pelanggan;
    private String nama_pelanggan;
    private String no_kartu;
    private String kontak;
    private String alamat;

    public UserController(String no_kartu){
        this.no_kartu = no_kartu;
    }

    public UserController(String id_pelanggan, String nama_pelanggan, String no_kartu ) {
        this.id_pelanggan = id_pelanggan;
        this.nama_pelanggan = nama_pelanggan;
        this.no_kartu = no_kartu;
    }


    public String getId_pelanggan() {
        return id_pelanggan;
    }

    public void setId_pelanggan(String id_pelanggan) {
        this.id_pelanggan = id_pelanggan;
    }

    public String getNama_pelanggan() {
        return nama_pelanggan;
    }

    public void setNama_pelanggan(String nama_pelanggan) {
        this.nama_pelanggan = nama_pelanggan;
    }

    public String getNo_kartu() {
        return no_kartu;
    }

    public void setNo_kartu(String no_kartu) {
        this.no_kartu = no_kartu;
    }

    public String getKontak() {
        return kontak;
    }

    public void setKontak(String kontak) {
        this.kontak = kontak;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

}
