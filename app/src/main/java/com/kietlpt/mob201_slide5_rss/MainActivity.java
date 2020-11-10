package com.kietlpt.mob201_slide5_rss;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    /** Called when the activity is first created. */
    private  RssAdapter DSTin;
    private EditText UrlText;
    private TextView TrangThai;
    private Handler Handler;
    private KETNOI Ketnoi_Rss;
    public static final int DODAI = 100;
    public static final String CHUOI_TIEUDETIN = "chuoi";
    public static final String CHON_TINDOC = "chon";
    public static final String LINK_TIN = "link";
    public static final String TRANGTHAI = "trangthai";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<RSSItems> items = new ArrayList<RSSItems>();
        DSTin = new RssAdapter(this, items);

        getListView().setAdapter(DSTin);

        UrlText = (EditText)findViewById(R.id.text);
        TrangThai = (TextView)findViewById(R.id.trangthai);

        Button download = (Button)findViewById(R.id.taitrang);
        download.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TaiTrang(UrlText.getText());
            }
        });
        Handler = new Handler();
    }




    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        RSSItems item = DSTin.getItem(position);

        Intent intent = new Intent(MainActivity.this,WebActivity.class);
        intent.putExtra("link",item.getLink().toString());


        startActivity(intent);
    }


    public void resetUI() {
        List<RSSItems> items = new ArrayList<RSSItems>();
        DSTin = new RssAdapter(this, items);
        getListView().setAdapter(DSTin);
        TrangThai.setText("");
        UrlText.requestFocus();
    }

    public synchronized void setCurrentWorker(KETNOI ketnoi) {
        if (Ketnoi_Rss != null) Ketnoi_Rss.interrupt();
        Ketnoi_Rss = ketnoi;
    }

    public synchronized boolean isCurrentWorker(KETNOI ketnoi) {
        return (Ketnoi_Rss == ketnoi);
    }



    private void TaiTrang(CharSequence rssUrl) {
        KETNOI worker = new KETNOI(rssUrl);
        setCurrentWorker(worker);
        resetUI();
        TrangThai.setText("Downloading....");
        worker.start();
    }


    private class ThemTin implements Runnable {
        RSSItems Item;
        ThemTin(RSSItems item) {
            Item = item;
        }

        public void run() {
            DSTin.add(Item);
        }
    }


    private class KETNOI extends Thread {
        private CharSequence Url;

        public KETNOI(CharSequence url) {
            Url = url;
            DSTin = DSTin;
        }

        @Override
        public void run() {
            String trangthai = "";
            try {
                URL url = new URL(Url.toString());
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(10000);
                connection.connect();
                InputStream in = connection.getInputStream();
                XuLiTinLayVe(in, DSTin);
                trangthai="Ket noi thanh cong: Tin da duoc load ve!";
            } catch (Exception e) {
                trangthai = "Khong tai duoc:" + e.getMessage();
            }
            final String temp = trangthai;
            if (isCurrentWorker(this)) {
                Handler.post(new Runnable() {
                    public void run() {
                        TrangThai.setText(temp);
                    }
                });
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int count = DSTin.getCount();
        ArrayList<CharSequence> strings = new ArrayList<CharSequence>();
        for (int i = 0; i < count; i++) {
            RSSItems item = DSTin.getItem(i);
            strings.add(item.getTitle());
            strings.add(item.getLink());
            strings.add(item.getDescription());
        }
        outState.putSerializable(CHUOI_TIEUDETIN, strings);
        if (getListView().hasFocus()) {
            outState.putInt(CHON_TINDOC, Integer.valueOf(getListView().getSelectedItemPosition()));
        }
        outState.putString(LINK_TIN, UrlText.getText().toString());
        outState.putCharSequence(TRANGTHAI, TrangThai.getText());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state == null) return;
        List<CharSequence> strings = (ArrayList<CharSequence>)state.getSerializable(CHUOI_TIEUDETIN);
        List<RSSItems> items = new ArrayList<RSSItems>();
        for (int i = 0; i < strings.size(); i += 3) {
            items.add(new RSSItems(strings.get(i), strings.get(i + 1), strings.get(i + 2)));
        }
        DSTin = new RssAdapter(this, items);
        getListView().setAdapter(DSTin);
        if (state.containsKey(CHON_TINDOC)) {
            getListView().requestFocus(View.FOCUS_FORWARD);
            getListView().setSelection(state.getInt(CHON_TINDOC));
        }
        UrlText.setText(state.getCharSequence(LINK_TIN));
        TrangThai.setText(state.getCharSequence(TRANGTHAI));
    }

    void XuLiTinLayVe(InputStream dauvao, RssAdapter dstin) throws IOException, XmlPullParserException {
        //Khai bao parser
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(dauvao, null);
        int sukien;
        String title = "";
        String link = "";
        String description = "";
        sukien = xpp.getEventType();
        while (sukien != XmlPullParser.END_DOCUMENT) {
            if (sukien == XmlPullParser.START_TAG) {
                String tag = xpp.getName();
                if (tag.equals("item")) {
                    title = link = description = "";
                } else if (tag.equals("title")) {
                    xpp.next();
                    title = xpp.getText();
                }
                else if (tag.equals("link")) {
                    xpp.next();
                    link = xpp.getText();
                } else if (tag.equals("description")) {
                    xpp.next();
                    description = xpp.getText();
                }
            } else if (sukien == XmlPullParser.END_TAG) {
                String tag = xpp.getName();
                if (tag.equals("item")) {
                    RSSItems item = new RSSItems(title, link, description);
                    Handler.post(new ThemTin(item));
                }
            }
            sukien = xpp.next();
        }
    }
}
