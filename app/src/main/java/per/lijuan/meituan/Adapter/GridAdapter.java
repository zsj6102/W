package per.lijuan.meituan.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;
import java.util.Map;

import per.lijuan.meituan.Interface.SubClickListener;
import per.lijuan.meituan.R;


/**
 * Created by admin on 2017/3/20.
 */

public class GridAdapter extends BaseAdapter {
    private List<Map<String,Object>> list;
    private LayoutInflater inflater;
    private boolean isShowDelete;
    private SubClickListener clicker;
    public GridAdapter(Context context, List<Map<String,Object>> list  ){
        this.list = list;
        inflater = LayoutInflater.from(context);


    }
    public void setClicker(SubClickListener clicker){
        this.clicker = clicker;
    }
    @Override
    public int getCount() {
        return list.size();
    }
    public void setIsShowDelete(boolean isShowDelete){
        this.isShowDelete=isShowDelete;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            viewHolder = new  ViewHolder();
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.tv) ;
            viewHolder.dv = (ImageView)convertView.findViewById(R.id.dv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.dv.setVisibility(isShowDelete?View.VISIBLE:View.GONE);
        viewHolder.iv.setBackgroundResource((int)list.get(position).get("ItemImage"));
        viewHolder.tv.setText(list.get(position).get("ItemText").toString());
        viewHolder.dv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicker!=null){
                    clicker.click(v,position);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {

        ImageView iv;
        TextView tv;
        ImageView dv;
    }

}
