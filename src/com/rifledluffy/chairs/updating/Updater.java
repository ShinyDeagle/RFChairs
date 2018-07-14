package com.rifledluffy.chairs.updating;

import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.rifledluffy.chairs.RFChairs;

public class Updater {
	
	static RFChairs plugin = RFChairs.getInstance();

    final static String VERSION_URL = "https://api.spiget.org/v2/resources/58809/versions?size=" + Integer.MAX_VALUE + "&spiget__ua=" + plugin.getDescription().getName() + "/" + plugin.getDescription().getVersion();
    final static String DESCRIPTION_URL = "https://api.spiget.org/v2/resources/58809/updates?size" + Integer.MAX_VALUE + "&spiget__ua=" + plugin.getDescription().getName() + "/" + plugin.getDescription().getVersion();

    @SuppressWarnings("deprecation")
	public static Object[] getLastUpdate()
    {
        try
        {
            JSONArray versionsArray = (JSONArray) JSONValue.parseWithException(IOUtils.toString(new URL(String.valueOf(VERSION_URL))));
            Double lastVersion = Double.parseDouble(((JSONObject) versionsArray.get(versionsArray.size() - 1)).get("name").toString());

            if(lastVersion > Double.parseDouble(plugin.getDescription().getVersion()))
            {
                JSONArray updatesArray = (JSONArray) JSONValue.parseWithException(IOUtils.toString(new URL(String.valueOf(DESCRIPTION_URL))));
                String updateName = ((JSONObject) updatesArray.get(updatesArray.size() - 1)).get("title").toString();
   
                Object[] update = {lastVersion, updateName};
                return update;
            }
        }
        catch (Exception e)
        {
            return new String[0];
        }

        return new String[0];
    }
}