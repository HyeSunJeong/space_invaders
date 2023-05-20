package org.newdawn.spaceinvaders;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

public class MainUI extends JPanel {
    Font NeoDung;
    Graphics2D g2;
    GameLobbyPanel glp;
    BufferedImage background, gameLogo, satellite, gameLogo_shadow, choiceButton;
    BufferedImage coinImg, healPotion, speedPotion, hardShip, luckShip, tutorialWindow, dialogWindow;
    BufferedImage basicShip;
    Color lightRed = new Color(250,90,90);
    public int commandNum = 0;
    public boolean coinLackState = false;
    public boolean possState = false;
    public boolean purchaseState = false;
    public boolean equipState = false;
    public boolean unableLoginState = false;
    public boolean outOfLengthState = false;
    public boolean idpwEqualState = false;
    public boolean pwConfirmErrorState = false;
    public boolean registerSuccessState = false;
    public boolean inputExistState = false;
    public boolean dialogState = false;
    public boolean exitConfirmState = false;
    public boolean signOutConfirmState = false;

    //상점 가격
    public static int hpcoin = 50;
    public static int spcoin = 50;
    public static int hscoin = 1000;
    public static int lscoin = 1500;

    public MainUI(GameLobbyPanel glp){
        this.glp = glp;
        loadTitleImg();
        try {
            InputStream is = getClass().getResourceAsStream("fonts/NeoDunggeunmoPro-Regular.ttf");
            NeoDung = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        //setFont(NeoDung);
    }

    public void draw(Graphics2D g2){
        this.g2 = g2;

        if(glp.gameState == glp.titleState){
            drawTitleScreen();
        }

        else if(glp.gameState == glp.shopState){
            drawShopScreen();
        }

        else if(glp.gameState == glp.userState){
            drawUserScreen();
        }

        else if(glp.gameState == glp.initialState){
            drawInitialScreen();
        }

        else if(glp.gameState == glp.signInState){
            drawSignInScreen();
        }
        else if(glp.gameState == glp.signUpState){
            drawSignUpScreen();
        }
        else if(glp.gameState == glp.tutorialState){
            drawTutorialScreen();
        }
        else if(glp.gameState == glp.changeNickState){
            drawChangeNickScreen();
        }
    }

    public void drawTitleScreen(){
        g2.drawImage(background,0,0,null);
        g2.drawImage(gameLogo_shadow,231,145,null);
        g2.drawImage(gameLogo,225,139,null);
        g2.drawImage(satellite,111,98,null);

        g2.setFont(NeoDung);
        g2.setColor(Color.white);

        String text = "";
        String additionalText = "";
        int x,y;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,35f));
        text = "start";
        x = 171;
        y = 450;
        g2.drawString(text,x, y);
        if(commandNum == 0 && !dialogState){
            selectOption(x,y,text);
            additionalText = "Start the game. There are a total of five stages and each stage has a boss. Good luck!";
            showExplanation(additionalText);
            if(UserDB.selected_ship == 0){
                g2.drawImage(basicShip,x+20,y-50,null);
            }
            if(UserDB.selected_ship == 1){
                g2.drawImage(hardShip,x+20,y-60,30,30,null);
            }
            if(UserDB.selected_ship == 2){
                g2.drawImage(luckShip,x+20,y-60,30,30,null);
            }
        }

        text = "shop";
        x = 310;
        g2.drawString(text,x,y);
        if(commandNum == 1 && !dialogState){
            selectOption(x,y,text);
            additionalText = "You can use the coins you got during the game. Be Stronger!";
            showExplanation(additionalText);
        }

        text = "user";
        x = 440;
        g2.drawString(text, x,y);
        if(commandNum == 2 && !dialogState){
            selectOption(x,y,text);
            additionalText = "You can check the number of potions and change the ship.";
            showExplanation(additionalText);
        }

        text = "quit";
        x = 570;
        g2.drawString(text, x,y);
        if(commandNum == 3 && !dialogState){
            selectOption(x,y,text);
            additionalText = "Save and exit the game.";
            showExplanation(additionalText);
        }

        if (UserDB.is_logged_in) {
            text = "sign out";
            x = 575;
            y = 88;
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN,20f));
            g2.drawString(text, x,y);
            if(commandNum == 4 && !dialogState){
                selectOption(x,y,text,true);
                additionalText = "Save the game and sign out.";
                showExplanation(additionalText);
            }
        }

        else {
            text = "to main";
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN,20f));
            x = 575;
            y = 88;
            g2.drawString(text, x,y);
            if(commandNum == 4 && !dialogState){
                selectOption(x,y,text,true);
                additionalText = "Go back to the lobby to sign in or sign up.";
                showExplanation(additionalText);
            }
        }

        text = UserDB.nickname;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,35f));
        x = getXforCenteredText(text);
        y = 350;
        g2.drawString(text, x, y);
        if(commandNum == 5 && !dialogState){
            selectOption(x,y,text);
            additionalText = "Change your nickname.";
            showExplanation(additionalText);
        }

        DecimalFormat df = new DecimalFormat("###,###");
        String scoreComma = df.format(UserDB.best_score);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,20f));
        text = "High Score: ";
        String scoreLength = text + scoreComma;
        x = getXforCenteredText(scoreLength);
        g2.drawString(text, x, 390);
        g2.setColor(Color.orange);
        x += g2.getFontMetrics().stringWidth(text);
        g2.drawString(scoreComma,x,390);

        if(dialogState){
            drawDialogWindow();
        }
    }

    public void drawShopScreen(){

        g2.drawImage(background,0,0,null);
        g2.drawImage(healPotion,113,273,70,70,null);
        g2.drawImage(speedPotion,269,273,70,70,null);
        g2.drawImage(hardShip,440,255,null);
        g2.drawImage(luckShip,591,255,null);
        g2.drawImage(coinImg,552,119,20,20,null);
        g2.drawImage(coinImg,126,398,null);
        g2.drawImage(coinImg,284,398,null);
        g2.drawImage(coinImg,459,398,null);
        g2.drawImage(coinImg,613,398,null);

        g2.setFont(NeoDung);
        g2.setColor(Color.white);

        String text = "";
        String additionalText = "";
        int x,y;
        Color textColor = Color.white;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,50f));
        text = "shop";
        x = 120;
        y = 130;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "potion";
        x = 106;
        y = 223;
        g2.drawString(text,x, y);

        text = "ship";
        x = 443;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,20f));
        g2.setColor(textColor);
        text = "heal potion";
        x = 99;
        y = 378;
        int nx = x;
        int ny = y;
        g2.drawString(text,x, y);
        if(commandNum == 0){
            selectOptionLightWithImg(x,y,text,false);
        }

        g2.setColor(textColor);
        text = "speed potion";
        x = 250;
        g2.drawString(text,x, y);
        if(commandNum == 1){
            selectOptionLightWithImg(x,y,text,false);
        }

        g2.setColor(textColor);
        text = "hard ship";
        x = 449;
        g2.drawString(text,x, y);
        if(commandNum == 2){
            selectOptionLightWithImg(x,y,text,false);
        }

        g2.setColor(textColor);
        text = "lucky ship";
        x = 598;
        g2.drawString(text,x, y);
        if(commandNum == 3){
            selectOptionLightWithImg(x,y,text,false);
        }

        g2.setColor(textColor);
        text = Integer.toString(hpcoin);
        x = 160;
        y = 418;
        g2.drawString(text,x, y);
        if(commandNum == 0){
            selectOptionLightWithImg(x,y,text,true);
            if(!coinLackState && !purchaseState) {
                additionalText = "Restore your 1 HP.";
                showExplanation(25f, 20f, Color.lightGray, 180, additionalText);
            }
            if(coinLackState) {
                String notice = "Not enough coins :(";
                showExplanation(25f,20f,lightRed,180,notice);
            }
            if(purchaseState){
                String notice = "Purchased! :)";
                showExplanation(25f,20f,Color.CYAN,180,notice);
            }
        }

        g2.setColor(textColor);
        text = Integer.toString(spcoin);
        x = x + 158;
        g2.drawString(text,x, y);
        if(commandNum == 1){
            selectOptionLightWithImg(x, y, text,true);
            if(!coinLackState && !purchaseState) {
                additionalText = "Can move FASTER!";
                showExplanation(25f, 20f, Color.lightGray, 180, additionalText);
            }
            if(coinLackState){
                String notice = "Not enough coins :(";
                showExplanation(25f,20f,lightRed,180,notice);
            }
            if(purchaseState){
                String notice = "Purchased! :)";
                showExplanation(25f,20f,Color.CYAN,180,notice);
            }
        }

        g2.setColor(textColor);
        text = Integer.toString(hscoin);
        x = x + 175;
        g2.drawString(text,x, y);
        if(commandNum == 2){
            selectOptionLightWithImg(x,y,text,true);
            if(!coinLackState && !possState && !purchaseState) {
                additionalText = "MAX HP increases by 2!";
                showExplanation(25f, 20f, Color.lightGray, 180, additionalText);
            }
            if(coinLackState) {
                String notice = "Not enough coins :(";
                showExplanation(25f,20f,lightRed,180,notice);
            }
            if(possState){
                String notice = "You already have this ship.";
                showExplanation(25f,20f,Color.orange,180,notice);
            }
            if(purchaseState){
                String notice = "Purchased! Can equip in user.";
                showExplanation(25f,20f,Color.CYAN,180,notice);
            }
        }

        g2.setColor(textColor);
        text = Integer.toString(lscoin);
        x = x + 154;
        g2.drawString(text,x, y);
        if(commandNum == 3){
            selectOptionLightWithImg(x,y,text,true);
            if(!coinLackState && !possState && !purchaseState) {
                additionalText = "Luck increases by 50%. For more coins!";
                showExplanation(25f, 20f, Color.lightGray, 180, additionalText);
            }
            if(coinLackState) {
                String notice = "Not enough coins :(";
                showExplanation(25f,20f,lightRed,180,notice);
            }
            if(possState){
                String notice = "You already have this ship.";
                showExplanation(25f,20f,Color.orange,180,notice);
            }
            if(purchaseState){
                String notice = "Purchased! Can equip in user.";
                showExplanation(25f,20f,Color.CYAN,180,notice);
            }
        }

        g2.setColor(Color.white);
        text = Integer.toString(UserDB.coin);
        x = 585;
        y = 135;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "back to title";
        x = getXforCenteredText(text);
        y = 490;
        g2.drawString(text,x, y);
        if(commandNum == 4){
            selectOption(x,y,text);
        }

        g2.setColor(Color.white);
        nx += 70;
        ny -= 30;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 25f));
        g2.setColor(Color.white);
        text = Integer.toString(UserDB.HP_potion);
        g2.drawString(text, nx, ny);

        nx += 160;
        text = Integer.toString(UserDB.speed_potion);
        g2.drawString(text, nx, ny);
    }

    public void drawUserScreen(){

        g2.drawImage(background,0,0,null);
        g2.drawImage(healPotion,97,263,50,50,null);
        g2.drawImage(speedPotion,97,343,50,50,null);
        g2.drawImage(basicShip,320,265,115,80,null);

        g2.setFont(NeoDung);
        g2.setColor(Color.white);

        String text = "";
        String additionalText = "";
        int x,y;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,50f));
        text = "user";
        x = 120;
        y = 130;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "potion";
        x = 106;
        y = 223;
        g2.drawString(text,x, y);

        text = "ship";
        x = 320;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,17f));
        text = "heal potion";
        x = 160;
        y = 272;
        g2.drawString(text,x, y);

        text = "speed potion";
        y += 80;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,20f));
        text = Integer.toString(UserDB.HP_potion);
        x = 170;
        y = 300;
        g2.drawString(text,x, y);

        text = Integer.toString(UserDB.speed_potion);
        y += 80;
        g2.drawString(text,x, y);

        text = "basic ship";
        x = 333;
        y = 378;
        int EX = x;
        int EY = y;
        g2.drawString(text,x, y);
        if(commandNum == 0){
            selectOption(x,y,text,true);
            if(!equipState) {
                additionalText = "A basic ship. If you enjoy the difficulty, Try it!";
                showExplanation(25f, 20f, Color.lightGray, 180, additionalText);
            }
            if(equipState) {
                additionalText = "Equipped!";
                showExplanation(25f, 20f, Color.cyan, 180, additionalText);
            }
        }

        x += 140;
        if (UserDB.is_hard_ship) {
            text = "hard ship";
            g2.drawString(text, x, y);
            g2.drawImage(hardShip,464,248,null);
            if (commandNum == 1) {
                selectOption(x, y, text, true);
                if (!equipState) {
                    additionalText = "The hardness of steel.";
                    showExplanation(25f, 20f, Color.lightGray, 180, additionalText);
                }
                if (equipState) {
                    additionalText = "Equipped!";
                    showExplanation(25f, 20f, Color.cyan, 180, additionalText);
                }
            }
        }

        x += 135;
        if (UserDB.is_lucky_ship) {
            text = "lucky ship";
            g2.drawString(text,x, y);
            g2.drawImage(luckShip,593,251,null);
            if(commandNum == 2){
                selectOption(x,y,text,true);
                if(!equipState) {
                    additionalText = "When using the skill , it becomes invincible.";
                    showExplanation(25f, 20f, Color.lightGray, 180, additionalText);
                }
                if(equipState) {
                    additionalText = "Equipped!";
                    showExplanation(25f, 20f, Color.cyan, 180, additionalText);
                }
            }
        }

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "back to title";
        x = getXforCenteredText(text);
        y = 490;
        g2.drawString(text,x, y);
        if(commandNum == 3) {
            selectOption(x, y, text);
        }

        EX += 80;
        EY -= 100;
        if(UserDB.selected_ship == 0) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 25f));
            g2.setColor(Color.cyan);
            text = "E";
            g2.drawString(text, EX, EY);
        }
        EX += 130;
        if(UserDB.selected_ship == 1) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 25f));
            g2.setColor(Color.cyan);
            text = "E";
            g2.drawString(text, EX, EY);
        }
        EX += 130;
        if(UserDB.selected_ship == 2) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 25f));
            g2.setColor(Color.cyan);
            text = "E";
            g2.drawString(text, EX, EY);
        }
    }

    public void drawInitialScreen(){
        g2.drawImage(background,0,0,null);
        g2.drawImage(gameLogo_shadow,231,145,null);
        g2.drawImage(gameLogo,225,139,null);
        g2.drawImage(satellite,111,98,null);

        g2.setFont(NeoDung);

        String text = "";
        String additionalText = "";
        int x,y;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        g2.setColor(Color.gray);
        text = "sign in";
        x = getXforCenteredText(text);
        y = 340;
        g2.drawString(text,x, y);
        if(commandNum == 0){
            selectOptionLight(x,y,text);
        }

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "sign up";
        x = getXforCenteredText(text);
        y += 55;
        g2.drawString(text,x,y);
        if(commandNum == 1){
            selectOptionLight(x,y,text);
        }

        text = "guest";
        x= getXforCenteredText(text);
        y += 55;
        g2.drawString(text, x,y);
        if(commandNum == 2){
            selectOptionLight(x,y,text);
           /* additionalText = "Access in guest mode. The history played will not be saved.";
            showExplanation(additionalText);*/
        }

        text = "quit";
        x = getXforCenteredText(text);
        y += 55;
        g2.drawString(text, x,y);
        if(commandNum == 3){
            selectOptionLight(x,y,text);
        }
    }

    public void  drawSignInScreen() {
        g2.drawImage(background,0,0,null);

        g2.setFont(NeoDung);

        String text = "";
        String additionalText = "";
        int x,y;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,50f));
        g2.setColor(Color.white);
        text = "sign in";
        x = getXforCenteredText(text);
        y = 145;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "ID :";
        x = 250;
        y = 235;
        g2.drawString(text,x, y);
        if(commandNum == 0){ selectOptionOnlyImg(x,y); }

        text = "PW :";
        x -= 5;
        y += 55;
        g2.drawString(text,x, y);
        if(commandNum == 1){ selectOptionOnlyImg(x,y); }

        text = glp.key.idString;
        x += 53;
        y = 235;
        g2.drawString(text,x, y);

        text = glp.key.pwString;
        String passwordField = new String(new char[text.length()]).replace("\0","*");
        y += 55;
        g2.drawString(passwordField,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "OK!";
        x = getXforCenteredText(text);
        y = 430;
        g2.drawString(text,x, y);
        if(commandNum == 2){ selectOption(x,y,text);}

        text = "back to title";
        x = getXforCenteredText(text);
        y += 50;
        g2.drawString(text,x, y);
        if(commandNum == 3){selectOption(x,y,text);}

        if(unableLoginState) {
            additionalText = "Incorrect ID or Password!";
            showExplanation(25f, 20f, lightRed, 340, additionalText);
        }
    }

    public void drawSignUpScreen(){
        g2.drawImage(background,0,0,null);

        g2.setFont(NeoDung);

        String text = "";
        String additionalText = "";
        int x,y;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,50f));
        g2.setColor(Color.white);
        text = "sign up";
        x = getXforCenteredText(text);
        y = 145;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "ID :";
        x = 250;
        y = 210;
        g2.drawString(text,x, y);
        if(commandNum == 0){ selectOptionOnlyImg(x,y); }

        text = "PW :";
        x -= 5;
        y += 50;
        g2.drawString(text,x, y);
        if(commandNum == 1){ selectOptionOnlyImg(x,y); }

        text = "reconfirm PW :";
        x -= 135;
        y += 50;
        g2.drawString(text,x, y);
        if(commandNum == 2){ selectOptionOnlyImg(x,y); }

        text = "nickname :";
        x += 53;
        y += 50;
        g2.drawString(text,x, y);
        if(commandNum == 3){ selectOptionOnlyImg(x,y); }

        text = glp.key.idString;
        x = 300;
        y = 210;
        g2.drawString(text,x, y);

        text = glp.key.pwString;
        String passwordField = new String(new char[text.length()]).replace("\0","*");
        y += 50;
        g2.drawString(passwordField,x, y);

        text = glp.key.recPwString;
        passwordField = new String(new char[text.length()]).replace("\0","*");
        y += 50;
        g2.drawString(passwordField,x, y);

        text = glp.key.nicString;
        y += 50;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "OK!";
        x = getXforCenteredText(text);
        y = 430;
        g2.drawString(text,x, y);
        if(commandNum == 4){ selectOption(x,y,text);}

        text = "back to title";
        x = getXforCenteredText(text);
        y += 50;
        g2.drawString(text,x, y);
        if(commandNum == 5){selectOption(x,y,text);}

        if(outOfLengthState) {
            additionalText = "Input must be 8 to 12 characters.";
            showExplanation(25f, 20f, lightRed, 395, additionalText);
        }
        if(idpwEqualState) {
            additionalText = "ID and password are the same.";
            showExplanation(25f, 20f, lightRed, 395, additionalText);
        }
        if(pwConfirmErrorState) {
            additionalText = "Password does not match.";
            showExplanation(25f, 20f, lightRed, 395, additionalText);
        }
        if(inputExistState) {
            additionalText = "The same ID, password, or nickname exists.";
            showExplanation(25f, 20f, lightRed, 395, additionalText);
        }
        if(registerSuccessState) {
            additionalText = "Register Successful!";
            showExplanation(25f, 20f, Color.green, 395, additionalText);
        }
    }

    public void drawTutorialScreen(){
        g2.drawImage(tutorialWindow,0,0,null);

        g2.setFont(NeoDung);

        String text = "";
        String additionalText = "";
        int x,y;

        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "OK!";
        x = getXforCenteredText(text);
        y = 520;
        g2.drawString(text,x, y);
        if(commandNum == 0){ selectOption(x,y,text);}

        text = "back to title";
        x = getXforCenteredText(text);
        y += 40;
        g2.drawString(text,x, y);
        if(commandNum == 1){selectOption(x,y,text);}
    }

    public void drawChangeNickScreen(){
        g2.drawImage(background,0,0,null);

        g2.setFont(NeoDung);

        String text = "";
        String additionalText = "";
        int x,y;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,50f));
        g2.setColor(Color.white);
        text = "change nickname";
        x = getXforCenteredText(text);
        y = 145;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "nickname :";
        x = 210;
        y = 285;
        g2.drawString(text,x, y);
        if(commandNum == 0){ selectOptionOnlyImg(x,y); }

        text = glp.key.nicString;
        x += 150;
        g2.drawString(text,x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "OK!";
        x = getXforCenteredText(text);
        y = 430;
        g2.drawString(text,x, y);
        if(commandNum == 1){ selectOption(x,y,text);}

        text = "back to title";
        x = getXforCenteredText(text);
        y += 50;
        g2.drawString(text,x, y);
        if(commandNum == 2){selectOption(x,y,text);}

        if(outOfLengthState) {
            additionalText = "Input must be 8 to 12 characters.";
            showExplanation(25f, 20f, lightRed, 340, additionalText);
        }
        else if(inputExistState) {
            additionalText = "Same nickname exists.";
            showExplanation(25f, 20f, lightRed, 340, additionalText);
        }
        else if(registerSuccessState) {
            additionalText = "Changed successful!";
            showExplanation(25f, 20f, Color.green, 340, additionalText);
        }
    }

    public void drawDialogWindow(){
        g2.drawImage(dialogWindow,0,0,null);

        g2.setFont(NeoDung);

        String text = "";
        int x,y;

        y = 265;
        if (exitConfirmState) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN,35f));
            g2.setColor(Color.white);
            text = "Game saved. Wanna exit?";
            x = getXforCenteredText(text);
            g2.drawString(text,x, y);
        }
        else if (signOutConfirmState) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
            g2.setColor(Color.white);
            text = "Game saved.";
            x = getXforCenteredText(text);
            g2.drawString(text,x, y);

            text = "Sign out and back to main.";
            x = getXforCenteredText(text);
            y += 35;
            g2.drawString(text,x, y);
        }

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,30f));
        text = "YES";
        x = glp.screenWidth/2 - 110;
        y = 370;
        g2.drawString(text,x, y);
        if(commandNum == 0){ selectOption(x,y,text);}

        text = "NO";
        x = glp.screenWidth/2 + 75;
        g2.drawString(text,x, y);
        if(commandNum == 1){ selectOption(x,y,text);}
    }


    public int getXforCenteredText(String text){
        int stringWidth = g2.getFontMetrics().stringWidth(text);
        int xCoordinate = (glp.screenWidth - stringWidth) / 2;
        return xCoordinate;
    }

    public void selectOption(int x, int y, String text){
        g2.drawImage(choiceButton,x - 32, y - 21,null);
        g2.setColor(Color.gray);
        g2.drawString(text,x,y);
        g2.setColor(Color.white);
        g2.drawString(text,x-3,y-3);
    }

    public void selectOption(int x, int y, String text, boolean img){
        if (img) g2.drawImage(choiceButton,x - 30, y - 17,null);
        g2.setColor(Color.gray);
        g2.drawString(text,x,y);
        g2.setColor(Color.white);
        g2.drawString(text,x-2,y-2);
    }

    public void selectOptionOnlyImg(int x, int y){
        g2.drawImage(choiceButton,x - 30, y - 19,null);
    }

    public void selectOptionLight(int x, int y, String text){
        g2.drawImage(choiceButton,x - 30, y - 17,null);
        g2.setColor(Color.white);
        g2.drawString(text,x,y);
        g2.setColor(Color.gray);
    }

    public void selectOptionWithImg(int x, int y, String text){
        g2.drawImage(choiceButton,x - 60, y - 15,null);
        g2.setColor(Color.gray);
        g2.drawString(text,x,y);
        g2.setColor(Color.white);
        g2.drawString(text,x-2,y-2);
    }

    public void selectOptionLightWithImg(int x, int y, String text, boolean img){
        if(img) g2.drawImage(choiceButton,x - 60, y - 17,null);
        g2.setColor(Color.yellow);
        g2.drawString(text,x,y);
    }

    public void showExplanation(String text){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 15f));
        g2.setColor(Color.lightGray);
        int x = getXforCenteredText(text);
        int y = 500;
        g2.drawString(text, x, y);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 35f));
        g2.setColor(Color.white);
    }

    public void showExplanation(float fontsize, float fontsize2,Color c,int y, String text){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, fontsize));
        g2.setColor(c);
        int x = getXforCenteredText(text);
        g2.drawString(text, x, y);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, fontsize2));
        g2.setColor(Color.white);
    }

    public void loadTitleImg() {
        try {
            //타이틀 이미지 로딩
            InputStream is = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/computer_back.png")));
            background = ImageIO.read(is);
            InputStream is2 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/gamelogo.png")));
            gameLogo = ImageIO.read(is2);
            InputStream is3 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/satellite.png")));
            satellite = ImageIO.read(is3);
            InputStream is4 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/gamelogo_shadow.png")));
            gameLogo_shadow = ImageIO.read(is4);
            InputStream is5 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/choice_tiny.png")));
            choiceButton = ImageIO.read(is5);

            //상점 이미지 로딩
            InputStream is6 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/coin.png")));
            coinImg = ImageIO.read(is6);
            InputStream is7 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/heal_potion.png")));
            healPotion = ImageIO.read(is7);
            InputStream is8 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/speed_potion.png")));
            speedPotion = ImageIO.read(is8);
            InputStream is9 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/hard_ship.png")));
            hardShip = ImageIO.read(is9);
            InputStream is10 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/lucky_ship.png")));
            luckShip = ImageIO.read(is10);
            InputStream is11 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/ship.gif")));
            basicShip = ImageIO.read(is11);
            InputStream is12 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/tutorial_window.png")));
            tutorialWindow = ImageIO.read(is12);
            InputStream is13 = new BufferedInputStream(Files.newInputStream(Paths.get("src/ui/dialog_window_trans.png")));
            dialogWindow = ImageIO.read(is13);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

