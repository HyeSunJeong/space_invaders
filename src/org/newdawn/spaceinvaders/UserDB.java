package org.newdawn.spaceinvaders;

import java.sql.*;

public final class UserDB {

    public static Connection conn;

    //DB 저장용 데이터 변수
    public static String userID;
    public static String nickname = "guest"; //임시 초기닉네임
    //각 스테이지 당 최고 스코어 기록
    public static int best_score = 0;
    //상점관련, 보유 코인 수
    public static int coin = 0;
    //우주선 보유 여부
    public static boolean is_hard_ship = false;
    public static boolean is_lucky_ship = false;
    //포션 보유 개수
    public static int HP_potion = 0;
    public static int speed_potion = 0;
    public static int selected_ship = 0;
    public static boolean is_first_play = true;
    //DB 저장용은 아님
    public static boolean is_logged_in = false;

    static {
        String url = "jdbc:mysql://localhost:3306/space-invaders?allowMultiQueries=true";
        String user = "user1";
        String password = "12345";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return conn;
    }
    public static void loggedIn(){is_logged_in = true;}
    public static void loggedOut(){is_logged_in = false;}
    public static void initializeDB(){
        userID = null;
        nickname = "guest"; //임시 초기닉네임
        best_score = 0;
        coin = 0;
        is_hard_ship = false;
        is_lucky_ship = false;
        HP_potion = 0;
        speed_potion = 0;
        selected_ship = 0;
    }

    private UserDB() {

    }
}