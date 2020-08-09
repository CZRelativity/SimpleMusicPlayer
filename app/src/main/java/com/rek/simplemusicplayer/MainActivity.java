package com.rek.simplemusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView nextIv,playIv,lastIv,albumIv;
    TextView singerTv,songTv;
    RecyclerView musicRv;

    List<MusicBean> musicData;
    private MusicAdapter musicAdapter;

    private int currentPosition=-1;
    MediaPlayer mediaPlayer=new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            initView();
        }
        musicData=new ArrayList<>();
        //创建适配器对象
        musicAdapter=new MusicAdapter(this,musicData);//因为之后会涉及到数据源的更新，所以这里要作为成员变量来写
        musicRv.setAdapter(musicAdapter);
        //设置布局管理器
        musicRv.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        //加载本地数据源
        loadMusicData();
        //设置每一项的点击事件
        setEventListener();
    }

    private void setEventListener() {
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                currentPosition=position;
                MusicBean musicBean=musicData.get(position);
                singerTv.setText(musicBean.getSinger());
                songTv.setText(musicBean.getSong());
                stopMusic();
                try {
                    mediaPlayer.setDataSource(musicBean.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playMusic();
            }
        });
    }

    private void playMusic() {
        if (mediaPlayer!=null&&!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.prepare();
                mediaPlayer.start();
                playIv.setImageResource(R.mipmap.icon_pause);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopMusic() {
        /* 停止播放音乐 */
        if(mediaPlayer!=null){
            mediaPlayer.reset();
        }
    }

    private void loadMusicData(){//加载本地存储当中的mp3音乐文件到集合当中
        //1.获取ContentResolver对象
        ContentResolver resolver=getContentResolver();
        //2.获取本地音乐存储的Uri地址
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//外部存储
        //3.开始查询地址
        Cursor cursor=resolver.query(uri,null,null,null,null,null);//projection投影
        //4.遍历Cursor
        int id=0;
        while(cursor.moveToNext()){
            String song=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            Long duration=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
            String time=sdf.format(new Date(duration));
            String path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH));
            id++;
            //5.将所有元素封装到对象中
            MusicBean bean=new MusicBean(id,song,singer,album,time,path);
            musicData.add(bean);
        }
        //数据源变化，提示Adapter更新
        musicAdapter.notifyDataSetChanged();
    }

    private void initView(){
        lastIv=findViewById(R.id.bottom_iv_last);
        nextIv=findViewById(R.id.bottom_iv_next);
        playIv=findViewById(R.id.bottom_iv_play);
        albumIv=findViewById(R.id.bottom_iv_album);
        songTv=findViewById(R.id.bottom_tv_song);
        singerTv=findViewById(R.id.bottom_tv_singer);
        musicRv=findViewById(R.id.music_rv);
        lastIv.setOnClickListener(this);//需要传入一个OnClickListener，可以new一个匿名内部类，也可以像这样用this去实现接口
        nextIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
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
        switch(v.getId()){
            case R.id.bottom_iv_last:

                break;
            case R.id.bottom_iv_play:

                break;
            case R.id.bottom_iv_next:

                break;
        }
    }
}
