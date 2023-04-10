package org.newdawn.spaceinvaders;

import jdk.jfr.internal.tool.Main;
import org.newdawn.spaceinvaders.entity.Entity;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import javax.swing.*;

public class MainView extends Canvas {
    private JFrame main_view; // 메인화면
    private BufferStrategy strategy;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean choicePressed = false;

    public MainView() {

        // Frame을 만드는 과정 (대지)
        main_view = new JFrame("Space Invaders 102");

        // Frame 내용 파악, 게임 해상도 설정
        JPanel panel = (JPanel) main_view.getContentPane();
        panel.setPreferredSize(new Dimension(800,600));
        panel.setLayout(null);

        // Canvas 크기를 설정하고, Frame 안에 넣는다.
        setBounds(0,0,800,600);
        panel.add(this);

        //button add
        JButton button = new JButton("Test");
        main_view.add(button);

        // AWT에게 다시 Canvas를 칠하지 말라고 말한다.
        // -> accelerated(가속) 모드에서 우리가 직접 할거기 때문에!
        setIgnoreRepaint(true);

        // Canvas에 보이게 한다.
        main_view.pack();
        main_view.setResizable(false);
        main_view.setVisible(true);



        // AWS를 허용하는 Buffering strategy를 만든다.
        // 가속화된 그래픽 관리를 위해서!
        createBufferStrategy(2);
        strategy = getBufferStrategy();

    }

    public void gameLoop() {

    }



    public static void main(String argv[]) throws IOException {
        MainView MV = new MainView();

        // Start the main game loop, note: this method will not
        // return until the game has finished running. Hence we are
        // using the actual main thread to run the game.

    }


}

