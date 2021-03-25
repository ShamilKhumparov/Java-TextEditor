package JEditor;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.multi.MultiLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.io.IOException;

class Main {

    public static void main(String args[]) throws IOException, UnsupportedLookAndFeelException {
        Editor e = new Editor();
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        e.editor();
    }
}