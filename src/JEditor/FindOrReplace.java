package JEditor;
import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import javax.swing.text.*;

public class FindOrReplace {
    Highlighter.HighlightPainter paint = new DefaultHighlighter.DefaultHighlightPainter(Color.cyan);
    //Find and replace word/words
    public void FindAndReplaceFunk(Document doc, JTextField searchT, JTextField replaceT){
        try{
            //Remove all old highlights
            doc.area.getHighlighter().removeAllHighlights();
            //Word to replace
            String word = searchT.getText();
            //Word to replace with
            String replaceWord = replaceT.getText();
            //Whole document
            String text = doc.area.getText(0, doc.area.getDocument().getLength());
            //Length of the word to replace
            int wordLength = word.length();
            //Length of the word to replace with
            int repLength = replaceWord.length();
            int offset = 0;

            //Not run if word to replace and with is empty and textpane is empty
            if(!word.isEmpty() && !replaceWord.isEmpty() && !text.isEmpty()){
                //Run to end of the document
                while ((offset = text.indexOf(word, offset)) != -1) {
                    //Locate and get length of the word to replace
                    doc.area.select(offset, offset + wordLength);
                    //Replace with new word(s)
                    doc.area.replaceSelection(replaceWord);

                    //Add highlights to the new word(s)
                    doc.area.getHighlighter().addHighlight(offset, offset + repLength, paint);
                    //Set new length of the doc
                    offset += repLength;
                    text = doc.area.getText(0, doc.area.getDocument().getLength());
                }
            }
        } catch (BadLocationException ble) {}
    }

    //Search and highlights word(0)s
    public void FindAndHighlight(Document doc, JTextField searchT){
        try{
            //Remove all old highlights
            doc.area.getHighlighter().removeAllHighlights();
            //Word to replace
            String word = searchT.getText();
            //Whole document
            String text = doc.area.getText(0, doc.area.getDocument().getLength());
            //Length of the word to replace
            int wordLength = word.length();
            int offset = 0;
            //Not Run if word and textpane is empty
            if(!word.isEmpty() && !text.isEmpty()) {
                //Run to end of the document
                while ((offset = text.indexOf(word, offset)) != -1) {
                    //Find length of the word(s)
                    doc.area.select(offset, offset + wordLength);
                    //Highlights the word(s)
                    doc.area.getHighlighter().addHighlight(offset, offset + wordLength, paint);
                    offset += wordLength;
                }
            }
        } catch (BadLocationException ble) {}
    }
}


