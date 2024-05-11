package org.example.controller;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.Date;

abstract class BarController extends Pane {
    public abstract Node getStructure();
    public abstract String getName();
    public abstract Date getDate();
}
