package pl.polsl.wkiro.recognizer.gui;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractPanel extends JPanel {

    public AbstractPanel() {
        super(new BorderLayout());
        initialize();
    }

    protected abstract void initialize();
}
