package org.newdawn.spaceinvaders;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.resource.SoundPlayer;

/**
 * The main hook of our game. This class with both act as a manager
 * for the display and central mediator for the game logic. 
 *
 * Display management will consist of a loop that cycles round all
 * entities in the game asking them to move and then drawing them
 * in the appropriate place. With the help of an inner class it
 * will also allow the player to control the main ship.
 *
 * As a mediator it will be informed when entities within our game
 * detect events (e.g. alient killed, played died) and will take
 * appropriate game actions.
 *
 * @author Kevin Glass
 */
public class Game extends Canvas
{
	private long coolTime = 3000;
	int timer;
	int timeCheck;
	int HPcooldownCheck;
	int SPcooldownCheck;

	private int score; /** 게임 스코어 **/

	/** The stragey that allows us to use accelerate page flipping */
	private BufferStrategy strategy;
	/** True if the game is currently "running", i.e. the game loop is looping */
	private boolean gameRunning = true;
	/** The list of all the entities that exist in our game */
	private ArrayList entities = new ArrayList();
	/** The list of entities that need to be removed from the game this loop */
	private ArrayList removeList = new ArrayList();
	/** The entity representing the player */
	private Entity ship;
	/** The speed at which the player's ship should move (pixels/sec) */
	private Entity boss; //보스 생성
	private Entity coidUI;
	private Entity coinPrefab;
	private  Entity[] bossHpUi = new Entity[150];
	private Entity[] playerHpUI = new Entity[10];
	private Entity obstacle;
	private Entity bossHpBar;
	private Entity alien;
	private Entity[] itemUi = new Entity[4];
	private Entity stageUI ;
	private double moveSpeed = 300;
	/** The time at which last fired a shot */
	private long lastFire = 0;
	private long lastUseSpeedPotion= 0;

	private long lastUseHealPotion=0;
	private long lastUsedShipImmortal = 0;
	/** The interval between our players shot (ms) */
	private long firingInterval = 200;
	/** The number of aliens left on the screen */
	private int alienCount;

	/** The message to display which waiting for a key press */
	private String message = "";
	/** True if we're holding up game play until a key has been pressed */
	private boolean waitingForKeyPress = true;
	/** True if the left cursor key is currently pressed */
	private boolean leftPressed = false;
	/** True if the right cursor key is currently pressed */
	private boolean rightPressed = false;
	/** True if we are firing */
	private boolean firePressed = false;
	/** True if game logic needs to be applied this loop, normally as a result of a game event */
	private boolean logicRequiredThisLoop = false;
	/** The last time at which we recorded the frame rate */
	private boolean escPressed = false;
	private long lastFpsTime;
	/** The current number of frames recorded */
	private int fps;
	/** The normal title of the game window */
	private String windowTitle = "Space Invaders 102";
	/** The game window that we'll update with the frame count */
	private JFrame container;
	//private LoginFrame lf;
	public GameLobbyPanel glp;
	public SoundPlayer sp = new SoundPlayer();
	long startTime = System.currentTimeMillis();
	private Boolean bossAlive = false;
	public int stage=1;

	private boolean isStageUiOn = false;

	private boolean usedHealPotion = false;
	private boolean usedSpeedPotion = false;
	private boolean reflectDamaged = false;
	private int HPCooldown = 3;
	private int SPCooldown = 3;
	private double reflectCooldown = 0.5;
	private BufferedImage round1,round2,round3,round4,round5;

	double addRound = 2;


	/**
	 * Construct our game and set it running.
	 */
	public Game(GameLobbyPanel glp) {
		this.glp = glp;
		container = new JFrame("Space Invaders 102");

		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800,600));
		panel.setLayout(null);

		setBounds(0,0,800,600);
		panel.add(this);

		//setIgnoreRepaint(true);

		// finally make the window visible
		container.pack();
		container.setLocation(glp.frameLocation);
		container.setResizable(false);
		container.setVisible(true);

		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		addKeyListener(new KeyInputHandler());

		requestFocus();

		createBufferStrategy(2);
		strategy = getBufferStrategy();

		loadBackImg();
		initEntities();
	}

	/**
	 * Start a fresh game, this should clear out any old data and
	 * create a new set.
	 */
	private void startGame() {
		// clear out any existing entities and intialise a new set
		entities.clear();
		initEntities();
		// blank out any keyboard settings we might currently have
		leftPressed = false;
		rightPressed = false;
		firePressed = false;
		escPressed= false;
		//sp.playMusic(0,0);

	}
	private void initEntities() {
		addShip();
		addAlien();
		addPlayerHpimg(ship.getHp());
		addPlayerCoinimg();
		addPlayerPotionimg();
		showStageimg();
		//addBoss(100);
	}

	public void showStageimg(){
		isStageUiOn = true;
		addRound = System.currentTimeMillis();
		addStageimg();
	}
	private void addStageimg() {
		switch (stage){
			case 1:
				stageUI = new GameUi(this,"sprites/window/game_window_round_1.png",0,0);
				entities.add(stageUI);
				break;
			case 2:
				stageUI = new GameUi(this,"sprites/window/game_window_round_2.png",0,0);
				entities.add(stageUI);
				break;
			case 3:
				stageUI = new GameUi(this,"sprites/window/game_window_round_3.png",0,0);
				entities.add(stageUI);
				break;
			case 4:
				stageUI = new GameUi(this,"sprites/window/game_window_round_4.png",0,0);
				entities.add(stageUI);
				break;
			case 5:
				stageUI = new GameUi(this,"sprites/window/game_window_round_5.png",0,0);
				entities.add(stageUI);
				break;
		}
	}

	/**플레이어 생성**/
	public void addShip(){
		if(glp.us.getSelectedShip()==0){
			ship = new ShipEntity(this,"sprites/ship.gif",370,550);
		} else if (glp.us.getSelectedShip() ==1) {
			ship = new ShipEntity(this,"sprites/mini_hard_ship.png",370,550);
			ship.setHp(2);
		}
		else {
			ship = new ShipEntity(this,"sprites/mini_lucky_ship.png",370,550);

		}
		entities.add(ship);
	}
	/**플레이어 HP 이미지**/
	public void addPlayerHpimg(int playerHp){
		for(int i=0; i<playerHp; i++){
			playerHpUI[i] = new GameUi(this,"sprites/heart.png",750-(35*i),15);
			entities.add(playerHpUI[i]);
		}
	}
	/**아이콘 생성**/
	public void addPlayerPotionimg(){
		itemUi[0] = new GameUi(this,"sprites/heal_potion.png",20,550);
		entities.add(itemUi[0]);
		itemUi[2] = new GameUi(this,"sprites/speed_potion.png",50,550);
		entities.add(itemUi[2]);
	}

	/**기본 적 생성 **/
	public void addAlien(){
		alienCount = 0;
		for (int row=0;row<5;row++) {
			for (int x=0;x<12;x++) {
				alien = new AlienEntity(this,100+(x*50),(50)+row*30);
				entities.add(alien);
				alienCount++;
			}
		}
	}

	/**보스 생성**/
	public  void addBoss(int hp){
		boss = new BossEntity(this,350,130);
		entities.add(boss);
		bossAlive = true;
		boss.setHp(hp);
		sp.playSE(6,0);
	}

	/**보스 HP바 img생성**/
	public void addBossHpBar(int bossHp){
		bossHpBar = new GameUi(this,"sprites/gage_bar.png",225,80);
		entities.add(bossHpBar);
		for(int i=0; i<bossHp; i++){
			bossHpUi[i] = new GameUi(this,"sprites/BossHp3.png",227+(i*3),83);
			entities.add(bossHpUi[i]);
		}
	}

	/** 플레이어 보유 코인img 생성 **/
	public void addPlayerCoinimg(){
		coidUI = new GameUi(this,"sprites/coin.png",675,50);
		entities.add(coidUI);
	}

	/** 코인 생성**/
	public void spawnCoin(int x, int y){
		coinPrefab = new ItemUi(this,"sprites/coin.png",x,y);
		entities.add(coinPrefab);
	}

    public void updateLogic() {
        logicRequiredThisLoop = true;
    }

	/**
	 * Remove an entity from the game. The entity removed will
	 * no longer move or be drawn.
	 *
	 * @param entity The entity that should be removed
	 */
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}

	/**
	 * Notification that the player has died.
	 */
	public void  notifyDeath() {
		//message = "Oh no! They got you, try again?";
		stage = 1;
		//게임오버 시 다시 할지 나갈지 결정(임시)
		pauseGame("You Died! Wanna Quit?","",true);
		if(glp.us.getHighScore() < score){
			glp.us.setHighScore(score);
		}
	}

    public void notifyWin() {
        message = "Well done! You Win!";
        waitingForKeyPress = true;
    }

    public void notifyAlienKilled() {
        // reduce the alient count, if there are none left, the player has won!
        alienCount--;
        score += 100;
        if (alienCount == 0) {
            switch (stage) {
                case 1:
					addBoss(100);
					addBossHpBar(100);
					break;
				case 2:
					addBoss(110);
					addBossHpBar(110);
					break;
				case 3:
					addBoss(120);
					addBossHpBar(120);
					break;
				case 4:
					addBoss(130);
					addBossHpBar(130);
					break;
				case 5:
					addBoss(140);
					addBossHpBar(140);
					break;
			}
		}
		// if there are still some aliens left then they all need to get faster, so
		// speed up all the existing aliens
		for (Object o : entities) {
			Entity entity = (Entity) o;

			if (entity instanceof AlienEntity) {
				// speed up by 2%
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
			}
		}
	}
	public void notifyBossKilled(){
		bossAlive = false;
		for (Entity value : bossHpUi) {
			removeEntity(value);
		}
		removeEntity(bossHpBar);
		stage++;
		score +=1000;
		showStageimg();
		if(!bossAlive){
			addAlien();
		}
		Entity entity = (Entity) entities.get(0);
		if(entity instanceof BossEntity){
			entity.setHorizontalMovement(entity.getHorizontalMovement()*1.02);
		}
	}
	public void tryToFire() {
		// check that we have waiting long enough to fire
		if (isBossAlive(System.currentTimeMillis() - lastFire < firingInterval)) return;

		// if we waited long enough, create the shot entity, and record the time.
		lastFire = System.currentTimeMillis();
		ShotEntity shot = new ShotEntity(this,"sprites/shot.gif",ship.getX()+10,ship.getY()-30);
		entities.add(shot);
		sp.playSE(16,-10);
	}
	/** 단게별 보스 패턴 **/
	public void bossFire(){ /**2단계 보스 패턴**/
		if (isBossAlive(!bossAlive)) return;
		if((stage ==2)||(stage ==4) ||(stage==5) ){
			BossShotEntity shot = new BossShotEntity(this,"sprites/bossShot.png",boss.getX()+30,boss.getY()+100);
			entities.add(shot);
			shot.shotXMove(ship.getX() - shot.getX(),300);
			sp.playSE(19,-20);
		}
	}

	public void bossUltiCos(int timer){/**보스 미사일 패턴**/
		if (isBossAlive(!bossAlive)) return;
		double cos = Math.toRadians(timer);
		double coss = Math.cos(cos);
		if((stage ==2)||(stage ==4) ||(stage==5) ){
			shotCos(timer, coss);
		}

	}

	public void bossUltiLayer(int timer){
		if (isBossAlive(!bossAlive)) return;
		if((stage ==2)||(stage ==4) ||(stage==5) ){
			shotLayer(timer);
		}
	}
	private void shotCos(int timer, double coss) {
		if ((timer >100&& timer <300)&&(timer %15==0)){
			BossShotEntity shot = new BossShotEntity(this,"sprites/bossShot.png",boss.getX()+30,boss.getY()+100);
			entities.add(shot);
			shot.shotXMove(coss *300,200);
			BossShotEntity shot2 = new BossShotEntity(this,"sprites/bossShot.png",boss.getX()+30,boss.getY()+100);
			entities.add(shot2);
			shot2.shotXMove(coss *300*-1,200);
			sp.playSE(7,0);
		}
	}

	private void shotLayer(int timer) {
		if(timer %500 ==0){
			for(int i=-4; i<=4; i++){
				BossShotEntity shot = new BossShotEntity(this,"sprites/bossShot.png",boss.getX()+30,boss.getY()+100);
				entities.add(shot);
				shot.shotXMove(50*i,150);
			}
		}
	}

	public void addObstacle(){ /**3단계 보스 패턴**//**장애물 생성**/
		int randomObstacle = (int) (Math.random() * 5); // 0~4까지의 랜덤한 정수
		if (isBossAlive(!bossAlive)) return;
		if((stage ==3)||(stage==5)){
			sp.playSE(15,0);
			loadobstacles(randomObstacle);
			entities.add(obstacle);
		}
	}
	private void loadobstacles(int randomObstacle) {
		if (randomObstacle == 0) {
			obstacle = new ObstacleEntity(this,"sprites/mini_obstacle_blue_moon.png",(int)(Math.random()*750),10);
		}
		else if (randomObstacle == 1) {
			obstacle = new ObstacleEntity(this,"sprites/mini_obstacle_moon.png",(int)(Math.random()*750),10);
		}
		else if (randomObstacle == 2) {
			obstacle = new ObstacleEntity(this,"sprites/mini_obstacle_purple_moon.png",(int)(Math.random()*750),10);
		}
		else if (randomObstacle == 3) {
			obstacle = new ObstacleEntity(this,"sprites/obstacle_saturn.png",(int)(Math.random()*750),10);
		}
		else if (randomObstacle == 4) {
			obstacle = new ObstacleEntity(this,"sprites/obstacle_sun.png",(int)(Math.random()*750),10);
		}
		else {
			obstacle = new ObstacleEntity(this,"sprites/obstacle_sun.png",(int)(Math.random()*750),10);
		}
	}
	public void bossReflectStart(){ /**반사시 캐릭터 체력 감소**/
		ship.setHp(-1);
	}
	private boolean isBossAlive(boolean bossAlive) {
		if(bossAlive){
			return true;
		}
		return false;
	}

	/**
	 * The main game loop. This loop is running during all game
	 * play as is responsible for the following activities:
	 * <p>
	 * - Working out the speed of the game loop to update moves
	 * - Moving the game entities
	 * - Drawing the screen contents (entities, text)
	 * - Updating game events
	 * - Checking Input
	 * <p>
	 */
	public void gameLoop() {
		long lastLoopTime = SystemTimer.getTime();
		// keep looping round til the game ends
		if (glp.gameState == glp.inGameState) {
			while (gameRunning) {
				// work out how long its been since the last update, this
				// will be used to calculate how far the entities should
				// move this loop
				long delta = SystemTimer.getTime() - lastLoopTime;
				lastLoopTime = SystemTimer.getTime();

				//
				long elapsedTime = System.currentTimeMillis() - startTime;
				long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) %60;
				long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime)%60;

				// update the frame counter
				lastFpsTime += delta;
				fps++;
				timer ++;
				if(timer>1000)
				{
					timer = 1;
				}
				// update our FPS counter if a second has passed since
				// we last recorded
				if (lastFpsTime >= 1000) {
					container.setTitle(windowTitle+" (FPS: "+fps+")");
					lastFpsTime = 0;
					fps = 0;
				}

				// Get hold of a graphics context for the accelerated
				// surface and blank it out

				// Get hold of a graphics context for the accelerated
				// surface and blank it out
				Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
				Graphics2D gi = (Graphics2D) strategy.getDrawGraphics();
				Graphics2D ggi = (Graphics2D) strategy.getDrawGraphics();
				g.setColor(Color.black);
				g.fillRect(0,0,800,600);
				switch (stage) {
					case 1: g.drawImage(round1, 0, 0, null); break;
					case 2: g.drawImage(round2, 0, 0, null); break;
					case 3: g.drawImage(round3, 0, 0, null); break;
					case 4: g.drawImage(round4, 0, 0, null); break;
					case 5: g.drawImage(round5, 0, 0, null); break;
				}


				// cycle round asking each entity to move itself
				if (!waitingForKeyPress) {
					for (Object o : entities) {
						Entity entity = (Entity) o;

						entity.move(delta);
					}
				}

				// cycle round drawing all the entities we have in the game
				for (Object o : entities) {
					Entity entity = (Entity) o;

					entity.draw(g);
				}

				// brute force collisions, compare every entity against
				// every other entity. If any of them collide notify
				// both entities that the collision has occured
				for (int p=0;p<entities.size();p++) {
					for (int s=p+1;s<entities.size();s++) {
						Entity me = (Entity) entities.get(p);
						Entity him = (Entity) entities.get(s);

						if (me.collidesWith(him)) {
							me.collidedWith(him);
							him.collidedWith(me);
						}
					}
				}

				// remove any entity that has been marked for clear up
				entities.removeAll(removeList);
				removeList.clear();

				// if a game event has indicated that game logic should
				// be resolved, cycle round every entity requesting that
				// their personal logic should be considered.
				if (logicRequiredThisLoop) {
					for (int i=0;i<entities.size();i++) {
						Entity entity = (Entity) entities.get(i);
						entity.doLogic();
					}

					logicRequiredThisLoop = false;
				}

				// if we're waiting for an "any key" press then draw the
				// current message
				if (waitingForKeyPress) {
					g.setColor(Color.white);
					//g.setFont(glp.mu.NeoDung);
					g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
					g.drawString("Press any key",(800-g.getFontMetrics().stringWidth("Press any key"))/2,300);
				}

				/**물약 남은 수**/
				ggi.setColor(Color.white);
				Font font1 = new Font("OCR A Extended",Font.PLAIN,15);
				printInformation(ggi, font1, String.valueOf(glp.us.getHealPotion()), 33, 580);
				ggi.drawString(String.valueOf(glp.us.getSpeedPotion()),66,580);

				gi.setColor(Color.white);
				gi.setFont(glp.mu.NeoDung);

				/** 시간**/

				printInformation(gi, gi.getFont().deriveFont(Font.PLAIN, 25f), minutes + ":" + seconds, 377, 35);
				printInformation(gi, gi.getFont().deriveFont(Font.PLAIN, 25f), "Score "+score, 29,35);
				printInformation(gi, gi.getFont().deriveFont(Font.PLAIN, 25f), String.valueOf(glp.us.getCoin()), 710,70);

				//아이템 쿨타임 표시
				showHPCooldown();
				if(usedHealPotion) gi.drawString(String.valueOf(HPCooldown),30,573);

				showSPCooldown();
				if(usedSpeedPotion) gi.drawString(String.valueOf(SPCooldown),60,573);



				strategy.show();


				// resolve the movement of the ship. First assume the ship
				// isn't moving. If either cursor key is pressed then
				// update the movement appropraitely
				ship.setHorizontalMovement(0);

				if ((leftPressed) && (!rightPressed)) {
					ship.setHorizontalMovement(-moveSpeed);
				} else if ((rightPressed) && (!leftPressed)) {
					ship.setHorizontalMovement(moveSpeed);
				}
				// if we're pressing fire, attempt to fire
				if (firePressed) {
					tryToFire();
				}
				if(timer%100== 0){
					addObstacle();
					bossFire();
				}
				//게임 일시 정지 & 로비로 나가기
				if(escPressed){
					escPressed = false;
					pauseGame("Paused","",false);
				}

				if(ship.getHp()<=0 && !waitingForKeyPress){
					notifyDeath();
				}
				bossPattern(seconds);
				/**보스 무적**/
				//bossReflectMode(timer); /**보스 데미지 반사**/
				removeBossHp();/**보스 hp ui**/
				shipGotHit();/** 플레이어 피격**/

				reflectTime();


				SystemTimer.sleep(lastLoopTime+10-SystemTimer.getTime());

				if ((System.currentTimeMillis() - lastUseHealPotion) > coolTime){
					changeHealPotionIcon();
				}
				if ((System.currentTimeMillis() - lastUseSpeedPotion) > coolTime){
					changeSpeedPotionIcon();
				}
				if ((System.currentTimeMillis() - lastUseSpeedPotion) > 2000){
					returnMoveSpeed();
				}
				if ((System.currentTimeMillis() - addRound) > 1000){
					removeRoundUi();
				}
				if((System.currentTimeMillis() - lastUsedShipImmortal) > coolTime){
					ship.setImmortal(false);
				}
			}
		}
	}

	private void printInformation(Graphics2D gi, Font gi1, String minutes, int x, int y) {
		gi.setFont(gi1);
		gi.drawString(minutes, x, y);
	}

	private void bossPattern(long seconds) {
		if (isBossAlive(!bossAlive)) return;
		switch (stage){
			case 1:
				boss.doImmortal(seconds);
				break;
			case 2:
				//BossUlti(timer);/**보스 공격패턴1**/
				bossUltiLayer(timer);/**보스 공격패턴2**/
				break;
			case 3:
				boss.doReflect(seconds);
				break;
			case 4:
				boss.doReflect(seconds);
				boss.ReflectCheck(timer);
				bossUltiCos(timer);
				//reflectTime();
				break;
			case 5:
				boss.doReflect(seconds);
				bossUltiLayer(timer);
				boss.ReflectCheck(timer);
				//reflectTime();
				break;
		}
	}
	public void shipGotHit(){
		if(ship.getHit()){
			removeEntity(playerHpUI[ship.getHp()]);
		}
		else{ship.setHit(false);}
	}

	public void removeRoundUi(){
		if (isBossAlive(!isStageUiOn)) return;
		for(int i=0; i<5; i++){
			removeEntity(stageUI);
		}
	}

	public  void useHealPotion(int i){
		if(glp.us.getSelectedShip() == 0 || glp.us.getSelectedShip() == 2) {
			if (i >= 5) return;
		}
		if(glp.us.getSelectedShip() == 1) {
			if (i >= 7) return;
		}
		if (isBossAlive(glp.us.getHealPotion() < 1)) return;
		if (isBossAlive((System.currentTimeMillis() - lastUseHealPotion) < coolTime)) return;
		lastUseHealPotion = System.currentTimeMillis();
			glp.us.incHealPotion(-1);
			ship.setHp(1);
			playerHpUI[i] = new GameUi(this,"sprites/heart.png",750-(35*i),15);
			entities.add(playerHpUI[i]);
			removeEntity(itemUi[0]);
			itemUi[1] = new GameUi(this,"sprites/used_heal_potion.png",20,550);
			entities.add(itemUi[1]);
			usedHealPotion = true;
			sp.playSE(17,0);
	}
	public void changeHealPotionIcon(){
		if (usedHealPotion) {
			removeEntity(itemUi[1]);
			entities.add(itemUi[0]);
		}
		usedHealPotion = false;
	}

	public void changeSpeedPotionIcon(){
		if (usedSpeedPotion) {
			removeEntity(itemUi[3]);
			entities.add(itemUi[2]);
		}
		usedSpeedPotion = false;
	}

	public void useSpeedPotion(){
		if (isBossAlive(glp.us.getSpeedPotion() < 1)) return;
		if (isBossAlive((System.currentTimeMillis() - lastUseSpeedPotion) < coolTime)) return;
		lastUseSpeedPotion = System.currentTimeMillis();
		moveSpeed = 500;
		glp.us.incSpeedPotion(-1);
		removeEntity(itemUi[2]);
		itemUi[3] = new GameUi(this,"sprites/used_speed_potion.png",50,550);
		entities.add(itemUi[3]);
		usedSpeedPotion = true;
		sp.playSE(18,0);
	}
	public void returnMoveSpeed(){
			moveSpeed = 300;
	}


	public void removeBossHp(){/**보스 hp ui 동작 **/
		if (isBossAlive(!bossAlive)) return;
		if(boss.getHit()){
			int num = boss.getHp();
			removeEntity(bossHpUi[num]);
			num--;
		}
		else{
			boss.setHit(false);
		}
	}

	//게임 일시정지(미완)
	public void pauseGame(String dialog_message, String title, boolean waitingTrue){
		gameRunning = false;
		int exitGame = JOptionPane.showConfirmDialog(this, dialog_message,title,JOptionPane.YES_NO_OPTION);
		if (exitGame == JOptionPane.YES_OPTION) {
			glp.frameLocation = container.getLocationOnScreen();
			container.dispose();
			glp.gameState = glp.titleState;
			//new MainFrame();
		}
		else {
			if(waitingTrue) {waitingForKeyPress = true;}
			gameRunning = true;
		}
	}
	public void reflectTime( ){
		if (reflectDamaged) {
			reflectCooldown -= 0.01;
			if(reflectCooldown<= 0){
				reflectCooldown = 0.5;
				reflectDamaged = false;
			}
		}
	}
	public void showHPCooldown( ){
		if (usedHealPotion) {
			HPcooldownCheck++;
			if (HPcooldownCheck>100){
				HPCooldown--;
				HPcooldownCheck =0;
			}
			if(HPCooldown <= 0){
				HPCooldown = 3;
			}
		}
	}

	public void showSPCooldown( ) {
		if (usedSpeedPotion) {
			SPcooldownCheck++;
			if (SPcooldownCheck > 100) {
				SPCooldown--;
				SPcooldownCheck = 0;
			}
			if (SPCooldown <= 0) {
				SPCooldown = 3;
			}
		}
	}

	public void loadBackImg() {
		try {
			//타이틀 이미지 로딩
			InputStream is = new BufferedInputStream(Files.newInputStream(Paths.get("src/sprites/window/round1_boss_window.png")));
			round1 = ImageIO.read(is);
			InputStream is2 = new BufferedInputStream(Files.newInputStream(Paths.get("src/sprites/window/round2_boss_window.png")));
			round2 = ImageIO.read(is2);
			InputStream is3 = new BufferedInputStream(Files.newInputStream(Paths.get("src/sprites/window/round3_boss_window.png")));
			round3 = ImageIO.read(is3);
			InputStream is4 = new BufferedInputStream(Files.newInputStream(Paths.get("src/sprites/window/round4_boss_window.png")));
			round4 = ImageIO.read(is4);
			InputStream is5 = new BufferedInputStream(Files.newInputStream(Paths.get("src/sprites/window/round5_boss_window.png")));
			round5 = ImageIO.read(is5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A class to handle keyboard input from the user. The class
	 * handles both dynamic input during game play, i.e. left/right
	 * and shoot, and more static type input (i.e. press any key to
	 * continue)
	 *
	 * This has been implemented as an inner class more through
	 * habbit then anything else. Its perfectly normal to implement
	 * this as seperate class if slight less convienient.
	 *
	 * @author Kevin Glass
	 */
	private class KeyInputHandler extends KeyAdapter {
		/** The number of key presses we've had while waiting for an "any key" press */
		private int pressCount = 1;

		/**
		 * Notification from AWT that a key has been pressed. Note that
		 * a key being pressed is equal to being pushed down but *NOT*
		 * released. Thats where keyTyped() comes in.
		 *
		 * @param e The details of the key that was pressed
		 */
		public void keyPressed(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't
			// want to do anything with just a "press"
			if (isBossAlive(waitingForKeyPress)) return;

			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = true;
			}
			if (e.getKeyChar() == 'x'){
				useSpeedPotion();
			}
			if (e.getKeyChar() == 'z'){
				useHealPotion(ship.getHp());
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				escPressed = true;
			}
			if (e.getKeyChar() == 'c'){
				ship.setImmortal(true);
				lastUsedShipImmortal = System.currentTimeMillis();
			}
		}

		/**
		 * Notification from AWT that a key has been released.
		 *
		 * @param e The details of the key that was released
		 */
		public void keyReleased(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't
			// want to do anything with just a "released"
			if (isBossAlive(waitingForKeyPress)) return;

			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				escPressed = false;
			}
		}

		/**
		 * Notification from AWT that a key has been typed. Note that
		 * typing a key means to both press and then release it.
		 *
		 * @param e The details of the key that was typed.
		 */
		public void keyTyped(KeyEvent e) {
			// if we're waiting for a "any key" type then
			// check if we've recieved any recently. We may
			// have had a keyType() event from the user releasing
			// the shoot or move keys, hence the use of the "pressCount"
			// counter.
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					// since we've now recieved our key typed
					// event we can mark it as such and start
					// our new game
					waitingForKeyPress = false;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				}
			}

			// if we hit escape, then quit the game
			/*if (e.getKeyChar() == 27) {
				System.exit(0);
			}*/
		}
	}
	/**
	 * The entry point into the game. We'll simply create an
	 * instance of class which will start the display and game
	 * loop.
	 *
	 * @param argv The arguments that are passed into our game
	 */
	public static void main(String argv[]) {
		Game g = new Game(new GameLobbyPanel());
		g.gameLoop();
	}
}


