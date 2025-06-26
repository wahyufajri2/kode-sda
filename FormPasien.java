package view;

import koneksi.KoneksiDB;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormPasien extends JPanel {
    private JTextField tfNik, tfNama, tfTanggal, tfAlamat;
    private JComboBox<String> cbJK;
    private JButton btnSimpan, btnLihat, btnReset;
    private JTable table;
    private DefaultTableModel model;

    public FormPasien() {
        setLayout(new BorderLayout());

        // Panel input
        JPanel panelInput = new JPanel(new GridLayout(5, 2, 5, 5));
        tfNik = new JTextField();
        tfNik.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e){
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && !Character.isISOControl(c)){
                    e.consume(); //blokir input selain angka
                }
            }
        });
        tfNama = new JTextField();
        tfNama.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e){
                char c = e.getKeyChar();
                if (Character.isDigit(c)){
                    e.consume(); //blokir angka
                }
            }
        });
        tfTanggal = new JTextField();
        cbJK = new JComboBox<>(new String[] {"Laki-laki", "Perempuan"});
        tfAlamat = new JTextField();
        btnSimpan = new JButton("Simpan");
        btnLihat = new JButton("Lihat Data");
        btnReset = new JButton("Reset");

        panelInput.setBorder(BorderFactory.createTitledBorder("Form Input Pasien"));
        panelInput.add(new JLabel("NIK:")); panelInput.add(tfNik);
        panelInput.add(new JLabel("Nama:")); panelInput.add(tfNama);
        panelInput.add(new JLabel("Tanggal Lahir (YYYY-MM-DD):")); panelInput.add(tfTanggal);
        panelInput.add(new JLabel("Jenis Kelamin:")); panelInput.add(cbJK);
        panelInput.add(new JLabel("Alamat:")); panelInput.add(tfAlamat);

        // Panel tombol (terpisah)
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButton.add(btnSimpan);
        panelButton.add(btnLihat);
        panelButton.add(btnReset);

        // Tabel
        model = new DefaultTableModel(new String[]{"ID", "NIK", "Nama", "Tgl Lahir", "JK", "Alamat", "Aksi"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // hanya kolom aksi
            }
        };
        table = new JTable(model);
        table.setRowHeight(40);

        // Tambah ButtonRenderer & Editor
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(table, new AksiEditHapus() {
            @Override
            public void onEdit(int row) {
                tfNik.setText(model.getValueAt(row, 1).toString());
                tfNama.setText(model.getValueAt(row, 2).toString());
                tfTanggal.setText(model.getValueAt(row, 3).toString());
                cbJK.setSelectedItem(model.getValueAt(row, 4).toString());
                tfAlamat.setText(model.getValueAt(row, 5).toString());
            }

            @Override
            public void onHapus(int id) {
                try (Connection conn = KoneksiDB.getConnection()) {
                    int konfirmasi = JOptionPane.showConfirmDialog(null, "Yakin hapus data ini?", "Hapus", JOptionPane.YES_NO_OPTION);
                    if (konfirmasi == JOptionPane.YES_OPTION) {
                        conn.createStatement().executeUpdate("DELETE FROM pasien WHERE id=" + id);
                        tampilkanData();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Gagal hapus: " + e.getMessage());
                }
            }
        }));

        JScrollPane scrollPane = new JScrollPane(table);

        // Action
        btnSimpan.addActionListener(e -> simpanData());
        btnLihat.addActionListener(e -> tampilkanData());
        btnReset.addActionListener(e -> resetData());

        // Gabungkan panel input dan tombol ke panel atas
        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(panelInput, BorderLayout.CENTER);
        panelAtas.add(panelButton, BorderLayout.SOUTH);

        add(panelAtas, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void simpanData() {
        try {
            Connection conn = KoneksiDB.getConnection();
            String sql = "INSERT INTO pasien (nik, nama, tanggal_lahir, jenis_kelamin, alamat) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tfNik.getText());
            stmt.setString(2, tfNama.getText());
            stmt.setString(3, tfTanggal.getText());
            stmt.setString(4, cbJK.getSelectedItem().toString());
            stmt.setString(5, tfAlamat.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pasien berhasil disimpan.");
            resetData();
            tampilkanData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + ex.getMessage());
        }
    }

    private void tampilkanData() {
        model.setRowCount(0);
        try {
            Connection conn = KoneksiDB.getConnection();
            String sql = "SELECT * FROM pasien";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nik"),
                    rs.getString("nama"),
                    rs.getString("tanggal_lahir"),
                    rs.getString("jenis_kelamin"),
                    rs.getString("alamat"),
                    "Aksi"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + ex.getMessage());
        }
    }

    private void resetData() {
        tfNik.setText("");
        tfNama.setText("");
        tfTanggal.setText("");
        tfAlamat.setText("");
    }
}
