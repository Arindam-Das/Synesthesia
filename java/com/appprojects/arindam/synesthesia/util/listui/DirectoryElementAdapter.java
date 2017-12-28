package com.appprojects.arindam.synesthesia.util.listui;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appprojects.arindam.synesthesia.R;

import java.util.List;

/**
 * @author Arindam Das
 * @version 24-10-2017.
 */

public class DirectoryElementAdapter extends ArrayAdapter<DirectoryElement> {

    public DirectoryElementAdapter(@NonNull Context context, @LayoutRes int resource,
                                   @NonNull List<DirectoryElement> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.directory_list_element, parent, false);
        }
        final DirectoryElement directoryElement = getItem(position);
        if(directoryElement == null)return convertView;

        TextView textView = convertView.findViewById(R.id.directoryName);
        textView.setText(directoryElement.getName());

        textView = convertView.findViewById(R.id.path_text);
        textView.setText(directoryElement.getPath());

        return convertView;
    }
}
