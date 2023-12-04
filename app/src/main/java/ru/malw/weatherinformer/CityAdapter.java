package ru.malw.weatherinformer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends ArrayAdapter<JSONObject> {

    private int layoutResourceId;
    private List<JSONObject> data;
    private View.OnClickListener onClickListener;

    public CityAdapter(Context context, int resource, List<JSONObject> data, View.OnClickListener l) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.data = data;
        this.onClickListener = l;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);
        }
        JSONObject city = data.get(position);
        ((TextView)convertView.findViewById(R.id.name)).setText(city.optString("name"));
        convertView.setTag(city.optInt("id"));
        convertView.setOnClickListener(this.onClickListener);
        return convertView;
    }
}
