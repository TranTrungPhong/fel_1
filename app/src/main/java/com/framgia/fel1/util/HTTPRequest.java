package com.framgia.fel1.util;

import android.util.Log;

import com.framgia.fel1.constant.APIService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by PhongTran on 04/14/2016.
 */
public class HttpRequest {
    public static final String TAG = "HttpRequest";

    public static String postJSON(String url, JSONObject jsonObject1, String method) {
        URL httpUrl = null;
        HttpURLConnection connect = null;
        try {
            httpUrl = new URL(url);
            connect = (HttpURLConnection) httpUrl.openConnection();
            connect.setRequestMethod(method);
            connect.setDoInput(true);
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json");

            DataOutputStream out = new DataOutputStream(connect.getOutputStream());
            out.write(jsonObject1.toString().getBytes());
            Log.d("phong", jsonObject1.toString());
            out.flush();
            out.close();
            connect.connect();
            int indexHttpResult = connect.getResponseCode();
            Log.d("phong", indexHttpResult + " ");
            StringBuilder sb = new StringBuilder();
            if (indexHttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(connect.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            } else {
                System.out.println(connect.getResponseMessage());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connect.disconnect();
        }
        return null;
    }

    public static String postJsonRequest(String url, JSONObject jsonObject1, String method)
            throws IOException {
        URL httpUrl = null;
        HttpURLConnection connect = null;
        InputStream inputStream;
        httpUrl = new URL(url);
        connect = (HttpURLConnection) httpUrl.openConnection();
        if (!method.equals(APIService.METHOD_PATCH))
            connect.setRequestMethod(method);
        connect.setDoInput(true);
        if (jsonObject1 != null)
            connect.setDoOutput(true);
        else connect.setDoOutput(false);
        if (method.equals(APIService.METHOD_PATCH))
            connect.setRequestProperty("X-HTTP-Method-Override", method);
        connect.setRequestProperty("Content-Type", "application/json");
        if (jsonObject1 != null) {
            DataOutputStream out = new DataOutputStream(connect.getOutputStream());
            out.write(jsonObject1.toString().getBytes());
            Log.d(TAG, jsonObject1.toString());
            out.flush();
            out.close();
        }
        connect.connect();
        StringBuilder sb = new StringBuilder();
        BufferedReader br;
        try {
            inputStream = connect.getInputStream();
            br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
        } catch (IOException e) {
            inputStream = connect.getErrorStream();
            br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
        } finally {
            if (connect != null)
                connect.disconnect();
        }
        return sb.toString();
    }

    public static String getJSON(String url) {
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestMethod(APIService.METHOD_GET);
            final StringBuilder output = new StringBuilder(url);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseOutput.append(line);
            }
            br.close();
            return responseOutput.toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
