package com.jbaudoux.smp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class PlayListAdapter extends BaseAdapter {

    private final List<PlayList> playLists;
    private final MainActivity mainActivity;

    public PlayListAdapter(List<PlayList> playLists, MainActivity mainActivity) {
        this.playLists = playLists;
        this.mainActivity = mainActivity;
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
            view = mainActivity.getLayoutInflater().inflate(R.layout.playlist, null);
        }

        // Title
        TextView title = view.findViewById(R.id.titleView);
        title.setText(playList.getName());

        // Label
        TextView subtitle = view.findViewById(R.id.subtitleView);
        subtitle.setText(mainActivity.getResources().getString(R.string.songs, playList.getCount()));

        // Button
        ImageButton playButton = view.findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.playSongsFromAPlaylist(playList.getName());
            }
        });

        return view;
    }
}
