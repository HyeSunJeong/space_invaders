package org.newdawn.spaceinvaders.resource;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class SoundPlayer {

    private Clip clip;
    private URL[] soundURL = new URL[30];

    public SoundPlayer(){
        soundURL[0] = getClass().getResource("/sound/game_back_music.wav");
        soundURL[1] = getClass().getResource("/sound/button.wav");
        soundURL[2] = getClass().getResource("/sound/main_back_music.wav");
        soundURL[3] = getClass().getResource("/sound/equip_spaceship.wav");
        soundURL[4] = getClass().getResource("/sound/buy_item.wav");
        soundURL[5] = getClass().getResource("/sound/denied.wav");
        soundURL[6] = getClass().getResource("/sound/boss_appearance.wav");
        soundURL[7] = getClass().getResource("/sound/boss_skill_shot.wav");
        soundURL[8] = getClass().getResource("/sound/boss_skill1.wav");
        soundURL[9] = getClass().getResource("/sound/boss_skill2.wav");
        soundURL[10] = getClass().getResource("/sound/hit_monster.wav");
        soundURL[11] = getClass().getResource("/sound/hit_player.wav");
        soundURL[12] = getClass().getResource("/sound/get_coin.wav");
        soundURL[13] = getClass().getResource("/sound/game_clear.wav");
        soundURL[14] = getClass().getResource("/sound/game_over.wav");
        soundURL[15] = getClass().getResource("/sound/meteor_falling.wav");
        soundURL[16] = getClass().getResource("/sound/player_shot.wav");
        soundURL[17] = getClass().getResource("/sound/heal.wav");
        soundURL[18] = getClass().getResource("/sound/speed_up.wav");
        soundURL[19] = getClass().getResource("/sound/boss_shot.wav");
    }

    private void setFile(int i){
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void play(){
        clip.start();
    }

    private void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    private void stop(){
        clip.stop();
        clip.close();
    }

    public void playMusic(int i, float volume){
        setFile(i);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(volume);
        play();
        loop();
    }

    public void stopMusic(int i){
        setFile(i);
        stop();
    }

    public void playSE(int i, float volume){
        setFile(i);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(volume);
        play();
    }
}

