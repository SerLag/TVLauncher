package com.droidlogic.tvlauncher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class LocalAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<? extends Map<String, ?>> mData;
    private int mDropDownResource;
    private SimpleFilter mFilter;
    private String[] mFrom;
    private LayoutInflater mInflater;
    private int mResource;
    private int[] mTo;
    private ArrayList<Map<String, ?>> mUnfilteredData;
    private ViewBinder mViewBinder;

    /* loaded from: classes.dex */
    public interface ViewBinder {
        boolean setViewValue(View view, Object obj, String str);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public LocalAdapter(Context context, List<? extends Map<String, ?>> list, int i, String[] strArr, int[] iArr) {
        this.mContext = context;
        this.mData = list;
        this.mDropDownResource = i;
        this.mResource = i;
        this.mFrom = strArr;
        this.mTo = iArr;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mData.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.mData.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        return createViewFromResource(i, view, viewGroup, this.mResource);
    }

    private View createViewFromResource(int i, View view, ViewGroup viewGroup, int i2) {
        if (view == null) {
            view = this.mInflater.inflate(i2, viewGroup, false);
        }
        try {
            bindView(i, view);
        } catch (Exception e) {
            Log.d("LocalAdapter", ">>>" + e.getMessage());
        }
        return view;
    }

    @Override // android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        return createViewFromResource(i, view, viewGroup, this.mDropDownResource);
    }

    private void bindView(int i, View view) {
        Map<String, ?> map = this.mData.get(i);
        if (map == null) {
            return;
        }
        ViewBinder viewBinder = this.mViewBinder;
        String[] strArr = this.mFrom;
        int[] iArr = this.mTo;
        int length = iArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            View findViewById = view.findViewById(iArr[i2]);
            if (findViewById != null) {
                Object obj = map.get(strArr[i2]);
                String obj2 = obj == null ? "" : obj.toString();
                String str = obj2 != null ? obj2 : "";
                if (viewBinder != null ? viewBinder.setViewValue(findViewById, obj, str) : false) {
                    continue;
                } else if (findViewById instanceof Checkable) {
                    if (obj instanceof Boolean) {
                        ((Checkable) findViewById).setChecked(((Boolean) obj).booleanValue());
                    } else if (findViewById instanceof TextView) {
                        setViewText((TextView) findViewById, str);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(findViewById.getClass().getName());
                        sb.append(" should be bound to a Boolean, not a ");
                        sb.append(obj == null ? "<unknown type>" : obj.getClass());
                        throw new IllegalStateException(sb.toString());
                    }
                } else if (findViewById instanceof TextView) {
                    setViewText((TextView) findViewById, str);
                } else if (findViewById instanceof ImageView) {
                    if (obj instanceof Integer) {
                        ((ImageView) findViewById).setImageDrawable(this.mContext.getResources().getDrawable(((Integer) obj).intValue()));
                    } else if (obj instanceof Drawable) {
                        ((ImageView) findViewById).setImageDrawable((Drawable) obj);
                    } else {
                        try {
                            ((ImageView) findViewById).setImageBitmap((Bitmap) obj);
                        } catch (NumberFormatException unused) {
                            ((ImageView) findViewById).setImageURI(Uri.parse(str));
                        }
                    }
                } else if (findViewById instanceof RelativeLayout) {
                    if (obj instanceof Integer) {
                        findViewById.setBackgroundDrawable(this.mContext.getResources().getDrawable(((Integer) obj).intValue()));
                    }
                } else {
                    throw new IllegalStateException(findViewById.getClass().getName() + " is not a  view that can be bounds by this LocalAdapter");
                }
            }
        }
    }

    public void setViewText(TextView textView, String str) {
        textView.setText(str);
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new SimpleFilter();
        }
        return this.mFilter;
    }

    /* loaded from: classes.dex */
    private class SimpleFilter extends Filter {
        private SimpleFilter() {
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            Filter.FilterResults filterResults = new Filter.FilterResults();
            if (LocalAdapter.this.mUnfilteredData == null) {
                LocalAdapter localAdapter = LocalAdapter.this;
                localAdapter.mUnfilteredData = new ArrayList(localAdapter.mData);
            }
            if (charSequence == null || charSequence.length() == 0) {
                ArrayList arrayList = LocalAdapter.this.mUnfilteredData;
                filterResults.values = arrayList;
                filterResults.count = arrayList.size();
            } else {
                String lowerCase = charSequence.toString().toLowerCase();
                ArrayList arrayList2 = LocalAdapter.this.mUnfilteredData;
                int size = arrayList2.size();
                ArrayList arrayList3 = new ArrayList(size);
                for (int i = 0; i < size; i++) {
                    Map map = (Map) arrayList2.get(i);
                    if (map != null) {
                        int length = LocalAdapter.this.mTo.length;
                        for (int i2 = 0; i2 < length; i2++) {
                            String[] split = ((String) map.get(LocalAdapter.this.mFrom[i2])).split(" ");
                            int length2 = split.length;
                            int i3 = 0;
                            while (true) {
                                if (i3 >= length2) {
                                    break;
                                } else if (split[i3].toLowerCase().startsWith(lowerCase)) {
                                    arrayList3.add(map);
                                    break;
                                } else {
                                    i3++;
                                }
                            }
                        }
                    }
                }
                filterResults.values = arrayList3;
                filterResults.count = arrayList3.size();
            }
            return filterResults;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            LocalAdapter.this.mData = (List) filterResults.values;
            if (filterResults.count > 0) {
                LocalAdapter.this.notifyDataSetChanged();
            } else {
                LocalAdapter.this.notifyDataSetInvalidated();
            }
        }
    }
}
