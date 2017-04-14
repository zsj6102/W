package per.lijuan.meituan.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import per.lijuan.meituan.Interface.AddClickListener;
import per.lijuan.meituan.Interface.CoItemListener;
import per.lijuan.meituan.Interface.MoveSwapListener;
import per.lijuan.meituan.View.DragGridView;
import per.lijuan.meituan.Interface.MoveBottomListener;
import per.lijuan.meituan.Interface.SubClickListener;
import per.lijuan.meituan.R;

/**
 * Created by admin on 2017/3/22.
 */

public class DragGridAdapter extends BaseAdapter implements DragGridView.DragGridBaseAdapter {
    private List<Map<String,Object>> mData;
    private boolean isShowDelete;
    private LayoutInflater inflater;
    private SubClickListener clicker;
    private MoveBottomListener listener;
    private MoveSwapListener swapListener;
    private AddClickListener addClickListener;
    private CoItemListener itemListener;
    public DragGridAdapter(Context context, List<Map<String,Object>> list ){
        inflater = LayoutInflater.from(context);
        mData = list;

    }
    @Override
    public int getCount() {
        return mData == null?1:mData.size()+1;
    }
    public void setIsShowDelete(boolean isShowDelete){
        this.isShowDelete=isShowDelete;
        notifyDataSetChanged();
    }

    public void setCoItemListener(CoItemListener listener){this.itemListener = listener;}
    public void setAddClickListener(AddClickListener addListener){this.addClickListener = addListener;}
    public void setClicker(SubClickListener clicker){
        this.clicker = clicker;
    }
    public void setBottomListener(MoveBottomListener listener){this.listener = listener;}
    public void setMoveSwapListener(MoveSwapListener listener){ this.swapListener = listener;}
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

          MyHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate( R.layout.item_gridview, parent, false);
            holder = MyHolder.create(convertView);
            convertView.setTag(holder);
        } else {
            holder = ( MyHolder) convertView.getTag();
        }
        /**
         * 在给View绑定显示的数据时，计算正确的position = position + curIndex * pageSize，
         */
                if(mData.size()==0 || position == mData.size()){
                    holder.imageView.setVisibility(View.GONE);
                }else{
                    holder.imageView.setVisibility(isShowDelete?View.VISIBLE:View.GONE);
                }
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(clicker!=null){
                            clicker.click(v,position);
                        }
                    }
                });
                if(mData.size()!=0 && position != mData.size()){
                    holder.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(itemListener!=null){
                                itemListener.click(position);
                            }
                        }
                    });
                    holder.mTextView.setText(mData.get(position).get("ItemText").toString());
                    holder.mImageView.setBackgroundResource((int)mData.get(position).get("ItemImage"));
                }else{
                    holder.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            holder.mImageView.setOnTouchListener(new View.OnTouchListener() {
//                                @Override
//                                public boolean onTouch(View v, MotionEvent event) {
//                                    switch (event.getAction()){
//                                        case MotionEvent.ACTION_DOWN:
//                                            holder.mImageView.
//                                            break;
//                                        case MotionEvent.ACTION_UP:
//                                            holder.mImageView.setBackgroundResource(R.mipmap.add_item);
//                                            break;
//                                        default:
//                                            break;
//                                    }
//                                    return false;
//                                }
//                            });
                            if(addClickListener!=null){
                                addClickListener.addItem();
                            }
                        }
                    });
                    holder.mTextView.setText("");

                    holder.mImageView.setBackgroundResource(R.mipmap.add_item);
                }




        return convertView;
    }
    private static class MyHolder {
        public ImageView mImageView;
        public TextView mTextView;
        public ImageView imageView;

        public MyHolder(ImageView imageView, TextView textView,ImageView iv ) {
            this.mImageView = imageView;
            this.mTextView = textView;
            this.imageView = iv;
        }


        public static MyHolder create(View rootView) {
            ImageView image = (ImageView) rootView.findViewById(R.id.imageView);
            TextView text = (TextView) rootView.findViewById(R.id.textView);
            ImageView iv = (ImageView)rootView.findViewById(R.id.delete);
            return new MyHolder(image, text,iv);
        }
    }
    //拖动到文件夹添加
    @Override
    public void reorderItems(int oldPosition, int newPosition) {

        if(swapListener!=null){ swapListener.swap(oldPosition,newPosition);}
    }


    @Override
    public void setHideItem(int hidePosition) {

    }
//拖动到下面添加
    @Override
    public void deleteItem(int deletePosition) {
        if(listener!=null){ listener.moveBottom(deletePosition);}
    }
}
