package org.newdawn.spaceinvaders;

import java.sql.*;

public final class UserDB {

    //DB 커넥션용
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

    //현재 장착한 우주선
    public static int selected_ship = 0;

    //튜토리얼 화면 띄울지 여부
    public static boolean is_first_play = true;
    //DB 저장용은 아님
    public static boolean is_logged_in = false;

    GameLobbyPanel glp;

    //DB 연결
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

    //로그인
    public void loginDB() {
        Connection conn = UserDB.getConnection();
        //로그인 시도
        try {
            String query = "SELECT id,password FROM userdata WHERE id = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, glp.key.idString);
            pstmt.setString(2, glp.key.pwString);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dataLoad = "SELECT * FROM userdata WHERE id = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(dataLoad);

                pstmt2.setString(1, glp.key.idString);

                ResultSet rs2 = pstmt2.executeQuery();
                //플레이어 데이터 로드 - 데이터베이스 테이블에서 데이터 로드, UserDB의 static 변수에 저장
                while (rs2.next()) {
                    userID = glp.key.idString;
                    nickname = rs2.getString("nickname");
                    best_score = rs2.getInt("best_score");
                    coin = rs2.getInt("coin");
                    is_hard_ship = rs2.getBoolean("is_hard_ship");
                    is_lucky_ship = rs2.getBoolean("is_lucky_ship");
                    HP_potion = rs2.getInt("HP_potion");
                    speed_potion = rs2.getInt("speed_potion");
                    selected_ship = rs2.getInt("selected_ship");
                    is_first_play = rs2.getBoolean("is_first_play");
                    loggedIn();
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

    //회원가입
    public void registerDB(){
        Connection conn = UserDB.getConnection();
        //중복체크용 문자열
        String dupid = "";

        //부적절한 입력정보 감지
        if (glp.key.nicString.length() < 5 || glp.key.nicString.length() > 10 || glp.key.idString.length() < 8  || glp.key.idString.length() > 12 || glp.key.pwString.length() < 8 || glp.key.pwString.length() > 12) {
            glp.mu.outOfLengthState = true;
            glp.sp.playSE(5,0);
        }

        else if (glp.key.idString.equals(glp.key.pwString)) {
            glp.mu.idpwEqualState = true;
            glp.sp.playSE(5,0);
        }

        else if (!glp.key.pwString.equals(glp.key.recPwString)) {
            glp.mu.pwConfirmErrorState = true;
            glp.sp.playSE(5,0);
        }

        //데이터베이스 접속, 중복된 ID, 패스워드, 닉네임 있는지 확인
        else {
            try {
                String dupCheck = "SELECT id,password,nickname FROM userdata WHERE id = ? OR password = ? OR nickname = ?";
                PreparedStatement dpstmt = conn.prepareStatement(dupCheck);

                dpstmt.setString(1, glp.key.idString);
                dpstmt.setString(2, glp.key.pwString);
                dpstmt.setString(3, glp.key.nicString);

                ResultSet dprs = dpstmt.executeQuery();

                while (dprs.next()){
                    dupid = dprs.getString("id");
                }

                if (dupid.equals("")) {
                    try {
                        String query = "INSERT INTO userdata (id, password, nickname) VALUES (?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(query);

                        pstmt.setString(1, glp.key.idString);
                        pstmt.setString(2, glp.key.pwString);
                        pstmt.setString(3, glp.key.nicString);

                        int result = pstmt.executeUpdate();

                        if (result > 0) {
                            glp.mu.registerSuccessState = true;
                            glp.key.idString = "";
                            glp.key.pwString = "";
                            glp.key.recPwString = "";
                            glp.key.nicString = "";
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

    //게임 나가기 전 저장, 게스트모드는 X
    public void saveGame() {
        Connection conn = UserDB.getConnection();
        try {
            String dataSave = "UPDATE userdata SET nickname = ?, best_score = ?, coin = ?, is_hard_ship = ?,is_lucky_ship = ?, HP_potion = ?, speed_potion = ? , selected_ship = ?, is_first_play = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(dataSave);

            pstmt.setString(1, nickname);
            pstmt.setInt(2, best_score);
            pstmt.setInt(3, coin);
            pstmt.setBoolean(4, is_hard_ship);
            pstmt.setBoolean(5, is_lucky_ship);
            pstmt.setInt(6, HP_potion);
            pstmt.setInt(7, speed_potion);
            pstmt.setInt(8, selected_ship);
            pstmt.setBoolean(9, is_first_play);
            pstmt.setString(10, userID);

            int updateResult = pstmt.executeUpdate();

            //종료 메세지 출력
            if (updateResult > 0) {
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //닉네임 변경
    public void changeNickname(){
        Connection conn = UserDB.getConnection();

        String dupnic = "";

        if (glp.key.nicString.length() > 12 || glp.key.nicString.length() < 8) {
            glp.mu.outOfLengthState = true;
            glp.sp.playSE(5,0);
            return;
        }

        try {
            String dupCheck = "SELECT nickname FROM userdata WHERE nickname = ?";
            PreparedStatement dpstmt = conn.prepareStatement(dupCheck);

            dpstmt.setString(1, glp.key.nicString);

            ResultSet dprs = dpstmt.executeQuery();
            while (dprs.next()){
                dupnic = dprs.getString("nickname");
            }
            if(dupnic.equals("")){
                try {
                    String query = "UPDATE userdata SET nickname = ? WHERE nickname = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);

                    pstmt.setString(1, glp.key.nicString);
                    pstmt.setString(2, nickname);

                    int result = pstmt.executeUpdate();

                    if (result > 0) {
                        glp.mu.registerSuccessState = true;
                        nickname = glp.key.nicString;
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

    public UserDB(GameLobbyPanel glp) {
        this.glp = glp;
    }
}