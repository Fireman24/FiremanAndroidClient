package kz.fireman.andreygolubkow.fireman;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by andreygolubkow on 17.11.2017.
 */

public class MapListAdapter extends ArrayAdapter<MapsModel> {

    private final Context context;
    private final MapsModel[] values;

    public MapListAdapter(@NonNull Context context, MapsModel[] values) {
        super(context, R.layout.maplistitem,values);
        this.context = context;
        this.values=values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.maplistitem, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.mapName);

        textView.setText(values[position].getName());

        return rowView;
    }
}
