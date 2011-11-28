package com.overseer;

import java.util.List;

import com.google.android.maps.MapView;
import com.overseer.models.ActivityPoint;
import com.overseer.models.Coordinate;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SingleEventViewer extends Activity{
	List<Coordinate> mCoordinates; //single or list?
	List<ActivityPoint> mActivities; //only in our scope of time?
	MapView mMapView; //a reference to the static mapview in Viewer
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_event_viewer);
        
        populateData();
        drawMap();
        drawActigraph();
    }
	
	public void populateData(){
		Bundle b = getIntent().getExtras();
		//mCoordinates = Coordinate.fromBundle(b);
	}
	
	public void drawMap(){
		mMapView = Viewer.materializeMap(this);
		LinearLayout mapLayout = (LinearLayout)findViewById(R.id.single_event_viewer_mapview);
		mapLayout.addView(mMapView);
	}
	
	public void drawActigraph(){
		
	}

}
