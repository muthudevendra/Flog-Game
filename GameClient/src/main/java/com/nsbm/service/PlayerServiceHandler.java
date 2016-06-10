/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsbm.service;

import com.google.gson.Gson;
import static com.nsbm.common.CommonUtil.setModelData;
import com.nsbm.common.PlayerStatus;
import com.nsbm.common.UserData;
import com.nsbm.entity.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.DefaultListModel;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 *
 * @author Lakshitha
 */
public class PlayerServiceHandler {

    private final static String PLAYERCLASS = "PlayerService/";
    private final static String BRAOCAST = "BroadCaster/";
    private static DefaultListModel<String> model = null;

    public static void setMainMenu(DefaultListModel<String> model) {
        PlayerServiceHandler.model = model;
    }

    public static String addPlayer(String playerName, String playerPassword) {
        String output = null;
        try {
            URL url = new URL(UserData.IP + PLAYERCLASS + "addPlayer");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            String input = "{\"username\":\"" + playerName + "\",\"password\":\"" + playerPassword + "\"}";
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            output = br.readLine();

        } catch (Exception e) {
            System.out.println(e);
        }
        return output;
    }

    public static Player[] getAllPlayers() {
        String output = null;
        Player[] playerList = null;
        try {
            URL url = new URL(UserData.IP + PLAYERCLASS + "getPlayers");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            output = br.readLine();
            Gson parser = new Gson();

            playerList = parser.fromJson(output, Player[].class);

        } catch (IOException e) {
            System.out.println(e);
        }
        return playerList;
    }

    public static void notifyPlayerJoin() {
        String output = null;
        try {
            URL url = new URL(UserData.IP + BRAOCAST + "sendPlayerJoin");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            String input = UserData.username;
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            output = br.readLine();
            System.out.println(output);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void listendToJoinEvent() {
        Client client = ClientBuilder.newBuilder()
                .register(SseFeature.class).build();
        WebTarget target = client.target(UserData.IP + BRAOCAST + "listendPlayerJoin");

        EventInput eventInput = target.request().get(EventInput.class);
        while (!eventInput.isClosed()) {
            final InboundEvent inboundEvent = eventInput.read();
            if (inboundEvent == null) {
                break;
            }
            if (UserData.playerStatus == PlayerStatus.PLAYING) {
                break;
            }
            setModelData(inboundEvent.readData(String.class),model);
        }
        System.out.println("Playing");
    }

}
