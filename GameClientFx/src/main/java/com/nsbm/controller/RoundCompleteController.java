/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsbm.controller;

import com.nsbm.common.CommonData;
import static com.nsbm.common.CommonUtil.setTableColumns;
import com.nsbm.entity.PlayerStatistic;
import static com.nsbm.service.PlayerServiceHandler.getRoundCompletedPlayers;
import static com.nsbm.service.PlayerServiceHandler.listenToRoundCompletionEvent;
import static com.nsbm.service.PlayerServiceHandler.notifyRoundCompletion;
import static com.nsbm.service.PlayerServiceHandler.removePlayer;
import static com.nsbm.service.PlayerServiceHandler.setLabelReference;
import static com.nsbm.service.PlayerServiceHandler.setPlayerScore;
import static com.nsbm.service.PlayerServiceHandler.setSpecialPointsLabel;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 *
 * @author Lakshitha
 */
public class RoundCompleteController implements Initializable {

    private String[] allCompletedPlayer;
    final ObservableList<String> model = FXCollections.observableArrayList();
    final ObservableList<PlayerStatistic> playerStatistics = FXCollections.observableArrayList();

    @FXML
    private Label nextRoundTime;
    @FXML
    private Label specialPoints;
    @FXML
    private Button exitButton;
    @FXML
    private TableView<PlayerStatistic> scoreTable;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        allCompletedPlayer = getRoundCompletedPlayers();
        setSpecialPointsLabel(specialPoints);
        setLabelReference(nextRoundTime);
        setPlayerScore(playerStatistics);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                setTableColumns(scoreTable, playerStatistics, allCompletedPlayer);
                listenToRoundCompletionEvent();
            }
        };
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ScoringMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
        notifyRoundCompletion();
    }

    @FXML
    private void exitAction(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        removePlayer(CommonData.username);
        System.exit(0);
    }
}