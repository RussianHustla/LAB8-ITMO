package clientUI.animations;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

public class Shake {
    private TranslateTransition tt;

    public Shake(Node node) {
        tt = new TranslateTransition(Duration.millis(70), node);
        tt.setFromX(0);
        tt.setByX(10f);
        tt.setCycleCount(2);
        tt.setAutoReverse(true);
    }

    public void play() {
        tt.playFromStart();
    }
}
