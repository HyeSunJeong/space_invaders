package org.newdawn.spaceinvaders.gameui;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.GameLobbyPanel;
import org.newdawn.spaceinvaders.UserDB;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UIKeyHandler extends KeyAdapter {
    private GameLobbyPanel glp;
    public String idString = "";
    public String pwString = "";
    public String recPwString = "";
    public String nicString = "";

    public UIKeyHandler(GameLobbyPanel glp) {
        this.glp = glp;
    }

    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        //타이틀 화면 조작
        if (glp.gameState == glp.titleState && !glp.mu.dialogState) {
            glp.sp.playSE(1, 0);

            switch (code) {
                case KeyEvent.VK_RIGHT:
                    if (glp.mu.commandNum != 4 && glp.mu.commandNum != 5) {
                        glp.mu.commandNum++;
                        if (glp.mu.commandNum > 3) {
                            glp.mu.commandNum = 3;
                        }
                    }
                    break;

                case KeyEvent.VK_LEFT:
                    if (glp.mu.commandNum != 4 && glp.mu.commandNum != 5) {
                        glp.mu.commandNum--;
                        if (glp.mu.commandNum < 0) {
                            glp.mu.commandNum = 0;
                        }
                    }
                    break;

                case KeyEvent.VK_UP:
                    if (glp.mu.commandNum == 3) {
                        glp.mu.commandNum = 4;
                    } else if (glp.mu.commandNum == 1 || glp.mu.commandNum == 2) {
                        if (UserDB.is_logged_in) {
                            glp.mu.commandNum = 5;
                        }
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (glp.mu.commandNum == 4) {
                        glp.mu.commandNum = 3;
                    } else if (glp.mu.commandNum == 5) {
                        glp.mu.commandNum = 1;
                    }
                    break;

                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    switch (glp.mu.commandNum) {
                        case 0:
                            if (UserDB.is_first_play) {
                                glp.gameState = glp.tutorialState;
                                glp.mu.commandNum = -1;
                            } else {
                                startGame();
                            }
                            break;

                        case 1:
                            glp.mu.commandNum = -2;
                            glp.gameState = glp.shopState;
                            break;

                        case 2:
                            glp.mu.commandNum = -2;
                            glp.gameState = glp.userState;
                            break;

                        case 3:
                            if (UserDB.is_logged_in) {
                                glp.us.saveGame();
                                glp.mu.commandNum = -1;
                                glp.mu.dialogState = true;
                                glp.mu.exitConfirmState = true;
                            } else {
                                System.exit(0);
                            }
                            break;

                        case 4:
                            if (UserDB.is_logged_in) {
                                glp.us.saveGame();
                                glp.mu.commandNum = -1;
                                glp.mu.dialogState = true;
                                glp.mu.signOutConfirmState = true;
                            } else {
                                glp.gameState = glp.initialState;
                                glp.mu.commandNum = -1;
                            }
                            break;

                        case 5:
                            glp.mu.commandNum = -1;
                            glp.gameState = glp.changeNickState;
                            break;
                    }
                    break;
            }
        }

        //상점 조작
        if (glp.gameState == glp.shopState) {
            glp.mu.coinLackState = false;
            glp.mu.possState = false;
            glp.mu.purchaseState = false;

            initializeControl(code);

            if (code == KeyEvent.VK_RIGHT) {
                if (glp.mu.commandNum != 4) {
                    glp.mu.commandNum++;
                    if (glp.mu.commandNum > 3) {
                        glp.mu.commandNum = 3;
                    }
                }
            }

            if (code == KeyEvent.VK_LEFT) {
                if (glp.mu.commandNum != 4) {
                    glp.mu.commandNum--;
                    if (glp.mu.commandNum < 0) {
                        glp.mu.commandNum = 0;
                    }
                }
            }

            if (code == KeyEvent.VK_UP) {
                if (glp.mu.commandNum == 4) {
                    glp.mu.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_DOWN) {
                glp.mu.commandNum = 4;
            }

            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                    switch (glp.mu.commandNum) {
                        case 0:
                            if (UserDB.coin >= MainUI.healPotionPrice) {
                                UserDB.HP_potion++;
                                UserDB.coin = UserDB.coin - MainUI.healPotionPrice;
                                glp.mu.purchaseState = true;
                                glp.sp.playSE(4, 0);
                            } else {
                                glp.mu.coinLackState = true;
                                glp.sp.playSE(5, 0);
                            }
                            break;

                        case 1:
                            if (UserDB.coin >= MainUI.speedPotionPrice) {
                                UserDB.speed_potion++;
                                UserDB.coin = UserDB.coin - MainUI.speedPotionPrice;
                                glp.mu.purchaseState = true;
                                glp.sp.playSE(4, 0);
                            } else {
                                glp.mu.coinLackState = true;
                                glp.sp.playSE(5, 0);
                            }
                            break;

                        case 2:
                            if (!UserDB.is_hard_ship && UserDB.coin >= MainUI.hardShipPrice) {
                                UserDB.is_hard_ship = true;
                                UserDB.coin = UserDB.coin - MainUI.hardShipPrice;
                                glp.mu.purchaseState = true;
                                glp.sp.playSE(4, 0);
                            } else if (!UserDB.is_hard_ship) {
                                glp.mu.coinLackState = true;
                                glp.sp.playSE(5, 0);
                            } else {
                                glp.mu.possState = true;
                                glp.sp.playSE(5, 0);
                            }
                            break;

                        case 3:
                            if (!UserDB.is_lucky_ship && UserDB.coin >= MainUI.luckyShipPrice) {
                                UserDB.is_lucky_ship = true;
                                UserDB.coin = UserDB.coin - MainUI.luckyShipPrice;
                                glp.mu.purchaseState = true;
                                glp.sp.playSE(4, 0);
                            } else if (!UserDB.is_lucky_ship) {
                                glp.mu.coinLackState = true;
                                glp.sp.playSE(5, 0);
                            } else {
                                glp.mu.possState = true;
                                glp.sp.playSE(5, 0);
                            }
                            break;

                        case 4:
                            glp.mu.commandNum = 1;
                            glp.gameState = glp.titleState;
                            glp.sp.playSE(1, 0);
                            break;
                    }
            }
        }

        //유저 상태창 조작
        if(glp.gameState == glp.userState){
            glp.mu.equipState = false;
            initializeControl(code);

            if (code == KeyEvent.VK_RIGHT) {
                if (glp.mu.commandNum != 3) {
                    if (UserDB.is_hard_ship && UserDB.is_lucky_ship) {
                        glp.mu.commandNum++;
                        if (glp.mu.commandNum > 2) {
                            glp.mu.commandNum = 2;
                        }
                    }
                    if(UserDB.is_hard_ship && !UserDB.is_lucky_ship){
                        glp.mu.commandNum++;
                        if (glp.mu.commandNum > 1) {
                            glp.mu.commandNum = 1;
                        }
                    }
                    if(!UserDB.is_hard_ship && UserDB.is_lucky_ship){
                        glp.mu.commandNum = 2;
                    }
                    else return;
                }
            }

            if (code == KeyEvent.VK_LEFT) {
                if (glp.mu.commandNum != 3) {
                    if (UserDB.is_hard_ship) {
                        glp.mu.commandNum--;
                        if (glp.mu.commandNum < 0) {
                            glp.mu.commandNum = 0;
                        }
                    }
                    if (!UserDB.is_hard_ship && UserDB.is_lucky_ship) {
                        glp.mu.commandNum = 0;
                    }
                    else return;
                }
            }

            if (code == KeyEvent.VK_UP) {
                if (glp.mu.commandNum == 3) {
                    glp.mu.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_DOWN) {
                glp.mu.commandNum = 3;
            }

            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                switch (glp.mu.commandNum) {
                    case 0:
                        showEquipped(0);
                        break;

                    case 1:
                        showEquipped(1);
                        break;

                    case 2:
                        showEquipped(2);
                        break;

                    case 3:
                        glp.mu.commandNum = 2;
                        glp.gameState = glp.titleState;
                        glp.sp.playSE(1, 0);
                        break;
                }
            }

        }

        if (glp.gameState == glp.initialState) {
            glp.sp.playSE(1, 0);
            switch (code) {
                case KeyEvent.VK_DOWN:
                    glp.mu.commandNum++;
                    if (glp.mu.commandNum > 3) {
                        glp.mu.commandNum = 3;
                    }
                    break;

                case KeyEvent.VK_UP:
                    glp.mu.commandNum--;
                    if (glp.mu.commandNum < 0) {
                        glp.mu.commandNum = 0;
                    }
                    break;

                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    switch (glp.mu.commandNum) {
                        case 0:
                            glp.gameState = glp.signInState;
                            glp.mu.commandNum = -1;
                            break;

                        case 1:
                            glp.mu.commandNum = -1;
                            glp.gameState = glp.signUpState;
                            break;

                        case 2:
                            glp.gameState = glp.titleState;
                            glp.mu.commandNum = -2;
                            break;

                        case 3:
                            System.exit(0);
                            break;
                    }
                    break;
            }
        }

        if (glp.gameState == glp.signInState) {
            glp.sp.playSE(1, 0);
            glp.mu.unableLoginState = false;
            switch (code) {
                case KeyEvent.VK_DOWN:
                    glp.mu.commandNum++;
                    if (glp.mu.commandNum > 3) {
                        glp.mu.commandNum = 3;
                    }
                    break;

                case KeyEvent.VK_UP:
                    glp.mu.commandNum--;
                    if (glp.mu.commandNum < 0) {
                        glp.mu.commandNum = 0;
                    }
                    break;

                case KeyEvent.VK_ENTER:
                    if (glp.mu.commandNum == 1) {
                            glp.us.loginDB();
                            if (UserDB.is_logged_in) {
                                glp.gameState = glp.titleState;
                                glp.mu.commandNum = -2;
                                idString = "";
                                pwString = "";
                            }
                            break;
                    }
                    break;

                case KeyEvent.VK_SPACE:
                    switch (glp.mu.commandNum) {
                        case 2:
                            glp.us.loginDB();
                            if (UserDB.is_logged_in) {
                                glp.gameState = glp.titleState;
                                glp.mu.commandNum = -2;
                                idString = "";
                                pwString = "";
                            }
                            break;

                        case 3:
                            idString = "";
                            pwString = "";
                            glp.gameState = glp.initialState;
                            glp.mu.commandNum = 0;
                            break;
                    }
                    break;

                case KeyEvent.VK_TAB:
                    if (glp.mu.commandNum == 0) {
                        glp.mu.commandNum++;
                    }
                    break;
            }
        }

        if (glp.gameState == glp.signUpState) {
            glp.mu.outOfLengthState = false;
            glp.mu.idpwEqualState = false;
            glp.mu.pwConfirmErrorState = false;
            glp.mu.registerSuccessState = false;
            glp.mu.inputExistState = false;
            glp.sp.playSE(1, 0);

            switch (code) {
                case KeyEvent.VK_DOWN:
                    glp.mu.commandNum++;
                    if (glp.mu.commandNum > 5) {
                        glp.mu.commandNum = 5;
                    }
                    break;

                case KeyEvent.VK_UP:
                    glp.mu.commandNum--;
                    if (glp.mu.commandNum < 0) {
                        glp.mu.commandNum = 0;
                    }
                    break;

                case KeyEvent.VK_ENTER:
                    if (glp.mu.commandNum == 3) {
                            glp.us.registerDB();
                            break;
                    }
                    break;

                case KeyEvent.VK_SPACE:
                    switch (glp.mu.commandNum) {
                        case 4:
                            glp.us.registerDB();
                            break;

                        case 5:
                            idString = "";
                            pwString = "";
                            recPwString = "";
                            nicString = "";
                            glp.gameState = glp.initialState;
                            glp.mu.commandNum = 0;
                            break;
                    }
                    break;
            }
        }

        if (glp.gameState == glp.tutorialState) {
            glp.sp.playSE(1, 0);

            switch (code) {
                case KeyEvent.VK_DOWN:
                    glp.mu.commandNum++;
                    if (glp.mu.commandNum > 1) {
                        glp.mu.commandNum = 1;
                    }
                    break;

                case KeyEvent.VK_UP:
                    glp.mu.commandNum--;
                    if (glp.mu.commandNum < 0) {
                        glp.mu.commandNum = 0;
                    }
                    break;

                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    switch (glp.mu.commandNum) {
                        case 0:
                            startGame();
                            break;

                        case 1:
                            glp.gameState = glp.titleState;
                            glp.mu.commandNum = -2;
                            break;
                    }
                    break;
            }
        }

        if (glp.gameState == glp.changeNickState) {
            glp.sp.playSE(1, 0);
            glp.mu.outOfLengthState = false;
            glp.mu.registerSuccessState = false;
            glp.mu.inputExistState = false;

            switch (code) {
                case KeyEvent.VK_DOWN:
                    glp.mu.commandNum++;
                    if (glp.mu.commandNum > 2) {
                        glp.mu.commandNum = 2;
                    }
                    break;

                case KeyEvent.VK_UP:
                    glp.mu.commandNum--;
                    if (glp.mu.commandNum < 0) {
                        glp.mu.commandNum = 0;
                    }
                    break;

                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    switch (glp.mu.commandNum) {
                        case 1:
                            glp.us.changeNickname();
                            break;

                        case 2:
                            nicString = "";
                            glp.gameState = glp.titleState;
                            glp.mu.commandNum = -2;
                            break;
                    }
                    break;
            }
        }

        if (glp.mu.dialogState) {
            glp.sp.playSE(1, 0);

            switch (code) {
                case KeyEvent.VK_RIGHT:
                    glp.mu.commandNum++;
                    if (glp.mu.commandNum > 1) {
                        glp.mu.commandNum = 0;
                    }
                    break;

                case KeyEvent.VK_LEFT:
                    glp.mu.commandNum--;
                    if (glp.mu.commandNum < 0) {
                        glp.mu.commandNum = 1;
                    }
                    break;

                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    if (glp.mu.exitConfirmState) {
                        switch (glp.mu.commandNum) {
                            case 0:
                                System.exit(0);
                                glp.mu.dialogState = false;
                                glp.mu.exitConfirmState = false;
                                break;

                            case 1:
                                glp.mu.dialogState = false;
                                glp.mu.exitConfirmState = false;
                                break;
                        }
                    }

                    if (glp.mu.signOutConfirmState) {
                        switch (glp.mu.commandNum) {
                            case 0:
                                UserDB.loggedOut();
                                UserDB.initializeDB();
                                glp.gameState = glp.initialState;
                                glp.mu.commandNum = -1;
                                glp.mu.dialogState = false;
                                glp.mu.signOutConfirmState = false;
                                break;

                            case 1:
                                glp.mu.dialogState = false;
                                glp.mu.signOutConfirmState = false;
                                break;
                        }
                    }
                    break;
            }
        }

    }

    public void keyTyped(KeyEvent e){
        char inputChar = e.getKeyChar();

        //로그인 창 타이핑 조작
        if(glp.gameState == glp.signInState) controlSignIn(inputChar);

        //회원가입 창 타이핑 조작
        if(glp.gameState == glp.signUpState) controlSignUp(inputChar);
        
        //닉네임 변경 타이핑 조작
        if(glp.gameState == glp.changeNickState){
            if(glp.mu.commandNum == 0 && inputChar != KeyEvent.VK_BACK_SPACE && inputChar != KeyEvent.VK_ENTER){
                if(nicString.length() < 12) {
                    nicString += inputChar;
                }
            }

            if(inputChar == KeyEvent.VK_BACK_SPACE){
                if(glp.mu.commandNum == 0 && nicString.length() != 0) nicString = nicString.substring(0,nicString.length()-1);
            }
        }
    }

    //스테이지 시작
    public void startGame(){
        glp.sp.stopMusic(2);
        glp.gameState = glp.inGameState;
        UserDB.is_first_play = false;
        glp.frameLocation = glp.getLocationOnScreen();
        Thread gameThread = new Thread(new Runnable() {
            public void run() {
                Game g = new Game(glp);
                g.gameLoop();
            }
        });
        gameThread.start();
    }

    //현재 장착하고 있는 우주선 위에 E 표시
    public void showEquipped(int selectedShip){
        glp.mu.equipState = true;
        UserDB.selected_ship = selectedShip;
        glp.sp.playSE(3,0);
    }

    public void initializeControl(int code) {
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
            glp.sp.playSE(1,0);
            if (glp.mu.commandNum == -2) {
                glp.mu.commandNum = 0;
            }
        }
    }

    public void controlSignIn(char inputChar){
        if(glp.mu.commandNum == 0 && inputChar != KeyEvent.VK_BACK_SPACE && inputChar != KeyEvent.VK_ENTER){
            if(idString.length() < 12) {
                idString += inputChar;
            }
        }

        if(glp.mu.commandNum == 1 && inputChar != KeyEvent.VK_BACK_SPACE && inputChar != KeyEvent.VK_ENTER){
            if(pwString.length() < 12) {
                pwString += inputChar;
            }
        }

        if(inputChar == KeyEvent.VK_BACK_SPACE){
            if(glp.mu.commandNum == 0 && idString.length() != 0) idString = idString.substring(0,idString.length()-1);
            if(glp.mu.commandNum == 1 && pwString.length() != 0) pwString = pwString.substring(0,pwString.length()-1);
        }
    }

    public void controlSignUp(char inputChar){
        controlSignIn(inputChar);
            if(glp.mu.commandNum == 2 && inputChar != KeyEvent.VK_BACK_SPACE && inputChar != KeyEvent.VK_ENTER){
                if(recPwString.length() < 12) {
                    recPwString += inputChar;
                }
            }

            if(glp.mu.commandNum == 3 && inputChar != KeyEvent.VK_BACK_SPACE && inputChar != KeyEvent.VK_ENTER){
                if(nicString.length() < 12) {
                    nicString += inputChar;
                }
            }

            if(inputChar == KeyEvent.VK_BACK_SPACE){
                if(glp.mu.commandNum == 2 && recPwString.length() != 0) recPwString = recPwString.substring(0,recPwString.length()-1);
                if(glp.mu.commandNum == 3 && nicString.length() != 0) nicString = nicString.substring(0,nicString.length()-1);
            }
    }
}
