package com.loan555.kisdapplication2.Kidsapp.model;

public class Forward {
    public int protocol;
    public int dport;
    public String raddr;
    public int rport;
    public int ruid;

    @Override
    public String toString() {
        return "protocol=" + protocol + " port " + dport + " to " + raddr + "/" + rport + " uid " + ruid;
    }
}
