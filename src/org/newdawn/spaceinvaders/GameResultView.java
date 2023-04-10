package org.newdawn.spaceinvaders;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class GameResultView extends JFrame {

    // Label 만들기
    JPanel play_time = new JPanel(); // 플레이하는데 걸린 시간
    JPanel score = new JPanel(); // 게임 점수
    JPanel killed_monster_count = new JPanel(); // 처치한 몬스터 개수
    JPanel killed_boss_count = new JPanel(); // 처치한 보스 개수
    JPanel get_coin_count = new JPanel(); // 얻은 코인 개수

    // Buttons
    JButton to_main_button = new JButton("메인화면"); // 메인화면으로 가는 버튼
    public GameResultView() {



        // Frame
        setTitle("Space Invaders 102");
        setPreferredSize(new Dimension(800,600));

        // Grid
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridLayout grid = new GridLayout(8,1);
        grid.setVgap(1);

        Container c = getContentPane();
        c.setLayout(grid);

        // Panel 생성


        play_time.add(new JLabel("Play Time: "));
        play_time.setAlignmentX(CENTER_ALIGNMENT);
        score.add(new JLabel("Score: "));
        score.setAlignmentX(CENTER_ALIGNMENT);
        killed_monster_count.setAlignmentX(CENTER_ALIGNMENT);
        killed_monster_count.add(new JLabel("Killed Monster: "));
        killed_boss_count.setAlignmentX(CENTER_ALIGNMENT);
        killed_boss_count.add(new JLabel("Killed Boss: "));
        get_coin_count.setAlignmentX(CENTER_ALIGNMENT);
        get_coin_count.add(new JLabel("Coin: "));


        c.add(new JPanel());
        c.add(play_time);
        c.add(score);
        c.add(killed_monster_count);
        c.add(killed_boss_count);
        c.add(get_coin_count);
        c.add(to_main_button);


        //add(main_panel);
        pack();
        setResizable(false);
        setVisible(true);
    }


    public static void main(String argv[]) throws IOException {
        GameResultView GRV = new GameResultView();

    }
}