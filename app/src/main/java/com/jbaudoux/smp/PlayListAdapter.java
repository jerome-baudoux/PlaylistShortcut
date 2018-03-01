package com.jbaudoux.smp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class PlayListAdapter extends BaseAdapter {

    private final List<PlayList> playLists;
    private final MediaService mediaService;
    private LayoutInflater layoutInflater;
    private Context context;

    public PlayListAdapter(List<PlayList> playLists, MainActivity mediaService, LayoutInflater layoutInflater, Context context) {
        this.playLists = playLists;
        this.mediaService = mediaService;
        this.layoutInflater = layoutInflater;
        this.context = context;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final PlayList playList = playLists.get(i);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.playlist, viewGroup, false);
        }

        // Title
        TextView title = view.findViewById(R.id.titleView);
        title.setText(playList.getName());

        // Label
        TextView subtitle = view.findViewById(R.id.subtitleView);
        subtitle.setText(context.getResources().getString(R.string.songs, playList.getCount()));

        // Button
        ImageButton playButton = view.findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mediaService.playSongsFromAPlaylist(playList);
            }
        });

        // Button
        ImageButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mediaService.createShortcutForPlaylist(playList);
            }
        });

        return view;
    }
}
