package com.jbaudoux.smp;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

public class MainActivity extends AppCompatActivity implements MediaService {

    private static final int READ_PLAY_LISTS = 1;

    public static final String ASC = " ASC";
    public static final String EXTERNAL = "external";
    public static final String INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String PLAYLIST = "playlist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadPlayLists();
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, AboutActivity.class));
        return true;
    }

    private void loadPlayLists() {
        ListView listView = findViewById(R.id.playlistView);
        listView.setEmptyView(findViewById(R.id.empty));
        listView.setAdapter(new PlayListAdapter(fetchPlayLists(), this, getLayoutInflater(), getApplicationContext()));
    }

    private List<PlayList> fetchPlayLists() {
        if (!requestPermissionForReadExtertalStorage(READ_PLAY_LISTS)) {
            return new ArrayList<>();
        }
        String idKey = MediaStore.Audio.Playlists._ID;
        String nameKey = MediaStore.Audio.Playlists.NAME;
        String[] columns = {idKey, nameKey};

        try (Cursor playListsCursor = getContentResolver().query(EXTERNAL_CONTENT_URI, columns, null, null, nameKey + ASC)) {
            List<PlayList> playLists = new LinkedList<>();
            if (playListsCursor == null) {
                return playLists;
            }
            int keyIndex = playListsCursor.getColumnIndex(idKey);
            int nameIndex = playListsCursor.getColumnIndex(nameKey);
            while (playListsCursor.moveToNext()) {
                playLists.add(new PlayList(
                        playListsCursor.getInt(keyIndex),
                        getSongCount(playListsCursor.getInt(keyIndex)),
                        playListsCursor.getString(nameIndex)
                ));
            }

            return playLists;
        }
    }

    private int getSongCount(int playListID) {
        String[] ARG_STRING = {MediaStore.Audio.Media._ID};
        Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri(EXTERNAL, playListID);
        try (Cursor songsWithinAPlayList = getContentResolver().query(membersUri, ARG_STRING, null, null, null)) {
            if (songsWithinAPlayList == null) {
                return 0;
            }
            return songsWithinAPlayList.getCount();
        }
    }

    @NonNull
    private Intent createRunPlaylistIntent(String playlistName) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,
                MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        intent.putExtra(MediaStore.EXTRA_MEDIA_PLAYLIST, playlistName);
        intent.putExtra(SearchManager.QUERY, PLAYLIST + " " + playlistName);
        return intent;
    }

    public void playSongsFromAPlaylist(PlayList playList) {
        Intent intent = createRunPlaylistIntent(playList.getName());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void createShortcutForPlaylist(PlayList playList) {
        Intent intent = createRunPlaylistIntent(playList.getName());

        final Intent putShortCutIntent = new Intent();
        putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        // Sets the custom shortcut's title
        putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, playList.getName());
        putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
        putShortCutIntent.setAction(INSTALL_SHORTCUT);
        sendBroadcast(putShortCutIntent);
    }

    public boolean requestPermissionForReadExtertalStorage(int action) {
        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, action);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
