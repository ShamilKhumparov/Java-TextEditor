package JEditor;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

public class UndoAndRedo {
    UndoManager undoManager;
    public void UndoRedo(JMenuItem undo, JMenuItem redo, boolean isUndo, UndoManager undoManager) {
        this.undoManager = undoManager;
        //Run is Undo button is used
        if(isUndo) {
            try {
                undoManager.undo();
            } catch (CannotRedoException cre) {
                cre.printStackTrace();
            }
            undo.setEnabled(undoManager.canUndo());
            redo.setEnabled(undoManager.canRedo());
        }
        //Or run redo
        else{
            try {
                undoManager.redo();
            } catch (CannotRedoException cre) {
                cre.printStackTrace();
            }
            undo.setEnabled(undoManager.canUndo());
            redo.setEnabled(undoManager.canRedo());
        }
    }
}