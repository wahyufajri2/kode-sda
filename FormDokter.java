package view;

import koneksi.KoneksiDB;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormDokter extends JPanel {
    private JTextField tfNama, tfSpesialisasi, tfStr;
    private JButton btnSimpan, btnLihat, btnReset;
    private JTable table;
    private DefaultTableModel model;

    public FormDokter() {
        setLayout(new BorderLayout());

        JPanel panelInput = new JPanel(new GridLayout(3, 2, 5, 5));
        tfNama = new JTextField();
        tfSpesialisasi = new JTextField();
        tfStr = new JTextField();

        btnSimpan = new JButton("Simpan");
        btnLihat = new JButton("Lihat Data");
        btnReset = new JButton("Reset");

        panelInput.setBorder(BorderFactory.createTitledBorder("Form Input Dokter"));
        panelInput.add(new JLabel("Nama Dokter:")); panelInput.add(tfNama);
        panelInput.add(new JLabel("Spesialisasi:")); panelInput.add(tfSpesialisasi);
        panelInput.add(new JLabel("No. STR:")); panelInput.add(tfStr);

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButton.add(btnSimpan);
        panelButton.add(btnLihat);
        panelButton.add(btnReset);

        model = new DefaultTableModel(new String[]{"ID", "Nama", "Spesialisasi", "No. STR", "Aksi"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);

        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(table, new AksiEditHapus() {
            @Override
            public void onEdit(int row) {
                tfNama.setText(model.getValueAt(row, 1).toString());
                tfSpesialisasi.setText(model.getValueAt(row, 2).toString());
                tfStr.setText(model.getValueAt(row, 3).toString());
            }

            @Override
            public void onHapus(int id) {
                try (Connection conn = KoneksiDB.getConnection()) {
                    int konfirmasi = JOptionPane.showConfirmDialog(null, "Yakin hapus data ini?", "Hapus", JOptionPane.YES_NO_OPTION);
                    if (konfirmasi == JOptionPane.YES_OPTION) {
                        conn.createStatement().executeUpdate("DELETE FROM dokter WHERE id=" + id);
                        tampilkanDokter();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Gagal hapus: " + e.getMessage());
                }
            }
        }));

        JScrollPane scrollPane = new JScrollPane(table);

        btnSimpan.addActionListener(e -> simpanDokter());
        btnLihat.addActionListener(e -> tampilkanDokter());
        btnReset.addActionListener(e -> resetDokter());

        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(panelInput, BorderLayout.CENTER);
        panelAtas.add(panelButton, BorderLayout.SOUTH);

        add(panelAtas, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void simpanDokter() {
        try {
            Connection conn = KoneksiDB.getConnection();
            String sql = "INSERT INTO dokter (nama, spesialisasi, no_str) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tfNama.getText());
            stmt.setString(2, tfSpesialisasi.getText());
            stmt.setString(3, tfStr.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data dokter berhasil disimpan.");
            resetDokter();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data dokter: " + ex.getMessage());
        }
    }

    private void tampilkanDokter() {
        model.setRowCount(0);
        try {
            Connection conn = KoneksiDB.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM dokter");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("spesialisasi"),
                        rs.getString("no_str")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal tampilkan data dokter: " + ex.getMessage());
        }
    }

    private void resetDokter() {
        tfNama.setText("");
        tfSpesialisasi.setText("");
        tfStr.setText("");
    }
}
