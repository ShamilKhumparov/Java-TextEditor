package JEditor;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;


public class Settings  {

    Properties hotkeyProperties;
    ResourceBundle langPack = ResourceBundle.getBundle("strings");
    JFrame settingsWindow;
    JPanel container;
    JPanel settingsPanel;
    JPanel langPanel;
    JPanel hotkeyPanel;
    JButton languages;
    JButton hotkeys;
    JButton saveSettings;

    //Labels for the hotkey options
    JLabel newDocLabel;
    JLabel openDocLabel;
    JLabel saveLabel;
    JLabel saveAsLabel;
    JLabel renameLabel;
    JLabel closeTabLabel;
    JLabel undoLabel;
    JLabel redoLabel;

    //Textfields for each hotkey option
    JTextField newDoc;
    JTextField openDoc;
    JTextField save;
    JTextField saveAs;
    JTextField rename;
    JTextField closeTab;
    JTextField undo;
    JTextField redo;

    //Hotkey variables used for saving to preferences
    String hotkey1;
    String hotkey2;
    String hotkey3;
    String hotkey4;
    String hotkey5;
    String hotkey6;
    String hotkey7;
    String hotkey8;


    //Dropdown list for languages
    static final String[] langList = {"English", "Norsk"};

    Settings() throws IOException {

        //Loads the hotkey properties file
        hotkeyProperties = new Properties();
        String userPref = "src/hotkeys.properties";
        FileInputStream loadHotkeyPref = new FileInputStream(userPref);
        hotkeyProperties.load(loadHotkeyPref);

        //Settings window properties
        settingsWindow = new JFrame(langPack.getString("settings"));
        settingsWindow.setSize(700,800);
        settingsWindow.setVisible(true);

        //Container to include multiple JPanels
        container = new JPanel();
        container.setLayout(new GridLayout(1,2));

        //Properties for the settings buttons
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        languages = new JButton(langPack.getString("langsetting"));
        hotkeys = new JButton(langPack.getString("shortcut"));
        saveSettings = new JButton(langPack.getString("save"));

        //Adds the buttons to the settings menu
        settingsPanel.add(Box.createRigidArea(new Dimension(5, 20)));
        settingsPanel.add(languages);
        settingsPanel.add(Box.createRigidArea(new Dimension(5, 20)));
        settingsPanel.add(hotkeys);
        settingsPanel.add(Box.createRigidArea(new Dimension(5, 120)));
        settingsPanel.add(saveSettings);
        container.add(settingsPanel);
        settingsWindow.add(container);
        settingsPanel.setBackground(Color.white);
        //Adds properties to the Language setting panel
        langPanel = new JPanel();
        langPanel.setLayout(new BoxLayout(langPanel, BoxLayout.Y_AXIS));
        JComboBox<String> langDropDown = new JComboBox<String>(langList);
        langPanel.add(Box.createRigidArea(new Dimension(5,20)));
        langDropDown.setMaximumSize(new Dimension(100, langDropDown.getMinimumSize().height));
        langDropDown.setEditable(false);
        langDropDown.setVisible(false);
        langPanel.add(langDropDown);

        //Adds properties to the Hotkey setting panel
        hotkeyPanel = new JPanel();

        hotkeyPanel.setLayout(new BoxLayout(hotkeyPanel, BoxLayout.Y_AXIS));

        //Add all the labels and input fields into the panel
        //New document
        newDocLabel = new JLabel(langPack.getString("newdocument"));
        newDoc = new JTextField(4);
        newDoc.setText(hotkeyProperties.getProperty("newdoc"));
        newDoc.setMaximumSize(new Dimension(150,newDoc.getMinimumSize().height));
        //Open document
        openDocLabel = new JLabel(langPack.getString("opendocument"));
        openDoc = new JTextField(4);
        openDoc.setText(hotkeyProperties.getProperty("opendoc"));
        openDoc.setMaximumSize(new Dimension(150,openDoc.getMinimumSize().height));
        //Save document
        saveLabel = new JLabel(langPack.getString("save"));
        save = new JTextField(4);
        save.setText(hotkeyProperties.getProperty("save"));
        save.setMaximumSize(new Dimension(150,save.getMinimumSize().height));
        //Save document as
        saveAsLabel = new JLabel(langPack.getString("saveAs"));
        saveAs = new JTextField(4);
        saveAs.setText(hotkeyProperties.getProperty("saveas"));
        saveAs.setMaximumSize(new Dimension(150,save.getMinimumSize().height));
        //Rename document
        renameLabel = new JLabel(langPack.getString("rename"));
        rename = new JTextField(4);
        rename.setText(hotkeyProperties.getProperty("rename"));
        rename.setMaximumSize(new Dimension(150,rename.getMinimumSize().height));
        //Close tab
        closeTabLabel = new JLabel(langPack.getString("closetab"));
        closeTab = new JTextField(4);
        closeTab.setText(hotkeyProperties.getProperty("closetab"));
        closeTab.setMaximumSize(new Dimension(150,closeTab.getMinimumSize().height));
        //Undo
        undoLabel = new JLabel(langPack.getString("undo"));
        undo = new JTextField(4);
        undo.setText(hotkeyProperties.getProperty("undo"));
        undo.setMaximumSize(new Dimension(150,undo.getMinimumSize().height));
        //Redo
        redoLabel = new JLabel(langPack.getString("redo"));
        redo = new JTextField(4);
        redo.setText(hotkeyProperties.getProperty("redo"));
        redo.setMaximumSize(new Dimension(150,undo.getMinimumSize().height));


        //Disables visibility of hotkey settings
        newDocLabel.setVisible(false);
        newDoc.setVisible(false);
        openDocLabel.setVisible(false);
        openDoc.setVisible(false);
        saveLabel.setVisible(false);
        save.setVisible(false);
        saveAsLabel.setVisible(false);
        saveAs.setVisible(false);
        renameLabel.setVisible(false);
        rename.setVisible(false);
        closeTabLabel.setVisible(false);
        closeTab.setVisible(false);
        undoLabel.setVisible(false);
        undo.setVisible(false);
        redoLabel.setVisible(false);
        redo.setVisible(false);


        //Adds all components into the hotkey panel
        hotkeyPanel.add(Box.createRigidArea(new Dimension(5,20)));
        hotkeyPanel.add(newDocLabel);
        hotkeyPanel.add(newDoc);
        hotkeyPanel.add(Box.createRigidArea(new Dimension(5,10)));
        hotkeyPanel.add(openDocLabel);
        hotkeyPanel.add(openDoc);
        hotkeyPanel.add(Box.createRigidArea(new Dimension(5,10)));
        hotkeyPanel.add(saveLabel);
        hotkeyPanel.add(save);
        hotkeyPanel.add(Box.createRigidArea(new Dimension(5,10)));
        hotkeyPanel.add(saveAsLabel);
        hotkeyPanel.add(saveAs);
        hotkeyPanel.add(Box.createRigidArea(new Dimension(5,10)));
        hotkeyPanel.add(renameLabel);
        hotkeyPanel.add(rename);
        hotkeyPanel.add(Box.createRigidArea(new Dimension(5,10)));
        hotkeyPanel.add(closeTabLabel);
        hotkeyPanel.add(closeTab);
        hotkeyPanel.add(Box.createRigidArea(new Dimension(5,10)));
        hotkeyPanel.add(undoLabel);
        hotkeyPanel.add(undo);
        hotkeyPanel.add(Box.createRigidArea(new Dimension(5,10)));
        hotkeyPanel.add(redoLabel);
        hotkeyPanel.add(redo);


        //Adds everything to the container
        container.add(langPanel);
        container.add(hotkeyPanel);

        //Activates the dropdown list for languages when clicking on 'Languages' settings
        languages.addActionListener(e-> {

            langDropDown.setVisible(true);
            newDocLabel.setVisible(false);
            newDoc.setVisible(false);
            openDocLabel.setVisible(false);
            openDoc.setVisible(false);
            saveLabel.setVisible(false);
            save.setVisible(false);
            saveAsLabel.setVisible(false);
            saveAs.setVisible(false);
            renameLabel.setVisible(false);
            rename.setVisible(false);
            closeTabLabel.setVisible(false);
            closeTab.setVisible(false);
            undoLabel.setVisible(false);
            undo.setVisible(false);
            redoLabel.setVisible(false);
            redo.setVisible(false);

        });

        //Activates the settings for hotkeys when clicking on "hotkey" button
        hotkeys.addActionListener(e-> {

            langDropDown.setVisible(false);
            newDocLabel.setVisible(true);
            newDoc.setVisible(true);
            openDocLabel.setVisible(true);
            openDoc.setVisible(true);
            saveLabel.setVisible(true);
            save.setVisible(true);
            saveAsLabel.setVisible(true);
            saveAs.setVisible(true);
            renameLabel.setVisible(true);
            rename.setVisible(true);
            closeTabLabel.setVisible(true);
            closeTab.setVisible(true);
            undoLabel.setVisible(true);
            undo.setVisible(true);
            redoLabel.setVisible(true);
            redo.setVisible(true);

        });

        //Adds event if Save button is pressed
        saveSettings.addActionListener(e-> {
            JOptionPane.showMessageDialog(null, langPack.getString("settingsrestart"));
            //Saving Language settings
            //Gets the name of the chosen language
            String selectedLang;
            selectedLang = Objects.requireNonNull(langDropDown.getSelectedItem()).toString();

            //Loads the config file to update with newly selected language
            FileInputStream in = null;
            try {
                in = new FileInputStream("src/setlanguage.properties");
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            Properties props = new Properties();
            try {
                props.load(in);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            //Overwrites lang properties file with the matching selected language
            if (selectedLang.equals("English")){

                try {
                    FileOutputStream out = new FileOutputStream("src/setlanguage.properties");
                    props.setProperty("language","en");
                    props.store(out,null);
                    out.close();
                } catch (IOException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }
            else if (selectedLang.equals("Norsk")){
                try {
                    FileOutputStream out = new FileOutputStream("src/setlanguage.properties");
                    props.setProperty("language","no");
                    props.store(out,null);
                    out.close();
                } catch (IOException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }


            //Loads and overwrites the hotkeys preferences file
            //Loading the hotkey file
            FileInputStream in2 = null;
            try {
                in2 = new FileInputStream("src/hotkeys.properties");
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            Properties prop2 = new Properties();
            try {
                prop2.load(in2);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                in2.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            //Saving Hotkey settings
            //Gets the desired hotkeys for the various features

            //New doc
            if(newDoc.getText() != null){
                hotkey1 = newDoc.getText();
            }
            //Open doc
            if(openDoc.getText() != null){
                hotkey2 = openDoc.getText();
            }
            //Save doc
            if(save.getText() != null){
                hotkey3 = save.getText();
            }
            //Save doc as
            if(saveAs.getText() != null){
                hotkey4 = saveAs.getText();
            }
            //Rename doc
            if(rename.getText() != null){
                hotkey5 = rename.getText();
            }
            //Close tab
            if(closeTab.getText() != null){
                hotkey6 = closeTab.getText();
            }
            //Undo
            if(undo.getText() != null){
                hotkey7 = undo.getText();
            }
            //Redo
            if(redo.getText() != null){
                hotkey8 = redo.getText();
            }

            //Overwriting the hotkey file
            try {
                FileOutputStream out2 = new FileOutputStream("src/hotkeys.properties");
                props.setProperty("newdoc",hotkey1);
                props.setProperty("opendoc",hotkey2);
                props.setProperty("save",hotkey3);
                props.setProperty("saveas",hotkey4);
                props.setProperty("rename",hotkey5);
                props.setProperty("closetab",hotkey6);
                props.setProperty("undo",hotkey7);
                props.setProperty("redo",hotkey8);

                props.store(out2,null);
                out2.close();
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            settingsWindow.dispose();
        });
    }

}
