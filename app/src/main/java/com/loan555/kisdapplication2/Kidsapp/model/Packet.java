package com.loan555.kisdapplication2.Kidsapp.model;

import android.util.Log;

public class Packet {
    public long time;
    public int version;
    public int protocol;
    public String flags;
    public String saddr;
    public int sport;
    public String daddr;
    public int dport;
    public String data;
    public int uid;
    public boolean allowed;

    @Override
    public String toString() {
        return "data="+data+" uid=" + uid + " v" + version + " p" + protocol + " " + daddr + "/" + dport;
    }
}
