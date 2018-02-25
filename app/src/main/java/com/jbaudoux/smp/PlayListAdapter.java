package com.jbaudoux.smp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PlayListAdapter extends BaseAdapter {

    private final List<PlayList> playLists;
    private final LayoutInflater layoutInflater;

    public PlayListAdapter(List<PlayList> playLists, LayoutInflater layoutInflater) {
        this.playLists = playLists;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return playLists.size();
    }

    @Override
    public Object getItem(int i) {
        return playLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.playlist, null);
        }
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(playLists.get(i).getName());
        return view;
    }
}
