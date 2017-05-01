package ssbanerjee.passwordmanager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gilbert on 4/30/2017.
 */

public class passwordListAdapter extends BaseAdapter {

    private Context myContext;
    private List<passwordItem> list;

    public passwordListAdapter(Context myContext, List<passwordItem> mList) {
        this.myContext = myContext;
        this.list = mList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(myContext, R.layout.password_list_item, null);
        TextView vName = (TextView) v.findViewById(R.id.itemName);
        vName.setText(list.get(position).getName());

        final TextView vPassword = (TextView) v.findViewById(R.id.itempassword);
        vPassword.setText(list.get(position).getPassword());

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });


        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vPassword.getVisibility() == View.GONE){
                    vPassword.setVisibility(View.VISIBLE);
                } else if (vPassword.getVisibility() == View.VISIBLE){
                    vPassword.setVisibility(View.GONE);
                }

            }
        });

        return v;
    }
}
