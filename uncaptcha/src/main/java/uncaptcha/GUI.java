package uncaptcha;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static uncaptcha.Uncaptcha.transform;

public class GUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setWidth(500);
        stage.setHeight(500);

        BorderPane root = new BorderPane();
        BufferedImage image = ImageIO.read(new File("./images/250787.png"));
        image = transform(image);

        double scale = 400d / image.getWidth();
        int width = 400;
        int height = (int) (image.getHeight() * scale);
        Image im = image.getScaledInstance(width, height, 0);
        image = new BufferedImage(width, height, image.getType());
        image.getGraphics().drawImage(im, 0, 0 , null);

        WritableImage writableImage = new WritableImage(width, height);
        ImageView view = new ImageView(SwingFXUtils.toFXImage(image, writableImage));

        view.setSmooth(true);
        view.setFitWidth(writableImage.getWidth());
        view.setFitHeight(writableImage.getHeight());
        root.setCenter(view);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
