package com.example.linec;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.net.*;

public class HelloController {
    public CheckBox server;
    public TextField tfIP;
    public TextField tfPORT;
    public Label lbSorszam;
    public Button btElker;
    public Button btNullaz;

    public DatagramSocket socket = null;
    public int sorszam = 0;
    public int last_x = -1;
    public int last_y = -1;
    public Pane pane;

    public void initialize() {
        try { socket = new DatagramSocket(); } catch (SocketException e) { e.printStackTrace(); }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { fogad(); }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void onClickCheckBox() {
        if (server.isSelected()) {
            tfIP.setDisable(true);
            tfPORT.setDisable(true);

            btElker.setDisable(false);
            btNullaz.setDisable(false);
        } else {
            tfIP.setDisable(false);
            tfPORT.setDisable(false);

            btElker.setDisable(true);
            btNullaz.setDisable(true);
        }
    }

    public void onClickElker() {
        kuld(sorszam+"", tfIP.getText(), Integer.parseInt(tfPORT.getText()));
    }

    public void onClickNullaz() {
        sorszam = 0;
        pane.getChildren().clear();
        lbSorszam.setText("0");
        btElker.setDisable(true);
    }

    private void kuld(String uzenet, String ip, int port) {
        try {
            byte[] adat = uzenet.getBytes("utf-8");
            InetAddress ipv4 = Inet4Address.getByName(ip);
            DatagramPacket packet = new DatagramPacket(adat, adat.length, ipv4, port);
            socket.send(packet);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void fogad() { // Külön szálon!
        byte[] adat = new byte[256];
        DatagramPacket packet = new DatagramPacket(adat, adat.length);
        while (true) {
            try {
                socket.receive(packet);
                String uzenet = new String(adat, 0, packet.getLength(), "utf-8");
                String ip = packet.getAddress().getHostAddress();
                int port = packet.getPort();
                Platform.runLater(() -> onFogad(uzenet, ip, port));
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void onFogad(String uzenet, String ip, int port) {
        int x = Integer.parseInt(uzenet.split(";")[0]);
        int y = Integer.parseInt(uzenet.split(";")[1]);

        if (x != -1 && y != -1) {
            Rectangle rectangle = new Rectangle(x-2, y-2, 4, 4);
            rectangle.setFill(Color.BLUE);

            pane.getChildren().add(rectangle);

            if (sorszam > 0) {
                Line line = new Line();
                line.setStartX(last_x);
                line.setStartY(last_y);
                line.setEndX(x);
                line.setEndY(y);
                pane.getChildren().add(line);
            }
            sorszam++;
        } else {
            btElker.setDisable(true);
        }

        last_x = x;
        last_y = y;
        lbSorszam.setText(sorszam+"");
    }
}