package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.*;

public class CreationPreviewController {

    @FXML
    private MediaView mediaView;
    private MediaPlayer player;
    @FXML
    private Button playPauseButton;
    @FXML
    private Button confirmButton;
    @FXML
    private TextField nameField;

    @FXML
    private void initialize(){
        confirmButton.setDisable(true);
        File videoURL = new File("./.temp/creation.mp4");
        Media video = new Media(videoURL.toURI().toString());
        player = new MediaPlayer(video);
        player.setAutoPlay(true);
        mediaView.setMediaPlayer(player);
    }

    @FXML
    private void confirmButtonClicked() throws InterruptedException, IOException {
        String creationName = nameField.getText();
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("/bin/bash", "-c", "[ -e ./creations/\"" + creationName + "\" ]");
        Process checkExist = pb.start();
        int exit = checkExist.waitFor();

        if (exit == 1) {
            pb.command("/bin/bash", "-c", "mv ./.temp ./creations/" + "\"" + creationName + "\"");
            Process moveTemp = pb.start();
            Stage thisStage = (Stage)nameField.getScene().getWindow();
            thisStage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.NONE, "Creation: '" + creationName + "' already exists. Do you wish to overwrite it?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Creation already exists");
            alert.setHeight(150);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                pb.command("/bin/bash", "-c", "rm -fr ./creations/\"" + creationName + "\"");
                Process removeExisting = pb.start();
                removeExisting.waitFor();

                pb.command("/bin/bash", "-c", "mv -f ./.temp ./creations/\"" + creationName + "\"");
                pb.start();

                Stage thisStage = (Stage)nameField.getScene().getWindow();
                thisStage.close();
            }
        }

    }

    @FXML
    private void disableButton(){
        String text = nameField.getText();
        boolean isEmpty = (text.isEmpty() || text.trim().isEmpty());
        confirmButton.setDisable(isEmpty);
    }

    @FXML
    private void playPauseVid(){
        if (player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.pause();
            playPauseButton.setText("Play");
        }
        else {
            player.play();
            playPauseButton.setText("Pause");
        }
    }

}
