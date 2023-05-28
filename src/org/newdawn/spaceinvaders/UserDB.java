package org.newdawn.spaceinvaders;

import java.sql.*;

public final class UserDB {

    //DB 커넥션용
    public static Connection conn;

    //DB 저장용 데이터 변수
    private String userID;
    private String nickname = "guest"; //임시 초기닉네임
    //각 스테이지 당 최고 스코어 기록
    private int highScore = 0;
    //상점관련, 보유 코인 수
    private int coin = 0;
    //우주선 보유 여부
    private boolean hardShip = false;
    private boolean luckyShip = false;
    //포션 보유 개수
    private int healPotion = 0;
    private int speedPotion = 0;
    //현재 장착한 우주선
    private int selectedShip = 0;
    //튜토리얼 화면 띄울지 여부
    private boolean firstPlay = true;
    //DB 저장용은 아님
    private boolean loggedIn = false;

    private GameLobbyPanel glp;

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

    public void initializeDB(){
        setUserID(null);
        setNickname("guest"); //임시 초기닉네임
        setHighScore(0);
        setCoin(0);
        setHardShip(false);
        setLuckyShip(false);
        setHealPotion(0);
        setSpeedPotion(0);
        setSelectedShip(0);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public void incCoin(int coin){
        this.coin += coin;
    }

    public boolean isHardShip() {
        return hardShip;
    }

    public void setHardShip(boolean hardShip) {
        this.hardShip = hardShip;
    }

    public boolean isLuckyShip() {
        return luckyShip;
    }

    public void setLuckyShip(boolean is_lucky_ship) {
        this.luckyShip = is_lucky_ship;
    }

    public int getHealPotion() {
        return healPotion;
    }

    public void setHealPotion(int healPotion) {
        this.healPotion = healPotion;
    }

    public void incHealPotion(int healPotion) {
        this.healPotion += healPotion;
    }

    public int getSpeedPotion() {
        return speedPotion;
    }

    public void setSpeedPotion(int speedPotion) {
        this.speedPotion = speedPotion;
    }

    public void incSpeedPotion(int speedPotion) {
        this.speedPotion += speedPotion;
    }

    public int getSelectedShip() {
        return selectedShip;
    }

    public void setSelectedShip(int selectedShip) {
        this.selectedShip = selectedShip;
    }

    public boolean isFirstPlay() {
        return firstPlay;
    }

    public void setFirstPlay(boolean firstPlay) {
        this.firstPlay = firstPlay;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
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
                    setUserID(glp.key.idString);
                    setNickname(rs2.getString("nickname"));
                    setHighScore(rs2.getInt("best_score"));
                    setCoin(rs2.getInt("coin"));
                    setHardShip(rs2.getBoolean("is_hard_ship"));
                    setLuckyShip(rs2.getBoolean("is_lucky_ship"));
                    setHealPotion(rs2.getInt("HP_potion"));
                    setSpeedPotion(rs2.getInt("speed_potion"));
                    setSelectedShip(rs2.getInt("selected_ship"));
                    setFirstPlay(rs2.getBoolean("is_first_play"));
                    setLoggedIn(true);
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

            pstmt.setString(1, getNickname());
            pstmt.setInt(2, getHighScore());
            pstmt.setInt(3, getCoin());
            pstmt.setBoolean(4, isHardShip());
            pstmt.setBoolean(5, isLuckyShip());
            pstmt.setInt(6, getHealPotion());
            pstmt.setInt(7, getSpeedPotion());
            pstmt.setInt(8, getSelectedShip());
            pstmt.setBoolean(9, isFirstPlay());
            pstmt.setString(10, getUserID());

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
                    pstmt.setString(2, getNickname());

                    int result = pstmt.executeUpdate();

                    if (result > 0) {
                        glp.mu.registerSuccessState = true;
                        setNickname(glp.key.nicString);
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