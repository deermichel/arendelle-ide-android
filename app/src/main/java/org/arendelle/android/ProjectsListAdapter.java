package org.arendelle.android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

/** custom list adapter for projects list */
public class ProjectsListAdapter extends ArrayAdapter<ProjectsListItem> {
    private Activity context;
    private int resId;
    private ProjectsListItem items[] = null;

    static class ViewHolder {
        public TextView text1;
        public ImageView preview;
    }

    public ProjectsListAdapter(Activity context, int resId, ProjectsListItem[] items) {
        super(context, resId, items);
        this.context = context;
        this.resId = resId;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        // reuse views
        if (row == null)  {

            LayoutInflater inflator = context.getLayoutInflater();
            row = inflator.inflate(R.layout.projects_listview_item, null);

            // configure view holder
            ViewHolder holder = new ViewHolder();
            holder.text1 = (TextView) row.findViewById(R.id.projects_listview_item_text1);
            holder.preview = (ImageView) row.findViewById(R.id.projects_listview_item_preview);
            row.setTag(holder);

        }

        // fill items
        ViewHolder holder = (ViewHolder) row.getTag();
        ProjectsListItem item = items[position];
        holder.text1.setText(item.name);
        holder.preview.setImageBitmap(item.preview);

        return row;
    }
}