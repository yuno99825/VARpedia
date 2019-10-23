package application;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizViewController {

    @FXML
    private MediaView mediaView;
    private MediaPlayer player;
    @FXML
    private Button replayButton;
    @FXML
    private Button startButton;
    @FXML
    private Label questionNumberLabel;
    @FXML
    private Button submitButton;
    @FXML
    private TextField answerField;
    @FXML
    private Label correctLabel;
    @FXML
    private Label wrongLabel;
    @FXML
    private StackPane stackPane;
    @FXML
    private VBox statisticsScreen;
    @FXML
    private Label wellDoneLabel;
    @FXML
    private Label gettingThereLabel;
    @FXML
    private Label practiseLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label recapMessageLabel;
    @FXML
    private Label accuracyPercentLabel;
    @FXML
    private Label labelWithAnswer;

    private List<String> _creationsList;
    private Stage _stage;
    private int _questionNumber = 0;
    private String _creationToPlay;
    private String _searchTerm = "";
    private int numberOfCorrect = 0;
    private int attemptNumber = 1;



    public void setCreationsList(List<String> creationsList) {
        _creationsList = creationsList;
    }

    public void setStage(Stage stage) {
        this._stage = stage;
    }

    private void playQuizMedia(){
        int numberOfCreations = _creationsList.size();
        questionNumberLabel.setVisible(true);
        if (numberOfCreations > 0) {
            if (!(mediaView.isVisible())) {
                mediaView.setVisible(true);
            }
            int randomCreation = (int) (Math.random() * numberOfCreations);
            _creationToPlay = _creationsList.get(randomCreation);
            _searchTerm = getTermFromTxtFile();
            _questionNumber++;
            questionNumberLabel.setText("Question " + _questionNumber + "!");

            File videoURL = new File("./creations/" + _creationToPlay + "/quiz.mp4");
            Media video = new Media(videoURL.toURI().toString());
            player = new MediaPlayer(video);
            player.setOnEndOfMedia(() -> {
                replayButton.toFront();
                replayButton.setVisible(true);
            });
            player.setAutoPlay(true);
            mediaView.setMediaPlayer(player);

            List<String> temp = new ArrayList<String>();
            for (int i = 0; i < _creationsList.size(); i++) {
                if (!(_creationsList.get(i).equals(_creationToPlay))) {
                        temp.add(_creationsList.get(i));
                }
            }
            _creationsList = temp;
        } else {
            dispStatsScreen();
        }
    }

    private void dispStatsScreen(){

        mediaView.setVisible(false);
        replayButton.setVisible(false);
        titleLabel.setVisible(false);
        questionNumberLabel.setVisible(false);
        answerField.setVisible(false);
        submitButton.setVisible(false);
        statisticsScreen.setVisible(true);
        String percent;
        if (numberOfCorrect == 0){
            practiseLabel.setVisible(true);
            percent = "0";
        } else {
            double ratio = (numberOfCorrect*1.00)/(_questionNumber*1.00);
            ratio = ratio*100.0;
            percent = String.format("%.1f",ratio);
            //percent = Double.toString(ratio);
            if (ratio >= 0.8) {
                wellDoneLabel.setVisible(true);
            } else if (ratio >= 0.5) {
                gettingThereLabel.setVisible(true);
            } else {
                practiseLabel.setVisible(true);
            }
        }
        recapMessageLabel.setText("You got " + Integer.toString(numberOfCorrect) + " answer(s) correct out of " + Integer.toString(_questionNumber));
        recapMessageLabel.setVisible(true);
        accuracyPercentLabel.setText(percent + "%");
        accuracyPercentLabel.setVisible(true);
    }


    private String getTermFromTxtFile(){
        String cmd = "cat ./creations/\"" + _creationToPlay + "\"/searchTerm.txt";
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream stdout = process.getInputStream();
        BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
        String line;
        String term = "";try {
            while ((line = stdoutBuffered.readLine()) != null) {
                term = term + line;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return term;
    }

    @FXML
    private void submitButtonClicked(){
        if (submitButton.isDisabled()) {return;}

        mediaView.setVisible(false);
        replayButton.setVisible(false);
        submitButton.setDisable(true);
        if((answerField.getText().equals(_searchTerm))||(answerField.getText().trim().equals(_searchTerm.trim()))){
            numberOfCorrect++;
            attemptNumber = 1;
            correctLabel.setVisible(true);
            player.pause();
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished( event -> {
                correctLabel.setVisible(false);
                submitButton.setDisable(false);
                playQuizMedia();
            } );
            delay.play();
            answerField.setText("");
            return;

        } else {

            wrongLabel.setVisible(true);
            player.pause();
            if (attemptNumber < 2) {
                attemptNumber++;
                labelWithAnswer.setText("Try Again!");
                labelWithAnswer.setVisible(true);
                PauseTransition delay = new PauseTransition(Duration.seconds(1));
                delay.setOnFinished(event -> {
                    wrongLabel.setVisible(false);
                    player.seek(Duration.ZERO);
                    player.play();
                    mediaView.setVisible(true);
                    labelWithAnswer.setVisible(false);
                    submitButton.setDisable(false);
                });
                delay.play();
            } else {
                attemptNumber = 1;
                labelWithAnswer.setText("Correct answer was: " + _searchTerm);
                labelWithAnswer.setVisible(true);
                PauseTransition delay = new PauseTransition(Duration.seconds(2.5));
                delay.setOnFinished(event -> {
                    wrongLabel.setVisible(false);
                    labelWithAnswer.setVisible(false);
                    submitButton.setDisable(false);
                    playQuizMedia();
                });
                delay.play();
            }
            answerField.setText("");
            return;
        }
    }


    @FXML
    private void startButtonClicked(){
        startButton.setVisible(false);
        answerField.setVisible(true);
        submitButton.setVisible(true);
        playQuizMedia();
    }

    @FXML
    private void playPauseVid(){
        if (player != null) {
            if (player.getStatus() == MediaPlayer.Status.PLAYING) {
                player.pause();
            } else {
                player.play();
            }
        }
    }

    @FXML
    private void replayButtonClicked() {
        mediaView.toFront();
        replayButton.setVisible(false);
        player.seek(Duration.ZERO);
        player.play();
    }

    @FXML
    private void backButtonClicked(){
        _stage.close();
    }

    @FXML
    private void keyReleased(){
        String text = answerField.getText();
        boolean isEmpty = (text.isEmpty() || text.trim().isEmpty());
        submitButton.setDisable(isEmpty);
    }

    public void stopVideo() {
        if (player != null) {
            player.pause();
        }
    }
}
