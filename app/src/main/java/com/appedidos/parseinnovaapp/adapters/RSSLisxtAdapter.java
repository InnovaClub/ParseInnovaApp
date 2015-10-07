package com.appedidos.parseinnovaapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.util.Linkify;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.appedidos.parseinnovaapp.ParseInnovaApp;
import com.appedidos.parseinnovaapp.R;
import com.appedidos.parseinnovaapp.Utils;
import com.einmalfel.earl.AtomEntry;
import com.einmalfel.earl.RSSItem;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jvargas on 10/7/15.
 */
public class RSSLisxtAdapter extends ArrayAdapter<RSSItem> {

    private ParseInnovaApp app;
    private Context context;
    private List<RSSItem> data;
    private int layoutId;
    private SparseBooleanArray collapsedContent;

    public RSSLisxtAdapter(Context context, List<RSSItem> data) {
        super(context, R.layout.item_face_news, data);
        this.context = context;
        this.data = data;
        this.layoutId = R.layout.item_face_news;
        this.collapsedContent = new SparseBooleanArray(data.size());
        this.app = (ParseInnovaApp) ((AppCompatActivity) context).getApplication();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutId, parent, false);

            holder = new ViewHolder();
            holder.tvDate = (TextView) row.findViewById(R.id.tv_date);

            holder.tvTitle = (TextView) row.findViewById(R.id.tv_title);
            holder.face = (Button) row.findViewById(R.id.buttonFace);
            holder.tvContent = (TextView) row.findViewById(R.id.expandable_text);
            holder.img = (ImageView) row.findViewById(R.id.external_image);
            holder.container = (FrameLayout) row.findViewById(R.id.container);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.tvDate.setText(Utils.dateToString(data.get(position).getPublicationDate()));


        holder.tvTitle.setText("Facebook "+position+" de "+data.size());

        Matcher m = Pattern.compile(" (?:src)=\"([^\"]+)").matcher(data.get(position).getDescription());
        if (m.find()) {
            holder.container.setVisibility(View.VISIBLE);
            Picasso.with(holder.img.getContext()).load(m.group(1)).into(holder.img);
        } else {
            holder.container.setVisibility(View.GONE);
        }


        String witouhtTags = stripHtml(data.get(position).getDescription()).trim();
        witouhtTags = witouhtTags.replaceAll("\uFFFC", "");
        if (!witouhtTags.isEmpty()){
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.tvContent.setText(witouhtTags.trim());
        } else {
            holder.tvContent.setVisibility(View.GONE);
        }

        Linkify.addLinks(holder.tvContent, Linkify.ALL);

        return row;
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    private class ViewHolder {
        TextView tvDate;
        FrameLayout container;
        TextView tvTitle;
        ImageView img;
        Button face;
        Button arrow;
        TextView tvContent;
    }
}
