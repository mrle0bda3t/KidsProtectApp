package com.loan555.kisdapplication2.JavaCode.Model;

public class History {
    public String _id;
    public String treEm;
    public String diaChi;
    public String tinhTrang;
    public String thoiGianYeuCau;
    public String tenapp;
    public Long timelong;

    public Long getTimelong() {
        return timelong;
    }

    public void setTimelong(Long timelong) {
        this.timelong = timelong;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getThoiGianYeuCau() {
        return thoiGianYeuCau;
    }

    public void setThoiGianYeuCau(String thoiGianYeuCau) {
        this.thoiGianYeuCau = thoiGianYeuCau;
    }

    public History(){}
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTreEm() {
        return treEm;
    }

    public void setTreEm(String treEm) {
        this.treEm = treEm;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getTenapp() {
        return tenapp;
    }

    public void setTenapp(String tenapp) {
        this.tenapp = tenapp;
    }

    @Override
    public String toString() {
        return "History{" +
                "_id='" + _id + '\'' +
                ", treEm='" + treEm + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", tinhTrang='" + tinhTrang + '\'' +
                ", thoiGianYeuCau='" + thoiGianYeuCau + '\'' +
                ", tenapp='" + tenapp + '\'' +
                '}';
    }
}
