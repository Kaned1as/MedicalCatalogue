package org.medic.SimLiKar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class PeopleAdapter extends CursorAdapter
{

    public PeopleAdapter(Context context, Cursor c)
    {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
    {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View newView = inflater.inflate(R.layout.man_list_item, viewGroup, false);
        TextView name = (TextView) newView.findViewById(R.id.title);
        name.setText(cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3));
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView name = (TextView) view.findViewById(R.id.title);
        name.setText(cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3));
    }

    @Override
    public long getItemId(int position)
    {
        getCursor().moveToPosition(position);
        return getCursor().getLong(0);
    }
}
