/*
 * Created by JFormDesigner on Tue Jul 11 23:19:02 IDT 2023
 */

package sakancom.pages;

import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

/**
 * @author amroo
 */
public class OwnerPage extends JFrame {

    private final HashMap<String, Object> ownerData;
    public OwnerPage(HashMap<String, Object> ownerData) {

        this.ownerData = ownerData;
        initComponents();
        setTitle("Owner Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Amro

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Amro
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
