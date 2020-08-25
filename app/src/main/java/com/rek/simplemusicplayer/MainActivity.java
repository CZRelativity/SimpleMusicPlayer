package com.rek.simplemusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnPreparedListener {

    ImageView nextIv, playIv, lastIv, albumIv;
    TextView singerTv, songTv;
    RecyclerView musicRv;

    List<MusicBean> musicData;
    private MusicAdapter musicAdapter;

    private int currentPosition = -1;
    private MusicBean musicBean;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private int pausedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        musicData = new ArrayList<>();
        //创建适配器对象
        musicAdapter = new MusicAdapter(this, musicData);//因为之后会涉及到数据源的更新，所以这里要作为成员变量来写
        musicRv.setAdapter(musicAdapter);
        //设置布局管理器
        musicRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        //加载本地数据源,增加动态获取权限，注意动态获取权限的判断要放在initView以后，否则无法闪退无法进入界面
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            loadMusicData();
        }
        //设置每一项的点击事件
        mediaPlayer.setOnPreparedListener(this);
        setEventListener();
    }

    private void setEventListener() {
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                currentPosition = position;
                resetMusic();
                prepareMusic();
                playMusic();
            }
        });
    }

    private void prepareMusic() {
        musicBean = musicData.get(currentPosition);
        singerTv.setText(musicBean.getSinger());
        songTv.setText(musicBean.getSong());
        resetMusic();
        try {
            //mediaPlayer.setDataSource(getApplicationContext(),musicBean.getUri());
            mediaPlayer.setDataSource(musicBean.getPath());
            //mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMusic() {
        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//            playIv.setImageResource(R.mipmap.icon_pause);
            mediaPlayer.prepareAsync();
            //当在主线程中使用MediaPlayer时，应该调用prepareAsync()而非prepare()，并实现MediaPlayer.OnPreparedListener,在准备就绪后
            //会调用OnPrepared()方法。否则会导致界面挂起，直到系统返回该方法。因为prepare()可能会涉及获取和解码媒体数据，对于任何可能需要很
            //长时间执行的方法，都应避免从主线程中调用
        }
    }

    private void resetMusic() {
        /* 停止播放音乐 */
        mediaPlayer.reset();
    }

    private void loadMusicData() {//加载本地存储当中的mp3音乐文件到集合当中
        //1.获取ContentResolver对象
        ContentResolver resolver = getContentResolver();
        //2.获取本地音乐存储的Uri地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//外部存储
        //3.开始查询地址
        Cursor cursor = resolver.query(uri, null, null, null, null, null);//projection投影
        //4.遍历Cursor
        int id = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                String time = sdf.format(new Date(duration));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//                Uri contentUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                id++;
                //5.将所有元素封装到对象中
                MusicBean bean = new MusicBean(id, song, singer, album, time, path);
//                MusicBean bean=new MusicBean(id,song,singer,album,time,contentUri);
                musicData.add(bean);
            } while (cursor.moveToNext());
        }
        //数据源变化，提示Adapter更新
        musicAdapter.notifyDataSetChanged();
    }

    private void initView() {
        lastIv = findViewById(R.id.bottom_iv_last);
        nextIv = findViewById(R.id.bottom_iv_next);
        playIv = findViewById(R.id.bottom_iv_play);
        albumIv = findViewById(R.id.bottom_iv_album);
        songTv = findViewById(R.id.bottom_tv_song);
        singerTv = findViewById(R.id.bottom_tv_singer);
        musicRv = findViewById(R.id.music_rv);
        lastIv.setOnClickListener(this);//需要传入一个OnClickListener，可以new一个匿名内部类，也可以像这样用this去实现接口
        nextIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView();
            } else {
                Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_iv_last:
                /*重新注入dataSource,而且是按从musicData里面的顺序,如果超出了第一个就返回最后
                 * 状态是play的话应该直接开始播放,状态是pause的话不播放只切换,下曲同理
                 * 需要记录这个状态,还希望最好下次点进来的时候可以加载之前退出的时候播放的歌曲*/
                if (currentPosition != -1) {
                    if (currentPosition == 0) {
                        currentPosition = musicData.size() - 1;
                    } else {
                        currentPosition--;
                    }
                    if (mediaPlayer.isPlaying()) {
                        prepareMusic();
                        playMusic();
                    } else {
                        prepareMusic();
                    }
                }
                break;
            case R.id.bottom_iv_play:
                if (currentPosition != -1) {
                    /*之前的问题修复了，问题原因主要有两个
                    1、不能用mediaPlayer!=null来做进入条件，不然基本是必进入的，意思只要mediaPlayer初始化了就不等于null了，用来判断释放资源还好说，其他的都不能用这个去判断
                    2、用一个布尔来判断也太僵了，改了以后就舒服多了
                    3、if...else if还是不用挨着break的，吓*/
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playIv.setImageResource(R.mipmap.icon_play);
                        pausedPosition = currentPosition;
                    } else if (pausedPosition == currentPosition) {
                        mediaPlayer.start();
                        playIv.setImageResource(R.mipmap.icon_pause);
                    } else {
                        playMusic();
                    }
                    break;
                }
            case R.id.bottom_iv_next:
                if (currentPosition != -1) {
                    if (currentPosition == musicData.size() - 1) {
                        currentPosition = 0;
                    } else {
                        currentPosition++;
                    }
                    if (mediaPlayer.isPlaying()) {
                        prepareMusic();
                        playMusic();
                    } else {
                        prepareMusic();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        playIv.setImageResource(R.mipmap.icon_pause);
    }
}
