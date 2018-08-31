package com.example.naruto.sharefile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate 2018/8/29 0029
 * @Note
 */
public class SelectOperationAdapter extends RecyclerView.Adapter<SelectOperationAdapter.MyViewHolder> {
    private List<Map<String, Object>> dataList;
    private Context context;
    private MyOnclickListener myOnclickListener;

    public SelectOperationAdapter(List<Map<String, Object>> dataList, Context context, MyOnclickListener myOnclickListener) {
        this.dataList = dataList;
        this.context = context;
        this.myOnclickListener = myOnclickListener;
    }

    @Override

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_select_operation, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * @Purpose
     * @Author Naruto Yang
     * @CreateDate 2018/8/29 0029
     * @Note
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvText;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myOnclickListener.onClick(v);
                }
            });
        }

        public void setData(int position) {
            Map<String, Object> map = dataList.get(position);
            try {
                ivIcon.setImageResource((int) map.get("icon"));
                tvText.setText((String) map.get("text"));
                itemView.setTag(map.get("icon"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface MyOnclickListener {
        void onClick(View v);
    }
}
