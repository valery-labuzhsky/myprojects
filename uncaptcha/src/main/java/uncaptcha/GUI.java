package uncaptcha;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static uncaptcha.Uncaptcha.transform;

public class GUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setWidth(500);
        stage.setHeight(500);

        BorderPane root = new BorderPane();
        BufferedImage image = ImageIO.read(new File("./images/386917.png"));
        image = transform(image);

        WritableImage writableImage = new WritableImage(image.getWidth(), image.getHeight());
        ImageView view = new ImageView(SwingFXUtils.toFXImage(image, writableImage));

        view.setSmooth(true);
        double scale = 400d / image.getWidth();
        view.setFitWidth(image.getWidth() * scale);
        view.setFitHeight(image.getHeight() * scale);
        root.setCenter(view);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
