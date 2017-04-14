package per.lijuan.meituan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import java.util.List;

import per.lijuan.meituan.R;

/**
 * Created by admin on 2017/3/29.
 */

public class DialogGridAdapter extends BaseAdapter{
    private List<Integer> list;
    private LayoutInflater inflater;
    private int  clickTemp=-1;
    private int[] res = {R.mipmap.bottom0,R.mipmap.bottom1,R.mipmap.bottom2,R.mipmap.bottom3,R.mipmap.bottom4,R.mipmap.bottom5,R.mipmap.bottom6,
    R.mipmap.bottom7,R.mipmap.bottom8};
    private int[] che = {R.mipmap.check0,R.mipmap.check1,R.mipmap.check2,R.mipmap.check3,R.mipmap.check4,R.mipmap.check5,R.mipmap.check6,R.mipmap.check7,R.mipmap.check8};
    public DialogGridAdapter(Context context,List<Integer> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    public void setSelection(int position){
        clickTemp = position;
    }
    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.dialog_grid, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(clickTemp == position){
            viewHolder.iv.setBackgroundResource(che[list.get(position)]);
        }else{
            viewHolder.iv.setBackgroundResource(res[list.get(position)]);
        }



        return convertView;
    }
    private class ViewHolder{
        ImageView iv;
    }
}
