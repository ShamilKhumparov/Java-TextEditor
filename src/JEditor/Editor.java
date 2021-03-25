package JEditor;

import java.awt.*;
import javax.print.Doc;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.*;
import java.util.List;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;


import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;


//Main class
class Editor extends JFrame implements ActionListener  {
    JFrame f;
    JTabbedPane tabPanel;
    List<Document> documents = new ArrayList<>();
    String currLang;
    ResourceBundle langPack = ResourceBundle.getBundle("strings");
    Properties hotkeyProperties;

    static JMenuItem saveMItem;
    static final String defaultFont = "Arial";
    static final int defaultFontSize = 12;
    static final String[] fontList = {"Arial", "Calibri", "Cambria", "Times New Roman",
            "Comic Sans MS", "SansSerif", "Helvetica", "Verdana"};
    static final String[] fontSizes = {"12", "14", "16", "18", "20", "22", "24", "26", "28", "30"};
    static String[] textAlignments = new String[0];
    static final char BULLET = '\u2022';
    static final String BULLETSTRING = BULLET + " ";

    public void editor() throws IOException {
        FontButtonActionListener fontButtonListener = new FontButtonActionListener();
        f = new JFrame("Editor");

        //Loads user preferences for hotkeys
        //Loads file containing hotkeys set by user
        hotkeyProperties = new Properties();
        String userPref = "src/hotkeys.properties";
        FileInputStream loadHotkeyPref = new FileInputStream(userPref);
        hotkeyProperties.load(loadHotkeyPref);

        //Sets the standard language to be English
        Locale.setDefault(new Locale("en", "EN"));
        langPack = ResourceBundle.getBundle("strings");

        //Sets the language to Norwegian on startup if defined in properties
        currLang = getCurrentLang();
        if (currLang.equals("no")) {
            Locale.setDefault(new Locale("no", "NO"));
            langPack = ResourceBundle.getBundle("strings");
        }

        textAlignments = new String[]{langPack.getString("left"), langPack.getString("center"),
                langPack.getString("right"), langPack.getString("justified")};

        documents.add(new Document(langPack.getString("untitled"), 0));

        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu(langPack.getString("file"));
        JMenu m2 = new JMenu(langPack.getString("edit"));
        JButton m3 = new JButton("", new ImageIcon("src/images/settings.png") {
        });

        //File menu button
        JMenuItem newMItem = new JMenuItem(langPack.getString("new"));
        JMenuItem openMItem = new JMenuItem(langPack.getString("open"));
        saveMItem = new JMenuItem(langPack.getString("save"));
        JMenuItem saveAsMItem = new JMenuItem(langPack.getString("saveAs"));
        JMenuItem renameMItem = new JMenuItem(langPack.getString("rename"));
        JMenuItem closeMItem = new JMenuItem(langPack.getString("closetab"));

        //Edit meny buttons
        JMenuItem undoItem = new JMenuItem(langPack.getString("undo"));
        JMenuItem redoItem = new JMenuItem(langPack.getString("redo"));

        //Setting File tooltips
        newMItem.setToolTipText(langPack.getString("newtooltip"));
        openMItem.setToolTipText(langPack.getString("opentooltip"));
        saveMItem.setToolTipText(langPack.getString("savetooltip"));
        saveAsMItem.setToolTipText(langPack.getString("saveastooltip"));
        renameMItem.setToolTipText(langPack.getString("renametooltip"));
        closeMItem.setToolTipText(langPack.getString("closetooltip"));

        //Loads the hotkey configured in the user pref file
        //New doc
        KeyStroke newDocKey = KeyStroke.getKeyStroke("control " + hotkeyProperties.getProperty("newdoc"));
        newMItem.setAccelerator(newDocKey);
        //Open doc
        KeyStroke openDocKey = KeyStroke.getKeyStroke("control " + hotkeyProperties.getProperty("opendoc"));
        openMItem.setAccelerator(openDocKey);
        //Save doc
        KeyStroke saveDocKey = KeyStroke.getKeyStroke("control " + hotkeyProperties.getProperty("save"));
        saveMItem.setAccelerator(saveDocKey);
        //Save as
        KeyStroke saveAsKey = KeyStroke.getKeyStroke("control " + hotkeyProperties.getProperty("saveas"));
        saveAsMItem.setAccelerator(saveAsKey);
        //Rename doc
        KeyStroke renameDocKey = KeyStroke.getKeyStroke("control " + hotkeyProperties.getProperty("rename"));
        renameMItem.setAccelerator(renameDocKey);
        //Close tab
        KeyStroke closeTabKey = KeyStroke.getKeyStroke("control " + hotkeyProperties.getProperty("closetab"));
        closeMItem.setAccelerator(closeTabKey);
        //Undo action
        KeyStroke undoKey = KeyStroke.getKeyStroke("control " + hotkeyProperties.getProperty("undo"));
        undoItem.setAccelerator(undoKey);
        //Redo action
        KeyStroke redoKey = KeyStroke.getKeyStroke("control " + hotkeyProperties.getProperty("redo"));
        redoItem.setAccelerator(redoKey);

        //Adding buttons
        m1.add(newMItem);
        m1.add(openMItem);
        m1.addSeparator();
        m1.add(saveMItem);
        m1.add(saveAsMItem);
        m1.addSeparator();
        m1.add(renameMItem);
        m1.add(closeMItem);
        mb.add(m1);


        //Edit menu button
        mb.add(m2);
        m2.add(undoItem);
        m2.add(redoItem);
        m2.addSeparator();
        JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText(langPack.getString("copy"));
        JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText(langPack.getString("paste"));
        JMenuItem cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText(langPack.getString("cut"));
        m2.add(copy);
        m2.add(paste);
        m2.add(cut);

        //Setting Edit tooltips
        undoItem.setToolTipText(langPack.getString("undotooltip"));
        redoItem.setToolTipText(langPack.getString("redotooltip"));
        copy.setToolTipText(langPack.getString("copytooltip"));
        paste.setToolTipText(langPack.getString("pastetooltip"));
        cut.setToolTipText(langPack.getString("cuttooltip"));


        //New button listener
        newMItem.addActionListener(e -> { newFile();
        });

        //Open button listener
        openMItem.addActionListener(e -> { openFile();
        });

        //Save button listener
        saveMItem.addActionListener(e -> { saveFile();
        });

        //Save button listener
        saveAsMItem.addActionListener(e -> {
            saveAsFile();
        });

        //Save button listener
        renameMItem.addActionListener(e -> {
            renameFile();
        });

        //Close tab button listener
        closeMItem.addActionListener(e -> {
            closeFile();
        });

        //Edit menu button
        mb.add(m2);
        mb.add(Box.createHorizontalGlue());
        //Settings menu button
        mb.add(m3);
        m3.addActionListener(e -> {
            try {
                new Settings();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        //Font Buttons color and style buttons
        JButton bold = new JButton(new StyledEditorKit.BoldAction());
        bold.setText("B");
        bold.setFont(new Font(defaultFont, Font.BOLD, 18));
        bold.addActionListener(fontButtonListener);
        JButton italic = new JButton(new StyledEditorKit.ItalicAction());
        italic.setText("I");
        italic.setFont(new Font(defaultFont, Font.ITALIC, 18));
        italic.addActionListener(fontButtonListener);
        JButton underline = new JButton(new StyledEditorKit.UnderlineAction());
        underline.setText("U");
        underline.setFont(new Font(defaultFont, Font.PLAIN, 18));
        underline.addActionListener(fontButtonListener);


        JTextField replaceT = new JTextField(18);
        JTextField searchT = new JTextField(18);
        JButton SearchB = new JButton(langPack.getString("search"));
        JButton ReplaceB = new JButton(langPack.getString("replace"));


        JButton color = new JButton(langPack.getString("textcolor"));
        JButton shadowColor = new JButton(langPack.getString("shadowcolor"));
        JButton addImage = new JButton(langPack.getString("image")) ;

        //Remove/Insert bulletpoint and number buttons
        JButton insertBullet = new JButton(langPack.getString("bulletinsert"));
        JButton removeBullet = new JButton(langPack.getString("bulletremove"));
        JButton insertNumber = new JButton(langPack.getString("numberinsert"));
        JButton removeNumber = new JButton(langPack.getString("numberremove"));

        //Sets toolbar button tooltips
        bold.setToolTipText(langPack.getString("boldtooltip"));
        italic.setToolTipText(langPack.getString("italictooltip"));
        underline.setToolTipText(langPack.getString("underlinetooltip"));
        color.setToolTipText(langPack.getString("textcolortooltip"));
        shadowColor.setToolTipText(langPack.getString("shadowcolortooltip"));
        addImage.setToolTipText(langPack.getString("imagetooltip"));
        insertBullet.setToolTipText(langPack.getString("insertbullettooltip"));
        removeBullet.setToolTipText(langPack.getString("removebullettooltip"));
        insertNumber.setToolTipText(langPack.getString("insertnumbertooltip"));
        removeNumber.setToolTipText(langPack.getString("removenumbertooltip"));
        SearchB.setToolTipText(langPack.getString("searchtooltip"));
        ReplaceB.setToolTipText(langPack.getString("replacetooltip"));

        //Dropdown menus for font style and text alignment
        JComboBox<String> fontDropDown = new JComboBox<>(fontList);
        fontDropDown.setEditable(false);
        JComboBox<String> fontSizeDropDown = new JComboBox<>(fontSizes);
        fontSizeDropDown.setEditable(false);
        JComboBox<String> alignmentDropDown = new JComboBox<>(textAlignments);
        alignmentDropDown.setEditable(false);

        //Dropdown menu tooltips
        fontDropDown.setToolTipText(langPack.getString("fonttooltip"));
        fontSizeDropDown.setToolTipText(langPack.getString("fontsizetooltip"));
        alignmentDropDown.setToolTipText(langPack.getString("alignmenttooltip"));

        //Panel for toolbar buttons
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(bold);
        panel.add(italic);
        panel.add(underline);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(color);
        panel.add(shadowColor);
        panel.add(addImage);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(fontDropDown);
        panel.add(fontSizeDropDown);
        panel.add(alignmentDropDown);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JSeparator(SwingConstants.VERTICAL));

        panel.add(searchT);
        panel.add(SearchB);
        panel.add(replaceT);
        panel.add(ReplaceB);


        panel.add(new JSeparator(SwingConstants.HORIZONTAL));

        panel.add(insertBullet);
        panel.add(removeBullet);
        panel.add(insertNumber);
        panel.add(removeNumber);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel2.add(searchT);
        panel2.add(SearchB);
        panel2.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel2.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel2.add(replaceT);
        panel2.add(ReplaceB);


        //Toolbar panel
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.PAGE_AXIS));
        toolbar.add(panel);
        toolbar.add(panel2);

        //JScrollPane scrollPane = new JScrollPane(t);

        f.setJMenuBar(mb);
        f.add(toolbar, BorderLayout.NORTH);
        //f.add(t);
        //f.add(scrollPane,BorderLayout.CENTER);
        f.setSize(1500, 1000);
        f.setVisible(true);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);


        //Tabs
        tabPanel = new JTabbedPane();
        //icon
        f.add(tabPanel);
        newTab(documents.get(0));
        tabPanel.addChangeListener(e -> {
            if (documents.size() > 1)
                saveMItem.setEnabled(!documents.get(tabPanel.getSelectedIndex()).saved);
        });

        documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();

        FindOrReplace FOR = new FindOrReplace();

        //Undo and Redo
        UndoAndRedo UR = new UndoAndRedo();

        undoItem.addActionListener(e -> {
            UR.UndoRedo(undoItem, redoItem, true, documents.get(tabPanel.getSelectedIndex()).undoManager);
            saveMItem.setEnabled(true);
            documents.get(tabPanel.getSelectedIndex()).saved = false;
        });
        redoItem.addActionListener(e -> {
            UR.UndoRedo(undoItem, redoItem, false, documents.get(tabPanel.getSelectedIndex()).undoManager);
            saveMItem.setEnabled(true);
            documents.get(tabPanel.getSelectedIndex()).saved = false;
        });


        //Change text color
        ResourceBundle finalLangPack = langPack;
        color.addActionListener(e -> {
            //Shows color menu
            Color newColor = JColorChooser.showDialog(f, finalLangPack.getString("colorselect"), Color.BLACK);

            //Checks if user actually picked a color
            if (newColor == null) {
                documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
                return;
            }
            //Adds the color to selected and new text
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, newColor);
            documents.get(tabPanel.getSelectedIndex()).area.setCharacterAttributes(attr, false);
            documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
            saveMItem.setEnabled(true);
            color.setForeground(newColor);
            documents.get(tabPanel.getSelectedIndex()).saved = false;
        });

        //Change text shadow color
        ResourceBundle finalLangPack1 = langPack;
        shadowColor.addActionListener(e -> {
            //Shows color menu
            Color newColor = JColorChooser.showDialog(f, finalLangPack1.getString("colorselect"), Color.WHITE);

            //Checks if user actually picked a color
            if (newColor == null) {
                documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
                return;
            }
            //Adds the new shadow to selected and new text
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setBackground(attr, newColor);
            documents.get(tabPanel.getSelectedIndex()).area.setCharacterAttributes(attr, false);
            documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
            saveMItem.setEnabled(true);
            shadowColor.setBackground(newColor);
            documents.get(tabPanel.getSelectedIndex()).saved = false;
        });

        addImage.addActionListener(e -> {
            chooseImg(documents.get(tabPanel.getSelectedIndex()));
        });

        //Change Font and alignment listeners
        fontDropDown.addItemListener(e -> {
            String font = (String) e.getItem();
            fontDropDown.setAction(new StyledEditorKit.FontFamilyAction(font, font));
            documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
            saveMItem.setEnabled(true);
            documents.get(tabPanel.getSelectedIndex()).saved = false;
        });

        fontSizeDropDown.addItemListener(e -> {
            String curSize = (String) e.getItem();
            int newSize = Integer.parseInt(curSize);
            fontSizeDropDown.setAction(new StyledEditorKit.FontSizeAction(curSize, newSize));
            documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
            saveMItem.setEnabled(true);
            documents.get(tabPanel.getSelectedIndex()).saved = false;

        });

        alignmentDropDown.addItemListener(e -> {
            String curAlignment = (String) e.getItem();
            int newAlignment = alignmentDropDown.getSelectedIndex();
            alignmentDropDown.setAction(new StyledEditorKit.AlignmentAction(curAlignment, newAlignment));
            documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
            saveMItem.setEnabled(true);
            documents.get(tabPanel.getSelectedIndex()).saved = false;
        });


        alignmentDropDown.addItemListener(e -> {
            String curAlignment = (String) e.getItem();
            int newAlignment = alignmentDropDown.getSelectedIndex();
            alignmentDropDown.setAction(new StyledEditorKit.AlignmentAction(curAlignment, newAlignment));
            documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
            saveMItem.setEnabled(true);
            documents.get(tabPanel.getSelectedIndex()).saved = false;
        });



        SearchB.addActionListener(e -> {
            FOR.FindAndHighlight(documents.get(tabPanel.getSelectedIndex()), searchT);
        });

        ReplaceB.addActionListener(e -> {
            FOR.FindAndReplaceFunk(documents.get(tabPanel.getSelectedIndex()), searchT, replaceT);
            saveMItem.setEnabled(true);
            documents.get(tabPanel.getSelectedIndex()).saved = false;
        });

        //Remove and insert bullet/number listeners
        insertBullet.addActionListener(e -> {
            try {
                bulletAction("Insert");
                saveMItem.setEnabled(true);
                documents.get(tabPanel.getSelectedIndex()).saved = false;
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        });
        removeBullet.addActionListener(e -> {
            try {
                bulletAction("Remove");
                saveMItem.setEnabled(true);
                documents.get(tabPanel.getSelectedIndex()).saved = false;
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        });
        insertNumber.addActionListener(e -> {
            try {
                numberAction("Insert");
                saveMItem.setEnabled(true);
                documents.get(tabPanel.getSelectedIndex()).saved = false;
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        });
        removeNumber.addActionListener(e -> {
            try {
                numberAction("Remove");
                saveMItem.setEnabled(true);
                documents.get(tabPanel.getSelectedIndex()).saved = false;
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        });

    }

    // If a button is pressed
    public void actionPerformed(ActionEvent e) {

    }

    //Class that puts the cursor back in the textpane after clicking a button.
    private class FontButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
            saveMItem.setEnabled(true);
            documents.get(tabPanel.getSelectedIndex()).saved = false;
        }
    }

    //New file
    public void newFile() {
        Object[] options = {langPack.getString("thistab"),langPack.getString("newtab"),
                langPack.getString("cancel")};

        //User decides new or current tab
        int n = JOptionPane.showOptionDialog(f,
                langPack.getString("newdocumentwhere"),
                langPack.getString("newdocument"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);

        if (n != JOptionPane.CANCEL_OPTION) {
            //This tab
            if (n == JOptionPane.OK_OPTION) {
                int currentIndex = tabPanel.getSelectedIndex();

                documents.remove(currentIndex);
                Document doc = new Document(langPack.getString("untitled"), currentIndex);
                documents.add(doc);

                tabPanel.setTitleAt(currentIndex, doc.name);
                tabPanel.setComponentAt(currentIndex, doc.s);
            }
            //New tab
            else if (n == JOptionPane.NO_OPTION) {
                int newIndex = tabPanel.getTabCount() - 1;

                Document doc = new Document(langPack.getString("untitled"), newIndex);
                documents.add(doc);

                newTab(doc);
                tabPanel.setSelectedIndex(newIndex + 1);
            }
        } else {
            System.out.println("Cancelling new document");
        }
    }

    //Open file
    public void openFile() {
        Document doc = new Document("", tabPanel.getTabCount() - 1);
        documents.add(doc);

        //Getting previous used filechooser
        Document curDoc = documents.get(tabPanel.getSelectedIndex());
        int response = curDoc.fc.showOpenDialog(this);

        //If file is chosen
        if (response == JFileChooser.APPROVE_OPTION) {
            doc.file = new File(curDoc.fc.getSelectedFile().getAbsolutePath());
            doc.name = doc.file.getName();

            //If .rtf
            if (doc.name.contains(".rtf")) {
                try {
                    FileReader fr = new FileReader(doc.file);
                    RTFEditorKit kit = new RTFEditorKit();

                    //If tab is chosen
                    if (tabChoice(doc)) {
                        doc.area.setContentType("text/rtf");
                        kit.read(fr, doc.area.getDocument(), 0);
                        fr.close();
                        doc.rtf = true;
                    }

                } catch (Exception e) {
                    System.out.println("The file cannot be opened...\n\n" + e);
                    JOptionPane.showMessageDialog(null, "The file cannot be opened...");
                    documents.remove(documents.size() - 1);
                }
            }
            //If .txt
            else if (doc.name.contains(".txt")) {
                try {
                    String line;
                    StringBuilder content;

                    FileReader fr = new FileReader(doc.file);
                    BufferedReader reader = new BufferedReader(fr);
                    content = new StringBuilder(reader.readLine());

                    while ((line = reader.readLine()) != null) {
                        content.append("\n").append(line);
                    }

                    //If tab is chosen
                    if (tabChoice(doc)) {
                        doc.area.setText(content.toString());
                        doc.rtf = false;
                    }

                } catch (Exception e) {
                    System.out.println("The file cannot be opened...\n\n" + e);
                    JOptionPane.showMessageDialog(null, langPack.getString("openfileerror"));
                    documents.remove(documents.size() - 1);
                }
            } else {
                System.out.println("File format is not supported...");
                JOptionPane.showMessageDialog(null, langPack.getString("fileformaterror"));
            }
            doc.saved = true;
            saveMItem.setEnabled(false);
        } else {
            System.out.println("Cancelling opening document");
            documents.remove(documents.size() - 1);
        }

    }

    //Save file
    public void saveFile() {
        Document doc = documents.get(tabPanel.getSelectedIndex());
        doc.saved = true;
        saveMItem.setEnabled(false);

        if (doc.file.exists()) {
            writeToFile(doc);
        } else {
            Object[] options = {langPack.getString("confirm"),langPack.getString("cancel")};
            int n = JOptionPane.showOptionDialog(f,
                    langPack.getString("savefileas"),
                    langPack.getString("savedoc"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);

            if (n == JOptionPane.OK_OPTION) {
                saveAsFile();
            } else {
                doc.saved = false;
                saveMItem.setEnabled(true);
            }
        }
    }

    //SaveAs file
    public void saveAsFile() {
        Document doc = documents.get(tabPanel.getSelectedIndex());

        int response = doc.fc.showSaveDialog(null);

        //If file is chosen/made
        if (response == JFileChooser.APPROVE_OPTION) {
            doc.file = new File(doc.fc.getSelectedFile().getAbsolutePath());
            doc.name = doc.file.getName();

            writeToFile(doc);
            saveMItem.setEnabled(false);
            doc.saved = true;
        } else {
            System.out.println("Cancelling saving as");
        }
    }

    //Rename file
    public void renameFile() {
        Document doc = documents.get(tabPanel.getSelectedIndex());
        String format;

        if (doc.file.exists()) {
            String newName = JOptionPane.showInputDialog(langPack.getString("newname"));

            if (newName != null) {

                if (doc.rtf)
                    format = ".rtf";
                else
                    format = ".txt";

                File file2 = new File(doc.file.getParent() + File.separator + newName + format);

                if (!file2.exists()) {

                    System.out.println(doc.file.getPath());
                    System.out.println(file2.getPath());

                    Path source = Paths.get(doc.file.getPath());
                    try {
                        Files.move(source, source.resolveSibling(newName + format));
                        doc.file = file2;
                        doc.name = newName + format;
                        tabPanel.setTitleAt(tabPanel.getSelectedIndex(), doc.name);

                    } catch (Exception e) {
                        System.out.println("The file cannot be renamed...");
                        JOptionPane.showMessageDialog(null, langPack.getString("renameError"));
                    }

                } else {
                    System.out.println("A file with that name already exists...");
                    JOptionPane.showMessageDialog(null, langPack.getString("filealreadyexists"));
                }
            }
        } else {
            System.out.println("Save your file before renaming");
            JOptionPane.showMessageDialog(null, langPack.getString("savebeforerename"));
        }

    }

    //Close file
    public void closeFile() {
        int index = tabPanel.getSelectedIndex();
        if (saveMItem.isEnabled()) {
            Object[] options = {langPack.getString("saveandclose"),langPack.getString("notsaveandclose"),
                    langPack.getString("cancel")};
            int n = JOptionPane.showOptionDialog(f,
                    langPack.getString("savebeforeclose"),
                    langPack.getString("closedoc"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            if (n != JOptionPane.CANCEL_OPTION) {
                //Save
                if (n == JOptionPane.OK_OPTION) {
                    saveFile();
                    tabPanel.remove(index);
                    documents.remove(index);
                }
                //Just close
                else if (n == JOptionPane.NO_OPTION) {
                    tabPanel.remove(index);
                    documents.remove(index);
                }
            } else {
                System.out.println("Cancelling closing document...");
            }
        } else {
            tabPanel.remove(index);
            documents.remove(index);
        }

        if (documents.size() == 0) {
            Document doc = new Document(langPack.getString("untitled"), 0);
            documents.add(doc);
            newTab(doc);
        }
    }

    //Makes a new tab
    public void newTab(Document doc) {
        //new Font(defaultFont,0,defaultFontSize);
        doc.area.setFont(new Font(defaultFont, Font.PLAIN, defaultFontSize));
        //Set focus to textpane by default
        doc.s.requestFocusInWindow();

        tabPanel.addTab(doc.name, null, doc.s, null);
    }


    //Prompts user for which tab to use
    public boolean tabChoice(Document doc) {
        //User decides new or current tab
        Object[] options = {langPack.getString("thistab"),langPack.getString("newtab"),
                langPack.getString("cancel")};
        int n = JOptionPane.showOptionDialog(f,
                langPack.getString("opendocumentwhere"),
                langPack.getString("opendocument"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);

        if (n != JOptionPane.CANCEL_OPTION) {
            //This tab
            if (n == JOptionPane.OK_OPTION) {
                int currentIndex = tabPanel.getSelectedIndex();

                documents.remove(currentIndex);

                tabPanel.setTitleAt(currentIndex, doc.name);
                tabPanel.setComponentAt(currentIndex, doc.s);
            }
            //New tab
            else if (n == JOptionPane.NO_OPTION) {
                newTab(doc);
                tabPanel.setSelectedIndex(tabPanel.getTabCount() - 1);
            }
            return true;
        } else {
            System.out.println("Cancelling opening document");
            documents.remove(documents.size() - 1);
            return false;
        }
    }

    public void writeToFile(Document doc) {
        //If .rtf
        if (doc.name.contains(".rtf")) {
            try {
                RTFEditorKit kit = new RTFEditorKit();
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(doc.file));
                kit.write(out, doc.area.getDocument(), 0, doc.area.getDocument().getLength());
                out.close();

                tabPanel.setTitleAt(tabPanel.getSelectedIndex(), doc.name);
                doc.rtf = true;
            } catch (Exception e) {
                System.out.println("The file cannot be saved as...\n\n" + e);
                JOptionPane.showMessageDialog(null, langPack.getString("savedaserror"));
            }
        }
        //If .txt
        else if (doc.name.contains(".txt")) {
            try {
                FileWriter fw = new FileWriter(doc.file, false);

                BufferedWriter writer = new BufferedWriter(fw);

                writer.write(doc.area.getText());
                writer.flush();
                writer.close();
                tabPanel.setTitleAt(tabPanel.getSelectedIndex(), doc.name);
                doc.rtf = false;

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, langPack.getString("savedaserror"));
            }
        } else {
            System.out.println("File format is not supported...");
            JOptionPane.showMessageDialog(null, langPack.getString("fileformaterror"));
        }
    }

    //Adds or removes a bullet point in front of selected text.
    public void bulletAction(String action) throws BadLocationException {
        //Gets selected text
        String selectedText = documents.get(tabPanel.getSelectedIndex()).area.getSelectedText();

        //Return if no text is selected
        if (selectedText == null || (selectedText.trim().isEmpty())) {
            documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
            return;
        }

        //Gets paragraphs and start of paragraph
        StyledDocument doc = getEditorDocument();
        Element p = doc.getParagraphElement(documents.get(tabPanel.getSelectedIndex()).area.getSelectionStart());
        int pStart = p.getStartOffset();
        int pEnd;

        //Loops through the selected text
        do {
            p = doc.getParagraphElement(pStart);
            pEnd = p.getEndOffset();

            //Check if empty line
            if ((pEnd - pStart) <= 1) {
                pStart = pEnd;
            }

            //Switch for insert or remove action
            switch (action) {
                case "Insert":
                    try {
                        if (!isBulleted(pStart)) {
                            //Inserts the bulletpoint at the start of the selection
                            getEditorDocument().insertString(pStart, BULLETSTRING, getAttributes(pStart));
                        }
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                case "Remove":
                    try {
                        if (isBulleted(pStart)) {
                            //Removes bulletpoint from the start of the selection
                            getEditorDocument().remove(pStart, BULLETSTRING.length());
                        }
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
            }

            p = doc.getParagraphElement(pStart);
            pEnd = p.getEndOffset();
            pStart = pEnd;

        } while (pEnd <= documents.get(tabPanel.getSelectedIndex()).area.getSelectionEnd());

        documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
    }

    //Checks if selected text is already bulleted
    public boolean isBulleted(int pStart) throws BadLocationException {
        String firstChar = documents.get(tabPanel.getSelectedIndex()).area.getText(pStart,1);
        char c = firstChar.charAt(0);
        return c == BULLET;
    }

    //Gets attributes i.e color so the bullet point and number matches
    public AttributeSet getAttributes(int pos) {
        StyledDocument doc = (DefaultStyledDocument) documents.get(tabPanel.getSelectedIndex()).area.getDocument();
        Element element = doc.getCharacterElement(pos);
        return element.getAttributes();
    }

    //Adds or removes a number in front of selected text
    private void numberAction(String action) throws BadLocationException {
        int n = 0;

        //Gets selected text
        String selectedText = documents.get(tabPanel.getSelectedIndex()).area.getSelectedText();

        //Return if no text is selected
        if (selectedText == null || (selectedText.trim().isEmpty())) {
            documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();
            return;
        }

        StyledDocument doc = getEditorDocument();
        Element p = doc.getParagraphElement(documents.get(tabPanel.getSelectedIndex()).area.getSelectionStart());
        int pStart = p.getStartOffset();
        int pEnd;

        //Loop through selected text
        do {
            p = doc.getParagraphElement(pStart);
            pEnd = p.getEndOffset();

            if ((pEnd - pStart) <= 1) { //Check if empty line
                pStart = pEnd;
            }

            switch (action) {
                case "Insert":
                    if (isBulleted(pStart)) { //Return if text is already bulleted
                        break;
                    }
                    if (!isNumbered(pStart)) { //Add number if its not already numbered
                        Integer nextNum = ++n;
                        try {
                            //Insert the next number at the start of the selected text
                            getEditorDocument().insertString(pStart, getNumberString(nextNum),
                                    getAttributesNumber(pStart, nextNum));
                        } catch (BadLocationException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    break;
                case "Remove":
                    if (isNumbered(pStart)) { //If numbered-remove it
                        getEditorDocument().remove(pStart, getNumberLength(pStart));
                    }
                    break;
            }
            p = doc.getParagraphElement(pStart);
            pEnd = p.getEndOffset();
            pStart = pEnd;

        } while (pEnd <= documents.get(tabPanel.getSelectedIndex()).area.getSelectionEnd());

        documents.get(tabPanel.getSelectedIndex()).area.requestFocusInWindow();

    }
    private void chooseImg(Document doc) {
        int option=doc.fc.showOpenDialog(this);
        // If user chooses to insert..
        if(option==JFileChooser.APPROVE_OPTION) {
            File file=doc.fc.getSelectedFile();
            if(isImage(file)) {
                // Insert the icon
                doc.area.insertIcon(new ImageIcon(file.getAbsolutePath()));
                saveMItem.setEnabled(true);
                documents.get(tabPanel.getSelectedIndex()).saved = false;
            }
            else
                // Show an error message, if not an image
                JOptionPane.showMessageDialog(this,"The file is not an image.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    //Check if selected file is a image
    private boolean isImage(File file) {
        String name=file.getName();
        return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".gif");
    }

    //Check if selected text is already numbered
    private boolean isNumbered(int pStart) throws BadLocationException {
        AttributeSet attributeSet = getAttributes(pStart);
        Integer pNum = (Integer) attributeSet.getAttribute("Number");
        String firstChar = documents.get(tabPanel.getSelectedIndex()).area.getText(pStart, 1);
        char c = firstChar.charAt(0);
        boolean num = Character.isDigit(c);

        return pNum != null && num;
    }

    //Get number string to place in front of text
    public String getNumberString(Integer next) {
        return next.toString() + "." + " ";
    }

    //Get the attributes for the number
    private AttributeSet getAttributesNumber(int pStart, Integer n) {
        AttributeSet attributeSet = getAttributes(pStart);
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet(attributeSet);
        simpleAttributeSet.addAttribute("Number", n);
        return simpleAttributeSet;

    }

    //Get length of number string
    private int getNumberLength(int pStart) {
        AttributeSet attributeSet = getAttributes(pStart);
        Integer pNum = (Integer) attributeSet.getAttribute("Number");
        return pNum.toString().length() + 2; //Number + . and " "
    }

    private StyledDocument getEditorDocument() {
        return (StyledDocument) documents.get(tabPanel.getSelectedIndex()).area.getDocument();
    }

    //Gets the language currently set in properties
    public String getCurrentLang() throws IOException {

        Properties currLang = new Properties();
        String langProperties = "src/setlanguage.properties";
        FileInputStream loadFile = new FileInputStream(langProperties);

        if (loadFile != null) {
            currLang.load(loadFile);
        } else {
            throw new FileNotFoundException(currLang + "not found.\n");
        }

        System.out.println(currLang.getProperty("language"));
        return currLang.getProperty("language");
    }
}


