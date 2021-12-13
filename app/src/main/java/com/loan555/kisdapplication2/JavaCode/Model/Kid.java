package com.loan555.kisdapplication2.JavaCode.Model;

public class Kid {
    private String idKid;
    private String nameKid;
    private String anhChanDung;

    public String getAnhChanDung() {
        return anhChanDung;
    }

    public void setAnhChanDung(String anhChanDung) {
        this.anhChanDung = anhChanDung;
    }

    public Kid(){}

    public Kid(String idKid, String nameKid) {
        this.idKid = idKid;
        this.nameKid = nameKid;
    }

    public String getIdKid() {
        return idKid;
    }

    public void setIdKid(String idKid) {
        this.idKid = idKid;
    }

    public String getNameKid() {
        return nameKid;
    }

    public void setNameKid(String nameKid) {
        this.nameKid = nameKid;
    }
}
