package application.scenes;

import application.PrimaryScene;
import application.SceneType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.*;

/**
 * This class displays the Creation that is being made, giving the user an opportunity to preview it,
 * confirm it is correct and then name it.
 */

public class CreationPreviewController extends PrimaryScene {

    @FXML
    private MediaView mediaView;
    private MediaPlayer player;
    @FXML
    private Button replayButton;
    @FXML
    private Button confirmButton;
    @FXML
    private TextField nameField;
    @FXML
    private Label nameErrorLabel;

    /**
     * Get the temporary Creation and play it automatically
     * @throws IOException
     */
    @FXML
    private void initialize() throws IOException {
        File videoURL = new File("./.temp/creation.mp4");
        Media video = new Media(videoURL.toURI().toString());
        player = new MediaPlayer(video);
        player.setOnEndOfMedia(() -> replayButton.setVisible(true));
        player.setAutoPlay(true);
        mediaView.setMediaPlayer(player);
    }

    /**
     * This method looks at the Creation name entered by the user and if it is valid it moves the temporary version to
     * the Creations folder and renames it to the submitted Creation name. If the name is not valid, it sends a warning
     * to the user. If the name already exists it gives the user the opportunity to overwrite.
     * @throws InterruptedException
     * @throws IOException
     */
    @FXML
    private void confirmButtonClicked() throws InterruptedException, IOException {
        String creationName = nameField.getText();
        if (nameIsValid(creationName)) {
            player.pause();
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("/bin/bash", "-c", "bash ./resources/scripts/moveTempFolder.sh \"" + creationName + "\"");
            Process process = pb.start();
            int exitStatus = process.waitFor();
            if (exitStatus == 1) {
                Alert alert = new Alert(Alert.AlertType.NONE, "Creation: '" + creationName + "' already exists. Do you wish to overwrite it?", ButtonType.YES, ButtonType.NO);
                alert.setTitle("Creation already exists");
                alert.setHeight(150);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    pb.command("/bin/bash", "-c", "rm -fr ./creations/\"" + creationName + "\"");
                    Process removeExisting = pb.start();
                    removeExisting.waitFor();

                    pb.command("/bin/bash", "-c", "mv -f ./.temp ./creations/\"" + creationName + "\"");
                    Process moveTemp = pb.start();
                    moveTemp.waitFor();
                    setScene(SceneType.MENU, stage);
                }
            } else {
                setScene(SceneType.MENU, stage);
            }
        } else {
            nameErrorLabel.setVisible(true);
        }
    }

    /**
     * This method warns the user that if they cancel the Creation process, all progress will be lost. It then allows
     * them to decide if they wish to cancel or not.
     * @throws IOException
     * @throws InterruptedException
     */
    @FXML
    public void cancelButtonClicked() throws IOException, InterruptedException {
        player.pause();
        // Create confirmation alert
        Alert alert = new Alert(Alert.AlertType.NONE, "Are you sure you want to cancel? All progress will be lost!.", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Cancel Creation");
        alert.setHeight(150);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "rm -fr ./.temp");
            Process delete = builder.start();
            delete.waitFor();
            setScene(SceneType.MENU, stage);
        }
    }

    @FXML
    private void keyReleased(){
        String text = nameField.getText();
        boolean isEmpty = (text.isEmpty() || text.trim().isEmpty());
        confirmButton.setDisable(isEmpty);
    }

    @FXML
    private void playPauseVid(){
        if (player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.pause();
        }
        else {
            player.play();
        }
    }

    @FXML
    private void replayButtonClicked() {
        replayButton.setVisible(false);
        player.seek(Duration.ZERO);
        player.play();
    }

    private boolean nameIsValid(String creationName) {
        return creationName.matches("[A-Za-z0-9_\\-][A-Za-z0-9_\\- ]*");
    }
}
