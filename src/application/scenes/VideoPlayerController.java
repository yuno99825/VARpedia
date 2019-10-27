package application.scenes;

import application.PrimaryScene;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class VideoPlayerController {
    /**
     * This Class displays a specific Creation (video) to the user, allowing the user to pause , play and replay.
     */

    @FXML
    private MediaView mediaView;
    private MediaPlayer player;
    @FXML
    private Button replayButton;

    /**
     * This method takes a Creation name and then finds the corresponding Creation in the creations folder.
     * It then plays the creation.
     * @param creationName
     * @param stage
     */
    public void setUpVideo(String creationName, Stage stage) {
        File videoURL = new File("./creations/" + creationName + "/creation.mp4");
        Media video = new Media(videoURL.toURI().toString());
        player = new MediaPlayer(video);
        player.setOnReady(() -> {
            stage.sizeToScene();
        });
        player.setOnEndOfMedia(() -> replayButton.setVisible(true));
        player.setAutoPlay(true);
        mediaView.setMediaPlayer(player);
        mediaView.fitWidthProperty().bind(stage.widthProperty());
        mediaView.fitHeightProperty().bind(stage.heightProperty());
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

    public void stopVideo() {
        player.pause();
    }

}
