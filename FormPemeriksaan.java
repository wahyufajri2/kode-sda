package view;

import koneksi.KoneksiDB;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class FormPemeriksaan extends JPanel {
    private JComboBox<String> cbPasien, cbDokter;
    private JTextField tfTanggal;
    private JTextArea taKeluhan, taDiagnosa, taTerapi;
    private JButton btnSimpan, btnLihat, btnReset;
    private JTable table;
    private DefaultTableModel model;

    private Map<String, Integer> pasienMap = new HashMap<>();
    private Map<String, Integer> dokterMap = new HashMap<>();

    public FormPemeriksaan() {
        setLayout(new BorderLayout());

        JPanel panelInput = new JPanel(new GridLayout(7, 2, 5, 5));
        cbPasien = new JComboBox<>();
        cbDokter = new JComboBox<>();
        tfTanggal = new JTextField();
        taKeluhan = new JTextArea(1, 20);
        taDiagnosa = new JTextArea(1, 20);
        taTerapi = new JTextArea(1, 20);
        btnSimpan = new JButton("Simpan");
        btnLihat = new JButton("Lihat Data");
        btnReset = new JButton("Reset");

        loadComboData();

        panelInput.setBorder(BorderFactory.createTitledBorder("Form Pemeriksaan"));
        panelInput.add(new JLabel("Pasien:")); panelInput.add(cbPasien);
        panelInput.add(new JLabel("Dokter:")); panelInput.add(cbDokter);
        panelInput.add(new JLabel("Tanggal (YYYY-MM-DD):")); panelInput.add(tfTanggal);
        panelInput.add(new JLabel("Keluhan:")); panelInput.add(new JScrollPane(taKeluhan));
        panelInput.add(new JLabel("Diagnosa:")); panelInput.add(new JScrollPane(taDiagnosa));
        panelInput.add(new JLabel("Terapi:")); panelInput.add(new JScrollPane(taTerapi));

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButton.add(btnSimpan);
        panelButton.add(btnLihat);
        panelButton.add(btnReset);

        model = new DefaultTableModel(new String[]{"ID", "Pasien", "Dokter", "Tanggal", "Keluhan", "Diagnosa", "Terapi", "Aksi"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        table = new JTable(model);
        table.setRowHeight(40);
        table.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(table, new AksiEditHapus() {
            @Override
            public void onEdit(int row) {
                cbPasien.setSelectedItem(model.getValueAt(row, 1).toString());
                cbDokter.setSelectedItem(model.getValueAt(row, 2).toString());
                tfTanggal.setText(model.getValueAt(row, 3).toString());
                taKeluhan.setText(model.getValueAt(row, 4).toString());
                taDiagnosa.setText(model.getValueAt(row, 5).toString());
                taTerapi.setText(model.getValueAt(row, 6).toString());
            }

            @Override
            public void onHapus(int id) {
                try (Connection conn = KoneksiDB.getConnection()) {
                    int konfirmasi = JOptionPane.showConfirmDialog(null, "Yakin hapus data ini?", "Hapus", JOptionPane.YES_NO_OPTION);
                    if (konfirmasi == JOptionPane.YES_OPTION) {
                        conn.createStatement().executeUpdate("DELETE FROM pemeriksaan WHERE id=" + id);
                        tampilkanData();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Gagal hapus data: " + ex.getMessage());
                }
            }
        }));

        JScrollPane scrollPane = new JScrollPane(table);

        btnSimpan.addActionListener(e -> simpanData());
        btnLihat.addActionListener(e -> tampilkanData());
        btnReset.addActionListener(e -> resetData());

        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(panelInput, BorderLayout.CENTER);
        panelAtas.add(panelButton, BorderLayout.SOUTH);

        add(panelAtas, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadComboData() {
        try (Connection conn = KoneksiDB.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pasien");
            while (rs.next()) {
                String nama = rs.getString("nama");
                int id = rs.getInt("id");
                cbPasien.addItem(nama);
                pasienMap.put(nama, id);
            }

            rs = stmt.executeQuery("SELECT * FROM dokter");
            while (rs.next()) {
                String nama = rs.getString("nama");
                int id = rs.getInt("id");
                cbDokter.addItem(nama);
                dokterMap.put(nama, id);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
        }
    }

    private void simpanData() {
        try (Connection conn = KoneksiDB.getConnection()) {
            String sql = "INSERT INTO pemeriksaan (pasien_id, dokter_id, tanggal, keluhan, diagnosa, terapi) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, pasienMap.get(cbPasien.getSelectedItem()));
            stmt.setInt(2, dokterMap.get(cbDokter.getSelectedItem()));
            stmt.setString(3, tfTanggal.getText());
            stmt.setString(4, taKeluhan.getText());
            stmt.setString(5, taDiagnosa.getText());
            stmt.setString(6, taTerapi.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pemeriksaan berhasil disimpan!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + ex.getMessage());
        }
    }

    private void tampilkanData() {
        model.setRowCount(0);
        try (Connection conn = KoneksiDB.getConnection()) {
            String sql = "SELECT p.id, pas.nama AS pasien, dok.nama AS dokter, p.tanggal, p.keluhan, p.diagnosa, p.terapi " +
                         "FROM pemeriksaan p " +
                         "JOIN pasien pas ON p.pasien_id = pas.id " +
                         "JOIN dokter dok ON p.dokter_id = dok.id";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("pasien"),
                    rs.getString("dokter"),
                    rs.getString("tanggal"),
                    rs.getString("keluhan"),
                    rs.getString("diagnosa"),
                    rs.getString("terapi")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + ex.getMessage());
        }
    }

    private void resetData() {
        cbPasien.setSelectedIndex(-1);
        cbDokter.setSelectedIndex(-1);
        tfTanggal.setText("");
        taKeluhan.setText("");
        taDiagnosa.setText("");
        taTerapi.setText("");
    }
}
