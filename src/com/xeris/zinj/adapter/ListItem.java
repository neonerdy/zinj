package com.xeris.zinj.adapter;

public class ListItem
{
    public final String text;
    public final int icon;

    public ListItem(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }
    @Override
    public String toString() {
        return text;
    }


}
