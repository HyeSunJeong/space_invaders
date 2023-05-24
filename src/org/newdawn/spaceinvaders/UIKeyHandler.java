package org.newdawn.spaceinvaders;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UIKeyHandler extends KeyAdapter {
    GameLobbyPanel glp;
    String idString = "";
    String pwString = "";
    String recPwString = "";
    String nicString = "";

    public UIKeyHandler(GameLobbyPanel glp) {
        this.glp = glp;
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        //타이틀 화면 조작
        if (glp.gameState == glp.titleState && !glp.mu.dialogState) {
            glp.sp.playSE(1,0);
            if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
                if (glp.mu.commandNum == -2) {
                    glp.mu.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_RIGHT) {
                if (glp.mu.commandNum != 4 && glp.mu.commandNum != 5) {
                    glp.mu.commandNum++;
                    if (glp.mu.commandNum > 3) {
                        glp.mu.commandNum = 3;
                    }
                }
            }

            if (code == KeyEvent.VK_LEFT) {
                if (glp.mu.commandNum != 4 && glp.mu.commandNum != 5) {
                    glp.mu.commandNum--;
                    if (glp.mu.commandNum < 0) {
                        glp.mu.commandNum = 0;
                    }
                }

            }

            if (code == KeyEvent.VK_UP) {
                if (glp.mu.commandNum == 3) {
                    glp.mu.commandNum = 4;
                }
                if (glp.mu.commandNum == 1 || glp.mu.commandNum == 2) {
                    if(UserDB.is_logged_in) {
                        glp.mu.commandNum = 5;
                    }
                }
            }

            if (code == KeyEvent.VK_DOWN) {
                if (glp.mu.commandNum == 4) {
                    glp.mu.commandNum = 3;
                }
                if (glp.mu.commandNum == 5) {
                    glp.mu.commandNum = 1;
                }
            }

            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {

                if (glp.mu.commandNum == 0) {
                    if(UserDB.is_first_play) {
                        glp.gameState = glp.tutorialState;
                        glp.mu.commandNum = -1;
                    }
                    else startGame();
                }

                if (glp.mu.commandNum == 1) {
                    glp.mu.commandNum = -2;
                    glp.gameState = glp.shopState;
                }

                if (glp.mu.commandNum == 2) {
                    glp.mu.commandNum = -2;
                    glp.gameState = glp.userState;
                }

                if (glp.mu.commandNum == 3) {
                    if (UserDB.is_logged_in) {
                        saveGame();
                        glp.mu.commandNum = -1;
                        glp.mu.dialogState = true;
                        glp.mu.exitConfirmState = true;
                    } else {
                        System.exit(0);
                    }
                }

                if (glp.mu.commandNum == 4) {
                    if(UserDB.is_logged_in) {
                        saveGame();
                        glp.mu.commandNum = -1;
                        glp.mu.dialogState = true;
                        glp.mu.signOutConfirmState = true;
                    }
                    else {
                        glp.gameState = glp.initialState;
                        glp.mu.commandNum = -1;
                    }
                }

                if (glp.mu.commandNum == 5) {
                    glp.mu.commandNum = -1;
                    glp.gameState = glp.changeNickState;
                }
            }
        }

        //상점 조작
        if (glp.gameState == glp.shopState) {
            glp.mu.coinLackState = false;
            glp.mu.possState = false;
            glp.mu.purchaseState = false;

            if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
                glp.sp.playSE(1,0);
                if (glp.mu.commandNum == -2) {
                    glp.mu.commandNum = 0;
                }
            }

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

                if (glp.mu.commandNum == 0) {
                    if (UserDB.coin >= MainUI.hpcoin) {
                        UserDB.HP_potion++;
                        UserDB.coin = UserDB.coin - MainUI.hpcoin;
                        glp.mu.purchaseState = true;
                        glp.sp.playSE(4,0);
                    } else {
                        glp.mu.coinLackState = true;
                        glp.sp.playSE(5,0);
                    }
                }

                if (glp.mu.commandNum == 1) {
                    if(UserDB.coin >= MainUI.spcoin) {
                        UserDB.speed_potion++;
                        UserDB.coin = UserDB.coin - MainUI.spcoin;
                        glp.mu.purchaseState = true;
                        glp.sp.playSE(4,0);
                    }
                    else {
                        glp.mu.coinLackState = true;
                        glp.sp.playSE(5,0);
                    }
                }

                if (glp.mu.commandNum == 2){
                    if(!UserDB.is_hard_ship && UserDB.coin >= MainUI.hscoin) {
                        UserDB.is_hard_ship = true;
                        UserDB.coin = UserDB.coin - MainUI.hscoin;
                        glp.mu.purchaseState = true;
                        glp.sp.playSE(4,0);
                    }

                    else if (!UserDB.is_hard_ship) {
                        glp.mu.coinLackState = true;
                        glp.sp.playSE(5,0);
                    }

                    else {
                        glp.mu.possState = true;
                        glp.sp.playSE(5,0);
                    }

                }

                if (glp.mu.commandNum == 3) {
                    if(!UserDB.is_lucky_ship && UserDB.coin >= MainUI.lscoin) {
                        UserDB.is_lucky_ship = true;
                        UserDB.coin = UserDB.coin - MainUI.lscoin;
                        glp.mu.purchaseState = true;
                        glp.sp.playSE(4,0);
                    }

                    else if (!UserDB.is_lucky_ship) {
                        glp.mu.coinLackState = true;
                        glp.sp.playSE(5,0);
                    }

                    else {
                        glp.mu.possState = true;
                        glp.sp.playSE(5,0);
                    }

                }

                if (glp.mu.commandNum == 4) {
                    glp.mu.commandNum = 1;
                    glp.gameState = glp.titleState;
                    glp.sp.playSE(1,0);
                }
            }
        }

        //유저 상태창 조작
        if(glp.gameState == glp.userState){
            glp.mu.equipState = false;
            if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
                glp.sp.playSE(1,0);
                if (glp.mu.commandNum == -2) {
                    glp.mu.commandNum = 0;
                }
            }

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

                if (glp.mu.commandNum == 0) {
                    glp.mu.equipState = true;
                    UserDB.selected_ship = 0;
                    glp.sp.playSE(3,0);
                }

                if (glp.mu.commandNum == 1) {
                    glp.mu.equipState = true;
                    UserDB.selected_ship = 1;
                    glp.sp.playSE(3,0);
                }

                if (glp.mu.commandNum == 2) {
                    glp.mu.equipState = true;
                    UserDB.selected_ship = 2;
                    glp.sp.playSE(3,0);
                }

                if (glp.mu.commandNum == 3) {
                    glp.mu.commandNum = 2;
                    glp.gameState = glp.titleState;
                    glp.sp.playSE(1,0);
                }
            }
        }

        if(glp.gameState == glp.initialState){
            glp.sp.playSE(1,0);
            if (code == KeyEvent.VK_DOWN) {
                glp.mu.commandNum++;
                if (glp.mu.commandNum > 3) {
                    glp.mu.commandNum = 3;
                }
            }

            if (code == KeyEvent.VK_UP) {
                glp.mu.commandNum--;
                if (glp.mu.commandNum < 0) {
                    glp.mu.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {

                if (glp.mu.commandNum == 0) {
                    glp.gameState = glp.signInState;
                    glp.mu.commandNum = -1;
                }

                if (glp.mu.commandNum == 1) {
                    glp.mu.commandNum = -1;
                    glp.gameState = glp.signUpState;
                }

                if (glp.mu.commandNum == 2) {
                    glp.gameState = glp.titleState;
                    glp.mu.commandNum = -2;
                }

                if (glp.mu.commandNum == 3) {
                    System.exit(0);
                }
            }
        }

        if (glp.gameState == glp.signInState) {
            glp.sp.playSE(1,0);
            glp.mu.unableLoginState = false;
            if (code == KeyEvent.VK_DOWN) {
                glp.mu.commandNum++;
                if (glp.mu.commandNum > 3) {
                    glp.mu.commandNum = 3;
                }
            }

            if (code == KeyEvent.VK_UP) {
                glp.mu.commandNum--;
                if (glp.mu.commandNum < 0) {
                    glp.mu.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_ENTER) {

                if (glp.mu.commandNum == 1) {
                    loginDB();
                    if (UserDB.is_logged_in) {
                        glp.gameState = glp.titleState;
                        glp.mu.commandNum = -2;
                        idString = "";
                        pwString = "";
                    }
                }
            }

            if(code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE){
                if (glp.mu.commandNum == 2) {
                    loginDB();
                    if (UserDB.is_logged_in) {
                        glp.gameState = glp.titleState;
                        glp.mu.commandNum = -2;
                        idString = "";
                        pwString = "";
                    }
                }

                if (glp.mu.commandNum == 3) {
                    idString = "";
                    pwString = "";
                    glp.gameState = glp.initialState;
                    glp.mu.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_TAB) {
                if(glp.mu.commandNum == 0){
                    glp.mu.commandNum++;
                }
            }
        }

        if(glp.gameState == glp.signUpState){
            glp.mu.outOfLengthState = false;
            glp.mu.idpwEqualState = false;
            glp.mu.pwConfirmErrorState = false;
            glp.mu.registerSuccessState = false;
            glp.mu.inputExistState = false;
            glp.sp.playSE(1,0);

            if (code == KeyEvent.VK_DOWN) {
                glp.mu.commandNum++;
                if (glp.mu.commandNum > 5) {
                    glp.mu.commandNum = 5;
                }
            }

            if (code == KeyEvent.VK_UP) {
                glp.mu.commandNum--;
                if (glp.mu.commandNum < 0) {
                    glp.mu.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_ENTER) {

                if (glp.mu.commandNum == 3) {
                    registerDB();
                }
            }

            if(code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE){
                if (glp.mu.commandNum == 4) {
                    registerDB();
                }

                if (glp.mu.commandNum == 5) {
                    idString = "";
                    pwString = "";
                    recPwString = "";
                    nicString = "";
                    glp.gameState = glp.initialState;
                    glp.mu.commandNum = 0;
                }
            }

            /*if (code == KeyEvent.VK_TAB) {
                if(glp.mu.commandNum == 0){
                    glp.mu.commandNum++;
                }
            }*/
        }

        if(glp.gameState == glp.tutorialState){
            glp.sp.playSE(1,0);
            if (code == KeyEvent.VK_DOWN) {
                glp.mu.commandNum++;
                if (glp.mu.commandNum > 1) {
                    glp.mu.commandNum = 1;
                }
            }

            if (code == KeyEvent.VK_UP) {
                glp.mu.commandNum--;
                if (glp.mu.commandNum < 0) {
                    glp.mu.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {

                if (glp.mu.commandNum == 0) {
                    startGame();
                }

                if (glp.mu.commandNum == 1) {
                    glp.gameState = glp.titleState;
                    glp.mu.commandNum = -2;
                }
            }
        }

        if(glp.gameState == glp.changeNickState){
            glp.sp.playSE(1,0);
            glp.mu.outOfLengthState = false;
            glp.mu.registerSuccessState = false;
            glp.mu.inputExistState = false;

            if (code == KeyEvent.VK_DOWN) {
                glp.mu.commandNum++;
                if (glp.mu.commandNum > 2) {
                    glp.mu.commandNum = 2;
                }
            }

            if (code == KeyEvent.VK_UP) {
                glp.mu.commandNum--;
                if (glp.mu.commandNum < 0) {
                    glp.mu.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_ENTER || (code == KeyEvent.VK_SPACE)) {

                if (glp.mu.commandNum == 1) {
                    changeNickname();
                }

                if (glp.mu.commandNum == 2) {
                    nicString = "";
                    glp.gameState = glp.titleState;
                    glp.mu.commandNum = -2;
                }
            }
        }

        if(glp.mu.dialogState){
            glp.sp.playSE(1,0);
            if (code == KeyEvent.VK_RIGHT) {
                glp.mu.commandNum++;
                if (glp.mu.commandNum > 1) {
                    glp.mu.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_LEFT) {
                glp.mu.commandNum--;
                if (glp.mu.commandNum < 0) {
                    glp.mu.commandNum = 1;
                }
            }

            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                if (glp.mu.exitConfirmState) {
                    if(glp.mu.commandNum == 0) {
                        System.exit(0);
                        glp.mu.dialogState = false;
                        glp.mu.exitConfirmState = false;
                    }
                    if(glp.mu.commandNum == 1) {
                        glp.mu.dialogState = false;
                        glp.mu.exitConfirmState = false;
                    }
                }

                if (glp.mu.signOutConfirmState) {
                    if(glp.mu.commandNum == 0){
                        UserDB.loggedOut();
                        UserDB.initializeDB();
                        glp.gameState = glp.initialState;
                        glp.mu.commandNum = -1;
                        glp.mu.dialogState = false;
                        glp.mu.signOutConfirmState = false;
                    }
                    if(glp.mu.commandNum == 1){
                        glp.mu.dialogState = false;
                        glp.mu.signOutConfirmState = false;
                    }
                }
            }
        }
    }

    public void keyTyped(KeyEvent e){
        char inputChar = e.getKeyChar();

        //로그인 창 타이핑 조작
        if(glp.gameState == glp.signInState){
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

        //회원가입 창 타이핑 조작
        if(glp.gameState == glp.signUpState){
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
                if(glp.mu.commandNum == 0 && idString.length() != 0) idString = idString.substring(0,idString.length()-1);
                if(glp.mu.commandNum == 1 && pwString.length() != 0) pwString = pwString.substring(0,pwString.length()-1);
                if(glp.mu.commandNum == 2 && recPwString.length() != 0) recPwString = recPwString.substring(0,recPwString.length()-1);
                if(glp.mu.commandNum == 3 && nicString.length() != 0) nicString = nicString.substring(0,nicString.length()-1);
            }
        }
        
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

    public void loginDB() {
        Connection conn = UserDB.getConnection();
        //로그인 시도
        try {
            String query = "SELECT id,password FROM userdata WHERE id = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, idString);
            pstmt.setString(2, pwString);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dataLoad = "SELECT * FROM userdata WHERE id = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(dataLoad);

                pstmt2.setString(1, idString);

                ResultSet rs2 = pstmt2.executeQuery();
                //플레이어 데이터 로드 - 데이터베이스 테이블에서 데이터 로드, UserDB의 static 변수에 저장
                while (rs2.next()) {
                    UserDB.userID = idString;
                    UserDB.nickname = rs2.getString("nickname");
                    UserDB.best_score = rs2.getInt("best_score");
                    UserDB.coin = rs2.getInt("coin");
                    UserDB.is_hard_ship = rs2.getBoolean("is_hard_ship");
                    UserDB.is_lucky_ship = rs2.getBoolean("is_lucky_ship");
                    UserDB.HP_potion = rs2.getInt("HP_potion");
                    UserDB.speed_potion = rs2.getInt("speed_potion");
                    UserDB.selected_ship = rs2.getInt("selected_ship");
                    UserDB.is_first_play = rs2.getBoolean("is_first_play");
                    UserDB.loggedIn();
                }
            } else {
                glp.mu.unableLoginState = true;
                glp.sp.playSE(5,0);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void registerDB(){
        Connection conn = UserDB.getConnection();
        //중복체크용 문자열
        String dupid = "";

        //부적절한 입력정보 감지
        if (nicString.length() < 5 || nicString.length() > 10 || idString.length() < 8  || idString.length() > 12 || pwString.length() < 8 || pwString.length() > 12) {
            glp.mu.outOfLengthState = true;
            glp.sp.playSE(5,0);
        }

        else if (idString.equals(pwString)) {
            glp.mu.idpwEqualState = true;
            glp.sp.playSE(5,0);
        }

        else if (!pwString.equals(recPwString)) {
            glp.mu.pwConfirmErrorState = true;
            glp.sp.playSE(5,0);
        }

        //데이터베이스 접속, 중복된 ID, 패스워드, 닉네임 있는지 확인
        else {
            try {
                String dupCheck = "SELECT id,password,nickname FROM userdata WHERE id = ? OR password = ? OR nickname = ?";
                PreparedStatement dpstmt = conn.prepareStatement(dupCheck);

                dpstmt.setString(1, idString);
                dpstmt.setString(2, pwString);
                dpstmt.setString(3, nicString);

                ResultSet dprs = dpstmt.executeQuery();

                while (dprs.next()){
                    dupid = dprs.getString("id");
                }

                if (dupid.equals("")) {
                    try {
                        String query = "INSERT INTO userdata (id, password, nickname) VALUES (?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(query);

                        pstmt.setString(1, idString);
                        pstmt.setString(2, pwString);
                        pstmt.setString(3, nicString);

                        int result = pstmt.executeUpdate();

                        if (result > 0) {
                            glp.mu.registerSuccessState = true;
                            idString = "";
                            pwString = "";
                            recPwString = "";
                            nicString = "";
                        }
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    glp.mu.inputExistState = true;
                }
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public void saveGame() {
        Connection conn = UserDB.getConnection();
        try {
            String dataSave = "UPDATE userdata SET nickname = ?, best_score = ?, coin = ?, is_hard_ship = ?,is_lucky_ship = ?, HP_potion = ?, speed_potion = ? , selected_ship = ?, is_first_play = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(dataSave);

            pstmt.setString(1, UserDB.nickname);
            pstmt.setInt(2, UserDB.best_score);
            pstmt.setInt(3, UserDB.coin);
            pstmt.setBoolean(4, UserDB.is_hard_ship);
            pstmt.setBoolean(5, UserDB.is_lucky_ship);
            pstmt.setInt(6, UserDB.HP_potion);
            pstmt.setInt(7, UserDB.speed_potion);
            pstmt.setInt(8, UserDB.selected_ship);
            pstmt.setBoolean(9, UserDB.is_first_play);
            pstmt.setString(10, UserDB.userID);

            int updateResult = pstmt.executeUpdate();

            //종료 메세지 출력
            if (updateResult > 0) {
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

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
    
    public void changeNickname(){
        Connection conn = UserDB.getConnection();
        
        String dupnic = "";
        
        if (nicString.length() > 12 || nicString.length() < 8) {
            glp.mu.outOfLengthState = true;
            glp.sp.playSE(5,0);
            return;
        }

        try {
            String dupCheck = "SELECT nickname FROM userdata WHERE nickname = ?";
            PreparedStatement dpstmt = conn.prepareStatement(dupCheck);

            dpstmt.setString(1, nicString);

            ResultSet dprs = dpstmt.executeQuery();
            while (dprs.next()){
                dupnic = dprs.getString("nickname");
            }
            if(dupnic.equals("")){
                try {
                    String query = "UPDATE userdata SET nickname = ? WHERE nickname = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);

                    pstmt.setString(1, nicString);
                    pstmt.setString(2, UserDB.nickname);

                    int result = pstmt.executeUpdate();

                    if (result > 0) {
                        glp.mu.registerSuccessState = true;
                        UserDB.nickname = nicString;
                    }
                    pstmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                glp.mu.inputExistState = true;
                glp.sp.playSE(5,0);
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

    }

}
