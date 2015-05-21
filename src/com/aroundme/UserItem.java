package com.aroundme;

public class UserItem {

    private String title;
    
//    private int icon;

    public UserItem() {}
//    public UserItem(String title, int icon) {
    
    public UserItem(String title) {
        this.title = title;
//        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
/*
    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
*/
}
