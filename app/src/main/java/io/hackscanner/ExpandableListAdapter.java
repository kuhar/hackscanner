package io.hackscanner;

/**
 * Created by Darek on 2016-10-08.
 */


        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Random;

        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Color;
        import android.graphics.Typeface;
        import android.graphics.drawable.Drawable;
        import android.net.Uri;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseExpandableListAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import org.w3c.dom.Text;

        import static java.security.AccessController.getContext;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private static int updateNo = 0;

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    //add
    private List<Drawable> _imgid;
    private Map<String, Double> _lowestPrieces;
    private Map<String, String> _uriForFlights;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData, List<Drawable> imgid, Map<String, Double> lowestPrieces, Map<String, String> uriForFlights) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._lowestPrieces = lowestPrieces;
        this._uriForFlights = uriForFlights;
        //add
        this._imgid = imgid;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        final int gPosition = groupPosition;

        if(_uriForFlights.containsKey((String) getGroup(groupPosition))) {
            txtListChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_uriForFlights.get((String) getGroup(gPosition))));
                    _context.startActivity(browserIntent);
                }
            });
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
        if(this._imgid.size() > groupPosition) {
            imageView.setImageDrawable(this._imgid.get(groupPosition));
        }

        TextView priceLabel = (TextView) convertView.findViewById(R.id.rowPriceLabel);
        if(_lowestPrieces.containsKey(_listDataHeader.get(groupPosition))) {;
            updatePrice(priceLabel, (_lowestPrieces.get(_listDataHeader.get(groupPosition))));
        } else {
            priceLabel.setText("??? Eur");
            priceLabel.setTextColor(Color.rgb(204, 204, 204));
        }

        return convertView;
    }

    public void updatePrice(TextView tv, double price) {
        tv.setText(new Double(price).toString() + " Eur");
        if (price <= 100) {//34,139,34
            tv.setTextColor(Color.rgb(34, 139, 34));
        } else if (price <= 200) {
            tv.setTextColor(Color.rgb(244, 164, 96));
        } else {
            tv.setTextColor(Color.RED);
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}