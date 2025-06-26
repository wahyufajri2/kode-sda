package view;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Aplikasi Rekam Medis");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 150, 243));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JLabel lblLogo = new JLabel("📋 Petugas RM", JLabel.CENTER);
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(lblLogo);

        JButton btnPasien = new JButton("Data Pasien");
        JButton btnDokter = new JButton("Data Dokter");
        JButton btnPemeriksaan = new JButton("Data Pemeriksaan");
        JButton btnKeluar = new JButton("Keluar");


        btnPasien.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDokter.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPemeriksaan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnKeluar.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(btnPasien);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnDokter);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnPemeriksaan);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnKeluar);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblJudul = new JLabel("Aplikasi Rekam Medis Pasien");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel lblUser = new JLabel("👤 User");

        header.add(lblJudul, BorderLayout.WEST);
        header.add(lblUser, BorderLayout.EAST);

        // Panel Kosong Tengah (default)
        JPanel defaultPanel = new JPanel();
        JLabel welcome = new JLabel("Selamat datang di Aplikasi Rekam Medis");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        defaultPanel.add(welcome);

        // Footer
        JPanel footer = new JPanel();
        JLabel copy = new JLabel("Copyright © 2025 – Aplikasi Rekam Medis Pasien");
        copy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.add(copy);

        // Konten utama panel dinamis
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(defaultPanel, BorderLayout.CENTER);

        // Event Button
        btnPasien.addActionListener(e -> {
            mainContent.removeAll();
            mainContent.add(new FormPasienPanel(), BorderLayout.CENTER);
            mainContent.revalidate();
            mainContent.repaint();
        });

        btnDokter.addActionListener(e -> {
            mainContent.removeAll();
            mainContent.add(new FormDokterPanel(), BorderLayout.CENTER);
            mainContent.revalidate();
            mainContent.repaint();
        });
        
        btnPemeriksaan.addActionListener(e -> {
            mainContent.removeAll();
            mainContent.add(new FormPemeriksaanPanel(), BorderLayout.CENTER);
            mainContent.revalidate();
            mainContent.repaint();
        });

        btnKeluar.addActionListener(e -> System.exit(0));

        // Final Compose
        add(sidebar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Wrapper Panel Pasien
    private class FormPasienPanel extends JPanel {
        public FormPasienPanel() {
            setLayout(new BorderLayout());
            add(new FormPasien(), BorderLayout.CENTER);
        }
    }

    // Wrapper Panel Dokter
    private class FormDokterPanel extends JPanel {
        public FormDokterPanel() {
            setLayout(new BorderLayout());
            add(new FormDokter(), BorderLayout.CENTER);
        }
    }
    
    //Wrapper Panel Pemeriksaan
    private class FormPemeriksaanPanel extends JPanel {
        public FormPemeriksaanPanel() {
            setLayout(new BorderLayout());
            add(new FormPemeriksaan(), BorderLayout.CENTER);
        }
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
