package com.kerchin.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by lijuan on 2016/9/12.
 */
public class GridViewAdapter extends BaseAdapter {
    private List<Map<String,Object>> mData;
    private LayoutInflater inflater;
    private boolean isShowDelete;
//    private ImageSetListener listener;
    /**
     * 页数下标,从0开始(当前是第几页)
     */
    private int curIndex;
    /**
     * 每一页显示的个数
     */
    private int pageSize;
    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();
    }
    public GridViewAdapter(Context context, List<Map<String,Object>> mData, int curIndex, int pageSize ) {
        inflater = LayoutInflater.from(context);
        this.mData = mData;
        this.curIndex = curIndex;
        this.pageSize = pageSize;
//        this.listener = listener;
    }

//    public void setListener(ImageSetListener listener) {
//        this.listener = listener;
//    }

    /**
     * 先判断数据集的大小是否足够显示满本页？mData.size() > (curIndex+1)*pageSize,
     * 如果够，则直接返回每一页显示的最大条目个数pageSize,
     * 如果不够，则有几项返回几,(mData.size() - curIndex * pageSize);(也就是最后一页的时候就显示剩余item)
     */
    @Override
    public int getCount() {
        return mData.size() > (curIndex + 1) * pageSize ? pageSize : (mData.size() - curIndex * pageSize);

    }

    @Override
    public Map<String,Object> getItem(int position) {
        return mData.get(position + curIndex * pageSize);
    }

    @Override
    public long getItemId(int position) {
        return position + curIndex * pageSize;
    }

    @Override
    public View getView(  int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gridview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.imageView);
          viewHolder.dv = (ImageView)convertView.findViewById(R.id.delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        /**
         * 在给View绑定显示的数据时，计算正确的position = position + curIndex * pageSize，
         */
        final  int pos = position + curIndex * pageSize;
         viewHolder.dv.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mData.remove(pos);
                 notifyDataSetChanged();
             }
         });
        viewHolder.tv.setText(mData.get(pos).get("ItemText").toString());
        viewHolder.iv.setBackgroundResource((int)mData.get(pos).get("ItemImage"));

        return convertView;
    }

    private class ViewHolder {
        TextView tv;
        ImageView iv;
        ImageView dv;
    }
    public void addItem(Map<String,Object> e){
        mData.add(e);
        notifyDataSetChanged();
    }

}