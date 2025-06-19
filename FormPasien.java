package view;

import koneksi.KoneksiDB;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormPasien extends JPanel {
    private JTextField tfNik, tfNama, tfTanggal, tfAlamat;
    private JComboBox<String> cbJK;
    private JButton btnSimpan, btnLihat;
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

        panelInput.setBorder(BorderFactory.createTitledBorder("Form Input Pasien"));
        panelInput.add(new JLabel("NIK:")); panelInput.add(tfNik);
        panelInput.add(new JLabel("Nama:")); panelInput.add(tfNama);
        panelInput.add(new JLabel("Tanggal Lahir (YYYY-MM-DD):")); panelInput.add(tfTanggal);
        panelInput.add(new JLabel("Jenis Kelamin:")); panelInput.add(cbJK);
        panelInput.add(new JLabel("Alamat:")); panelInput.add(tfAlamat);
        
        // Panel tombol (terpisah)
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSimpan = new JButton("Simpan");
        btnLihat = new JButton("Lihat Data");

        panelButton.add(btnSimpan);
        panelButton.add(btnLihat);

        // Tabel
        model = new DefaultTableModel(new String[]{"ID", "NIK", "Nama", "Tgl Lahir", "JK", "Alamat"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Action
        btnSimpan.addActionListener(e -> simpanData());
        btnLihat.addActionListener(e -> tampilkanData());

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
            tfNik.setText(""); tfNama.setText(""); tfTanggal.setText(""); tfAlamat.setText("");
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
                    rs.getString("alamat")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + ex.getMessage());
        }
    }
}
