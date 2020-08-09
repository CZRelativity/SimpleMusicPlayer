package com.rek.simplemusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private Context context;//与ListView一样需要传入Context和数据源
    private List<MusicBean> musicData;

    OnItemClickListener onItemClickListener;
//    然后写一个方法来传入这个接口
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
//        需要传入的就是点击的View还有位置
        void OnItemClick(View view,int position);
    }

    public MusicAdapter(Context context,List<MusicBean> data){//用构造方法进行传递
        this.context=context;
        this.musicData=data;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//在这个方法里我们需要创建并且返回一个ViewHolder对象
        //ViewHolder的构造函数必须传入一个View的值，inflate的第一个参数是把item转换成View对象，第二个是所在的ViewGroup
        View view=LayoutInflater.from(context).inflate(R.layout.item_music,parent,false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, final int position) {
        MusicBean musicBean=musicData.get(position);
        holder.tv_id.setText(Integer.toString(musicBean.getId()));
        holder.tv_song.setText(musicBean.getSong());
        holder.tv_singer.setText(musicBean.getSinger());
        holder.tv_album.setText(musicBean.getAlbum());
        holder.tv_time.setText(musicBean.getTime());
        //我们可以在onBindViewHolder中给itemView设置点击事件，但是我们每个item都要用到MediaPlayer对象，用构造函数来传递的话要传递的东西非常多，
        //还需要改变下面的歌曲和歌手名等，我们把每一个点击事件写在MainActivity当中，在Adapter中开设接口，用接口回调的方式来传参
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v,position);
//                一旦哪一项被点击了，就会调用
            }
        });
    }

    @Override//这两个是Adapter怎么从自己的DataList取数据
    public int getItemCount() {
        return musicData.size();
    }

    class MusicViewHolder extends RecyclerView.ViewHolder{//需要把每一个item的内容都要定义在ViewHolder中

        TextView tv_song,tv_singer,tv_id,tv_album,tv_time;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_id=itemView.findViewById(R.id.card_tv_id);
            tv_song=itemView.findViewById(R.id.card_tv_song);
            tv_singer=itemView.findViewById(R.id.card_tv_singer);
            tv_album=itemView.findViewById(R.id.card_tv_album);
            tv_time=itemView.findViewById(R.id.card_tv_time);
        }
    }
}
