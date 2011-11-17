package com.overseer;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.overseer.utils.BasicItemizedOverlay;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class Viewer extends MapActivity {
	MapView mMapView;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mMapView = (MapView)findViewById(R.id.main_map_view);
        
        List<Overlay> mapOverlays = mMapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        BasicItemizedOverlay itemizedoverlay = new BasicItemizedOverlay(drawable);
        
        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
        
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);
        
    }


	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}