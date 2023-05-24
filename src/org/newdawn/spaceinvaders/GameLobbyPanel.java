package org.newdawn.spaceinvaders;

import javax.swing.*;
import java.awt.*;

public class GameLobbyPanel extends JPanel implements Runnable {

    Thread gameThread;

    int gameState = 3;
    final int titleState = 0;
    final int shopState = 1;
    final int userState = 2;
    final int initialState = 3;
    final int signInState = 4;
    final int signUpState = 5;
    final int inGameState = 6;
    final int tutorialState = 7;
    final int changeNickState = 8;
    final int screenWidth = 800;
    final int screenHeight = 600;

    Point frameLocation;
    public MainUI mu = new MainUI(this);
    public UIKeyHandler key = new UIKeyHandler(this);
    public SoundPlayer sp = new SoundPlayer();


    public GameLobbyPanel(){
        this.setPreferredSize(new Dimension(800,600));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(key);
        this.setFocusable(true);
        sp.playMusic(2,-20.0f);
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run(){
        while (gameThread != null){
            update();
            repaint();
            try {
                //로비 화면은 15프레임 고정. 리소스를 덜 먹기 위함
                Thread.sleep(32);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update(){
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        mu.draw(g2);
        g2.dispose();
    }
}
