package com.mobvoi.ticwear.gravitysensortest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends Activity{

    private WearableListView listView;
    private List<String> sensorList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                listView = (WearableListView) stub.findViewById(R.id.wearable_list);
                SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

                List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
                ArrayList<Sensor> copy=new ArrayList<Sensor>(sensors);
                Collections.sort(copy, new Comparator<Sensor>() {
                    @Override
                    public int compare(Sensor sensor1, Sensor sensor2) {
                        return sensor1.getType()-sensor2.getType();
                    }
                });
                sensorList=new ArrayList<String>();
                for(Sensor sensor:copy){
                    sensorList.add(sensor.getType()+"."+sensor.getName());
                }
                Adapter adapter=new Adapter(MainActivity.this,sensorList.toArray(new String[0]));

                listView.setAdapter(adapter);
                listView.setClickListener(new WearableListView.ClickListener() {
                    @Override
                    public void onClick(WearableListView.ViewHolder viewHolder) {
                        int pos = viewHolder.getPosition();
                        String s = sensorList.get(pos);
                        String[] arr = s.split(Pattern.quote("."), 2);
                        int sensorType = Integer.valueOf(arr[0]);
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        intent.putExtra(MainActivity2.EXTRA_SENSOR_TYPE, sensorType);
                        MainActivity.this.startActivity(intent);
                    }

                    @Override
                    public void onTopEmptyRegionClick() {

                    }
                });

            }
        });
    }

    private static final class Adapter extends WearableListView.Adapter {
        private String[] mDataset;
        private final Context mContext;
        private final LayoutInflater mInflater;

        // Provide a suitable constructor (depends on the kind of dataset)
        public Adapter(Context context, String[] dataset) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mDataset = dataset;
        }

        // Provide a reference to the type of views you're using
        public static class ItemViewHolder extends WearableListView.ViewHolder {
            private TextView textView;
            public ItemViewHolder(View itemView) {
                super(itemView);
                // find the text view within the custom item's layout
                textView = (TextView) itemView.findViewById(R.id.name);
            }
        }

        // Create new views for list items
        // (invoked by the WearableListView's layout manager)
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
            // Inflate our custom layout for list items
            return new ItemViewHolder(mInflater.inflate(R.layout.list_item, null));
        }

        // Replace the contents of a list item
        // Instead of creating new views, the list tries to recycle existing ones
        // (invoked by the WearableListView's layout manager)
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder,
                                     int position) {
            // retrieve the text view
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            TextView view = itemHolder.textView;
            // replace text contents
            view.setText(mDataset[position]);
            // replace list item's metadata
            holder.itemView.setTag(position);
        }

        // Return the size of your dataset
        // (invoked by the WearableListView's layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }

}


