package JEditor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class Document implements KeyListener {
    File file;
    JTextPane area = new JTextPane();
    JFileChooser fc = new JFileChooser(JFileChooser.getDefaultLocale().getDisplayName());   //So user opens at correct directory location
    JScrollPane s;

    String name;
    int tabIndex;
    boolean saved = false;
    boolean rtf = true;
    UndoManager undoManager = new UndoManager();

    Editor editor = new Editor();
    boolean numbered;
    boolean bulleted;
    int prevStart;
    String prevText;

    Document(String name, int tabIndex) {
        this.name = name;
        this.tabIndex = tabIndex;

        TextLineNumber lineNr = new TextLineNumber(area);

        fc.addChoosableFileFilter(new FileNameExtensionFilter(".rtf", ".rtf"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(".txt", ".txt"));


        //Make textarea scrollable
        s = new JScrollPane(area);
        s.setRowHeaderView(lineNr);

        area.addKeyListener(this);
        file = new File("");

        area.getDocument().addUndoableEditListener(
                e -> {
                    undoManager.addEdit(e.getEdit());
                });
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //Enable saving on edited document
        saved = false;
        Editor.saveMItem.setEnabled(true);
        area.getHighlighter().removeAllHighlights();

    }

    @Override
    public void keyPressed(KeyEvent e) {

        bulleted = false;
        numbered = false;
        int pos = area.getCaretPosition();
        StyledDocument doc = area.getStyledDocument();
        Element p = doc.getParagraphElement(pos);
        try {
            //Return if text is not bulleted or numbered
            if(!isBulleted(p.getStartOffset()) && !isNumbered(p.getStartOffset())) {
                return;
            }
        } catch (BadLocationException badLocationException) {
            badLocationException.printStackTrace();
        }
        //Gets text details on press of enter
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("Enter");
            try {
                getDetails(pos);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {
        //Return if text is not bulleted or numbered so no nothing gets added
        if(!bulleted && !numbered) {
            return;
        }
        //If it is a bulleted list
        if(bulleted) {
            //Runs the bulletAction function when enter is released
            if(e.getKeyCode() == KeyEvent.VK_ENTER){
                try {
                    bulletAction();
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
            }
        }
        //If it is a numbered list
        if(numbered) {
            //Runs the numberAction function when enter is released
            if(e.getKeyCode() == KeyEvent.VK_ENTER){
                try {
                    numberAction();
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
            }
        }
    }

    //Get text details: Bulleted or not, text and start
    private void getDetails(int pos) throws BadLocationException {
        pos -= 1;
        StyledDocument doc = (StyledDocument) area.getDocument();
        Element p = doc.getParagraphElement(pos);
        //If bulleted set bool to true and get details
        if(isBulleted(p.getStartOffset())) {
            bulleted = true;
            prevStart = p.getStartOffset();
            prevText=doc.getText(prevStart, (p.getEndOffset()-prevStart));
        }
        //If numbered set numbered to true and get details
        else if(isNumbered(p.getStartOffset())) {
            numbered = true;
            prevStart = p.getStartOffset();
            prevText=doc.getText(prevStart, (p.getEndOffset()-prevStart));
        }
    }

    //Runs when enter is released and text is a bulleted list.
    //Adds new bullet point on next line or removes it if current line has no text.
    private void bulletAction() throws BadLocationException {
        System.out.println("Enter Released Bullet");
        StyledDocument doc = (StyledDocument) area.getDocument();
        //Check if line is empty
        if(prevText.length() < 4) {
            doc.remove(prevStart, (Editor.BULLETSTRING.length()+1));
            area.setCaretPosition(prevStart);
            return;
        }
        doc.insertString(area.getCaretPosition(), Editor.BULLETSTRING, getAttributes(prevStart));
    }

    //Runs when enter is pressed and text is a numbered list.
    //Adds new number on next line or removes it if current line has no text.
    private void numberAction() throws BadLocationException {
        System.out.println("Enter Released Number");
        StyledDocument doc = (StyledDocument) area.getDocument();
        AttributeSet attributeSet = getAttributes(prevStart);
        Integer n = (Integer) attributeSet.getAttribute("Number");
        int len = n.toString().length()+3;
        //Check if line is empty
        if(prevText.length() == len) {
            doc.remove(prevStart,len);
            area.setCaretPosition(prevStart);
            return;
        }
        Integer num = getNumber(prevStart);
        num++;
        doc.insertString(area.getCaretPosition(),editor.getNumberString(num), getAttributesNumber(prevStart,num));

        //Get details of inserted number
        Element newP = doc.getParagraphElement(area.getCaretPosition());
        int newEnd = newP.getEndOffset();
        if(newEnd > doc.getLength()) { //Return if no text after insert
            return;
        }
        //If there is text after number, gets details of this text.
        Element nextP = doc.getParagraphElement(newEnd +1);
        int nextStart = nextP.getStartOffset();
        //Check if text is a numbered list.
        if(isNumbered(nextStart)) {
            boolean nextIsNumbered = true;
            System.out.println("Jadsf");
            //While the following text is numbered, loop and replace the numbers after new inserted number
            while (nextIsNumbered) {
                Integer oldNum = getNumber(nextStart);
                num++;
                //replace numbers
                DefaultStyledDocument defaultDoc = (DefaultStyledDocument) area.getDocument();
                defaultDoc.replace(nextStart, editor.getNumberString(oldNum).length(),
                        editor.getNumberString(num),getAttributesNumber(nextStart,num));
                int nextEnd = nextP.getEndOffset();
                int nextPos = nextEnd +1;
                if(nextPos > doc.getLength()) { //Return if no more text
                    return;
                }
                nextP = doc.getParagraphElement(nextPos);
                nextStart = nextP.getStartOffset();
                nextIsNumbered = isNumbered(nextStart);
            }
        }
    }
    //Get current number in list
    private Integer getNumber(int pStart) {
        AttributeSet attributeSet = getAttributes(pStart);
        return (Integer) attributeSet.getAttribute("Number");
    }
    //Check if text is has bullet point
    public boolean isBulleted(int pStart) throws BadLocationException {
        String firstChar = area.getText(pStart,1);
        char c = firstChar.charAt(0);
        return c == Editor.BULLET;
    }
    //Get text attributes i.e color
    public AttributeSet getAttributes(int pos) {
        StyledDocument doc = (DefaultStyledDocument) area.getDocument();
        Element element = doc.getCharacterElement(pos);
        return  element.getAttributes();
    }
    //Get number attribute
    public AttributeSet getAttributesNumber(int pStart, Integer n) {
        AttributeSet attributeSet = getAttributes(pStart);
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet(attributeSet);
        simpleAttributeSet.addAttribute("Number", n);
        return simpleAttributeSet;
    }
    //Check if text is a numbered list
    private boolean isNumbered(int pStart) throws BadLocationException {
        AttributeSet attributeSet = getAttributes(pStart);
        Integer pNum = (Integer) attributeSet.getAttribute("Number");
        String firstChar = area.getText(pStart, 1);
        char c = firstChar.charAt(0);
        boolean num = Character.isDigit(c);
        return pNum != null && num;
    }
}

