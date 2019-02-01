package com.sourcey.refind.model;

public class  PostinganModel {
    private String id , users_id, users, latitude, longitude, postingan, gambar, tgl_buat;
    private int komentar, likestat, suka;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsers_id() {
        return users_id;
    }

    public void setUsers_id(String users_id) {
        this.users_id = users_id;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPostingan() {
        return postingan;
    }

    public void setPostingan(String postingan) {
        this.postingan = postingan;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getTgl_buat() {
        return tgl_buat;
    }

    public void setTgl_buat(String tgl_buat) {
        this.tgl_buat = tgl_buat;
    }

    public int getKomentar() {
        return komentar;
    }

    public void setKomentar(int komentar) {
        this.komentar = komentar;
    }

    public int getSuka() {
        return suka;
    }

    public void setSuka(int suka) {
        this.suka = suka;
    }

    public int getLikestat() {
        return likestat;
    }

    public void setLikestat(int likestat) {
        this.likestat = likestat;
    }
}
