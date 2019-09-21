package com.example.Pratyush.myapplication.backend;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class LatLng {
    public JSONObject getJSON(){
        List<String> latList = new ArrayList();
        List<String> lngList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();

        latList.add("19.12554");
        lngList.add("73.01332");
        nameList.add("tc23RCP");

        latList.add("19.129766");
        lngList.add("73.014712");
        nameList.add("tc30RCP");

        latList.add("19.14771");
        lngList.add("73.080569");
        nameList.add("volgaRio");

        latList.add("19.169986");
        lngList.add("73.072989");
        nameList.add("bella");

        latList.add("19.124422");
        lngList.add("73.005398");
        nameList.add("gateHouse");

        latList.add("19.190712");
        lngList.add("73.091445");
        nameList.add("dMartDombivali");

        latList.add("19.118665");
        lngList.add("73.017013");
        nameList.add("gateF");

        latList.add("19.113882");
        lngList.add("73.012868");
        nameList.add("countryINN");

        latList.add("19.109837");
        lngList.add("73.031043");
        nameList.add("lnT");

        latList.add("19.137700");
        lngList.add("73.008920");
        nameList.add("MIDC");

        latList.add("19.139593");
        lngList.add("73.036238");
        nameList.add("midcStart");

        latList.add("19.142907");
        lngList.add("73.046570");
        nameList.add("midcEnd");

        latList.add("19.150359");
        lngList.add("73.055194");
        nameList.add("toyota");

        latList.add("19.156288");
        lngList.add("73.064131");
        nameList.add("khidKTemple");

        latList.add("19.159664");
        lngList.add("73.068493");
        nameList.add("ganeshTemple");

        latList.add("19.163391");
        lngList.add("73.073310");
        nameList.add("footballPalava");

        latList.add("19.165469");
        lngList.add("73.075123");
        nameList.add("xperiaMall");

        latList.add("19.163057");
        lngList.add("73.075585");
        nameList.add("saiTemple");

        latList.add("19.164748");
        lngList.add("73.073950");
        nameList.add("lodhaSchool");

        latList.add("19.155453");
        lngList.add("73.074322");
        nameList.add("prasnSweets");

        latList.add("19.147922");
        lngList.add("73.078603");
        nameList.add("pawarSchool");

        latList.add("19.151956");
        lngList.add("73.084150");
        nameList.add("gramPanOff");

        latList.add("19.167989");
        lngList.add("73.071737");
        nameList.add("lodhaGolf");

        latList.add("19.122513");
        lngList.add("72.999954");
        nameList.add("dmartGhansoli");

        latList.add("19.116990");
        lngList.add("73.006747");
        nameList.add("man");

        JSONObject mJSONArray = new JSONObject();
        try
        {
            for (int i = 0; i< latList.size(); i++) {
                JSONObject mJSON = new JSONObject();
                mJSON.put("lat", latList.get(i));
                mJSON.put("lng", lngList.get(i));
                mJSONArray.put(nameList.get(i), mJSON);
            }
        } catch (JSONException jse) {
            jse.getMessage();
        }

        return mJSONArray;
    }
}