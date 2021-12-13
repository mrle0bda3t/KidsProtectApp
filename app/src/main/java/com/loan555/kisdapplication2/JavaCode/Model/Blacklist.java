package com.loan555.kisdapplication2.JavaCode.Model;

public class Blacklist {
    private String idBl;
    private String idbl;
    private String nameBl;
    private String typeBl;
    private String idkid;
    public Blacklist(){this.idkid="";}

    public Blacklist(String idBl, String nameBl, String typeBl) {
        this.idBl = idBl;
        this.nameBl = nameBl;
        this.typeBl = typeBl;
    }

    public String getIdbl() {
        return idbl;
    }

    public void setIdbl(String idbl) {
        this.idbl = idbl;
    }

    public String getIdkid() {
        return idkid;
    }

    public void setIdkid(String idkid) {
        this.idkid = idkid;
    }

    public String getIdBl() {
        return idBl;
    }

    public void setIdBl(String idBl) {
        this.idBl = idBl;
    }

    public String getNameBl() {
        return nameBl;
    }

    public void setNameBl(String nameBl) {
        this.nameBl = nameBl;
    }

    public String getTypeBl() {
        return typeBl;
    }

    public void setTypeBl(String typeBl) {
        this.typeBl = typeBl;
    }
}
