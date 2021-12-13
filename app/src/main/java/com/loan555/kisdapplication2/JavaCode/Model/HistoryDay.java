package com.loan555.kisdapplication2.JavaCode.Model;

import java.util.ArrayList;

public class HistoryDay {
    private String day;
    private ArrayList<History> listHistory;

    public HistoryDay() {
        day = "";
        listHistory = new ArrayList<>();
    }

    public void addToList(History history) {
        this.listHistory.add(history);
    }

    public ArrayList<History> getList() {
        return listHistory;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDay(){
        return this.day;
    }

    @Override
    public String toString() {
        return "HistoryDay{" +
                "day='" + day + '\'' +
                ", listHistory=" + listHistory.toString() +
                '}';
    }
}
