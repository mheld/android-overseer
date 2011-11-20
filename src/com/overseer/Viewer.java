package com.overseer;

import java.util.List;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.overseer.db.DbDoer;
import com.overseer.models.Coordinate;
import com.overseer.utils.BasicItemizedOverlay;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class Viewer extends MapActivity {
	MapView mMapView;
	List<Coordinate> mCoordinates;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ensureSampling();
        setupState();
        drawMap();
    }
    
    private void ensureSampling(){
    	Intent i = new Intent(this, SamplerScheduler.class);
    	this.sendBroadcast(i);
    }
    
    private void setupState(){
    	new DbDoer<Object>(this){

			@Override
			public Object perform() {
				mCoordinates = Coordinate.allCoordinates(db);
				return null;
			}
        	
        };
        
        mMapView = (MapView)findViewById(R.id.main_map_view);
    }
    
    private void drawMap(){
    	List<Overlay> mapOverlays = mMapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        
        BasicItemizedOverlay itemizedoverlay = new BasicItemizedOverlay(drawable);        
        for(Coordinate c : mCoordinates){
        	itemizedoverlay.addOverlay(new OverlayItem(c.toGeoPoint(), "", ""));
        }
        
        mapOverlays.add(itemizedoverlay);
    }


	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}