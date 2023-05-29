package org.newdawn.spaceinvaders.gameui;

import org.newdawn.spaceinvaders.GameLobbyPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

public class MainUI extends JPanel {
    //게임 시각 자원
    public Font NeoDung;
    private Graphics2D g2;
    private GameLobbyPanel glp;
    private BufferedImage background, gameLogo, satellite, gameLogo_shadow, choiceButton;
    private BufferedImage coinImg, healPotion, speedPotion, hardShip, luckShip, tutorialWindow, dialogWindow;
    private BufferedImage basicShip;
    private final Color lightRed = new Color(250,90,90);

    //키보드로 조작하여 움직이는 변수. UIKeyHandler 클래스에서 조작
    public int commandNum = 0;

    //코인 부족, 아이디 생성 불가 등 불가 메세지 출력에 사용되는 boolean
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
    public static final int healPotionPrice = 50;
    public static final int speedPotionPrice = 50;
    public static final int hardShipPrice = 1000;
    public static final int luckyShipPrice = 1500;

    public MainUI(GameLobbyPanel glp){
        this.glp = glp;
        loadTitleImg();
        loadFont();
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

    private void drawTitleScreen(){
        setUpScreen(35f,Color.white);
        g2.drawImage(gameLogo_shadow,231,145,null);
        g2.drawImage(gameLogo,225,139,null);
        g2.drawImage(satellite,111,98,null);

        int y = 450;

        drawStartButton();

        drawMenuButton("shop","You can use the coins you got during the game. Be Stronger!",310,y,1);

        drawMenuButton("user","You can check the number of potions and change the ship.",440,y,2);

        drawMenuButton("quit","Save and exit the game.",570,y,3);

        if (glp.us.isLoggedIn()) drawMenuButton("sign out","Save the game and sign out.",575,88,4,true,20f);

        else drawMenuButton("to main","Go back to the lobby to sign in or sign up.",575,88,4,true,20f);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,35f));
        drawMenuButton(glp.us.getNickname(),"Change your nickname.",getXforCenteredText(glp.us.getNickname()),350,5);

        showHighScore();

        if(dialogState){
            drawDialogWindow();
        }
    }

    private void drawShopScreen(){
        setUpScreen(50f,Color.white);
        g2.drawImage(healPotion,113,273,70,70,null);
        g2.drawImage(speedPotion,269,273,70,70,null);
        g2.drawImage(hardShip,440,255,null);
        g2.drawImage(luckShip,591,255,null);
        g2.drawImage(coinImg,552,119,20,20,null);
        g2.drawImage(coinImg,126,398,null);
        g2.drawImage(coinImg,284,398,null);
        g2.drawImage(coinImg,459,398,null);
        g2.drawImage(coinImg,613,398,null);

        String text;
        int x,y;

        x = 120;
        y = 130;
        drawButton("shop",x,y,50f);

        x = 106;
        y = 223;
        drawButton("potion",x,y,30f);

        x = 443;
        drawButton("ship",x,y,30f);

        x = 99;
        y = 378;
        int nx = x;
        int ny = y;

        drawShopButton("heal potion",x,y,0);

        x = 250;
        drawShopButton("speed potion",x,y,1);

        x = 449;
        drawShopButton("hard ship",x,y,2);

        x = 598;
        drawShopButton("lucky ship",x,y,3);

        x = 160;
        y = 418;
        drawShopPrice(Integer.toString(healPotionPrice),"Restore your 1 HP.",x,y,0);

        x += 158;
        drawShopPrice(Integer.toString(speedPotionPrice),"Can move FASTER!",x,y,1);

        x += 175;
        drawShopShipPrice(Integer.toString(hardShipPrice),"MAX HP Increases by 2!",x,y,2);

        x += 154;
        drawShopShipPrice(Integer.toString(luckyShipPrice),"Luck increases by 50%. For more coins!",x,y,3);

        x = 585;
        y = 135;
        drawButton(Integer.toString(glp.us.getCoin()),x,y,20f);

        y = 490;
        drawButton("back to title",getXforCenteredText("back to title"),y,30f,Color.white,true,4);

        nx += 70;
        ny -= 30;
        drawButton(Integer.toString(glp.us.getHealPotion()),nx,ny,25f);

        nx += 160;
        drawButton(Integer.toString(glp.us.getSpeedPotion()),nx,ny,25f);
    }

    private void drawUserScreen(){
        setUpScreen(50f,Color.white);
        g2.drawImage(healPotion,97,263,50,50,null);
        g2.drawImage(speedPotion,97,343,50,50,null);
        g2.drawImage(basicShip,320,265,115,80,null);

        int x,y;

        x = 120;
        y = 130;
        drawButton("user",x,y,50f);

        x = 106;
        y = 223;
        drawButton("potion",x,y,30f);

        x = 320;
        drawButton("ship",x,y,30f);

        x = 160;
        y = 272;
        drawButton("heal potion",x,y,17f);

        y += 80;
        drawButton("speed potion",x,y,17f);

        x = 170;
        y = 300;
        drawButton(Integer.toString(glp.us.getHealPotion()),x,y,20f);

        y += 80;
        drawButton(Integer.toString(glp.us.getSpeedPotion()),x,y,20f);

        x = 333;
        y = 378;
        int EX = x;
        int EY = y;

        drawUserState("basic ship","A basic ship. If you enjoy the difficulty, Try it!",x,y,0,basicShip,0,0,false);

        x += 140;
        if (glp.us.isHardShip())
            drawUserState("hard ship","The hardness of steel.",x,y,1,hardShip,464,248,true);

        x += 135;
        if (glp.us.isLuckyShip())
            drawUserState("lucky ship","When using the skill , it becomes invincible.",x,y,2,luckShip,593,251,true);

        y = 490;
        drawBoldButton("back to title",getXforCenteredText("back to title"),y,30f,Color.white,true,3);

        EX += 80;
        EY -= 100;
        if(glp.us.getSelectedShip() == 0) {
            drawBoldButton("E",EX,EY,25f,Color.cyan,false,0);
        }

        EX += 130;
        if(glp.us.getSelectedShip() == 1) {
            drawButton("E",EX,EY,25f,Color.cyan,false,0);
        }

        EX += 130;
        if(glp.us.getSelectedShip() == 2) {
            drawBoldButton("E",EX,EY,25f,Color.cyan,false,0);
        }
    }

    private void drawInitialScreen(){
        setUpScreen(30f,Color.white);
        g2.drawImage(gameLogo_shadow,231,145,null);
        g2.drawImage(gameLogo,225,139,null);
        g2.drawImage(satellite,111,98,null);

        int y;

        y = 340;
        drawButtonLight("sign in",getXforCenteredText("sign in"),y,30f,Color.gray,true,0);

        y += 55;
        drawButtonLight("sign up",getXforCenteredText("sign up"),y,30f,Color.gray,true,1);

        y += 55;
        drawButtonLight("guest",getXforCenteredText("guest"),y,30f,Color.gray,true,2);

        y += 55;
        drawButtonLight("quit",getXforCenteredText("quit"),y,30f,Color.gray,true,3);
    }

    private void  drawSignInScreen() {
        setUpScreen(50f,Color.white);

        int x,y;

        y = 145;
        drawButton("sign in",getXforCenteredText("sign in"),y,50f);

        x = 250;
        y = 235;
        drawButtonOnlyImg("ID :",x,y,30f,Color.white,true,0);

        x -= 5;
        y += 55;
        drawButtonOnlyImg("PW :",x,y,30f,Color.white,true,1);

        //glp.key.idString = UIKeyHandler에서 입력한 ID/Password/닉네임 문자열
        x += 53;
        y = 235;
        drawButton(glp.key.idString,x,y,30f);

        y += 55;
        drawButton(getPasswordField(glp.key.pwString),x,y,30f);

        y = 430;
        drawButtonOnlyImg("OK!",getXforCenteredText("OK!"),y,30f,Color.white,true,2);

        y += 50;
        drawButtonOnlyImg("back to title",getXforCenteredText("back to title"),y,30f,Color.white,true,3);

        showNotice(395);
    }

    private void drawSignUpScreen(){
        setUpScreen(50f,Color.white);

        int x,y;

        y = 145;
        drawButton("sign up",getXforCenteredText("sign up"),y,50f);

        x = 250;
        y = 210;
        drawButtonOnlyImg("ID :",x,y,30f,Color.white,true,0);

        x -= 5;
        y += 50;
        drawButtonOnlyImg("PW :",x,y,30f,Color.white,true,1);

        x -= 135;
        y += 50;
        drawButtonOnlyImg("reconfirm PW :",x,y,30f,Color.white,true,2);

        x += 53;
        y += 50;
        drawButtonOnlyImg("nickname :",x,y,30f,Color.white,true,3);

        x = 300;
        y = 210;
        drawButton(glp.key.idString,x,y,30f);

        y += 50;
        drawButton(getPasswordField(glp.key.pwString),x,y,30f);

        y += 50;
        drawButton(getPasswordField(glp.key.recPwString),x,y,30f);

        y += 50;
        drawButton(glp.key.nicString,x,y,30f);

        y = 430;
        drawButtonOnlyImg("OK!",getXforCenteredText("OK!"),y,30f,Color.white,true,4);

        y += 50;
        drawButtonOnlyImg("back to title",getXforCenteredText("back to title"),y,30f,Color.white,true,5);

        showNotice(395);
    }

    private void drawTutorialScreen(){
        g2.drawImage(tutorialWindow,0,0,null);
        g2.setFont(NeoDung);

        int y;

        y = 520;
        drawButton("OK!",getXforCenteredText("OK!"),y,30f,Color.white,true,0);

        y += 40;
        drawButton("back to title",getXforCenteredText("back to title"),y,30f,Color.white,true,1);
    }

    private void drawChangeNickScreen(){
        setUpScreen(50f,Color.white);

        int x,y;

        y = 145;
        drawButton("change nickname",getXforCenteredText("change nickname"),y,50f);

        x = 210;
        y = 285;
        drawButton("nickname :",getXforCenteredText("nickname :"),y,30f);

        x += 150;
        drawButton(glp.key.nicString,x,y,30f);

        y = 430;
        drawButton("OK!",getXforCenteredText("OK!"),y,30f,Color.white,true,1);

        y += 50;
        drawButton("back to title",getXforCenteredText("back to title"),y,30f,Color.white,true,2);

        showNotice(340);
    }

    private void drawDialogWindow(){
        g2.drawImage(dialogWindow,0,0,null);

        g2.setFont(NeoDung);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,35f));
        g2.setColor(Color.white);

        int y;

        y = 265;
        if (exitConfirmState) {
            drawButton("Game saved. Wanna exit?",getXforCenteredText("Game saved. Wanna exit?"),y,35f);
        }
        else if (signOutConfirmState) {
            drawButton("Game saved.",getXforCenteredText("Game saved."),y,30f);

            y += 35;
            drawButton("Sign out and back to main.",getXforCenteredText("Sign out and back to main."),y,30f);
        }

        y = 370;
        drawButton("YES",glp.screenWidth/2 - 110,y,30f,Color.white,true,0);

        drawButton("NO",glp.screenWidth/2 + 75,y,30f,Color.white,true,1);
    }


    //문자열의 크기를 기반으로 텍스트가 화면 중앙에 위치하도록 X값을 잡아주는 함수
    private int getXforCenteredText(String text){
        int stringWidth = g2.getFontMetrics().stringWidth(text);
        int xCoordinate = (glp.screenWidth - stringWidth) / 2;
        return xCoordinate;
    }

    //문자열이 비밀번호처럼 *로 표기되게하는 메소드
    private String getPasswordField(String pw){
        String field;
        field = new String(new char[pw.length()]).replace("\0","*");
        return field;
    }

    //선택된 옵션을 음영 + 삼각형으로 강조하기
    private void selectOption(int x, int y, String text){
        g2.drawImage(choiceButton,x - 32, y - 21,null);
        g2.setColor(Color.gray);
        g2.drawString(text,x,y);
        g2.setColor(Color.white);
        g2.drawString(text,x-3,y-3);
    }

    //문자열에 이미지가 포함되어있을 때의 강조 옵션
    private void selectOption(int x, int y, String text, boolean img){
        if (img) g2.drawImage(choiceButton,x - 30, y - 17,null);
        g2.setColor(Color.gray);
        g2.drawString(text,x,y);
        g2.setColor(Color.white);

        g2.drawString(text,x-2,y-2);
    }

    //문자열이 아닌 이미지만을 강조
    private void selectOptionOnlyImg(int x, int y){
        g2.drawImage(choiceButton,x - 30, y - 19,null);
    }

    //음영이 아닌 밝게 하여 강조
    private void selectOptionLight(int x, int y, String text){
        g2.drawImage(choiceButton,x - 30, y - 17,null);
        g2.setColor(Color.white);
        g2.drawString(text,x,y);
        g2.setColor(Color.gray);
    }

    //이미지와 함께하는 문자열일 때 밝게 강조함
    private void selectOptionLightWithImg(int x, int y, String text, boolean img){
        if(img) g2.drawImage(choiceButton,x - 60, y - 17,null);
        g2.setColor(Color.yellow);
        g2.drawString(text,x,y);
    }

    //회색으로 표기되는 설명문
    private void showExplanation(String text){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 15f));
        g2.setColor(Color.lightGray);
        int x = getXforCenteredText(text);
        int y = 500;
        g2.drawString(text, x, y);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 35f));
        g2.setColor(Color.white);
    }

    //다른 색, 위치, 폰트 크기로 표기되는 설명문
    private void showExplanation(float fontsize, float fontsize2,Color c,int y, String text){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, fontsize));
        g2.setColor(c);
        int x = getXforCenteredText(text);
        g2.drawString(text, x, y);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, fontsize2));
        g2.setColor(Color.white);
    }

    //하이스코어 표시
    private void showHighScore(){
        String text;
        int x;

        DecimalFormat df = new DecimalFormat("###,###");
        String scoreComma = df.format(glp.us.getHighScore());
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,20f));
        text = "High Score: ";
        String scoreLength = text + scoreComma;
        x = getXforCenteredText(scoreLength);
        g2.drawString(text, x, 390);
        g2.setColor(Color.orange);
        x += g2.getFontMetrics().stringWidth(text);
        g2.drawString(scoreComma,x,390);
    }

    //경고문 표시
    private void showNotice(int y){
        String additionalText;
        if(unableLoginState) {
            additionalText = "Incorrect ID or Password!";
            showExplanation(25f, 20f, lightRed, y, additionalText);
        }
        if(outOfLengthState) {
            additionalText = "Input must be 8 to 12 characters.";
            showExplanation(25f, 20f, lightRed, y, additionalText);
        }
        if(idpwEqualState) {
            additionalText = "ID and password are the same.";
            showExplanation(25f, 20f, lightRed, y, additionalText);
        }
        if(pwConfirmErrorState) {
            additionalText = "Password does not match.";
            showExplanation(25f, 20f, lightRed, y, additionalText);
        }
        if(inputExistState) {
            additionalText = "The same ID, password, or nickname exists.";
            showExplanation(25f, 20f, lightRed, y, additionalText);
        }
        if(registerSuccessState) {
            additionalText = "Register Successful!";
            showExplanation(25f, 20f, Color.green, y, additionalText);
        }
    }

    //문자 그리기
    private void drawButton(String text, int x, int y, float fontSize){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, fontSize));
        g2.setColor(Color.white);
        g2.drawString(text, x, y);
    }

    //선택 시 강조효과(음영)가 뜨는 문자
    private void drawButton(String text, int x, int y, float fontSize, Color fontColor, boolean isCommand, int command){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, fontSize));
        g2.setColor(fontColor);
        g2.drawString(text, x, y);
        if(isCommand && commandNum == command){
            selectOption(x,y,text);
        }
    }

    //선택 시 강조효과(빛남)이 뜨는 문자
    private void drawButtonLight(String text, int x, int y, float fontSize, Color fontColor, boolean isCommand, int command){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, fontSize));
        g2.setColor(fontColor);
        g2.drawString(text, x, y);
        if(isCommand && commandNum == command){
            selectOptionLight(x,y,text);
        }
    }

    //선택 시 삼각형만 뜨는 문자
    private void drawButtonOnlyImg(String text, int x, int y, float fontSize, Color fontColor, boolean isCommand, int command){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, fontSize));
        g2.setColor(fontColor);
        g2.drawString(text, x, y);
        if(isCommand && commandNum == command){
            selectOptionOnlyImg(x,y);
        }
    }

    //볼드체인 문자
    private void drawBoldButton(String text, int x, int y, float fontSize, Color fontColor, boolean isCommand, int command){
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, fontSize));
        g2.setColor(fontColor);
        g2.drawString(text, x, y);
        if(isCommand && commandNum == command){
            selectOption(x,y,text);
        }
    }

    //타이틀 화면 버튼 그리기
    private void drawMenuButton(String text, String additionalText, int x, int y, int command){
        g2.drawString(text,x,y);
        if(commandNum == command && !dialogState){
            selectOption(x,y,text);
            showExplanation(additionalText);
        }
    }

    //타이틀 화면 버튼 중 폰트조절이 필요한 것
    private void drawMenuButton(String text, String additionalText, int x, int y, int command, boolean imgTrue,float fontSize){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,fontSize));
        g2.drawString(text, x,y);
        if(commandNum == command && !dialogState){
            selectOption(x,y,text,imgTrue);
            showExplanation(additionalText);
        }
    }

    private void drawStartButton(){
        String text = "start";
        String additionalText = "Start the game. There are a total of five stages and each stage has a boss. Good luck!";
        int x = 171;
        int y = 450;
        g2.drawString(text,x, y);
        if(commandNum == 0 && !dialogState) {
            selectOption(x, y, text);
            showExplanation(additionalText);
            switch (glp.us.getSelectedShip()) {
                case 0:
                    g2.drawImage(basicShip, x + 20, y - 50, null);
                    break;
                case 1:
                    g2.drawImage(hardShip, x + 20, y - 60, 30, 30, null);
                    break;
                case 2:
                    g2.drawImage(luckShip, x + 20, y - 60, 30, 30, null);
                    break;
                default:
                    break;
            }
        }
    }

    //상점 버튼 그리기
    private void drawShopButton(String text, int x, int y, int command){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,20f));
        g2.setColor(Color.white);
        g2.drawString(text,x, y);
        if(commandNum == command){
            selectOptionLightWithImg(x,y,text,false);
        }
    }

    //상점 가격표 그리기 & 경고문 표시
    private void drawShopPrice(String text, String additionalText, int x, int y, int command){
        g2.setColor(Color.white);
        g2.drawString(text,x,y);
        if(commandNum == command){
            selectOptionLightWithImg(x,y,text,true);
            if(!coinLackState && !purchaseState) {
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
    }

    //상점 물품 중 우주선 가격표 그리기 & 경고문 표시
    private void drawShopShipPrice(String text, String additionalText, int x, int y, int command){
        g2.setColor(Color.white);
        g2.drawString(text,x, y);
        if(commandNum == command){
            selectOptionLightWithImg(x,y,text,true);
            if(!coinLackState && !possState && !purchaseState) {
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
    }

    //유저창 버튼 그리기
    private void drawUserState(String text, String additionalText, int x, int y, int command, BufferedImage ship, int imgX,int imgY, boolean img){
        g2.drawString(text,x, y);
        if(img) g2.drawImage(ship,imgX,imgY,null);
        if(commandNum == command){
            selectOption(x,y,text,true);
            if(!equipState) {
                showExplanation(25f, 20f, Color.lightGray, 180, additionalText);
            }
            if(equipState) {
                showExplanation(25f, 20f, Color.cyan, 180, "Equipped!");
            }
        }
    }

    //화면 초기설정
    private void setUpScreen(float fontSize, Color fontColor){
        g2.drawImage(background,0,0,null);
        g2.setFont(NeoDung);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,fontSize));
        g2.setColor(fontColor);
    }

    //폰트 로딩
    private void loadFont(){
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/NeoDunggeunmoPro-Regular.ttf");
            NeoDung = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    //이미지 로딩
    private void loadTitleImg() {
        try {
            background = loadImage("src/ui/computer_back.png");
            gameLogo = loadImage("src/ui/gamelogo.png");
            satellite = loadImage("src/ui/satellite.png");
            gameLogo_shadow = loadImage("src/ui/gamelogo_shadow.png");
            choiceButton = loadImage("src/ui/choice_tiny.png");
            coinImg = loadImage("src/ui/coin.png");
            healPotion = loadImage("src/ui/heal_potion.png");
            speedPotion = loadImage("src/ui/speed_potion.png");
            hardShip = loadImage("src/ui/hard_ship.png");
            luckShip = loadImage("src/ui/lucky_ship.png");
            basicShip = loadImage("src/ui/ship.gif");
            tutorialWindow = loadImage("src/ui/tutorial_window.png");
            dialogWindow = loadImage("src/ui/dialog_window_trans.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage loadImage(String path) throws IOException {
        InputStream is = new BufferedInputStream(Files.newInputStream(Paths.get(path)));
        return ImageIO.read(is);
    }

}

