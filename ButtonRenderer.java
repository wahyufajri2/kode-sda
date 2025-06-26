package model;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ButtonRenderer extends JPanel implements TableCellRenderer {
    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(new JButton("Edit"));
        add(new JButton("Hapus"));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}
