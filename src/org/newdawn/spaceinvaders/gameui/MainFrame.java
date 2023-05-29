package org.newdawn.spaceinvaders.gameui;

import org.newdawn.spaceinvaders.GameLobbyPanel;

import javax.swing.*;

public class MainFrame {
    public MainFrame() {
    }
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Space Invaders");

        GameLobbyPanel glp = new GameLobbyPanel();
        window.add(glp);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        glp.startGameThread();
    }
}
