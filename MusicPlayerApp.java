import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import javazoom.jl.player.Player;

import java.io.*;
import java.util.concurrent.atomic.*;
import javax.sound.sampled.*;

public class MusicPlayerApp {
    private JFrame frame;
    private JPanel currentPanel;
    private int currentUserId = -1;
    private String currentUserName = "";
    
    // Colors
   // Colors - Blue and Black Theme
private final Color PRIMARY_COLOR = new Color(0, 150, 255);  // Bright blue
private final Color SECONDARY_COLOR = new Color(20, 20, 40);  // Dark blue-black
private final Color TEXT_COLOR = new Color(200, 230, 255);  // Light blue-white
private final Color BACKGROUND_COLOR = new Color(10, 10, 20);  // Almost black
private final Color TABLE_COLOR = new Color(30, 30, 60);  // Dark blue
private final Color TABLE_HEADER_COLOR = new Color(0, 100, 180);  // Darker blue

    // Database tables
    private static final String[] SONG_COLUMNS = {"song_id", "title", "genre", "duration", "language", "artist_name", "album_name"};
    private static final String[] PLAYLIST_COLUMNS = {"playlist_id", "playlist_name"};
    private static final String[] HISTORY_COLUMNS = {"song_id", "title", "play_date", "play_duration"};

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MusicPlayerApp window = new MusicPlayerApp();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MusicPlayerApp() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Music Player App");
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        showLoginPanel();
    }

    private void applyModernStyle(JComponent component) {
        component.setForeground(TEXT_COLOR);
        component.setBackground(SECONDARY_COLOR);
        component.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        if (component instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) component;
            button.setFocusPainted(false);
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.BLACK);  // Keep button text black for contrast
            button.setFont(new Font("Arial", Font.BOLD, 12));
        }
        
        if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setBackground(TABLE_COLOR);
            table.setForeground(TEXT_COLOR);
            table.setGridColor(PRIMARY_COLOR);
            table.setSelectionBackground(PRIMARY_COLOR);
            table.setSelectionForeground(Color.BLACK);
            table.getTableHeader().setBackground(TABLE_HEADER_COLOR);
            table.getTableHeader().setForeground(TEXT_COLOR);
        }
        
        if (component instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) component;
            tabbedPane.setBackground(SECONDARY_COLOR);
            tabbedPane.setForeground(TEXT_COLOR);
        }
        
        if (component instanceof JMenuBar || component instanceof JMenu) {
            component.setBackground(SECONDARY_COLOR);
            component.setForeground(TEXT_COLOR);
        }
    }

    private void showLoginPanel() {
        if (currentPanel != null) {
            frame.remove(currentPanel);
        }

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Music Player Login");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(lblTitle, gbc);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(lblUsername, gbc);

        JTextField txtUsername = new JTextField(20);
        applyModernStyle(txtUsername);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(lblPassword, gbc);

        JPasswordField txtPassword = new JPasswordField(20);
        applyModernStyle(txtPassword);
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(txtPassword, gbc);

        JButton btnLogin = new JButton("Login");
        applyModernStyle(btnLogin);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            if (authenticateUser(username, password)) {
                showHomePanel();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        loginPanel.add(btnLogin, gbc);

        JButton btnRegister = new JButton("Register");
        applyModernStyle(btnRegister);
        gbc.gridx = 0;
        gbc.gridy = 4;
        btnRegister.addActionListener(e -> showRegistrationPanel());
        loginPanel.add(btnRegister, gbc);

        currentPanel = loginPanel;
        frame.add(currentPanel);
        frame.revalidate();
        frame.repaint();
    }
    private void showRegistrationPanel() {
        frame.remove(currentPanel);

        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Register New User");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        registerPanel.add(lblTitle, gbc);

        JLabel lblUsername = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        registerPanel.add(lblUsername, gbc);

        JTextField txtUsername = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        registerPanel.add(txtUsername, gbc);

        JLabel lblEmail = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        registerPanel.add(lblEmail, gbc);

        JTextField txtEmail = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        registerPanel.add(txtEmail, gbc);

        JLabel lblPassword = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        registerPanel.add(lblPassword, gbc);

        JPasswordField txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        registerPanel.add(txtPassword, gbc);

        JLabel lblCountry = new JLabel("Country:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        registerPanel.add(lblCountry, gbc);

        JTextField txtCountry = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 4;
        registerPanel.add(txtCountry, gbc);

        JButton btnRegister = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        btnRegister.addActionListener(e -> {
            String username = txtUsername.getText();
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());
            String country = txtCountry.getText();
        
            // Validate inputs
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "Please fill in all required fields", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        
            if (registerUser(username, email, password, country)) {
                JOptionPane.showMessageDialog(frame, 
                    "Registration successful! Please login.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                showLoginPanel();
            }
            // Error message is now shown within registerUser method
        });
        registerPanel.add(btnRegister, gbc);

        JButton btnBack = new JButton("Back to Login");
        gbc.gridx = 0;
        gbc.gridy = 6;
        btnBack.addActionListener(e -> showLoginPanel());
        registerPanel.add(btnBack, gbc);

        currentPanel = registerPanel;
        frame.add(currentPanel);
        frame.revalidate();
        frame.repaint();
    }
    private void showHomePanel() {
        frame.remove(currentPanel);
    
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(BACKGROUND_COLOR);
        
        // Add to the top panel
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(SECONDARY_COLOR);
    
        JMenu menu = new JMenu("Library");
        menu.setForeground(TEXT_COLOR);
        JMenuItem initLibraryItem = new JMenuItem("Initialize Music Library");
        initLibraryItem.setBackground(SECONDARY_COLOR);
        initLibraryItem.setForeground(TEXT_COLOR);
        initLibraryItem.addActionListener(e -> initializeMusicLibrary());
        menu.add(initLibraryItem);
        menuBar.add(menu);
    
        frame.setJMenuBar(menuBar);
        
        // Top panel with welcome message and search
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(SECONDARY_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblWelcome = new JLabel("Welcome, " + currentUserName + "!");
        lblWelcome.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblWelcome.setForeground(TEXT_COLOR);
        topPanel.add(lblWelcome, BorderLayout.WEST);
        
        JButton btnLogout = new JButton("Logout");
        applyModernStyle(btnLogout);
        btnLogout.addActionListener(e -> {
            currentUserId = -1;
            currentUserName = "";
            showLoginPanel();
        });
        topPanel.add(btnLogout, BorderLayout.EAST);
        
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(SECONDARY_COLOR);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(TEXT_COLOR);
        searchPanel.add(searchLabel);
        
        JTextField txtSearch = new JTextField(30);
        applyModernStyle(txtSearch);
        searchPanel.add(txtSearch);
        
        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setForeground(TEXT_COLOR);
        searchPanel.add(filterLabel);
        
        JComboBox<String> cmbFilter = new JComboBox<>(new String[]{"All", "Title", "Genre", "Artist", "Album"});
        applyModernStyle(cmbFilter);
        searchPanel.add(cmbFilter);
        
        JButton btnSearch = new JButton("Search");
        applyModernStyle(btnSearch);
        searchPanel.add(btnSearch);
        
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        homePanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        applyModernStyle(tabbedPane);
        
        // Songs tab
        JPanel songsPanel = new JPanel(new BorderLayout());
        songsPanel.setBackground(BACKGROUND_COLOR);
        
        DefaultTableModel songsModel = new DefaultTableModel(SONG_COLUMNS, 0);
        JTable songsTable = new JTable(songsModel);
        applyModernStyle(songsTable);
        loadSongs(songsModel);
        
        JScrollPane songsScrollPane = new JScrollPane(songsTable);
        songsScrollPane.setBackground(BACKGROUND_COLOR);
        songsPanel.add(songsScrollPane, BorderLayout.CENTER);
        
        JPanel songsButtonPanel = new JPanel();
        songsButtonPanel.setBackground(SECONDARY_COLOR);
        
        JButton btnPlay = new JButton("Play");
        applyModernStyle(btnPlay);
        JButton btnRate = new JButton("Rate Song");
        applyModernStyle(btnRate);
        JButton btnAddToPlaylist = new JButton("Add to Playlist");
        applyModernStyle(btnAddToPlaylist);
        
        songsButtonPanel.add(btnPlay);
        songsButtonPanel.add(btnRate);
        songsButtonPanel.add(btnAddToPlaylist);
        songsPanel.add(songsButtonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Songs", songsPanel);
        
        // Playlists tab
        JPanel playlistsPanel = new JPanel(new BorderLayout());
        playlistsPanel.setBackground(BACKGROUND_COLOR);
        
        DefaultTableModel playlistsModel = new DefaultTableModel(PLAYLIST_COLUMNS, 0);
        JTable playlistsTable = new JTable(playlistsModel);
        applyModernStyle(playlistsTable);
        loadPlaylists(playlistsModel);
        
        JScrollPane playlistsScrollPane = new JScrollPane(playlistsTable);
        playlistsScrollPane.setBackground(BACKGROUND_COLOR);
        playlistsPanel.add(playlistsScrollPane, BorderLayout.CENTER);
        
        JPanel playlistButtonPanel = new JPanel();
        playlistButtonPanel.setBackground(SECONDARY_COLOR);
        
        JButton btnCreatePlaylist = new JButton("Create Playlist");
        applyModernStyle(btnCreatePlaylist);
        JButton btnViewPlaylist = new JButton("View Songs");
        applyModernStyle(btnViewPlaylist);
        JButton btnDeletePlaylist = new JButton("Delete Playlist");
        applyModernStyle(btnDeletePlaylist);
        
        playlistButtonPanel.add(btnCreatePlaylist);
        playlistButtonPanel.add(btnViewPlaylist);
        playlistButtonPanel.add(btnDeletePlaylist);
        playlistsPanel.add(playlistButtonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Playlists", playlistsPanel);
        
        // History tab
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(BACKGROUND_COLOR);
        
        DefaultTableModel historyModel = new DefaultTableModel(HISTORY_COLUMNS, 0);
        JTable historyTable = new JTable(historyModel);
        applyModernStyle(historyTable);
        loadHistory(historyModel);
        
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setBackground(BACKGROUND_COLOR);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Listening History", historyPanel);
        
        homePanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Button actions (remain exactly the same)
        btnSearch.addActionListener(e -> {
            String searchText = txtSearch.getText();
            String filter = cmbFilter.getSelectedItem().toString();
            searchSongs(songsModel, searchText, filter);
        });
        
        btnPlay.addActionListener(e -> {
            int selectedRow = songsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int songId = (int) songsModel.getValueAt(selectedRow, 0);
                String songTitle = (String) songsModel.getValueAt(selectedRow, 1);
                playSong(songId, songTitle);
                loadHistory(historyModel);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a song to play", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnRate.addActionListener(e -> {
            int selectedRow = songsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int songId = (int) songsModel.getValueAt(selectedRow, 0);
                String songTitle = (String) songsModel.getValueAt(selectedRow, 1);
                rateSong(songId, songTitle);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a song to rate", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnAddToPlaylist.addActionListener(e -> {
            int selectedRow = songsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int songId = (int) songsModel.getValueAt(selectedRow, 0);
                String songTitle = (String) songsModel.getValueAt(selectedRow, 1);
                addToPlaylist(songId, songTitle);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a song to add to playlist", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnCreatePlaylist.addActionListener(e -> createPlaylist(playlistsModel));
        
        btnViewPlaylist.addActionListener(e -> {
            int selectedRow = playlistsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int playlistId = (int) playlistsModel.getValueAt(selectedRow, 0);
                String playlistName = (String) playlistsModel.getValueAt(selectedRow, 1);
                viewPlaylistSongs(playlistId, playlistName);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a playlist to view", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnDeletePlaylist.addActionListener(e -> {
            int selectedRow = playlistsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int playlistId = (int) playlistsModel.getValueAt(selectedRow, 0);
                deletePlaylist(playlistId, playlistsModel);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a playlist to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        currentPanel = homePanel;
        frame.add(currentPanel);
        frame.revalidate();
        frame.repaint();
    }
    private boolean authenticateUser(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT user_id, user_name FROM Users WHERE user_name = ? AND password = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                currentUserId = rs.getInt("user_id");
                currentUserName = rs.getString("user_name");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(conn, stmt, rs);
        }
        return false;
    }

    private boolean registerUser(String username, String email, String password, String country) {
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // First check if username or email already exists
            String checkSql = "SELECT user_id FROM Users WHERE user_name = ? OR email = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // User with this username or email already exists
                return false;
            }
            
            // If we get here, the user doesn't exist - proceed with registration
            String insertSql = "INSERT INTO Users (user_name, email, password, country) VALUES (?, ?, ?, ?)";
            insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);
            insertStmt.setString(4, country);
            
            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Provide more specific error messages
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(frame, 
                    "Username or email already exists. Please choose different credentials.",
                    "Registration Failed", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Registration failed due to database error: " + e.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            return false;
        } finally {
            DatabaseConnection.close(conn, checkStmt, rs);
            DatabaseConnection.close(null, insertStmt, null);
        }
    }

    private void loadSongs(DefaultTableModel model) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT s.song_id, s.title, s.genre, s.duration, s.language, a.artist_name, al.album_name " +
                         "FROM Songs s " +
                         "JOIN Artists a ON s.artist_id = a.artist_id " +
                         "JOIN Albums al ON s.album_id = al.album_id";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            model.setRowCount(0); // Clear existing data
            
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("song_id");
                row[1] = rs.getString("title");
                row[2] = rs.getString("genre");
                row[3] = rs.getString("duration");
                row[4] = rs.getString("language");
                row[5] = rs.getString("artist_name");
                row[6] = rs.getString("album_name");
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(conn, stmt, rs);
        }
    }

    private void searchSongs(DefaultTableModel model, String searchText, String filter) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT s.song_id, s.title, s.genre, s.duration, s.language, a.artist_name, al.album_name " +
                         "FROM Songs s " +
                         "JOIN Artists a ON s.artist_id = a.artist_id " +
                         "JOIN Albums al ON s.album_id = al.album_id " +
                         "WHERE ";
            
            switch (filter) {
                case "Title":
                    sql += "s.title LIKE ?";
                    break;
                case "Genre":
                    sql += "s.genre LIKE ?";
                    break;
                case "Artist":
                    sql += "a.artist_name LIKE ?";
                    break;
                case "Album":
                    sql += "al.album_name LIKE ?";
                    break;
                default:
                    sql += "(s.title LIKE ? OR s.genre LIKE ? OR a.artist_name LIKE ? OR al.album_name LIKE ?)";
                    break;
            }
            
            stmt = conn.prepareStatement(sql);
            String searchParam = "%" + searchText + "%";
            
            if (filter.equals("All")) {
                stmt.setString(1, searchParam);
                stmt.setString(2, searchParam);
                stmt.setString(3, searchParam);
                stmt.setString(4, searchParam);
            } else {
                stmt.setString(1, searchParam);
            }
            
            rs = stmt.executeQuery();
            model.setRowCount(0); // Clear existing data
            
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("song_id");
                row[1] = rs.getString("title");
                row[2] = rs.getString("genre");
                row[3] = rs.getString("duration");
                row[4] = rs.getString("language");
                row[5] = rs.getString("artist_name");
                row[6] = rs.getString("album_name");
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(conn, stmt, rs);
        }
    }

    private void loadPlaylists(DefaultTableModel model) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT playlist_id, playlist_name FROM Playlist WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUserId);
            rs = stmt.executeQuery();
            
            model.setRowCount(0); // Clear existing data
            
            while (rs.next()) {
                Object[] row = new Object[2];
                row[0] = rs.getInt("playlist_id");
                row[1] = rs.getString("playlist_name");
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(conn, stmt, rs);
        }
    }

    private void loadHistory(DefaultTableModel model) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT lh.song_id, s.title, lh.play_date, lh.play_duration " +
                         "FROM Listening_History lh " +
                         "JOIN Songs s ON lh.song_id = s.song_id " +
                         "WHERE lh.user_id = ? " +
                         "ORDER BY lh.play_date DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUserId);
            rs = stmt.executeQuery();
            
            model.setRowCount(0); // Clear existing data
            
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getInt("song_id");
                row[1] = rs.getString("title");
                row[2] = rs.getTimestamp("play_date");
                row[3] = rs.getInt("play_duration");
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(conn, stmt, rs);
        }
    }
    // ... [previous methods remain the same until playSong] ...

    private void playSong(int songId, String songTitle) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Get song file path
            String filePathSql = "SELECT file_path, duration FROM Songs WHERE song_id = ?";
            stmt = conn.prepareStatement(filePathSql);
            stmt.setInt(1, songId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String filePath = rs.getString("file_path");
                String duration = rs.getString("duration");
                
                // Create player dialog
                JDialog playDialog = new JDialog(frame, "Now Playing: " + songTitle, false);
                playDialog.setSize(300, 150);
                playDialog.setLayout(new BorderLayout());
                playDialog.setLocationRelativeTo(frame);
                
                // Create player instance
                WavPlayer player = new WavPlayer();
                
                // Add stop button
                JButton btnStop = new JButton("Stop");
                applyModernStyle(btnStop);
                btnStop.addActionListener(e -> {
                    player.stop();
                    playDialog.dispose();
                });
                
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(btnStop);
                playDialog.add(buttonPanel, BorderLayout.SOUTH);
                
                // Play the song in a separate thread
                new Thread(() -> {
                    try {
                        player.play(filePath);
                        
                        // Convert duration to seconds
                        int playDuration = convertDurationToSeconds(duration);
                        
                        // Record in listening history
                        String historySql = "INSERT INTO Listening_History (user_id, song_id, play_date, play_duration) " +
                                           "VALUES (?, ?, NOW(), ?)";
                        try (Connection historyConn = DatabaseConnection.getConnection();
                             PreparedStatement historyStmt = historyConn.prepareStatement(historySql)) {
                            historyStmt.setInt(1, currentUserId);
                            historyStmt.setInt(2, songId);
                            historyStmt.setInt(3, playDuration);
                            historyStmt.executeUpdate();
                        }
                    } catch (Exception e) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(frame, "Error playing song: " + e.getMessage(), 
                                "Playback Error", JOptionPane.ERROR_MESSAGE);
                        });
                    } finally {
                        SwingUtilities.invokeLater(() -> playDialog.dispose());
                    }
                }).start();
                
                playDialog.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error playing song: " + e.getMessage(), 
                "Playback Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseConnection.close(conn, stmt, null);
        }
    }

// Helper method to convert "mm:ss" duration to seconds
private int convertDurationToSeconds(String duration) {
    try {
        String[] parts = duration.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return (minutes * 60) + seconds;
    } catch (Exception e) {
        return 0; // Default value if conversion fails
    }
}
    
    // Helper method to refresh the history tab
    private void refreshHistoryTab() {
        if (currentPanel instanceof JPanel) {
            Component[] components = ((JPanel) currentPanel).getComponents();
            for (Component comp : components) {
                if (comp instanceof JTabbedPane) {
                    JTabbedPane tabbedPane = (JTabbedPane) comp;
                    Component historyTab = tabbedPane.getComponentAt(2); // Assuming history is the 3rd tab
                    if (historyTab instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) historyTab;
                        JViewport viewport = scrollPane.getViewport();
                        if (viewport.getView() instanceof JTable) {
                            JTable historyTable = (JTable) viewport.getView();
                            DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
                            loadHistory(model);
                        }
                    }
                }
            }
        }
    }
    
    
    private void rateSong(int songId, String songTitle) {
        String ratingStr = JOptionPane.showInputDialog(frame, 
            "Enter your rating for '" + songTitle + "' (1-5):",
            "Rate Song", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (ratingStr == null || ratingStr.trim().isEmpty()) {
            return; // User cancelled
        }
    
        try {
            int rating = Integer.parseInt(ratingStr);
            if (rating < 1 || rating > 5) {
                JOptionPane.showMessageDialog(frame, 
                    "Please enter a rating between 1 and 5", 
                    "Invalid Rating", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false); // Start transaction
                
                // Check for existing rating
                String checkSql = "SELECT rating_id FROM Ratings WHERE user_id = ? AND song_id = ?";
                stmt = conn.prepareStatement(checkSql);
                stmt.setInt(1, currentUserId);
                stmt.setInt(2, songId);
                rs = stmt.executeQuery();
                
                if (rs.next()) {
                    // Update existing rating
                    int ratingId = rs.getInt("rating_id");
                    stmt.close();
                    String updateSql = "UPDATE Ratings SET rating = ? WHERE rating_id = ?";
                    stmt = conn.prepareStatement(updateSql);
                    stmt.setInt(1, rating);
                    stmt.setInt(2, ratingId);
                    stmt.executeUpdate();
                } else {
                    // Insert new rating
                    stmt.close();
                    String insertSql = "INSERT INTO Ratings (user_id, song_id, rating) VALUES (?, ?, ?)";
                    stmt = conn.prepareStatement(insertSql);
                    stmt.setInt(1, currentUserId);
                    stmt.setInt(2, songId);
                    stmt.setInt(3, rating);
                    stmt.executeUpdate();
                }
                
                // Update song's average rating
                updateSongAverageRating(conn, songId);
                
                conn.commit(); // Commit transaction
                JOptionPane.showMessageDialog(frame, 
                    "Rating submitted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(frame, 
                    "Error submitting rating: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                DatabaseConnection.close(conn, stmt, rs);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, 
                "Please enter a valid number between 1 and 5", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Modified updateSongAverageRating method
    private void updateSongAverageRating(Connection conn, int songId) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // First check if the columns exist
            boolean columnsExist = false;
            try {
                DatabaseMetaData meta = conn.getMetaData();
                rs = meta.getColumns(null, null, "Songs", "average_ratings");
                columnsExist = rs.next();
                rs.close();
            } catch (SQLException e) {
                // If we can't check, assume columns don't exist
                columnsExist = false;
            }
    
            if (!columnsExist) {
                // Columns don't exist - skip the update
                return;
            }
    
            // Calculate new average
            String avgSql = "SELECT AVG(rating) as avg_rating, COUNT(*) as total_ratings " +
                           "FROM Ratings WHERE song_id = ?";
            stmt = conn.prepareStatement(avgSql);
            stmt.setInt(1, songId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                double avgRating = rs.getDouble("avg_rating");
                int totalRatings = rs.getInt("total_ratings");
                
                // Update song record
                stmt.close();
                String updateSql = "UPDATE Songs SET average_ratings = ?, total_ratings = ? " +
                                  "WHERE song_id = ?";
                stmt = conn.prepareStatement(updateSql);
                stmt.setDouble(1, avgRating);
                stmt.setInt(2, totalRatings);
                stmt.setInt(3, songId);
                stmt.executeUpdate();
            }
        } finally {
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        }
    }
    private void recordSongPlay(int songId, int durationSeconds) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Listening_History (user_id, song_id, play_date, play_duration) " +
                         "VALUES (?, ?, NOW(), ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, songId);
            stmt.setInt(3, durationSeconds);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("Failed to record play history");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(conn, stmt, null);
        }
    }
    private int playMp3File(String filePath, String songTitle) {
        File songFile = new File(filePath).getAbsoluteFile();
        
        // Check file permissions
        if (!songFile.exists()) {
            JOptionPane.showMessageDialog(frame, 
                "File not found: " + songFile.getPath(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        
        if (!songFile.canRead()) {
            JOptionPane.showMessageDialog(frame, 
                "Cannot read file (access denied): " + songFile.getPath(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    
        // Create player dialog
        JDialog playDialog = new JDialog(frame, "Now Playing: " + songTitle, true);
        playDialog.setSize(400, 200);
        playDialog.setLayout(new BorderLayout());
        playDialog.setLocationRelativeTo(frame);
        
        // Player state
        AtomicBoolean isPlaying = new AtomicBoolean(true);
        AtomicInteger playDuration = new AtomicInteger(0);
        AtomicReference<Player> playerRef = new AtomicReference<>();
        
        try {
            FileInputStream fis = new FileInputStream(songFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            Player player = new Player(bis);
            playerRef.set(player);
            
            // Start playback in a separate thread
            new Thread(() -> {
                try {
                    player.play();
                    if (isPlaying.get()) {
                        playDuration.set(getSongDuration(songFile)); // Record full duration if played completely
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(frame, 
                            "Playback error: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    });
                } finally {
                    playDialog.dispose();
                }
            }).start();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, 
                "Error opening file: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        
        // Add controls to dialog
        JButton btnStop = new JButton("Stop");
        btnStop.addActionListener(e -> {
            isPlaying.set(false);
            playerRef.get().close();
            playDialog.dispose();
        });
        
        playDialog.add(btnStop, BorderLayout.SOUTH);
        playDialog.setVisible(true);
        
        return playDuration.get();
    }
    
    private int getSongDuration(File file) {
        // This is a placeholder - implement actual duration calculation
        // For MP3 files, you would need a library to read the actual duration
        return 180; // Default 3 minutes if we can't determine
    }
private void initializeMusicLibrary() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Select Music Library Folder");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
    int result = chooser.showOpenDialog(frame);
    if (result == JFileChooser.APPROVE_OPTION) {
        File libraryFolder = chooser.getSelectedFile();
        scanAndImportSongs(libraryFolder);
    }
}

private void scanAndImportSongs(File libraryFolder) {
    Connection conn = null;
    PreparedStatement stmt = null;
    
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        
        // Get all songs from database
        String selectSql = "SELECT song_id, title FROM Songs";
        stmt = conn.prepareStatement(selectSql);
        ResultSet rs = stmt.executeQuery();
        
        // Create map of song titles to IDs
        Map<String, Integer> songMap = new HashMap<>();
        while (rs.next()) {
            songMap.put(rs.getString("title").toLowerCase(), rs.getInt("song_id"));
        }
        rs.close();
        stmt.close();
        
        // Scan for MP3 files
        File[] songFiles = libraryFolder.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".mp3"));
        
        if (songFiles != null && songFiles.length > 0) {
            String updateSql = "UPDATE Songs SET file_path = ? WHERE song_id = ?";
            stmt = conn.prepareStatement(updateSql);
            
            int updatedCount = 0;
            for (File songFile : songFiles) {
                String fileName = songFile.getName().replace(".mp3", "").toLowerCase();
                if (songMap.containsKey(fileName)) {
                    stmt.setString(1, songFile.getAbsolutePath());
                    stmt.setInt(2, songMap.get(fileName));
                    stmt.addBatch();
                    updatedCount++;
                }
            }
            
            stmt.executeBatch();
            conn.commit();
            JOptionPane.showMessageDialog(frame, 
                "Updated paths for " + updatedCount + " songs", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, 
                "No MP3 files found in selected folder", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
    } catch (SQLException e) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) {}
        }
        JOptionPane.showMessageDialog(frame, 
            "Error importing songs: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    } finally {
        DatabaseConnection.close(conn, stmt, null);
    }
}
        private void updateSongAverageRating(int songId) throws SQLException {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            
            try {
                // Calculate new average
                String avgSql = "SELECT AVG(rating) as avg_rating, COUNT(*) as total_ratings FROM Ratings WHERE song_id = ?";
                stmt = conn.prepareStatement(avgSql);
                stmt.setInt(1, songId);
                rs = stmt.executeQuery();
                
                if (rs.next()) {
                    double avgRating = rs.getDouble("avg_rating");
                    int totalRatings = rs.getInt("total_ratings");
                    
                    // Update song record
                    String updateSql = "UPDATE Songs SET average_ratings = ?, total_ratings = ? WHERE song_id = ?";
                    stmt = conn.prepareStatement(updateSql);
                    stmt.setDouble(1, avgRating);
                    stmt.setInt(2, totalRatings);
                    stmt.setInt(3, songId);
                    stmt.executeUpdate();
                }
            } finally {
                DatabaseConnection.close(conn, stmt, rs);
            }
        }
    
        private void addToPlaylist(int songId, String songTitle) {
            // Get user's playlists
            List<String[]> playlists = new ArrayList<>();
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "SELECT playlist_id, playlist_name FROM Playlist WHERE user_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, currentUserId);
                rs = stmt.executeQuery();
                
                while (rs.next()) {
                    String[] playlist = new String[2];
                    playlist[0] = rs.getString("playlist_id");
                    playlist[1] = rs.getString("playlist_name");
                    playlists.add(playlist);
                }
                
                if (playlists.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "You don't have any playlists. Please create one first.", "No Playlists", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Create selection dialog
                String[] options = new String[playlists.size()];
                for (int i = 0; i < playlists.size(); i++) {
                    options[i] = playlists.get(i)[1]; // playlist names
                }
                
                String selectedPlaylist = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select playlist to add '" + songTitle + "' to:",
                    "Add to Playlist",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
                
                if (selectedPlaylist != null) {
                    // Find the playlist ID
                    int playlistId = -1;
                    for (String[] playlist : playlists) {
                        if (playlist[1].equals(selectedPlaylist)) {
                            playlistId = Integer.parseInt(playlist[0]);
                            break;
                        }
                    }
                    
                    // Add song to playlist
                    sql = "INSERT INTO Playlist_Songs (playlist_id, song_id) VALUES (?, ?)";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, playlistId);
                    stmt.setInt(2, songId);
                    
                    try {
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Song added to playlist successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException e) {
                        if (e.getErrorCode() == 1062) { // Duplicate entry
                            JOptionPane.showMessageDialog(frame, "This song is already in the selected playlist", "Duplicate", JOptionPane.WARNING_MESSAGE);
                        } else {
                            throw e;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error adding song to playlist", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                DatabaseConnection.close(conn, stmt, rs);
            }
        }
    
        private void createPlaylist(DefaultTableModel playlistsModel) {
            String playlistName = JOptionPane.showInputDialog(frame, "Enter playlist name:");
            if (playlistName == null || playlistName.trim().isEmpty()) return;
            
            Connection conn = null;
            PreparedStatement stmt = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "INSERT INTO Playlist (user_id, playlist_name, created_on) VALUES (?, ?, NOW())";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, currentUserId);
                stmt.setString(2, playlistName);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Playlist created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPlaylists(playlistsModel); // Refresh playlist list
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error creating playlist", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                DatabaseConnection.close(conn, stmt, null);
            }
        }
        
        private void viewPlaylistSongs(int playlistId, String playlistName) {
            JDialog playlistDialog = new JDialog(frame, "Playlist: " + playlistName, true);
            playlistDialog.setSize(600, 400);
            playlistDialog.setLocationRelativeTo(frame);
            
            JPanel panel = new JPanel(new BorderLayout());
            
            DefaultTableModel model = new DefaultTableModel(SONG_COLUMNS, 0);
            JTable table = new JTable(model);
            
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "SELECT s.song_id, s.title, s.genre, s.duration, s.language, a.artist_name, al.album_name " +
                             "FROM Songs s " +
                             "JOIN Playlist_Songs ps ON s.song_id = ps.song_id " +
                             "JOIN Artists a ON s.artist_id = a.artist_id " +
                             "JOIN Albums al ON s.album_id = al.album_id " +
                             "WHERE ps.playlist_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, playlistId);
                rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Object[] row = new Object[7];
                    row[0] = rs.getInt("song_id");
                    row[1] = rs.getString("title");
                    row[2] = rs.getString("genre");
                    row[3] = rs.getString("duration");
                    row[4] = rs.getString("language");
                    row[5] = rs.getString("artist_name");
                    row[6] = rs.getString("album_name");
                    model.addRow(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DatabaseConnection.close(conn, stmt, rs);
            }
            
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel();
            JButton btnPlay = new JButton("Play");
            JButton btnRemove = new JButton("Remove from Playlist");
            
            btnPlay.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int songId = (int) model.getValueAt(selectedRow, 0);
                    String songTitle = (String) model.getValueAt(selectedRow, 1);
                    playSong(songId, songTitle);
                    playlistDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(playlistDialog, "Please select a song to play", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            btnRemove.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int songId = (int) model.getValueAt(selectedRow, 0);
                    removeFromPlaylist(playlistId, songId, model);
                } else {
                    JOptionPane.showMessageDialog(playlistDialog, "Please select a song to remove", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            buttonPanel.add(btnPlay);
            buttonPanel.add(btnRemove);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            playlistDialog.add(panel);
            playlistDialog.setVisible(true);
        }
    
        private void removeFromPlaylist(int playlistId, int songId, DefaultTableModel model) {
            Connection conn = null;
            PreparedStatement stmt = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "DELETE FROM Playlist_Songs WHERE playlist_id = ? AND song_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, playlistId);
                stmt.setInt(2, songId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Remove from the table model
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if ((int) model.getValueAt(i, 0) == songId) {
                            model.removeRow(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(frame, "Song removed from playlist", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error removing song from playlist", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                DatabaseConnection.close(conn, stmt, null);
            }
        }
    
        private void deletePlaylist(int playlistId, DefaultTableModel model) {
            int confirm = JOptionPane.showConfirmDialog(
                frame, 
                "Are you sure you want to delete this playlist?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) return;
            
            Connection conn = null;
            PreparedStatement stmt = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                
                // First delete from Playlist_Songs (cascade should handle this but we'll do it explicitly)
                String sql = "DELETE FROM Playlist_Songs WHERE playlist_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, playlistId);
                stmt.executeUpdate();
                
                // Then delete the playlist
                sql = "DELETE FROM Playlist WHERE playlist_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, playlistId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Remove from the table model
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if ((int) model.getValueAt(i, 0) == playlistId) {
                            model.removeRow(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(frame, "Playlist deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error deleting playlist", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                DatabaseConnection.close(conn, stmt, null);
            }
    }

    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
    // ... [rest of the methods remain the same] ...
}