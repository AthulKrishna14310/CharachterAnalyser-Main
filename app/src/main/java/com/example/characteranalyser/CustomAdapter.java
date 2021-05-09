package com.example.characteranalyser;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class CustomAdapter implements ListAdapter {
    ArrayList<String> arrayList;
    Context context;

    public CustomAdapter(Context context, ArrayList<String> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
    }
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String errorUrl = arrayList.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.error_image, null);
            ImageView imag = convertView.findViewById(R.id.imageViewError);
            Picasso.get()
                    .load(errorUrl)
                    .into(imag);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePopup imagePopup=new ImagePopup(context);
                    imagePopup.initiatePopupWithPicasso(errorUrl);
                    imagePopup.setFullScreen(false);
                    imagePopup.viewPopup();
                }
            });
        }
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
}