/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsbm.controller;

import com.nsbm.common.CommonData;
import static com.nsbm.common.CommonData.currentRound;
import static com.nsbm.common.CommonUtil.setModelData;
import com.nsbm.common.PlayerStatus;
import com.nsbm.entity.Player;
import static com.nsbm.service.PlayerServiceHandler.getAllPlayers;
import static com.nsbm.service.PlayerServiceHandler.listenToJoinEvent;
import static com.nsbm.service.PlayerServiceHandler.notifyPlayerJoin;
import static com.nsbm.service.PlayerServiceHandler.setModelReference;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Muthu
 */
public class MainMenuController implements Initializable {

    final ObservableList<String> model = FXCollections.observableArrayList();

    private String[] playerNames;
    private Player[] allPlayers;

    @FXML
    private ListView<String> listBox;
    @FXML
    private Label userNameLabel;
    @FXML
    private Button startButton;
    @FXML
    private AnchorPane extendableNotificationPane;
    @FXML
    private Rectangle clipRect;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userNameLabel.setText(CommonData.username);
        allPlayers = getAllPlayers();
        setModelData(allPlayers, model);
        listBox.setItems(model);
        setModelReference(model);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                notifyPlayerJoin();
                if (currentRound == 0) {
                    listenToJoinEvent();
                }
            }
        });
        t.setDaemon(true);
        t.start();
        
        double widthInitial = 200;
        double heightInitial = 400;
        clipRect = new Rectangle();
        clipRect.setWidth(widthInitial);
        clipRect.setHeight(0);
        clipRect.translateYProperty().set(heightInitial);
        extendableNotificationPane.setClip(clipRect);
        extendableNotificationPane.translateYProperty().set(-heightInitial);
        extendableNotificationPane.prefHeightProperty().set(0);
    }

    public void startGame(ActionEvent event) throws IOException { 
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/GameWindow.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        CommonData.playerStatus = PlayerStatus.PLAYING;
        stage = (Stage) startButton.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void notificationClick() {
        clipRect.setWidth(extendableNotificationPane.getWidth());
        if (clipRect.heightProperty().get() != 0) {
            // Animation for scroll up.
            Timeline timelineUp = new Timeline();
            // Animation of sliding the search pane up, implemented via
            // clipping.
            final KeyValue kvUp1 = new KeyValue(clipRect.heightProperty(), 0);
            final KeyValue kvUp2 = new KeyValue(clipRect.translateYProperty(), extendableNotificationPane.getHeight());
            // The actual movement of the search pane. This makes the table grow
            final KeyValue kvUp4 = new KeyValue(extendableNotificationPane.prefHeightProperty(), 0);
            final KeyValue kvUp3 = new KeyValue(extendableNotificationPane.translateYProperty(), -extendableNotificationPane.getHeight());

            final KeyFrame kfUp = new KeyFrame(Duration.millis(200), kvUp1, kvUp2, kvUp3, kvUp4);
            timelineUp.getKeyFrames().add(kfUp);
            timelineUp.play();
        } else {
            // Animation for scroll down.
            Timeline timelineDown = new Timeline();
            // Animation for sliding the search pane down. No change in size, just making the visible part of the pane bigger.
            final KeyValue kvDwn1 = new KeyValue(clipRect.heightProperty(), extendableNotificationPane.getHeight());
            final KeyValue kvDwn2 = new KeyValue(clipRect.translateYProperty(), 0);
            // Growth of the pane.
            final KeyValue kvDwn4 = new KeyValue(extendableNotificationPane.prefHeightProperty(), extendableNotificationPane.getHeight());
            final KeyValue kvDwn3 = new KeyValue(extendableNotificationPane.translateYProperty(), 0);
            final KeyFrame kfDwn = new KeyFrame(Duration.millis(200), createBouncingEffect(extendableNotificationPane.getHeight()), kvDwn1, kvDwn2, kvDwn3, kvDwn4);
            timelineDown.getKeyFrames().add(kfDwn);
            timelineDown.play();
        }
    }
    private EventHandler<ActionEvent> createBouncingEffect(double height) {
        final Timeline timelineBounce = new Timeline();
        timelineBounce.setCycleCount(2);
        timelineBounce.setAutoReverse(true);
        final KeyValue kv1 = new KeyValue(clipRect.heightProperty(), (height - 15));
        final KeyValue kv2 = new KeyValue(clipRect.translateYProperty(), 15);
        final KeyValue kv3 = new KeyValue(extendableNotificationPane.translateYProperty(), -15);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(100), kv1, kv2, kv3);
        timelineBounce.getKeyFrames().add(kf1);

        // Event handler to call bouncing effect after the scroll down is
        // finished.
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                        timelineBounce.play();
                }
        };
        return handler;
    }
}
