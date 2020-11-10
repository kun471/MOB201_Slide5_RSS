package com.example.mob201_lab05;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TwoLineListItem;

import java.util.List;

public class RssAdapter extends ArrayAdapter<RSSItems> {
    public static final int DODAI = 100;
    private LayoutInflater Inflater;
    public RssAdapter(Context context, List<RSSItems> objects) {
        super(context, 0, objects);
        Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TwoLineListItem view;
        if (convertView == null) {
            view = (TwoLineListItem) Inflater.inflate(android.R.layout.simple_list_item_2, null);
        } else {
            view = (TwoLineListItem) convertView;
        }
        RSSItems item = this.getItem(position);
        view.getText1().setText(item.getTitle());
        String description = item.getDescription().toString();
        description = removeTags(description);
        view.getText2().setText(description.substring(0, Math.min(description.length(), DODAI)));
        return view;
    }
    //Xoa cac ki tu //<.*?> va thay the bang ki tu " ".
    public String removeTags(String str) {
        str = str.replaceAll("<.*?>", " ");
        str = str.replaceAll("\\s+", " ");
        return str;
    }
}
