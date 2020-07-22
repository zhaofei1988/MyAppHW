package everlinkintl.com.myappwh.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/***
 * 调用实例 MyAdapter
 * @param <T>
 */
public abstract class MyBaseAdapter<T> extends android.widget.BaseAdapter {
    protected Context mContext;
    protected List<T> mData = new ArrayList<T>();
    LayoutInflater mInflater;

    public MyBaseAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }
    final public void setData(List<T> list) {
        this.mData = list;
    }
    final public List<T> getData() {
        return mData;
    }
    @Override
    final public int getCount() {
        return mData.size();
    }
    @Override
    public T getItem(int i) {
        return mData.get(i);
    }

    @Override
    final public long getItemId(int i) {
        return i;
    }
    public abstract int getItemViewType(int position);
    public abstract int getItemLayoutId(int getItemViewType);
    public abstract void handleItem(int itemViewType, int position, T item, ViewHolder holder, boolean isRecycle);

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        int itemLayoutType = getItemViewType(position);
        ViewHolder viewHolder = null;
        boolean isRecycle = false;
        if (view == null) {
            view = mInflater.inflate(getItemLayoutId(itemLayoutType), null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            isRecycle = true;
        }
        handleItem(itemLayoutType, position, mData.get(position), viewHolder, isRecycle);
        return view;
    }

    public static class ViewHolder {
        View mRootView;
        SparseArray<View> mViews = new SparseArray<View>();

        public ViewHolder(View view) {
            this.mRootView = view;
        }

        public View getView() {
            return mRootView;
        }

        public <T extends View> T get(int viewId) {
            View childView = mViews.get(viewId);
            if (childView == null) {
                childView = mRootView.findViewById(viewId);
                mViews.put(viewId, childView);
            }
            return (T) childView;
        }

        public <T extends View> T get(int viewId, Class<T> viewClass) {
            View childView = mViews.get(viewId);
            if (childView == null) {
                childView = mRootView.findViewById(viewId);
                mViews.put(viewId, childView);
            }
            return (T) childView;
        }

    }
}
