package com.hackyourself.guitair.back;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.hackyourself.guitair.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import io.socket.client.Socket;
import java.util.Date;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class NetworkManager implements IObserver {

    private static String LOG_TAG = "NetworkManager";
    private Socket mSocket;
    private DataOutputStream mDataOutputStream;
    private MainActivity mMainActivity;
    public NetworkManager(Context mainActivity) {
        mMainActivity = (MainActivity)mainActivity;
    }

    /**
     * Finds ip of our computer.
     *
     * Ip is stored in a file.
     *
     * @return host or ip of server pc
     */
    public String getHost() {
        String ip = Utils.getIPAddress(true);
        return ip;
    }

    public boolean start() {
        try {
            //Log.d(LOG_TAG, "IP: " + getHost());
            final int port = 5000;
            final Bundle bundle = new Bundle();
            mSocket = IO.socket("http://192.168.43.110:5000");
            //mSocket = IO.socket("http://176.112.207.201:5000");
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... objects) {
                    try {
                        mSocket.send(new JSONObject()
                                .put("type", "phone")
                                .put("code", MainActivity.code));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Connect");
                }
            }).on("event", new Emitter.Listener() {

                @Override
                public void call(Object... args) {}

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) { System.out.println("Disconnect"); }

            }).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    /*for(Object obj: args){
                        try {
                            Log.d(LOG_TAG, ((JSONObject)obj).getString("sdf"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }*/
                }
            });
            mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void stop() {
        try {
            // Send stop command.
            long time = new Date().getTime();
            mSocket.send(new JSONObject()
                    .put("time", time)
                    .put("movement", Movement.STOP).toString());
            // Close connection.
            mSocket.close();
            Log.d(LOG_TAG, "Client is closed...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Data data) {
        Log.d(LOG_TAG, "Sending " + Long.toString(data.getTime()) + " " +
                data.getMovement() + "...");
        try {
            mSocket.send(new JSONObject()
                    .put("time", data.getTime())
                    .put("move", data.getMovement()));
            Log.d(LOG_TAG, "Sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
