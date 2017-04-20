package pl.polsl.wkiro.recognizer.gui;

import pl.polsl.wkiro.recognizer.dto.Settings;
import pl.polsl.wkiro.recognizer.utils.Dictionary;
import pl.polsl.wkiro.recognizer.utils.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigurationPanel extends AbstractPanel implements ActionListener {

    private static final Logger log = new Logger(ConfigurationPanel.class);

    private JPanel configCheckboxPanel;
    private JCheckBox saveResultsJsonCheckbox;
    private JCheckBox saveMatchedFramesCheckbox;
    private JCheckBox drawRecognitionPlateCheckbox;
    private JCheckBox drawFoundPlateCheckbox;
    private JPanel configValuePanel;
    private JLabel topResultsLabel;
    private JSpinner topResultsJSpinner;
    private JLabel frameOffsetLabel;
    private JSpinner frameOffsetSpinner;
    private JLabel plateRegionLabel;
    private JComboBox plateRegionCombo;
    private JLabel countryCodeLabel;
    private JComboBox countryCodeCombo;

    private static final String DEFAULT_REGION = Dictionary.Regions.EUROPE;

    public Settings getSettings() {
        Settings settings = new Settings();
        settings.setProcessingParameters((Integer)topResultsJSpinner.getValue(), (Integer)frameOffsetSpinner.getValue());
        settings.setExportFlags(
                saveResultsJsonCheckbox.isSelected(),
                saveMatchedFramesCheckbox.isSelected(),
                drawRecognitionPlateCheckbox.isSelected(),
                drawFoundPlateCheckbox.isSelected());
        settings.setRegions((String)plateRegionCombo.getSelectedItem(), (String)countryCodeCombo.getSelectedItem());

        return settings;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() instanceof JComboBox) {
            if(e.getSource().equals(plateRegionCombo)) {
                handlePlateRegionChange();
            }
        }
    }

    @Override
    protected void initialize() {

        setBorder(new TitledBorder("Configuration"));

        configValuePanel = new JPanel(new GridLayout(1, 4));
        topResultsLabel = new JLabel("Top match results:");
        topResultsJSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
        configValuePanel.add(new JPanel() {{
            add(topResultsLabel);
            add(topResultsJSpinner);
        }});
        frameOffsetLabel = new JLabel("Frame skipping offset ms:");
        frameOffsetSpinner = new JSpinner(new SpinnerNumberModel(1000, 10, 60000, 10));
        configValuePanel.add(new JPanel() {{
            add(frameOffsetLabel);
            add(frameOffsetSpinner);
        }});
        plateRegionLabel = new JLabel("Plate region:");
        plateRegionCombo = new JComboBox(Dictionary.getRegions().toArray());
        plateRegionCombo.setSelectedItem(DEFAULT_REGION);
        plateRegionCombo.addActionListener(this);
        configValuePanel.add(new JPanel() {{
            add(plateRegionLabel);
            add(plateRegionCombo);
        }});
        countryCodeLabel = new JLabel("Specific region code:");
        countryCodeCombo = new JComboBox(Dictionary.getSpecificRegions(DEFAULT_REGION).toArray());
        configValuePanel.add(new JPanel() {{
            add(countryCodeLabel);
            add(countryCodeCombo);
        }});
        add(configValuePanel, BorderLayout.NORTH);

        configCheckboxPanel = new JPanel(new GridLayout(1, 3));
        saveResultsJsonCheckbox = new JCheckBox("Export results to JSON", true);
        saveResultsJsonCheckbox.addActionListener(this);
        configCheckboxPanel.add(saveResultsJsonCheckbox);
        saveMatchedFramesCheckbox = new JCheckBox("Save frames with match", true);
        saveMatchedFramesCheckbox.addActionListener(this);
        configCheckboxPanel.add(saveMatchedFramesCheckbox);
        drawRecognitionPlateCheckbox = new JCheckBox("Draw recognition on plate", true);
        drawRecognitionPlateCheckbox.addActionListener(this);
        configCheckboxPanel.add(drawRecognitionPlateCheckbox);
        drawFoundPlateCheckbox = new JCheckBox("Select found plate", true);
        drawFoundPlateCheckbox.addActionListener(this);
        configCheckboxPanel.add(drawFoundPlateCheckbox);
        add(configCheckboxPanel);
    }

    private void handlePlateRegionChange() {
        String selectedRegion = (String)plateRegionCombo.getSelectedItem();
        java.util.List<String> specificRegions = Dictionary.getSpecificRegions(selectedRegion);
        countryCodeCombo.setModel(new DefaultComboBoxModel(specificRegions.toArray()));
    }
}
