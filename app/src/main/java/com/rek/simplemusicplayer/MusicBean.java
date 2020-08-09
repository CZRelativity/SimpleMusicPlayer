package com.rek.simplemusicplayer;

public class MusicBean {

    private int id;
    private String song;
    private String singer;
    private String album;
    private String time;
    private String path;

    public MusicBean(int id, String song, String singer, String album, String time, String path) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.time = time;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
