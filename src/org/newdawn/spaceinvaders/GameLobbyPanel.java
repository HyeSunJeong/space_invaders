package org.newdawn.spaceinvaders;

import org.newdawn.spaceinvaders.gameui.MainUI;
import org.newdawn.spaceinvaders.gameui.UIKeyHandler;
import org.newdawn.spaceinvaders.resource.SoundPlayer;

import javax.swing.*;
import java.awt.*;

public class GameLobbyPanel extends JPanel implements Runnable {

    //타이틀화면 스레드
    public Thread gameThread;

    //게임의 현 상태
    public int gameState = 3;
    public final int titleState = 0;
    public final int shopState = 1;
    public final int userState = 2;
    public final int initialState = 3;
    public final int signInState = 4;
    public final int signUpState = 5;
    public final int inGameState = 6;
    public final int tutorialState = 7;
    public final int changeNickState = 8;

    //윈도우 규격
    public final int screenWidth = 800;
    public final int screenHeight = 600;
    public Point frameLocation;

    //타 클래스 레퍼런스
    public MainUI mu = new MainUI(this);
    public UIKeyHandler key = new UIKeyHandler(this);
    public SoundPlayer sp = new SoundPlayer();
    public UserDB us = new UserDB(this);


    public GameLobbyPanel(){
        //윈도우 프레임 설정
        this.setPreferredSize(new Dimension(800,600));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(key);
        this.setFocusable(true);

        //배경음악 실행
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

    //구성 요소 그리기
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        mu.draw(g2);
        g2.dispose();
    }
}
