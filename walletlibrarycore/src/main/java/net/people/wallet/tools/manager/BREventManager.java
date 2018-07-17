package net.people.wallet.tools.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import net.people.platform.APIClient;
import net.people.wallet.tools.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BREventManager implements Utils.OnAppBackgrounded {
    private static final String TAG = BREventManager.class.getName();

    private static BREventManager instance;
    private String sessionId;
    private List<Event> events = new ArrayList<>();

    private BREventManager() {
        sessionId = UUID.randomUUID().toString();
        Utils.addOnBackgroundedListener(this);
    }

    public static BREventManager getInstance() {
        if (instance == null) instance = new BREventManager();
        return instance;
    }

    public void pushEvent(String eventName, Map<String, String> attributes) {
        Log.d(TAG, "pushEvent: " + eventName);
        Event event = new Event(sessionId, System.currentTimeMillis() * 1000, eventName, attributes);
        events.add(event);
    }

    public void pushEvent(String eventName) {
        Log.d(TAG, "pushEvent: " + eventName);
        Event event = new Event(sessionId, System.currentTimeMillis() * 1000, eventName, null);
        events.add(event);
    }

    @Override
    public void onBackgrounded() {
        Log.e(TAG, "onBackgrounded: ");
        saveEvents();
        pushToServer();
    }

    private void saveEvents() {
//        Log.d(TAG, "saveEvents: ");
        JSONArray array = new JSONArray();
        for (Event event : events) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("sessionId", event.sessionId);
                obj.put("time", event.time);
                obj.put("eventName", event.eventName);
                JSONObject mdObj = new JSONObject();
                if (event.attributes != null && event.attributes.size() > 0) {
                    for (Map.Entry<String, String> entry : event.attributes.entrySet()) {
//                        System.out.println(entry.getKey() + "/" + entry.getValue());
                        mdObj.put(entry.getKey(), entry.getValue());
                    }
                }
                obj.put("metadata", mdObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Log.e(TAG, "saveEvents: insert json to array: " + obj);
            array.put(obj);
        }
        Context app = Utils.getContext();
        if (app != null) {
            String fileName = app.getFilesDir().getAbsolutePath() + "/events/" + UUID.randomUUID().toString();
            writeEventsToDisk(fileName, array.toString());
        } else {
            Log.e(TAG, "saveEvents: FAILED TO WRITE EVENTS TO FILE: app is null");
        }
    }

    private void pushToServer() {
        Log.d(TAG, "pushToServer()");
        Context app = Utils.getContext();
        Log.d(TAG, "BREventManager TEST -1 -> ");

        if (app != null) {
            Log.d(TAG, "BREventManager TEST 0 -> ");

            List<JSONArray> arrs = getEventsFromDisk(app);
            int fails = 0;
            for (JSONArray arr : arrs) {
                JSONObject obj = new JSONObject();
                try {
                    Log.d(TAG, "BREventManager TEST 1 -> ");

                    obj.put("deviceType", 1);
                    int verCode = -1;
                    try {
                        PackageInfo pInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
                        verCode = pInfo.versionCode;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    obj.put("appVersion", verCode);
                    obj.put("events", arr);

                    String strUtl = APIClient.BASE_URL + "/events";

                    final MediaType JSON = MediaType.parse("application/json");
                    RequestBody requestBody = RequestBody.create(JSON, obj.toString());
                    Request request = new Request.Builder()
                            .url(strUtl)
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .post(requestBody).build();
                    String strResponse = null;
                    Response response = null;
                    Log.d(TAG, "BREventManager TEST  2 -> ");

                    try {
                        Log.d(TAG, "Making request to -> " + strUtl);

                        response = APIClient.getInstance(app).sendRequest(request, true, 0);
                        if (response != null)
                            strResponse = response.body().string();
                        Log.d(TAG, "Events response -> " + strResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                        fails++;
                    } finally {
                        if (response != null) response.close();
                    }
                    if (Utils.isNullOrEmpty(strResponse)) {
                        Log.e(TAG, "pushToServer: response is empty");
                        fails++;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    fails++;
                }
            }
            if (fails == 0) {
                //if no fails then remove the local files.
                File dir = new File(app.getFilesDir().getAbsolutePath() + "/events/");
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                } else {
                    Log.e(TAG, "pushToServer:  HUH?");
                }
            } else {
                Log.e(TAG, "pushToServer: FAILED with:" + fails + " fails");
            }
        } else {
            Log.e(TAG, "pushToServer: Failed to push, app is null");
        }
    }

    private boolean writeEventsToDisk(String fileName, String json) {
        Log.e(TAG, "saveEvents: eventsFile: " + fileName + ", \njson: " + json);
        try {
            FileWriter file = new FileWriter(fileName);
            file.write(json);
            file.flush();
            file.close();
            return true;
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
        return false;
    }

    //returns the list of JSONArray which consist of Event arrays
    private static List<JSONArray> getEventsFromDisk(Context context) {
        Log.d(TAG, "getEventsFromDisk()");
        List<JSONArray> result = new ArrayList<>();
        File dir = new File(context.getFilesDir().getAbsolutePath() + "/events/");
        if (dir.listFiles() == null) return result;
        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                String name = f.getName();
                Log.e(TAG, "getEventsFromDisk: name:" + name);
                try {
                    JSONArray arr = new JSONArray(readFile(name));
                    result.add(arr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "getEventsFromDisk: Unexpected directory where file is expected: " + f.getName());
            }
        }

        Log.d(TAG, "getEventsFromDisk result - > " + result);
        return result;
    }

    private static String readFile(String fileName) {
        try {
            File f = new File(fileName);
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }

    public class Event {
        public String sessionId;
        public long time;
        public String eventName;
        public Map<String, String> attributes;

        public Event(String sessionId, long time, String eventName, Map<String, String> attributes) {
            this.sessionId = sessionId;
            this.time = time;
            this.eventName = eventName;
            this.attributes = attributes;
        }
    }
}