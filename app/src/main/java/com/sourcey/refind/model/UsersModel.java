package com.sourcey.refind.model;

public class UsersModel {
    private String id, username, nama, jenis_kelamin, email, lat, lng, jarak, like_status;
//
//    public PostinganModel(String users_id, String users, String latitude, String longitude, String postingan, String gambar, String tgl_buat, int komentar, int suka) {
//        this.users_id = users_id;
//        this.users = users;
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.postingan = postingan;
//        this.gambar = gambar;
//        this.tgl_buat = tgl_buat;
//        this.komentar = komentar;
//        this.suka = suka;
//    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getJarak() {
        return jarak;
    }

    public void setJarak(String jarak) { this.jarak = jarak;
    }

    public String getLike_status() {
        return like_status;
    }

    public void setLike_status(String like_status) {
        this.like_status = like_status;
    }

}
