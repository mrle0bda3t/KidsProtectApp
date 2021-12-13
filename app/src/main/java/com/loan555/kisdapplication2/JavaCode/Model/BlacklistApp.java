package com.loan555.kisdapplication2.JavaCode.Model;

public class BlacklistApp {
    private String id;
    private String nameBl;
    private String timeStart = null;
    private String timeEnd = null;
    private String idkid;
    private Integer img = null;
    private Integer activate = null;

    public Integer getActivate() {
        return activate;
    }

    public void setActivate(Integer activate) {
        this.activate = activate;
    }

    public BlacklistApp() {
        this.idkid = null;
    }
    public BlacklistApp(String id, String nameBl, Integer img, String timeStart, String timeEnd, String idkid) {
        this.idkid = idkid;
        this.id = id;
        this.nameBl = nameBl;
        this.img = img;
        this.timeEnd = timeEnd;
        this.timeStart = timeStart;
    }
    public String getIdkid() {
        return idkid;
    }

    public void setIdkid(String idkid) {
        this.idkid = idkid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameBl() {
        return nameBl;
    }

    public void setNameBl(String nameBl) {
        this.nameBl = nameBl;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }
}