package com.weather.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.myviewHodler> {

    private Context context;
    private ArrayList<String> nameList;
    private OnItemClickListener onItemClickListener;

    Adapter(Context context, ArrayList<String> nameList) {
        this.context = context;
        this.nameList = nameList;
    }

    @NonNull
    @Override
    public Adapter.myviewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item, null);
        return new myviewHodler(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.myviewHodler holder, final int position) {
        String data = nameList.get(position);
        if(onItemClickListener!=null){
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.OnItemClick(v,position);
                }
            });
        }
        holder.Name.setText(data);
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }
    class myviewHodler extends RecyclerView.ViewHolder{
        private TextView Name;
        private LinearLayout item;
        myviewHodler(@NonNull View itemView) {
            super(itemView);
            Name = (TextView)itemView.findViewById(R.id.item_name);
            item = (LinearLayout)itemView.findViewById(R.id.item);
        }
    }
    public interface OnItemClickListener {
        public void OnItemClick( View view,int position);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
