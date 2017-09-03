package illnino.com.shipment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.Random;

/**
 * Created by No'_Mercy on 12/13/2014.
 */
public class CircleOverlay extends Overlay {

    private final GeoPoint mCenter = new GeoPoint(0, 0);

    private final Point mMapCenter = new Point();

    private final Paint innerPaint;
    private final Paint borderPaint;

    public CircleOverlay(Context context) {
        super(context);
        innerPaint = new Paint();
        //innerPaint.setARGB(100, 0, 0, 65); // gray
        innerPaint.setARGB(24, 24, 24, 24);
        innerPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setARGB(255, 0, 0, 200);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1);
    }

    public void setCenter(double latitude, double longitude) {
        mCenter.setCoordsE6((int) (latitude * 1E6), (int) (longitude * 1E6));
    }

    @Override
    protected void draw(Canvas c, MapView mapView, boolean shadow) {
        MapView.Projection pj = mapView.getProjection();
        pj.toMapPixels(mCenter, mMapCenter);

        final float radius = pj.metersToEquatorPixels(1000);

        c.drawCircle(mMapCenter.x, mMapCenter.y, radius, innerPaint);
        c.drawCircle(mMapCenter.x, mMapCenter.y, radius, borderPaint);
    }

}