package com.jbaudoux.smp;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int READ_PLAY_LISTS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadPlayLists();
    }

    private void loadPlayLists() {
        ListView listView = findViewById(R.id.playlistView);
        listView.setAdapter(new PlayListAdapter(fetchPlayLists(), getLayoutInflater()));
    }

    private List<PlayList> fetchPlayLists() {
        if (!requestPermissionForReadExtertalStorage(READ_PLAY_LISTS)) {
            return new ArrayList<>();
        }
        String idKey = MediaStore.Audio.Playlists._ID;
        String nameKey = MediaStore.Audio.Playlists.NAME;
        String[] columns = {idKey, nameKey};

        try (Cursor playListsCursor = getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, columns, null, null, nameKey + " ASC")) {
            List<PlayList> playLists = new LinkedList<>();
            if (playListsCursor == null) {
                return playLists;
            }
            int keyIndex = playListsCursor.getColumnIndex(idKey);
            int nameIndex = playListsCursor.getColumnIndex(nameKey);
            while (playListsCursor.moveToNext()) {
                playLists.add(new PlayList(
                        playListsCursor.getInt(keyIndex),
                        0,
                        playListsCursor.getString(nameIndex)
                ));
            }

            playSongsFromAPlaylist(playLists.get(0).getName());

            return playLists;
        }
    }

    public void playSongsFromAPlaylist(String playlistName) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,
                MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        intent.putExtra(MediaStore.EXTRA_MEDIA_PLAYLIST, playlistName);
        intent.putExtra(SearchManager.QUERY, "playlist " + playlistName);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void playSongsFromAPlaylistOld(int playListID){
        String[] ARG_STRING = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Video.Media.SIZE,android.provider.MediaStore.MediaColumns.DATA};
        Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListID);
        Cursor songsWithingAPlayList = getContentResolver().query(membersUri, ARG_STRING, null, null, "RANDOM()");
        int theSongIDIwantToPlay = 0; // PLAYING FROM THE FIRST SONG
        if(songsWithingAPlayList != null) {
            songsWithingAPlayList.moveToPosition(theSongIDIwantToPlay);
            String DataStream = songsWithingAPlayList.getString(4);
            playMusic(DataStream);
            songsWithingAPlayList.close();
        }
    }

    public void playMusic(String DataStream){
        MediaPlayer mpObject = new MediaPlayer();
        if(DataStream == null)
            return;
        try {
            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mpObject.setWakeMode(this.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mpObject.setLooping(true);
            mpObject.setDataSource(DataStream);
            mpObject.prepare();
            mpObject.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean requestPermissionForReadExtertalStorage(int action) {
        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, action);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PLAY_LISTS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadPlayLists();
                }
            }
        }
    }
}
