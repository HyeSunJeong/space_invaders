package org.newdawn.spaceinvaders;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.*;

import org.newdawn.spaceinvaders.entity.*;


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
	int timer;
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

	private  Entity[] bossHpUi = new Entity[150];
	private Entity[] playerHpUI = new Entity[10];
	private Entity obstacle;
	/**화면에 남은 보스 수 **/
	private int bossCount;
	private double moveSpeed = 300;
	/** The time at which last fired a shot */
	private long lastFire = 0;
	private long bossLastFire = 0;
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
	GameLobbyPanel glp;

	private Boolean bossAlive = false;
	private int stage=2;

	/**
	 * Construct our game and set it running.
	 */
	public Game(GameLobbyPanel glp) {
		this.glp = glp;
		//UserDB.loggedIn();
		/*GamePanel gp = (GamePanel) lf.getContentPane();
		gp.setPreferredSize(new Dimension(800,600));
		setBounds(0,0,800,600);
		gp.add(this);*/
		// create a frame to contain our game
		container = new JFrame("Space Invaders 102");

		// get hold the content of the frame and set up the resolution of the game
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800,600));
		panel.setLayout(null);

		// setup our canvas size and put it into the content of the frame
		setBounds(0,0,800,600);
		panel.add(this);

		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		//setIgnoreRepaint(true);

		// finally make the window visible
		container.pack();
		container.setLocationRelativeTo(null);
		container.setResizable(false);
		container.setVisible(true);

		// add a listener to respond to the user closing the window. If they
		// do we'd like to exit the game
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// add a key input system (defined below) to our canvas
		// so we can respond to key pressed
		addKeyListener(new KeyInputHandler());

		// request the focus so key events come to us
		requestFocus();

		// create the buffering strategy which will allow AWT
		// to manage our accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		// initialise the entities in our game so there's something
		// to see at startup
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
	}


	/**
	 * Initialise the starting state of the entities (ship and aliens). Each
	 * entitiy will be added to the overall list of entities in the game.
	 */
	private void initEntities() {
		// create the player ship and place it roughly in the center of the screen
		AddShip();
		AddAlien();
		//AddBoss(100);
		//AddBossHp(100);
		AddPlayerHp(ship.getHp());
	}

	/**플레이어 생성**/
	public void AddShip(){
		ship = new ShipEntity(this,"sprites/ship.gif",370,550);
		entities.add(ship);
	}
	/**플레이어 HP IU**/
	public void AddPlayerHp(int playerHp){
		for(int i=0; i<playerHp; i++){
			playerHpUI[i] = new GameUi(this,"sprites/heart.png",300+(33*i),10);
			entities.add(playerHpUI[i]);
		}
	}
	/**기본 적 생성 **/
	public void AddAlien(){
		alienCount = 0;
		for (int row=0;row<5;row++) {
			for (int x=0;x<12;x++) {
				Entity alien = new AlienEntity(this,100+(x*50),(50)+row*30);
				entities.add(alien);
				alienCount++;
			}
		}
	}
	/**보스 생성**/
	public  void AddBoss(int hp){
		bossCount = 1;
		boss = new BossEntity(this,350,100);
		entities.add(boss);
		bossAlive = true;
		boss.setHp(hp);
	}

	/**보스 HP UI**/
	public void AddBossHp(int bossHp){
		for(int i=0; i<bossHp; i++){
			bossHpUi[i] = new GameUi(this,"sprites/bossHpBar.png",10+i,10);
			entities.add(bossHpUi[i]);
		}
	}
	/**
	 * Notification from a game entity that the logic of the game
	 * should be run at the next opportunity (normally as a result of some
	 * game event)
	 */
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
	}

	/**
	 * Notification that the player has won since all the aliens
	 * are dead.
	 */
	public void notifyWin() {
		message = "Well done! You Win!";
		waitingForKeyPress = true;
	}

	/**
	 * Notification that an alien has been killed
	 */
	public void notifyAlienKilled() {
		// reduce the alient count, if there are none left, the player has won!
		alienCount--;
		if (alienCount == 0) {
			switch (stage){
				case 1:
					AddBoss(100);
					//AddBossHp(100);
					break;
				case 2:
					AddBoss(110);
					//AddBossHp(110);
					break;
				case 3:
					AddBoss(120);
					//AddBossHp(120);
					break;
				case 4:
					AddBoss(130);
					//AddBossHp(130);
					break;
				case 5:
					AddBoss(140);
					//AddBossHp(140);
					break;
			}
		}
		// if there are still some aliens left then they all need to get faster, so
		// speed up all the existing aliens
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);

			if (entity instanceof AlienEntity) {
				// speed up by 2%
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
			}
		}
	}
	public void notifyBossKilled(){
		bossAlive = false;
		bossCount--;
		stage++;
		if(bossCount ==0){
			AddAlien();
		}
		Entity entity = (Entity) entities.get(0);
		if(entity instanceof BossEntity){
			entity.setHorizontalMovement(entity.getHorizontalMovement()*1.02);
		}
	}

	/**
	 * Attempt to fire a shot from the player. Its called "try"
	 * since we must first check that the player can fire at this
	 * point, i.e. has he/she waited long enough between shots
	 */
	public void tryToFire() {
		// check that we have waiting long enough to fire
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}

		// if we waited long enough, create the shot entity, and record the time.
		lastFire = System.currentTimeMillis();
		ShotEntity shot = new ShotEntity(this,"sprites/shot.gif",ship.getX()+10,ship.getY()-30);
		entities.add(shot);

	}

	/** 단게별 보스 패턴 **/
	public void BossGodMode(int time){ /**1단계 보스 패턴**/
		if(!bossAlive){
			return;
		}
		if((stage==1)||(stage==5)){
			boss.ImmortallityCheck(time);
		}
	}
	public void BossFire(){ /**2단계 보스 패턴**/
		if(!bossAlive){
			return;
		}
		if((stage ==2)||(stage ==4) ||(stage==5) ){
			BossShotEntity shot = new BossShotEntity(this,"sprites/shot.gif",boss.getX()+30,boss.getY()+100);
			entities.add(shot);
			shot.shotXMove(ship.getX() - shot.getX(),300);
		}
	}

	public void BossUlti(int timer){/**보스 미사일 패턴**/
		if(!bossAlive){
			return;
		}
		double cos = Math.toRadians(timer);
		double coss = Math.cos(cos);
		if((stage ==2)||(stage ==4) ||(stage==5) ){
			if ((timer>100&&timer<400)&&(timer%10==0)){
				BossShotEntity shot = new BossShotEntity(this,"sprites/shot.gif",boss.getX()+30,boss.getY()+100);
				entities.add(shot);
				shot.shotXMove(coss*300,200);
				BossShotEntity shot2 = new BossShotEntity(this,"sprites/shot.gif",boss.getX()+30,boss.getY()+100);
				entities.add(shot2);
				shot2.shotXMove(coss*300*-1,200);
			}
		}
		//CircleBossShot();
	}
	public void AddBossShot(int startX){
		BossShotEntity shot = new BossShotEntity(this,"sprites/shot.gif",boss.getX()+startX,boss.getY()+100);
		entities.add(shot);
		shot.shotXMove(0,200);
	}
	public void CircleBossShot(){
		if((stage ==2)||(stage ==4) ||(stage==5) ){
			switch (timer){
				case 100:
					AddBossShot(30);
					break;
				case 105:
					AddBossShot(20);
					AddBossShot(40);
					break;
				case 110:
					AddBossShot(10);
					AddBossShot(50);
					break;
				case 115:
					AddBossShot(20);
					AddBossShot(40);
					break;
				case 120:
					AddBossShot(30);
					break;
			}
		}
	}
	public void AddObstacle(){ /**3단계 보스 패턴**//**장애물 생성**/
		if(!bossAlive){
			return;}
		if((stage ==3)||(stage==5)){
			obstacle = new ObstacleEntity(this,"sprites/Obstacle.png",(int)(Math.random()*750),10);
			entities.add(obstacle);
		}
	}
	public void BossReflectMode(int time){ /**4단계 보스 패턴**//**보스 데미지 반사**/
		if(!bossAlive){
			return;
		}
		if ((stage == 4)||(stage ==5)) {
			boss.ReflectCheck(time);
		}
	}
	public void bossReflectStart(){ /**반사시 캐릭터 체력 감소**/
		ship.setHp(-1);
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
				Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
				g.setColor(Color.black);
				g.fillRect(0,0,800,600);

				// cycle round asking each entity to move itself
				if (!waitingForKeyPress) {
					for (int i=0;i<entities.size();i++) {
						Entity entity = (Entity) entities.get(i);

						entity.move(delta);
					}
				}

				// cycle round drawing all the entities we have in the game
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);

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
					g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
					g.drawString("Press any key",(800-g.getFontMetrics().stringWidth("Press any key"))/2,300);
				}
				/** 타이머**/
				g.setColor(Color.white);
				g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
				g.drawString("타이머 "+String.valueOf(timer),720,30);

				/** 스테이지 **/
				g.setColor(Color.white);
				g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
				g.drawString("현제 스테이지  "+ String.valueOf(stage),30,580);

				/** 미니언 수 **/
				g.setColor(Color.white);
				g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
				g.drawString("남은 적 수 "+String.valueOf(alienCount),10,30);

				/**플레이어 체력**/
				g.setColor(Color.white);
				g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
				g.drawString("플레이어 체력 "+String.valueOf(ship.getHp()),700,580);


				/**보스 체력**/
				if(bossAlive){
					g.setColor(Color.white);
					g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
					g.drawString("보스 체력  "+String.valueOf(boss.getHp()),10,100);
				}

				if(bossAlive){
					g.setColor(Color.white);
					g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
					g.drawString("보스 피격  "+String.valueOf(boss.getHit()),10,400);
				}

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
					AddObstacle();
					BossFire();
				}
				//게임 일시 정지 & 로비로 나가기
				if(escPressed){
					escPressed = false;
					pauseGame("Paused","",false);
				}

				if(ship.getHp()<=0 && !waitingForKeyPress){
					notifyDeath();
				}

				BossGodMode(timer); /**보스 무적**/
				BossReflectMode(timer); /**보스 데미지 반사**/
				// we want each frame to take 10 milliseconds, to do this
				// we've recorded when we started the frame. We add 10 milliseconds
				// to this and then factor in the current time to give
				// us our final value to wait for
				SystemTimer.sleep(lastLoopTime+10-SystemTimer.getTime());
				BossUlti(timer);

				BossHpDeal();

				if(ship.getHit()){
					removeEntity(playerHpUI[ship.getHp()]);
				}
				else{ship.setHit(false);}
			}
		}
	}
	public  void UseItem(int i){
		ship.setHp(1);
		playerHpUI[i] = new GameUi(this,"sprites/heart.png",300+(33*i),10);
		entities.add(playerHpUI[i]);
	}
	public void BossHpDeal(){
		if(!bossAlive){return;}
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
			LoginFrame.frameLocation = container.getLocationOnScreen();
			container.dispose();
			glp.gameState = glp.titleState;
			//new MainFrame();
		}
		else {
			if(waitingTrue) {waitingForKeyPress = true;}
			gameRunning = true;
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
			if (waitingForKeyPress) {
				return;
			}


			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				escPressed = true;
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
			if (waitingForKeyPress) {
				return;
			}

			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
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
			if (e.getKeyChar() == 27) {
				System.exit(0);
			}
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
		//UserDB.is_logged_in = true;
		Game g = new Game(new GameLobbyPanel());
		// Start the main game loop, note: this method will not
		// return until the game has finished running. Hence we are
		// using the actual main thread to run the game.
		g.gameLoop();
	}
}
