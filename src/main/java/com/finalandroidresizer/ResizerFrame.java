package com.finalandroidresizer;
/*
 *
 
 Copyright (c) 2014, Sebastian Breit
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

3. Neither the name of the <ORGANIZATION> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 
 * 
 * */

import com.finalandroidresizer.ui.CheckBoxTitledBorder;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class ResizerFrame extends JFrame {

    private static final String LAST_ANDROID_RES_FOLDER_PATH = "lastAndroidResFolderPath";
    private static final String LAST_IOS_RES_FOLDER_PATH = "lastIOSResFolderPath";
    private static final String IOS_ENABLED = "iOSEnabled";
    private static final String ANDROID_ENABLED = "androidEnabled";
    private static final String PROPS_FILE = "resizer.properties";

    private JPanel contentPane;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel panel_1;
    private JLabel lblResourcesDirectory;
    private JButton btnBrowse;
    private JLabel lblAndroidNoDirectorySelected;
    private JLabel lblIOSNoDirectorySelected;
    private JPanel panel_4;
    private JLabel lblDragDrop;
    private JPanel exportPanel;

    static private Properties props;

    private boolean androidEnabled;
    private boolean iOSEnabled;

    /**
     * Launch the application.
     */
    public static void main(final String[] args) {
        System.out.print("Current path: " + new File(".").getAbsolutePath());
        EventQueue.invokeLater(() -> new ResizerFrame(args));
    }

    private void initializeFrameWithProperties(final String[] args) {
        Properties props = getProps();
        androidEnabled = Boolean.valueOf(getProps().getProperty(ANDROID_ENABLED, "true"));
        iOSEnabled = Boolean.valueOf(getProps().getProperty(IOS_ENABLED, "true"));
        String lastAndroidPath = getProps().getProperty(LAST_ANDROID_RES_FOLDER_PATH);
        String lastIOSPath = getProps().getProperty(LAST_IOS_RES_FOLDER_PATH);

        if (args.length < 2) {
            try {
                props.load(new FileInputStream(PROPS_FILE));

                if (lastAndroidPath != null && !lastAndroidPath.isEmpty()) {
                    setAndroidResOutputFile(lastAndroidPath);
                }

                if (lastIOSPath != null && !lastIOSPath.isEmpty()) {
                    setAndroidResOutputFile(lastIOSPath);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (args.length > 1) {
                setAndroidResOutputFile(args[1]);
            }

            if (args.length > 2) {
                setIOSResOutputFile(args[2]);
            }
        }

        setVisible(true);
    }

    static private Properties getProps() {
        if (props == null) {
            props = new Properties();
        }
        return props;
    }

    protected void setAndroidResOutputFile(String resPath) {
        this.androidResOutputFile = new File(resPath);
        if (androidResOutputFile != null) {
            lblAndroidNoDirectorySelected.setText(androidResOutputFile.getAbsolutePath());
        }
        pack();
    }

    protected void setIOSResOutputFile(String resPath) {
        this.iOSResOutputFile = new File(resPath);
        if (iOSResOutputFile != null) {
            lblIOSNoDirectorySelected.setText(iOSResOutputFile.getAbsolutePath());
        }
        pack();
    }

    public void setAndroidEnabled(boolean androidEnabled) {
        this.androidEnabled = androidEnabled;
        EventQueue.invokeLater(() ->
            getProps().setProperty(ANDROID_ENABLED, Boolean.toString(androidEnabled))
        );
    }

    public void setiOSEnabled(boolean iOSEnabled) {
        this.iOSEnabled = iOSEnabled;
        EventQueue.invokeLater(() ->
            getProps().setProperty(IOS_ENABLED, Boolean.toString(iOSEnabled))
        );
    }

    public boolean isAndroidEnabled() {
        return androidEnabled;
    }

    public boolean isiOSEnabled() {
        return iOSEnabled;
    }

    /**
     * Create the frame.
     */
    File androidResOutputFile = null;
    File iOSResOutputFile = null;
    private JLabel lblInputDensity;
    private JComboBox<ImageProcessor.Sizes> inputDensity;
    private JLabel lblInputDirectory;
    private JComboBox<String> inputDirectory;
    private JCheckBox ch_overwrite;
//    private JCheckBox ch_xxxhdpi;
//    private JCheckBox ch_tvdpi;
//    private JCheckBox ch_ldpi;
//    private JCheckBox ch_mdpi;
//    private JCheckBox ch_hdpi;
//    private JCheckBox ch_xhdpi;
//    private JCheckBox ch_xxhdpi;

    public ResizerFrame(String[] args) {
        this();
        EventQueue.invokeLater(() -> initializeFrameWithProperties(args));
    }

    public ResizerFrame() {
        super("Final Android Resizer");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 350);

        createContentPanel();

        createTopPanel();

        createBottomPanel();

        createExportPanel();

        createOptionsPanel();

        createAndroidOutputsPickers();

        createIOSOutputsPickers();

        createInputDensityComboBox();

        createOverrideExistingCheckBox();

        createDragDropPanel();
    }

    private Vector<ImageProcessor.Sizes> getExportFolders() {
        Vector<ImageProcessor.Sizes> ret = new Vector<>();

        for (Component component : exportPanel.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox exportCheckbox = (JCheckBox) component;

                ImageProcessor.Sizes size = (ImageProcessor.Sizes) exportCheckbox.getClientProperty(ImageProcessor.Sizes.class);

                if (exportCheckbox.isSelected() &&
                        (size.isIOS() && isiOSEnabled() || (size.isAndroid() && isAndroidEnabled()))
                ) {
                    ret.add(size);
                }
            }
        }

        return ret;
    }

    private void showError(String text) {
        JOptionPane.showMessageDialog(this, text, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String text) {
        JOptionPane.showMessageDialog(this, text, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void createContentPanel() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gblContentPane = new GridBagLayout();
        gblContentPane.columnWidths = new int[]{0, 0, 0, 0};
        gblContentPane.rowHeights = new int[]{0, 0};
        gblContentPane.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gblContentPane.rowWeights = new double[]{0.0, 0.0};
        contentPane.setLayout(new GridBagLayout());
    }

    private void createTopPanel() {
        topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gblTopPane = new GridBagLayout();
        gblTopPane.columnWidths = new int[]{0, 0, 0, 0};
        gblTopPane.rowHeights = new int[]{0, 0};
        gblTopPane.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gblTopPane.rowWeights = new double[]{0.0, 0.0};
        topPanel.setLayout(gblTopPane);

        GridBagConstraints gbcTopPanelCh = new GridBagConstraints();
        gbcTopPanelCh.insets = new Insets(0, 0, 0, 0);
        gbcTopPanelCh.fill = GridBagConstraints.HORIZONTAL;
        gbcTopPanelCh.gridx = 0;
        gbcTopPanelCh.gridy = 0;

        contentPane.add(topPanel, gbcTopPanelCh);
    }

    private void createBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gblBottomPane = new GridBagLayout();
        gblBottomPane.columnWidths = new int[]{0, 0, 0, 0};
        gblBottomPane.rowHeights = new int[]{0, 0};
        gblBottomPane.columnWeights = new double[]{0.0, 0.0};
        gblBottomPane.rowWeights = new double[]{0.0, 0.0};
        bottomPanel.setLayout(gblBottomPane);

        GridBagConstraints gbcBottomPanelCh = new GridBagConstraints();
        gbcBottomPanelCh.insets = new Insets(0, 0, 0, 0);
        gbcBottomPanelCh.fill = GridBagConstraints.HORIZONTAL;
        gbcBottomPanelCh.gridx = 0;
        gbcBottomPanelCh.gridy = 1;

        contentPane.add(bottomPanel, gbcBottomPanelCh);
    }

    private void createOptionsPanel() {
        panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder("Options"));

        GridBagConstraints gbc_panel_1_ch = new GridBagConstraints();
        gbc_panel_1_ch.insets = new Insets(0, 0, 0, 5);
        gbc_panel_1_ch.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel_1_ch.gridx = 0;
        gbc_panel_1_ch.gridy = 0;


        topPanel.add(panel_1, gbc_panel_1_ch);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[]{0, 0, 0, 0};
        gbl_panel_1.rowHeights = new int[]{0, 0};
        gbl_panel_1.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_panel_1.rowWeights = new double[]{0.0, 0.0};


        panel_1.setLayout(gbl_panel_1);
    }

    private void createInputDensityComboBox() {
        lblInputDensity = new JLabel("Input density");
        GridBagConstraints gbc_lblInputDensity = new GridBagConstraints();
        gbc_lblInputDensity.anchor = GridBagConstraints.WEST;
        gbc_lblInputDensity.insets = new Insets(0, 0, 0, 5);
        gbc_lblInputDensity.gridx = 0;
        gbc_lblInputDensity.gridy = 1;
        panel_1.add(lblInputDensity, gbc_lblInputDensity);

        Vector<ImageProcessor.Sizes> comboBoxItems = new Vector<>();
        comboBoxItems.addAll(Arrays.asList(ImageProcessor.Sizes.values()));

        final DefaultComboBoxModel<ImageProcessor.Sizes> model = new DefaultComboBoxModel<>(comboBoxItems);

        inputDensity = new JComboBox<>(model);
        inputDensity.setSelectedIndex(ImageProcessor.Sizes.values().length - 1);

        GridBagConstraints gbc_inputDensity = new GridBagConstraints();
        gbc_inputDensity.insets = new Insets(0, 0, 0, 5);
        gbc_inputDensity.fill = GridBagConstraints.HORIZONTAL;
        gbc_inputDensity.gridx = 1;
        gbc_inputDensity.gridy = 1;
        panel_1.add(inputDensity, gbc_inputDensity);
    }

    private void createOverrideExistingCheckBox() {
        ch_overwrite = new JCheckBox("Overwrite existing");
        ch_overwrite.setSelected(true);
        GridBagConstraints gbc_ch_overwrite = new GridBagConstraints();
        gbc_ch_overwrite.insets = new Insets(0, 0, 0, 5);
        gbc_ch_overwrite.fill = GridBagConstraints.HORIZONTAL;
        gbc_ch_overwrite.gridx = 0;
        gbc_ch_overwrite.gridy = 2;
        panel_1.add(ch_overwrite, gbc_ch_overwrite);
    }

    private void createAndroidOutputsPickers() {

        JPanel panel_2 = new JPanel();
        CheckBoxTitledBorder checkBoxTitledBorder = new CheckBoxTitledBorder("Android Output", true);
        checkBoxTitledBorder.addItemListener(e -> setAndroidEnabled(e.getStateChange() == ItemEvent.SELECTED));
        panel_2.setBorder(checkBoxTitledBorder);

        GridBagConstraints gbc_panel_2_ch = new GridBagConstraints();
        gbc_panel_2_ch.insets = new Insets(0, 0, 0, 5);
        gbc_panel_2_ch.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel_2_ch.gridx = 0;
        gbc_panel_2_ch.gridy = 1;

        topPanel.add(panel_2, gbc_panel_2_ch);

        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[]{0, 0, 0, 0};
        gbl_panel_2.rowHeights = new int[]{0, 0};
        gbl_panel_2.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[]{0.0, 0.0};
        panel_2.setLayout(gbl_panel_2);

        lblResourcesDirectory = new JLabel("Output directory:");
        GridBagConstraints gbc_lblResourcesDirectory = new GridBagConstraints();
        gbc_lblResourcesDirectory.anchor = GridBagConstraints.WEST;
        gbc_lblResourcesDirectory.insets = new Insets(0, 0, 5, 5);
        gbc_lblResourcesDirectory.gridx = 0;
        gbc_lblResourcesDirectory.gridy = 0;
        panel_2.add(lblResourcesDirectory, gbc_lblResourcesDirectory);

        btnBrowse = new JButton("Browse");
        btnBrowse.addActionListener(event -> {
            JFileChooser j = new JFileChooser();
            j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            Integer returnVal = j.showOpenDialog(btnBrowse);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                androidResOutputFile = j.getSelectedFile();
                lblAndroidNoDirectorySelected.setText(androidResOutputFile.getAbsolutePath());
                try {
                    getProps().setProperty(LAST_ANDROID_RES_FOLDER_PATH, androidResOutputFile.getAbsolutePath());
                    getProps().store(new FileOutputStream(new File(PROPS_FILE)), null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pack();
            }
        });

        GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
        gbc_btnBrowse.insets = new Insets(0, 0, 5, 5);
        gbc_btnBrowse.gridx = 1;
        gbc_btnBrowse.gridy = 0;
        panel_2.add(btnBrowse, gbc_btnBrowse);

        lblAndroidNoDirectorySelected = new JLabel("No directory selected yet");
        GridBagConstraints gbc_lblNoDirectorySelected = new GridBagConstraints();
        gbc_lblNoDirectorySelected.insets = new Insets(0, 0, 5, 20);
        gbc_lblNoDirectorySelected.gridx = 2;
        gbc_lblNoDirectorySelected.gridy = 0;
        panel_2.add(lblAndroidNoDirectorySelected, gbc_lblNoDirectorySelected);

        lblInputDirectory = new JLabel("Output type");
        GridBagConstraints gbc_lblInputDirectory = new GridBagConstraints();
        gbc_lblInputDirectory.anchor = GridBagConstraints.WEST;
        gbc_lblInputDirectory.insets = new Insets(0, 0, 5, 5);
        gbc_lblInputDirectory.gridx = 3;
        gbc_lblInputDirectory.gridy = 0;
        panel_2.add(lblInputDirectory, gbc_lblInputDirectory);

        Vector<String> comboBoxItemsDirectory = new Vector<String>();
        comboBoxItemsDirectory.add("drawable");
        comboBoxItemsDirectory.add("mipmap");

        final DefaultComboBoxModel<String> modelDirectory = new DefaultComboBoxModel<String>(comboBoxItemsDirectory);
        inputDirectory = new JComboBox<String>(modelDirectory);
        inputDirectory.setSelectedIndex(0);
        GridBagConstraints gbc_inputDirectory = new GridBagConstraints();
        gbc_inputDirectory.insets = new Insets(0, 0, 5, 0);
        gbc_inputDirectory.fill = GridBagConstraints.HORIZONTAL;
        gbc_inputDirectory.gridx = 4;
        gbc_inputDirectory.gridy = 0;
        panel_2.add(inputDirectory, gbc_inputDirectory);
    }

    private void createIOSOutputsPickers() {

        JPanel panel_2 = new JPanel();
        CheckBoxTitledBorder checkBoxTitledBorder = new CheckBoxTitledBorder("iOS Output", true);
        checkBoxTitledBorder.addItemListener(e -> setAndroidEnabled(e.getStateChange() == ItemEvent.SELECTED));
        panel_2.setBorder(checkBoxTitledBorder);

        GridBagConstraints gbc_panel_2_ch = new GridBagConstraints();
        gbc_panel_2_ch.insets = new Insets(0, 0, 0, 0);
        gbc_panel_2_ch.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel_2_ch.gridx = 0;
        gbc_panel_2_ch.gridy = 2;

        topPanel.add(panel_2, gbc_panel_2_ch);

        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[]{0, 0, 0, 0};
        gbl_panel_2.rowHeights = new int[]{0, 0};
        gbl_panel_2.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[]{0.0, 0.0};
        panel_2.setLayout(gbl_panel_2);

        lblResourcesDirectory = new JLabel("Output directory:");
        GridBagConstraints gbc_lblResourcesDirectory = new GridBagConstraints();
        gbc_lblResourcesDirectory.anchor = GridBagConstraints.WEST;
        gbc_lblResourcesDirectory.insets = new Insets(0, 0, 5, 5);
        gbc_lblResourcesDirectory.gridx = 0;
        gbc_lblResourcesDirectory.gridy = 0;
        panel_2.add(lblResourcesDirectory, gbc_lblResourcesDirectory);

        btnBrowse = new JButton("Browse");
        btnBrowse.addActionListener(event ->  {
            JFileChooser j = new JFileChooser();
            j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            Integer returnVal = j.showOpenDialog(btnBrowse);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                iOSResOutputFile = j.getSelectedFile();
                lblIOSNoDirectorySelected.setText(iOSResOutputFile.getAbsolutePath());
                try {
                    getProps().setProperty(LAST_IOS_RES_FOLDER_PATH, iOSResOutputFile.getAbsolutePath());
                    getProps().store(new FileOutputStream(new File(PROPS_FILE)), null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pack();
            }
        });

        GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
        gbc_btnBrowse.insets = new Insets(0, 0, 5, 5);
        gbc_btnBrowse.gridx = 1;
        gbc_btnBrowse.gridy = 0;
        panel_2.add(btnBrowse, gbc_btnBrowse);

        lblIOSNoDirectorySelected = new JLabel("No directory selected yet");
        GridBagConstraints gbc_lblNoDirectorySelected = new GridBagConstraints();
        gbc_lblNoDirectorySelected.insets = new Insets(0, 0, 5, 20);
        gbc_lblNoDirectorySelected.gridx = 2;
        gbc_lblNoDirectorySelected.gridy = 0;
        panel_2.add(lblIOSNoDirectorySelected, gbc_lblNoDirectorySelected);
    }

    private void createExportPanel() {
        exportPanel = new JPanel();
        exportPanel.setBorder(new TitledBorder("Export"));

        GridBagConstraints gbc_panel_1_ch = new GridBagConstraints();
        gbc_panel_1_ch.insets = new Insets(0, 0, 0, 0);
        gbc_panel_1_ch.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel_1_ch.anchor = GridBagConstraints.WEST;
        gbc_panel_1_ch.weightx = 1;

        bottomPanel.add(exportPanel, gbc_panel_1_ch);

        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{56, 0};
        gbl_panel.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_panel.columnWeights = new double[]{1.0, 1.0};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        exportPanel.setLayout(gbl_panel);

        JLabel androidLabel = new JLabel("Android");
        GridBagConstraints androidLabelConstraints = new GridBagConstraints();
        androidLabelConstraints.insets = new Insets(0, 10, 5, 5);
        androidLabelConstraints.anchor = GridBagConstraints.NORTHWEST;
        androidLabelConstraints.gridx = 0;
        androidLabelConstraints.gridy = 0;

        exportPanel.add(androidLabel, androidLabelConstraints);

        JLabel iOSLabel = new JLabel("iOS");
        GridBagConstraints iOSLabelConstraints = new GridBagConstraints();
        iOSLabelConstraints.insets = new Insets(0, 10, 5, 5);
        iOSLabelConstraints.anchor = GridBagConstraints.NORTHWEST;
        iOSLabelConstraints.gridx = 1;
        iOSLabelConstraints.gridy = 0;

        exportPanel.add(iOSLabel, iOSLabelConstraints);

        int androidIndex = 0;
        for (ImageProcessor.Sizes size : ImageProcessor.Sizes.values()) {
            int yIndex = (size.isAndroid() ? androidIndex : size.ordinal() - androidIndex) + 1;

            createSizeCheckbox(size.getSize(), size.getDevice().ordinal(), yIndex, exportPanel, size);

            if (size.isAndroid()) {
                androidIndex++;
            }
        }

//        ch_ldpi = new JCheckBox("ldpi");
//        ch_ldpi.setSelected(true);
//        GridBagConstraints gbc_ch_ldpi = new GridBagConstraints();
//        gbc_ch_ldpi.insets = new Insets(0, 0, 5, 5);
//        gbc_ch_ldpi.anchor = GridBagConstraints.NORTHWEST;
//        gbc_ch_ldpi.gridx = 0;
//        gbc_ch_ldpi.gridy = 0;
//        panel.add(ch_ldpi, gbc_ch_ldpi);
//
//        ch_mdpi = new JCheckBox("mdpi");
//        ch_mdpi.setSelected(true);
//        ch_mdpi.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gbc_ch_mdpi = new GridBagConstraints();
//        gbc_ch_mdpi.anchor = GridBagConstraints.WEST;
//        gbc_ch_mdpi.insets = new Insets(0, 0, 5, 5);
//        gbc_ch_mdpi.gridx = 0;
//        gbc_ch_mdpi.gridy = 1;
//        panel.add(ch_mdpi, gbc_ch_mdpi);
//
//        ch_tvdpi = new JCheckBox("tvdpi");
//        GridBagConstraints gbc_ch_tvdpi = new GridBagConstraints();
//        gbc_ch_tvdpi.anchor = GridBagConstraints.WEST;
//        gbc_ch_tvdpi.insets = new Insets(0, 0, 5, 5);
//        gbc_ch_tvdpi.gridx = 0;
//        gbc_ch_tvdpi.gridy = 2;
//        panel.add(ch_tvdpi, gbc_ch_tvdpi);
//
//        ch_hdpi = new JCheckBox("hdpi");
//        ch_hdpi.setSelected(true);
//        GridBagConstraints gbc_ch_hdpi = new GridBagConstraints();
//        gbc_ch_hdpi.anchor = GridBagConstraints.WEST;
//        gbc_ch_hdpi.insets = new Insets(0, 0, 5, 5);
//        gbc_ch_hdpi.gridx = 0;
//        gbc_ch_hdpi.gridy = 3;
//        panel.add(ch_hdpi, gbc_ch_hdpi);
//
//        ch_xhdpi = new JCheckBox("xhdpi");
//        ch_xhdpi.setSelected(true);
//        GridBagConstraints gbc_ch_xhdpi = new GridBagConstraints();
//        gbc_ch_xhdpi.anchor = GridBagConstraints.WEST;
//        gbc_ch_xhdpi.insets = new Insets(0, 0, 5, 5);
//        gbc_ch_xhdpi.gridx = 0;
//        gbc_ch_xhdpi.gridy = 4;
//        panel.add(ch_xhdpi, gbc_ch_xhdpi);
//
//        ch_xxhdpi = new JCheckBox("xxhdpi");
//        ch_xxhdpi.setSelected(true);
//        GridBagConstraints gbc_ch_xxhdpi = new GridBagConstraints();
//        gbc_ch_xxhdpi.anchor = GridBagConstraints.WEST;
//        gbc_ch_xxhdpi.insets = new Insets(0, 0, 5, 5);
//        gbc_ch_xxhdpi.gridx = 0;
//        gbc_ch_xxhdpi.gridy = 5;
//        panel.add(ch_xxhdpi, gbc_ch_xxhdpi);
//
//        ch_xxxhdpi = new JCheckBox("xxxhdpi");
//        ch_xxxhdpi.setSelected(true);
//        GridBagConstraints gbc_ch_xxxhdpi = new GridBagConstraints();
//        gbc_ch_xxxhdpi.insets = new Insets(0, 0, 5, 5);
//        gbc_ch_xxxhdpi.gridx = 0;
//        gbc_ch_xxxhdpi.gridy = 6;
//        panel.add(ch_xxxhdpi, gbc_ch_xxxhdpi);
    }

    private void createSizeCheckbox(String text, int gridX, int gridY, JPanel panel, ImageProcessor.Sizes value) {
        JCheckBox sizeCB = new JCheckBox(text);
        sizeCB.setSelected(true);
        sizeCB.putClientProperty(ImageProcessor.Sizes.class, value);
        GridBagConstraints gbcChSizeCB = new GridBagConstraints();
        gbcChSizeCB.insets = new Insets(0, 0, 5, 5);
        gbcChSizeCB.anchor = GridBagConstraints.WEST;
        gbcChSizeCB.gridx = gridX;
        gbcChSizeCB.gridy = gridY;

        panel.add(sizeCB, gbcChSizeCB);
    }

    private void createDragDropPanel() {
        panel_4 = new JPanel();
        GridBagConstraints gbcChPannel4 = new GridBagConstraints();
        gbcChPannel4.insets = new Insets(0, 0, 0, 0);
        gbcChPannel4.fill = GridBagConstraints.BOTH;
        gbcChPannel4.anchor = GridBagConstraints.EAST;
        gbcChPannel4.weightx = 1;

        bottomPanel.add(panel_4, gbcChPannel4);

        panel_4.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        lblDragDrop = new JLabel("Drag & drop your image(s) here");
        panel_4.add(lblDragDrop, gbc);

        new FileDrop(System.out, panel_4, /* dragBorder, */ this::onFilesDropped);
    }

    private void onFilesDropped(File[] files) {
        if (androidResOutputFile == null && isAndroidEnabled()) {
            showWarning("Please select an Android destination folder first!");
            return;
        }

        if (iOSResOutputFile == null && isiOSEnabled()) {
            showWarning("Please select an iOS destination folder first!");
            return;
        }

        lblDragDrop.setText("Processing..");
        Thread t = new Thread(() -> {
            Vector<ImageProcessor.Sizes> export = getExportFolders();
            Map<File, List<IOSContentsProcessor.ScaledFile>> fileSizeMapForFileProcessor = new HashMap<>();

            for (File file : files) {
                for (ImageProcessor.Sizes size : export) {
                    try {
                        File scaledFile = ImageProcessor.processImage(
                                file,
                                size.isAndroid() ? androidResOutputFile : iOSResOutputFile,
                                (ImageProcessor.Sizes) inputDensity.getSelectedItem(),
                                size.isAndroid() ? (String) inputDirectory.getSelectedItem() : null,
                                ch_overwrite.isSelected(),
                                size
                        );

                        if (size.isIOS()) {
                            fileSizeMapForFileProcessor.computeIfAbsent(file, key -> new ArrayList<>())
                                    .add(new IOSContentsProcessor.ScaledFile(scaledFile, size));
                        }
                    } catch (FileAlreadyExistsException e) {
                        showWarning("The file " + file.getName()
                                + " already exists! This image will not be processed.");
                        break;
                    } catch (IOException e) {
                        showError("An IO Error occurred while processing " + file.getName());
                        e.printStackTrace();
                        break;
                    } catch (NullPointerException e) {
                        showError("The file " + file.getName()
                                + " is not an image and will be omitted");
                        e.printStackTrace();
                        break;
                    }

                    lblDragDrop.setText(lblDragDrop.getText() + ".");
                }
            }

            processIOSFiles(iOSResOutputFile, fileSizeMapForFileProcessor);

            lblDragDrop.setText("Done! Gimme some more...");
        });

        t.start();
    }

    private void processIOSFiles(File resDirectory, Map<File, List<IOSContentsProcessor.ScaledFile>> fileSizeMapForFileProcessor) {
        IOSContentsProcessor iosContentsProcessor = new IOSContentsProcessor();

        for (Map.Entry<File, List<IOSContentsProcessor.ScaledFile>> entry : fileSizeMapForFileProcessor.entrySet()) {
            try {
                iosContentsProcessor.generateContents(resDirectory, entry.getKey(), entry.getValue());
            } catch (IOException e) {
                showError("An IO Error occurred while processing " + entry.getKey().getName());
                e.printStackTrace();
            }
        }
    }
}
