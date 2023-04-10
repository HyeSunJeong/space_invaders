package org.newdawn.spaceinvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserDB extends JFrame implements ActionListener {

    private JFrame loginFrame;
    private JLabel titleLabel, idLabel, pwLabel, confirmPwLabel;
    private JTextField idField;
    private JPasswordField pwField, confirmPwField;
    private JButton loginButton, registerButton;

    private static Connection conn;

    static {
        String url = "jdbc:mysql://localhost:3306/space-invaders";
        String user = "user1";
        String password = "12345";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    Dimension frameDim = new Dimension(800,600);
    public UserDB() {
        CardLayout cards = new CardLayout();

        loginFrame = new JFrame("Login System");
        loginFrame.setPreferredSize(frameDim);
        loginFrame.setLayout(cards);

        // set window properties
        loginFrame.getContentPane().add("mainPanel",new mainPanel(this));
        loginFrame.pack();
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);
        loginFrame.setVisible(true);
    }

    public class mainPanel extends JPanel{

        public mainPanel(UserDB U){


            setLayout(null);

            idLabel = new JLabel("ID:");
            pwLabel = new JLabel("Password:");
            confirmPwLabel = new JLabel("Confirm Password:");

            idField = new JTextField();
            pwField = new JPasswordField();
            confirmPwField = new JPasswordField();

            //로그인, 회원가입 버튼
            loginButton = new JButton("Login");
            registerButton = new JButton("Register");

            titleLabel = new JLabel("SPACE INVADERS");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 30));

            //setBounds로 컴포넌트 크기, 위치 조정
            idLabel.setBounds(275,200,50,25);
            idField.setBounds(425,200,100,25);
            pwLabel.setBounds(275,250,100,25);
            pwField.setBounds(425,250,100,25);
            confirmPwLabel.setBounds(275,300,125,25);
            confirmPwField.setBounds(425,300,100,25);
            loginButton.setBounds(275,350,100,25);
            registerButton.setBounds(425,350,100,25);
            titleLabel.setBounds(265,100,300,50);


            add(titleLabel);
            add(idLabel);
            add(idField);
            add(pwLabel);
            add(pwField);
            add(confirmPwLabel);
            add(confirmPwField);
            add(loginButton);
            add(registerButton);

            // add action listener to buttons
            loginButton.addActionListener(UserDB.this::actionPerformed);
            registerButton.addActionListener(UserDB.this::actionPerformed);

        }



    }


    public void actionPerformed(ActionEvent e){
        if (e.getSource() == loginButton) {
            String id = idField.getText();
            String pw = new String(pwField.getPassword());

            try {
                Connection conn = UserDB.getConnection();
                String query = "SELECT * FROM userdata WHERE id = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);

                pstmt.setString(1, id);
                pstmt.setString(2, pw);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect ID or password!");
                }

                rs.close();
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == registerButton) {
            String id = idField.getText();
            String pw = new String(pwField.getPassword());
            String confirmPw = new String(confirmPwField.getPassword());

            if (!pw.equals(confirmPw)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            try {
                Connection conn = UserDB.getConnection();
                String query = "INSERT INTO userdata (id, password) VALUES (?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);

                pstmt.setString(1, id);
                pstmt.setString(2, pw);

                int result = pstmt.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Registration successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed!");
                }

                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new UserDB();
    }
}