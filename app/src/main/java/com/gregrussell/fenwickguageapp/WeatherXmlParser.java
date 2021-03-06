package com.gregrussell.fenwickguageapp;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by greg on 6/3/2018.
 */

public class WeatherXmlParser {

    private static final String URL_TAG = "url";
    private static final String NAME_TAG = "name";
    private static final String ID_TAG = "id";
    private static final String LAT_TAG = "lat";
    private static final String LON_TAG = "lon";
    private static final String ADDRESS_TAG = "address";

    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException{
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readSite(parser);
        } finally {
            in.close();
        }
    }

    private List readSite(XmlPullParser parser) throws XmlPullParserException, IOException{
        List gauges = new ArrayList();

       // Log.d("xmlData", "readingSite");



        /*parser.require(XmlPullParser.START_TAG, ns, "site");
        while (parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            Log.d("xmldata", "name is: " + name);
            //Starts by looking for the entery tag
            if (name.equals("datum")){
                Log.d("xmlData", "found a datum");
                datums.add(readDatum(parser));
            }
            else {
                skip(parser);
            }
        }*/

        //parser.require(XmlPullParser.START_TAG, ns, "site");
        int eventType = parser.getEventType();

        while(eventType != XmlPullParser.END_DOCUMENT){
            if(eventType == XmlPullParser.START_TAG){
                String name = parser.getName();
                //Log.d("xmldata", "name is: " + name);
                if (name == null){
                    continue;
                }
                if (name.equals("gauge")){
                    //Log.d("xmldata", "found a datum");
                    gauges.add(readGaugeData(parser));
                }
            }
            eventType = parser.next();
        }

        return  gauges;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.

    private Gauge readGaugeData(XmlPullParser parser) throws XmlPullParserException, IOException{
        //Log.d("xmlData", "readGaugeData");
        parser.require(XmlPullParser.START_TAG, ns, "gauge");
        String url = null;
        String name = null;
        String id = null;
        double lat = 0.0;
        double lon = 0.0;
        String address = null;
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String tag = parser.getName();
            if(tag.equals(URL_TAG)){
                url = readURL(parser);
            }else if(tag.equals(NAME_TAG)){
                name = readName(parser);
            }else if(tag.equals(ID_TAG)){
                id = readID(parser);
            }else if(tag.equals(LAT_TAG)){
                lat = readLat(parser);
            }else if(tag.equals(LON_TAG)){
                //Log.d("xmlData9","found a lon");
                lon = readLon(parser);
            }else if(tag.equals(ADDRESS_TAG)){
                //Log.d("xmlData10","found an address");
                address = readAddress(parser);
            }else{
                skip(parser);
            }
        }
        return new Gauge(url, name, id, lat, lon, address);
    }
    // Processes url tags from site

    private String readURL(XmlPullParser parser) throws IOException, XmlPullParserException{
        parser.require(XmlPullParser.START_TAG,ns,URL_TAG);
        String gaugeURL = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, URL_TAG);
        return gaugeURL;
    }

    // Processes name tags from site.
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException{
        //Log.d("xmlData", "readingValid");
        parser.require(XmlPullParser.START_TAG, ns, NAME_TAG);
        String gaugeName = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, NAME_TAG);
        return gaugeName;
    }

    // Processes id tags from site.
    private String readID(XmlPullParser parser) throws IOException, XmlPullParserException{
        //Log.d("xmlData", "readingPrimary");
        parser.require(XmlPullParser.START_TAG, ns, ID_TAG);
        String gaugeID = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ID_TAG);
        return gaugeID;
    }

    // Processes lat tags from site
    private double readLat(XmlPullParser parser) throws IOException, XmlPullParserException{
        parser.require(XmlPullParser.START_TAG,ns,LAT_TAG);
        String gaugeLat = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,LAT_TAG);
        double latCoordinate = 0.0;
        try{
            latCoordinate = Double.parseDouble(gaugeLat);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return latCoordinate;
    }
    //processes lon tags from site
    private double readLon(XmlPullParser parser) throws IOException, XmlPullParserException{
        parser.require(XmlPullParser.START_TAG,ns,LON_TAG);
        String gaugeLon = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,LON_TAG);
        double lonCoordinate = 0.0;
        try{
            lonCoordinate = Double.parseDouble(gaugeLon);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return lonCoordinate;
    }

    private String readAddress(XmlPullParser parser) throws IOException, XmlPullParserException{
        parser.require(XmlPullParser.START_TAG,ns,ADDRESS_TAG);
        String gaugeAddress = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,ADDRESS_TAG);
        return gaugeAddress;
    }


    //For the tags gaugeName and gaugeID, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException{
        //Log.d("xmlData", "readingText");
        String result = "";
        if(parser.next() == XmlPullParser.TEXT){
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    //skip tags we don't want
    private void skip(XmlPullParser parser) throws  XmlPullParserException, IOException{
        //Log.d("xmlData", "skip");
        if(parser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0){
            switch(parser.next()){
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }



}
