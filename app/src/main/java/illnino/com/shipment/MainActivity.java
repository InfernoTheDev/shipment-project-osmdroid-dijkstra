package illnino.com.shipment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends ActionBarActivity {
    MapView mapView;
    TextView textView;
    GeoPoint center = new GeoPoint(16.18508998688923, 103.30267749477109); //16.184973, 103.302658
    ArrayList<GeoPoint> wayPoints = new ArrayList<GeoPoint>();
    ArrayList<OverlayItem> OverlayItemArray;
    ArrayList<OverlayItem> overlayItemArray2;

    ItemizedOverlayWithFocus<OverlayItem> overlayDeItems;
    ItemizedOverlayWithFocus<OverlayItem> overlayDeItems2;

    PathOverlay myPath, myPath2;
    RouteMapOverlay routeMapOverlay, routeMapOverlay2;
    Button routButton;
    Button clearButton;
    Button gpsButton;
    CircleOverlay circleOverlay;
    Button trackButton;
    MultiAutoCompleteTextView multiAutoCompleteTextView;
    ArrayList<String> wptName;
    ArrayList<GeoPoint> geoMark;
    String getRef[];
    String autoStr[];
    LocationManager locationManager;


    GeoPoint locGeoPoint;
    private GeoPoint pGPS;
    private GeoPoint pNW;
    ArrayList<String> getRefNodeList = new ArrayList<String>();
    ArrayList<Vertex> vertexArrayList;
    ArrayList<Position> positionArrayList;
    ArrayList<String> inputPosition;
    ArrayList<Wpt> wptArrayList;
    double d = 0, droutoverlay2 = 0, sum = 0;

    GeoPoint geobuff;
    ArrayList<Double> dbuff;
    ArrayList<Double> we = new ArrayList<Double>();
    boolean chktrack = false;
    boolean chkgps = false;
    boolean chkcal = false;
    int buffidxplodnode;
    int chkgps2 = 0;
    int chktrack2 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);
        textView = (TextView) findViewById(R.id.tv);
        routButton = (Button) findViewById(R.id.calB);
        clearButton = (Button) findViewById(R.id.clrB);
        gpsButton = (Button) findViewById(R.id.gpsB);
        trackButton = (Button) findViewById(R.id.TrackB);
        textView.setText("");


        final Canvas canvas = new Canvas();


        readPlace();

        for (int i = 0; i < autoStr.length; i++) {
            System.out.println("XXXXXXXXXX = " + autoStr[i]);
        }
        //String autoStr [] = {"ผดุงนารี","สารคามพิทยาคม","bangalore","mumbai","pune"};

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, autoStr);

        multiAutoCompleteTextView = (MultiAutoCompleteTextView) findViewById(R.id.actv);
        multiAutoCompleteTextView.setAdapter(myAdapter);
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        mapView.getController().setZoom(13); //1
        mapView.getController().setCenter(center); //2
        mapView.setBuiltInZoomControls(true);

        mapView.setUseDataConnection(true);


        //myLocationNewOverlay.enableMyLocation();


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // getLastKnownLocation from GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            createGpsDisabledAlert();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, myLocationListener);

        }

        if (lastLocation != null) {
            updateLoc(lastLocation);
        }


////////////////// button ///////////////////////////////////////////

        routButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                geoMark = new ArrayList<GeoPoint>();
                myPath = new PathOverlay(Color.RED, getBaseContext());
                myPath.setAlpha(1000);
                routeMapOverlay = new RouteMapOverlay();
                routeMapOverlay2 = new RouteMapOverlay();


                myPath2 = new PathOverlay(Color.BLUE, getBaseContext());
                myPath2.setAlpha(1000);
                OverlayItemArray = new ArrayList<OverlayItem>();
                overlayItemArray2 = new ArrayList<OverlayItem>();
                wptName = new ArrayList<String>();
                vertexArrayList = new ArrayList<Vertex>();
                positionArrayList = new ArrayList<Position>();
                wptArrayList = new ArrayList<Wpt>();
                inputPosition = new ArrayList<String>();
                int chk = 0;


                String tmlName = String.valueOf(multiAutoCompleteTextView.getText());
                System.out.println("************ tmlName ; " + tmlName);
                String trimSpace[] = tmlName.split(" ");
                String buff = "";

                for (int i = 0; i < trimSpace.length; i++) {
                    buff += trimSpace[i];
                    System.out.println("HHHHHHHHHH = " + buff);
                }
                String tmlNameTotal[] = buff.split(",");

                /*for (int i = 0; i < tmlNameTotal.length; i++) {
                    System.out.println("HHHHHHHHHH = "+tmlNameTotal[i]);
                }*/


                textView.setText("");

                boolean chktmlnametotal = false;

                for (int i = 0; i < tmlNameTotal.length; i++) {
                    if (tmlNameTotal[i] != "") {
                        System.out.println(tmlNameTotal[i] + ".....");
                        chktmlnametotal = true;
                    } else {
                        System.out.println("tmlnametotal is null");
                        chktmlnametotal = false;
                    }

                }
                int tc = 0;
                if (tmlNameTotal.length > 1) {
                    for (int i = 0; i < tmlNameTotal.length - 1; i++) {
                        if (tmlNameTotal[i].equalsIgnoreCase(tmlNameTotal[i + 1])) {
                            tc += 1;
                        }
                    }
                }
                if (tmlNameTotal.length == 1) tc += 1;
                if (tc != 0) chktmlnametotal = false;

                if (chktmlnametotal == false) {
                    warning();
                    multiAutoCompleteTextView.setText("");
                    //Toast.makeText(getBaseContext(), "Please input Place !!!", Toast.LENGTH_LONG).show();

                } else {

                    mapView.invalidate();

                    readFileGPX(); // readfile

                    double aa = locGeoPoint.getLatitudeE6();
                    double bb = locGeoPoint.getLongitudeE6();
                    double zz[];

                    for (int i = 0; i < 10; i++) {
                        aa += 0.0001;
                        for (int j = 0; j < positionArrayList.size(); j++) {
                            if (aa == positionArrayList.get(j).geoPoint.getLatitudeE6()) {

                            }

                        }
                    }

                    for (int i = 0; i < tmlNameTotal.length; i++) {
                        for (int j = 0; j < wptArrayList.size(); j++) {
                            if (tmlNameTotal[i].equalsIgnoreCase(wptArrayList.get(j).name)) {
                                System.out.println("************ tmlNametotal ; " + tmlNameTotal[i] + " chk " + chk + " wpt name " + wptArrayList.get(j).name);
                                System.out.println("************ tmlNametotal length " + tmlNameTotal.length);
                                chk += 1;
                            } else {
                                chk += 0;
                            }

                        }
                    }
                    if (chk == tmlNameTotal.length) {
                        clearButton.setVisibility(View.VISIBLE);
                        chkcal = true;

                            /*for (int i = 0; i < vertexArrayList.size(); i++) {
                                System.out.println("vertex[" + i + "] = " + vertexArrayList.get(i));

                            }*/
                        getRef = null;
                        double totalD = 0;
                        for (int i = 0; i < getRefNodeList.size(); i++) {

                            System.out.println("getRefNodeList[" + i + "] = " + getRefNodeList.get(i));
                            getRef = getRefNodeList.get(i).split(",");
                                /*if (!getRefNodeList.get(i).toCharArray().equals(",")) {
                                    getRef = getRefNodeList.get(i).toCharArray();
                                    for (int j=0;j<getRef.length;j++) {
                                        System.out.println("getref [" + j + "] " + getRef[j]);
                                    }
                                }*/

                            int length = 0;

                            //System.out.println("getref [] " + getRef.length +"  *** "+getRef[0] +" ///sdfsfs/// "+vertexArrayList.get(i).toString() );
                            if (getRef[0].equalsIgnoreCase(vertexArrayList.get(i).toString())) {//*******
                                if (getRef.length != 0) {
                                    length = getRef.length - 1;
                                    vertexArrayList.get(i).adjacencies = new Edge[getRef.length - 1];
                                } else {
                                    length = getRef.length;
                                    vertexArrayList.get(i).adjacencies = new Edge[getRef.length];
                                }

                                int idx = 0;

                                for (int j = 0; j < getRef.length; j++) {
                                    for (int k = 0; k < vertexArrayList.size(); k++) {
                                        if (getRef[j].equalsIgnoreCase(vertexArrayList.get(k).toString())) {

                                            GeoPoint aPosition = positionArrayList.get(i).geoPoint;
                                            GeoPoint bPosition = positionArrayList.get(k).geoPoint;


                                            /////////// cal path ////////////////////////////////////////////////////////////////////////
                                            double d2r = Math.PI / 180;

                                            try {
                                                double dlong = (bPosition.getLongitudeE6() - aPosition.getLongitudeE6()) * d2r;
                                                double dlat = (bPosition.getLatitudeE6() - aPosition.getLatitudeE6()) * d2r;
                                                double a =
                                                        Math.pow(Math.sin(dlat / 2.0), 2)
                                                                + Math.cos(aPosition.getLatitudeE6() * d2r)
                                                                * Math.cos(bPosition.getLatitudeE6() * d2r)
                                                                * Math.pow(Math.sin(dlong / 2.0), 2);
                                                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                                d = 6367 * c;
                                                totalD += d;

                                                //return d;


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            // textView.setText("ระยะทาง จาก [ " + wptName.get(0) + " ] ไป [" + wptName.get(1) + "] = " + totalD + " กิโลเมตร");
///////////////////////////////////////////////////////////////////////////////

                                            if (!vertexArrayList.get(i).equals(vertexArrayList.get(k))) { //if not itself
                                                vertexArrayList.get(i).adjacencies[idx] = new Edge(vertexArrayList.get(k), d);//add Edge **
                                                idx++;

                                                System.out.println("vertext " + vertexArrayList.get(i) +
                                                        " // edge = " + vertexArrayList.get(k));

                                                System.out.println("ระยะทาง จาก [ " + positionArrayList.get(i).vertex.toString() + " ] ไป ["
                                                        + positionArrayList.get(k).vertex.toString() + "] = " + d + " กิโลเมตร");

                                            }
                                            break;

                                        }
                                    }
                                }
                            }

                        }
                        System.out.println("ระยะทาง รวม = " + totalD + " กิโลเมตร");


                        Dijkstra dijkstra = new Dijkstra();
                        String pln = "";
                        String pln2 = "";


                        ArrayList<String> plotnode_name = new ArrayList<String>();

                        for (int i = 0; i < tmlNameTotal.length; i++) {
                            plotnode_name.add(tmlNameTotal[i]);
                        }

                        ArrayList<Integer> plotnode = new ArrayList<Integer>();

                        for (int i = 0; i < plotnode_name.size(); i++) {
                            for (int j = 0; j < wptArrayList.size(); j++) {

                                if (plotnode_name.get(i).equalsIgnoreCase(wptArrayList.get(j).name)) {

                                    if (i == 0) {
                                        pln += wptArrayList.get(j).name.toString() + " - ";
                                        pln2 += wptArrayList.get(j).name.toString();
                                    } else {
                                        pln += wptArrayList.get(j).name.toString() + " - "; //getname to show on textview
                                    }


                                    for (int k = 0; k < positionArrayList.size(); k++) {
                                        if (wptArrayList.get(j).geoPoint.equals(positionArrayList.get(k).geoPoint)) {
                                            plotnode.add(k); //s=j;
                                        }
                                    }
                                }
                            }
                        }
                        int first = plotnode.get(0);
                        buffidxplodnode = plotnode.get(0);
                        plotnode.add(first);


                        for (int i = 0; i < plotnode.size(); i++) {
                            System.out.println("********** plotnode" + plotnode.get(i));

                        }


                        //////////////// call Dijkstra.computePaths /////////////////////////

                        for (int i = 0; i < plotnode.size() - 1; i++) {

                            dijkstra.computePaths(positionArrayList.get(plotnode.get(i)).vertex); //source vertex
                            System.out.println(">>>>>>>>>>> Plotnode i & i+1 = " + plotnode.get(i) + " and " + plotnode.get(i + 1));

                            for (Vertex v : vertexArrayList) {
                                if (v == vertexArrayList.get(plotnode.get(i + 1))) {
                                    System.out.println("ระยะทางจาก " + vertexArrayList.get(plotnode.get(i)) + " ไป " + v + ": " + v.minDistance + " กิโลเมตร");
                                    List<Vertex> dijkstraShortestPathTopath = dijkstra.getShortestPathTo(v);
                                    System.out.println("เส้นทาง : " + dijkstraShortestPathTopath);


                                    sum += v.minDistance; //ระยะทางรวม

                                    //textView.setText("ระยะทางจาก " + startPoint + " ไป " + endPoint + ": " + v.minDistance + " กิโลเมตร");

                                    for (int j = 0; j < dijkstraShortestPathTopath.size(); j++) {
                                        for (int k = 0; k < positionArrayList.size(); k++) {

                                            if (dijkstraShortestPathTopath.get(j) == positionArrayList.get(k).vertex) {
                                                //myPath.addPoint(positionArrayList.get(k).geoPoint);
                                                routeMapOverlay.addPoint(positionArrayList.get(k).geoPoint);
                                            }

                                        }
                                    }
                                }
                            }

                            for (Vertex v : vertexArrayList) {
                                v.clear();
                            }
                        }


                        /*for(int i = 0; i < positionArrayList.size(); i++){
                            System.out.println("positionArraylist ["+i+"] = "+positionArrayList.get(i).vertex.toString()
                            +" loc = "+positionArrayList.get(i).geoPoint.toString() );
                        }
                        for(int i = 0; i < wptArrayList.size(); i++){
                            System.out.println("WptArraylist ["+i+"] = "+wptArrayList.get(i).name
                                    +" loc = "+wptArrayList.get(i).geoPoint.toString() );
                        }*/

                        for (int i = 0; i < plotnode.size() - 1; i++) {
                            OverlayItemArray.add(new OverlayItem(plotnode_name.get(i).toString()
                                    , plotnode_name.get(i).toString()
                                    , positionArrayList.get(plotnode.get(i)).geoPoint));
                        }


                        overlayDeItems = new ItemizedOverlayWithFocus<OverlayItem>(getBaseContext(), OverlayItemArray, null);

                        mapView.getController().setCenter(OverlayItemArray.get(0).getPoint());

                        //}

                        /*for (int i=0;i<wptName.size();i++){
                            //System.out.println("wptName["+i+"] = "+wptName.get(i));
                            System.out.println("geoMark["+i+"] = "+geoMark.get(i));
                        }*/

                         /*if (chkcal == true && chkgps == true){
                            dbuff = new ArrayList<Double>();
                             double bufff = 0;
                            int ii = 0;
                            for(int i = 0; i < OverlayItemArray.size(); i++) {
                                dbuff.add(calpath(locGeoPoint, OverlayItemArray.get(i).getPoint()));

                                if (i==0) {
                                    bufff = dbuff.get(0);
                                    System.out.println("bufffffffffff i=0 = "+bufff);
                                }else{
                                    if (dbuff.get(i) <= bufff) {
                                        bufff = dbuff.get(i);
                                        geobuff = OverlayItemArray.get(i).getPoint();
                                        ii=i;
                                        System.out.println("bufffffffffff else = " + bufff+" : "+geobuff);
                                        droutoverlay2 = dbuff.get(i);
                                    }else{
                                        bufff = bufff;
                                        geobuff = OverlayItemArray.get(ii).getPoint();
                                        System.out.println("bufffffffffff else if = " + bufff+" : "+geobuff);
                                    }
                                }

                            }*/
                        double bufff = 0;
                        int ii = 0;
                        dbuff = new ArrayList<Double>();
                        if (chkcal == true && chkgps == true) {


                            for (int i = 0; i < positionArrayList.size(); i++) {
                                dbuff.add(calpath(locGeoPoint, positionArrayList.get(i).geoPoint));

                                if (i == 0) {
                                    bufff = dbuff.get(0);
                                    System.out.println("bufffffffffff i=0 = " + bufff);
                                } else {
                                    if (dbuff.get(i) <= bufff) {
                                        bufff = dbuff.get(i);
                                        geobuff = positionArrayList.get(ii).geoPoint;
                                        ii = i;
                                        System.out.println("bufffffffffff else = " + bufff + " : " + geobuff);
                                        droutoverlay2 = dbuff.get(i);
                                    } else {
                                        bufff = bufff;
                                        geobuff = positionArrayList.get(ii).geoPoint;
                                        System.out.println("bufffffffffff else if = " + bufff + " : " + geobuff);
                                    }
                                }

                            }


                            routeMapOverlay2.addPoint(locGeoPoint);
                            routeMapOverlay2.addPoint(geobuff);
//////////////// call Dijkstra.computePaths  current to first place/////////////////////////


                            dijkstra.computePaths(positionArrayList.get(ii).vertex); //source vertex

                            for (Vertex v : vertexArrayList) {
                                if (v == vertexArrayList.get(plotnode.get(0))) {
                                    System.out.println("222222ระยะทางจาก " + positionArrayList.get(ii).vertex + " ไป " + v + ": " + v.minDistance + " กิโลเมตร");
                                    List<Vertex> dijkstraShortestPathTopath = dijkstra.getShortestPathTo(v);
                                    System.out.println("222222เส้นทาง : " + dijkstraShortestPathTopath);


                                    sum += v.minDistance; //ระยะทางรวม

                                    //textView.setText("ระยะทางจาก " + startPoint + " ไป " + endPoint + ": " + v.minDistance + " กิโลเมตร");

                                    for (int j = 0; j < dijkstraShortestPathTopath.size(); j++) {
                                        for (int k = 0; k < positionArrayList.size(); k++) {

                                            if (dijkstraShortestPathTopath.get(j) == positionArrayList.get(k).vertex) {
                                                //myPath.addPoint(positionArrayList.get(k).geoPoint);
                                                routeMapOverlay2.addPoint(positionArrayList.get(k).geoPoint);
                                            }

                                        }
                                    }
                                }
                            }

                            for (Vertex v : vertexArrayList) {
                                v.clear();
                            }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            for (int i = 0; i < dbuff.size(); i++) {
                                System.out.println("dbuff [" + i + "] : " + dbuff.get(i));
                            }
                        } else {
                            if (routeMapOverlay2 != null) {
                                routeMapOverlay2.points.clear();
                                mapView.getOverlays().remove(routeMapOverlay2);
                            }
                            mapView.invalidate();
                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++no");
                        }

///////////////////////////////////////////////////////////////////////////////
                        double z = sum + droutoverlay2;

                        textView.setText("เส้นทาง : " + pln + "" + pln2 +
                                " \n______________________________\nระยะทางรวม " + ((int) z) + " กิโลเมตร");
                        textView.getScrollBarStyle();
                        getRef = null;
                        //textView.setText(info);


                        //mapView.getOverlays().add(myPath);
                        mapView.getOverlays().add(routeMapOverlay);
                        mapView.getOverlays().add(routeMapOverlay2);

                        mapView.getOverlays().add(myPath2);
                        mapView.getOverlays().add(overlayDeItems);
                        mapView.invalidate();

                        multiAutoCompleteTextView.setText("");

                    } else {
                        //Toast.makeText(getBaseContext(), "Input wrong !!!", Toast.LENGTH_LONG).show();
                        System.out.println("chk = " + chk);
                        getRef = null;
                        chk = 0;
                        System.out.println("and set chk = " + chk);
                        multiAutoCompleteTextView.setText("");

                        if (getRefNodeList != null) {
                            getRefNodeList.clear();
                            System.out.println("ZZZZZZZZZZZZZZZ getRefNodeList.clear();");
                        }

                        warning();


                    }
                }
            }


        });////// end Route Button
        ////////////////////////// GPS Button ////////////////////////////////
        gpsButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                chkgps2 += 1;

                if (chkgps2 == 1) {

                    chkgps = true;
                    Toast.makeText(getBaseContext(), "GPS is ON !!", Toast.LENGTH_LONG).show();

                    mapView.getOverlays().remove(overlayDeItems2);
                    mapView.invalidate();

                    mapView.getController().setCenter(locGeoPoint);
                    mapView.invalidate();

                    circleOverlay = new CircleOverlay(getBaseContext());
                    circleOverlay.setCenter(locGeoPoint.getLatitudeE6(), locGeoPoint.getLongitudeE6());
                    circleOverlay.draw(canvas, mapView, true);

                    overlayItemArray2 = new ArrayList<OverlayItem>();

                    overlayItemArray2.add(0, new OverlayItem("Me", "Me", locGeoPoint));

                    Drawable newMarker = getResources().getDrawable(R.drawable.location_48);
                    overlayItemArray2.get(0).setMarker(newMarker);

                    overlayDeItems2 = new ItemizedOverlayWithFocus<OverlayItem>(getBaseContext(), overlayItemArray2, null);
                    dbuff = new ArrayList<Double>();
                    Dijkstra dijkstra = new Dijkstra();
                    double bufff = 0;
                    int ii = 0;

                    if (chkcal == true && chkgps == true) {

                        for (int i = 0; i < positionArrayList.size(); i++) {
                            dbuff.add(calpath(locGeoPoint, positionArrayList.get(i).geoPoint));

                            if (i == 0) {
                                bufff = dbuff.get(0);
                                System.out.println("bufffffffffff i=0 = " + bufff);
                            } else {
                                if (dbuff.get(i) <= bufff) {
                                    bufff = dbuff.get(i);
                                    geobuff = positionArrayList.get(ii).geoPoint;
                                    ii = i;
                                    System.out.println("bufffffffffff else = " + bufff + " : " + geobuff);
                                    droutoverlay2 = dbuff.get(i);
                                } else {
                                    bufff = bufff;
                                    geobuff = positionArrayList.get(ii).geoPoint;
                                    System.out.println("bufffffffffff else if = " + bufff + " : " + geobuff);
                                }
                            }

                        }

                        routeMapOverlay2.addPoint(locGeoPoint);
                        routeMapOverlay2.addPoint(geobuff);

//////////////// call Dijkstra.computePaths  current to first place/////////////////////////


                        dijkstra.computePaths(positionArrayList.get(ii).vertex); //source vertex

                        for (Vertex v : vertexArrayList) {
                            if (v == vertexArrayList.get(buffidxplodnode)) {
                                System.out.println("222222ระยะทางจาก " + positionArrayList.get(ii).vertex + " ไป " + v + ": " + v.minDistance + " กิโลเมตร");
                                List<Vertex> dijkstraShortestPathTopath = dijkstra.getShortestPathTo(v);
                                System.out.println("222222เส้นทาง : " + dijkstraShortestPathTopath);


                                sum += v.minDistance; //ระยะทางรวม

                                //textView.setText("ระยะทางจาก " + startPoint + " ไป " + endPoint + ": " + v.minDistance + " กิโลเมตร");

                                for (int j = 0; j < dijkstraShortestPathTopath.size(); j++) {
                                    for (int k = 0; k < positionArrayList.size(); k++) {

                                        if (dijkstraShortestPathTopath.get(j) == positionArrayList.get(k).vertex) {
                                            //myPath.addPoint(positionArrayList.get(k).geoPoint);
                                            routeMapOverlay2.addPoint(positionArrayList.get(k).geoPoint);
                                        }

                                    }
                                }
                            }
                        }

                        for (Vertex v : vertexArrayList) {
                            v.clear();
                        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                        mapView.getOverlays().add(routeMapOverlay2);


                    } else {
                        if (routeMapOverlay2 != null)
                            routeMapOverlay2.points.clear();
                        mapView.getOverlays().remove(routeMapOverlay2);
                        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++no");
                    }

                    mapView.getOverlays().add(overlayDeItems2);
                    mapView.getOverlays().add(circleOverlay);

                    mapView.invalidate();


                } else if (chkgps2 == 2) {
                    chkgps = false;
                    chkgps2 = 0;
                    Toast.makeText(getBaseContext(), "GPS is OFF !!", Toast.LENGTH_LONG).show();

                    mapView.invalidate();
                    mapView.getOverlays().remove(overlayDeItems2);
                    mapView.getOverlays().remove(circleOverlay);
                    routeMapOverlay2.points.clear();
                    mapView.getOverlays().remove(routeMapOverlay2);
                    mapView.invalidate();
                }


            }
        });
        ////////////////////////// END GPS Button /////////////////////////////
        ////////////////////////// Track Button /////////////////////////////
        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chktrack2 += 1;
                if (chktrack2 == 1) {
                    chktrack = true;
                    Toast.makeText(getBaseContext(), "Tracking is ON !!", Toast.LENGTH_LONG).show();


                } else if (chktrack2 == 2) {
                    chktrack = false;

                    Toast.makeText(getBaseContext(), "Tracking is OFF !!", Toast.LENGTH_LONG).show();
                    chktrack2 = 0;

                    mapView.getOverlays().remove(overlayDeItems2);
                    mapView.invalidate();
                }
                //mapView.getController().animateTo(locGeoPoint);
                mapView.invalidate();
            }
        });
        ////////////////////////// END Track Button /////////////////////////////
        ////////////////////////// Clear Button /////////////////////////////
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chkgps = false;
                chkcal = false;
                if (routeMapOverlay != null) {
                    mapView.getOverlays().remove(routeMapOverlay);
                    System.out.println("ZZZZZZZZZZZZZZZ routeMapOverlay.clearPath();");
                }
                if (routeMapOverlay2 != null) {
                    routeMapOverlay2.points.clear();
                    mapView.getOverlays().remove(routeMapOverlay2);
                    System.out.println("ZZZZZZZZZZZZZZZ routeMapOverlay.clearPath();");
                }
                if (myPath2 != null) {
                    myPath2.clearPath();
                    mapView.getOverlays().remove(myPath2);
                    System.out.println("ZZZZZZZZZZZZZZZ myPath2.clearPath();");
                }
                if (getRefNodeList != null) {
                    getRefNodeList.clear();
                    System.out.println("ZZZZZZZZZZZZZZZ getRefNodeList.clear();");
                }
                if (vertexArrayList != null) {
                    vertexArrayList.clear();
                    System.out.println("ZZZZZZZZZZZZZZZ vertexArrayList.clear();");
                }
                if (positionArrayList != null) {
                    positionArrayList.clear();
                    System.out.println("ZZZZZZZZZZZZZZZ positionArrayList.clear();");
                }/*if (overlayItemArray2 != null) {
                overlayItemArray2.clear();//*
                System.out.println("ZZZZZZZZZZZZZZZ overlayItemArray2.clear();");
            }*/
                if (OverlayItemArray != null) {
                    OverlayItemArray.clear();//*
                    System.out.println("ZZZZZZZZZZZZZZZ OverlayItemArray.clear();");
                }
                mapView.getOverlays().remove(overlayDeItems);
                clearButton.setVisibility(View.GONE);
                textView.setText("");
                mapView.invalidate();

            }
        });
        //////////////////////// End Clear Button /////////////////
        /////////////////////////////////////////////////////////////////////
        mapView.invalidate();
    }

    private void createGpsDisabledAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(
                        "คุณยังไม่ได้เปิดใช้งาน GPS ต้องการเปิด GPS หรือไม่?")
                .setCancelable(false).setPositiveButton("เปิด GPS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showGpsOptions();
                    }
                });
        builder.setNegativeButton("ไม่เปิด",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showGpsOptions() {
        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }

    public void warning() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("กรอกข้อมูลไม่ถูกต้อง");

        // set dialog message
        alertDialogBuilder
                .setMessage("กรุณากรอกข้อมูลใหม่อีกครั้ง ตัวอย่างเช่น \n"
                        + "สถานที่1, สถานที่2, สถานที่3")
                .setCancelable(false)
                .setNegativeButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing

                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

/////////// decode GPX /////////////////////////////////

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void readPlace() {
        try {

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/Download/graphnode.gpx");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodelist_wpt = doc.getElementsByTagName("wpt");//wpt, rte>rtept ,trk>trkseg>trkpt

            autoStr = new String[nodelist_wpt.getLength()];

            for (int i = 0; i < nodelist_wpt.getLength(); i++) {

                Node node_wpt = nodelist_wpt.item(i);

                NamedNodeMap attributes = node_wpt.getAttributes();

                String wptname = attributes.getNamedItem("name").getTextContent();

                autoStr[i] = wptname;

            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void readFileGPX() {
        try {

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/Download/graphnode.gpx");
            //textView.setText("\n 1111111");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            System.out.println("Root element " + doc.getDocumentElement().getNodeName());

            NodeList nodelist_wpt = doc.getElementsByTagName("wpt");//wpt, rte>rtept ,trk>trkseg>trkpt

            for (int i = 0; i < nodelist_wpt.getLength(); i++) {

                Node node_wpt = nodelist_wpt.item(i);
                //System.out.println("wpt = " + i + " : " + node_wpt.getNodeName());

                NamedNodeMap attributes = node_wpt.getAttributes();

                String wptname = attributes.getNamedItem("name").getTextContent();


                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                Double newLatitude_double = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                Double newLongitude_double = Double.parseDouble(newLongitude);

                String newLocationName = newLatitude + ":" + newLongitude;

                Location newLocation = new Location(newLocationName);
                newLocation.setLatitude(newLatitude_double);
                newLocation.setLongitude(newLongitude_double);


                wptArrayList.add(new Wpt(new GeoPoint(newLocation), wptname));//wpt place , lat lon


            }

            NodeList nodelist_trkpt = doc.getElementsByTagName("trkpt");//wpt, rte>rtept ,trk>trkseg>trkpt


            int index = 0;


            for (int i = 0; i < nodelist_trkpt.getLength(); i++) {

                Node node_trkpt = nodelist_trkpt.item(i);
                System.out.println("**************===== node_trkpt.item[" + i + "].getNodeName = " + node_trkpt.getNodeName());

                if (node_trkpt.getNodeName() != "#text") {

                    NamedNodeMap attributes = node_trkpt.getAttributes();

                    String newLatitude = attributes.getNamedItem("lat").getTextContent();
                    Double newLatitude_double = Double.parseDouble(newLatitude);

                    String newLongitude = attributes.getNamedItem("lon").getTextContent();
                    Double newLongitude_double = Double.parseDouble(newLongitude);

                    String getName = attributes.getNamedItem("id").getTextContent();

                    String getRef[] = attributes.getNamedItem("ref").getTextContent().split(",");

                    vertexArrayList.add(new Vertex(getName));


                    System.out.println("id = " + vertexArrayList.get(index) + " ref = " + attributes.getNamedItem("ref").getTextContent());
                    for (int j = 0; j < getRef.length; j++) {
                        System.out.println("getRef[" + j + "] = " + getRef[j]);
                    }
                    getRefNodeList.add(vertexArrayList.get(index).toString() + "," + attributes.getNamedItem("ref").getTextContent());
                    String newLocationName = newLatitude + ":" + newLongitude;

                    Location newLocation = new Location(newLocationName);
                    newLocation.setLatitude(newLatitude_double);
                    newLocation.setLongitude(newLongitude_double);

                    //GpxNode newGpxNode = new GpxNode(newLocation, null, null);
                    //list.add(newGpxNode);

                    positionArrayList.add(new Position(new GeoPoint(newLocation), vertexArrayList.get(index))); // geopoint, vertex name
                    System.out.println("pooooooooooooooo" + positionArrayList.get(i).vertex.name);
                    //wayPoints.add( new GeoPoint(newLocation)); //i=index
                    //myPath.addPoint(wayPoints.get(i));
                    index += 1;

                }
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    ////////////////////////////////////
    public class Dijkstra {
        public void computePaths(Vertex source) {
            source.minDistance = 0.;
            PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
            vertexQueue.add(source);

            while (!vertexQueue.isEmpty()) {
                Vertex u = vertexQueue.poll();

                // Visit each edge exiting u
                for (Edge e : u.adjacencies) {
                    Vertex v = e.target;
                    double weight = e.weight;
                    double distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = distanceThroughU;
                        v.previous = u;
                        vertexQueue.add(v);
                    }
                }
            }
        }

        public List<Vertex> getShortestPathTo(Vertex target) {
            List<Vertex> path = new ArrayList<Vertex>();
            for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
                path.add(vertex);
            Collections.reverse(path);
            return path;
        }

    }

    //////////////////////////////////
    public double calpath(GeoPoint gA, GeoPoint gB) {
        ////////// cal path ////////////////////////////////////////////////////////////////////////
        double dis;
        double d2r = Math.PI / 180;


        try {

            double dlong = (gB.getLongitudeE6() - gA.getLongitudeE6()) * d2r;
            double dlat = (gB.getLatitudeE6() - gA.getLatitudeE6()) * d2r;
            double a =
                    Math.pow(Math.sin(dlat / 2.0), 2)
                            + Math.cos(gA.getLatitudeE6() * d2r)
                            * Math.cos(gB.getLatitudeE6() * d2r)
                            * Math.pow(Math.sin(dlong / 2.0), 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            dis = 6367 * c;

            return dis;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return d2r;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        locationManager.removeUpdates(myLocationListener);

    }

    private void updateLoc(Location loc){
        locGeoPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());

        System.out.println("************ loc = " + loc.getLatitude()+" : "+loc.getLongitude());
        System.out.println("************ locGeo = " + locGeoPoint);


        if (chktrack == true) {
            mapView.getController().animateTo(locGeoPoint);
            mapView.invalidate();
            mapView.getOverlays().remove(overlayDeItems2);
            mapView.invalidate();

            overlayItemArray2 = new ArrayList<OverlayItem>();

            overlayItemArray2.add(0, new OverlayItem("Me", "Me", locGeoPoint));

            Drawable newMarker = getResources().getDrawable(R.drawable.ic_menu_mylocation);
            overlayItemArray2.get(0).setMarker(newMarker);

            overlayDeItems2 = new ItemizedOverlayWithFocus<OverlayItem>(getBaseContext(), overlayItemArray2, null);


            mapView.getOverlays().add(overlayDeItems2);
            mapView.invalidate();
        }

        mapView.invalidate();
    }


    private LocationListener myLocationListener = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            updateLoc(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    };
    public class RouteMapOverlay extends Overlay {

        public ArrayList<GeoPoint> points;

        public RouteMapOverlay(){
            super(getBaseContext());
            points  = new ArrayList<GeoPoint>();
        }

        public void addPoint(GeoPoint point){
            points.add(point);
        }
        public void draw(Canvas canvas, MapView mapv, boolean shadow){


            Paint mPaint = new Paint();
            mPaint.setDither(true);
            mPaint.setColor(Color.BLUE);
            mPaint.setAlpha(19000);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(20);

            Path path = new Path();
            MapView.Projection projection = mapv.getProjection();

            for (int i=0; i<points.size()-1; i++){
                Point p1 = new Point();
                Point p2 = new Point();

                projection.toPixels((GeoPoint)points.get(i),p1);
                projection.toPixels((GeoPoint)points.get(i+1),p2);

                path.moveTo(p2.x, p2.y);
                path.lineTo(p1.x,p1.y);

            }

            canvas.drawPath(path, mPaint);
        }
    }




}