package application.scenes;

import application.Chunk;
import application.Main;
import application.PrimaryScene;
import application.SceneType;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageSelectionController extends PrimaryScene {
    @FXML
    private TilePane imagesTilePane;
    @FXML
    private Button nextButton;
    private String searchTerm;
    private ObservableList<Chunk> chunks;
    private List<Integer> selectedImages = new ArrayList<Integer>();
    int numImages = 0;

    @FXML
    private void initialize() {
        int i = 1;
        for (Node node: imagesTilePane.getChildren()) {
            StackPane stackPane = (StackPane) node;
            ImageView imageView = (ImageView) stackPane.getChildren().get(0);
            File file = new File(".temp/images/" + i + ".jpg");
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            i++;
        }
    }

    public void setUp(String searchTerm, ObservableList<Chunk> chunks) {
        this.searchTerm = searchTerm;
        this.chunks = chunks;
    }

    @FXML
    private void imageClicked(Event event) {
        StackPane stackPane = (StackPane) event.getSource();
        VBox vBox = (VBox) stackPane.getChildren().get(1);
        if (vBox.isVisible()) {
            numImages--;
        } else {
            numImages++;
        }
        vBox.setVisible(!vBox.isVisible());
        if (numImages <= 0) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }
    }

    @FXML
    private void nextButtonClicked() throws IOException, InterruptedException {
        if (!nextButton.isDisabled()) {
            int i = 1;
            for (Node node: imagesTilePane.getChildren()) {
                StackPane stackPane = (StackPane) node;
                VBox vBox = (VBox) stackPane.getChildren().get(1);
                if (vBox.isVisible()) {
                    selectedImages.add(i);
                }
                i++;
            }
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/resources/scenes/ProgressScreen.fxml"));
            Parent root = loader.load();
            ProgressScreenController progressScreenController = loader.getController();
            progressScreenController.go(chunks, searchTerm, selectedImages);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (progressScreenController.isSuccess()) {
                setScene(SceneType.CREATION_PREVIEW, this.stage);
            } else {
                ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "rm -fr .temp/images/selected");
                Process process = pb.start();
                process.waitFor();
            }
        }
    }
}