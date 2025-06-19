package view;

import koneksi.KoneksiDB;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormDokter extends JPanel {
    private JTextField tfNama, tfSpesialisasi, tfStr;
    private JButton btnSimpan, btnLihat;
    private JTable table;
    private DefaultTableModel model;

    public FormDokter() {
        setLayout(new BorderLayout());

        JPanel panelInput = new JPanel(new GridLayout(4, 2, 5, 5));
        tfNama = new JTextField();
        tfSpesialisasi = new JTextField();
        tfStr = new JTextField();
        btnSimpan = new JButton("Simpan");
        btnLihat = new JButton("Lihat Data");

        panelInput.setBorder(BorderFactory.createTitledBorder("Form Input Dokter"));
        panelInput.add(new JLabel("Nama Dokter:")); panelInput.add(tfNama);
        panelInput.add(new JLabel("Spesialisasi:")); panelInput.add(tfSpesialisasi);
        panelInput.add(new JLabel("No. STR:")); panelInput.add(tfStr);
        panelInput.add(btnSimpan); panelInput.add(btnLihat);

        model = new DefaultTableModel(new String[]{"ID", "Nama", "Spesialisasi", "No. STR"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        btnSimpan.addActionListener(e -> simpanDokter());
        btnLihat.addActionListener(e -> tampilkanDokter());

        add(panelInput, BorderLayout.NORTH);
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
            tfNama.setText(""); tfSpesialisasi.setText(""); tfStr.setText("");
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
}
