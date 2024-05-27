package org.example.view;

import javafx.animation.*;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChangePrimarySceneTheme {
    public static void setThemeOnTButton(Map<String, HBox> hBoxMap, ToggleButton toggleButton, AnchorPane anchorPane, Circle circle) {
        TranslateTransition translate = new TranslateTransition(Duration.seconds(0.5), circle);
        FillTransition fillTransition = new FillTransition(Duration.seconds(0.5), circle);
        if (toggleButton.getStyleClass().contains("button-light")) {
            toggleButton.setDisable(true);
            toggleButton.getStyleClass().remove("button-light");
            toggleButton.getStyleClass().add("button-dark");

            fillTransition.setToValue(Color.WHITE);
            fillTransition.setFromValue(Color.rgb(73, 117, 159, 1));

            translate.setByX(30);

            animateButtonBackgroundColor(toggleButton, Color.WHITE, Color.rgb(73, 117, 159, 1));

            translate.setOnFinished(e -> toggleButton.setDisable(false));

            translate.play();
            fillTransition.play();
            List<HBox> collect = getBoxList(hBoxMap);
            for (var h : collect) { h.setBackground(Background.fill(Color.rgb(25, 25, 25, 0.9))); }

        } else {
            toggleButton.setDisable(true);
            toggleButton.getStyleClass().remove("button-dark");
            toggleButton.getStyleClass().add("button-light");

            fillTransition.setToValue(Color.rgb(73, 117, 159, 1));
            fillTransition.setFromValue(Color.WHITE);

            translate.setByX(-30);
            animateButtonBackgroundColor(toggleButton, Color.rgb(73, 117, 159, 1), Color.WHITE);

            translate.setOnFinished(e -> toggleButton.setDisable(false));

            translate.play();
            fillTransition.play();
            anchorPane.setBackground(Background.fill(Color.rgb(234, 252, 248, 0.5)));
            List<HBox> collect = getBoxList(hBoxMap);
            for (var h : collect) { h.setBackground(Background.fill(Color.rgb(234, 252, 248, 0.5))); }
        }
    }

    private static List<HBox> getBoxList(Map<String, HBox> hBoxMap) {
        List<HBox> collect = hBoxMap.entrySet().stream()
                .filter(entry -> Integer.parseInt(entry.getKey().substring(1)) >= 3)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        return collect;
    }

    private static void animateButtonBackgroundColor(ToggleButton button, Color startColor, Color endColor) {
        String rgbStart = String.format("rgba(%d, %d, %d, 1)",
                (int) (startColor.getRed() * 255),
                (int) (startColor.getGreen() * 255),
                (int) (startColor.getBlue() * 255));
        String rgbEnd = String.format("rgba(%d, %d, %d, 1)",
                (int) (endColor.getRed() * 255),
                (int) (endColor.getGreen() * 255),
                (int) (endColor.getBlue() * 255));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(button.styleProperty(), "-fx-background-color: " + rgbStart + ";")),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(button.styleProperty(), "-fx-background-color: " + rgbEnd + ";"))
        );
        timeline.play();
    }
}
