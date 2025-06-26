package model;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton btnEdit, btnHapus;
    private JTable table;
    private AksiEditHapus listener;

    public ButtonEditor(JTable table, AksiEditHapus listener) {
        this.table = table;
        this.listener = listener;

        panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");

        panel.add(btnEdit);
        panel.add(btnHapus);

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            listener.onEdit(row);
            fireEditingStopped();
        });

        btnHapus.addActionListener(e -> {
            int row = table.getSelectedRow();
            int id = Integer.parseInt(table.getValueAt(row, 0).toString());
            listener.onHapus(id);
            fireEditingStopped();
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}
