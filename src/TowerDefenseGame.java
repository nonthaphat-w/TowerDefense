import java.util.ArrayList;
import java.util.LinkedList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


import org.newdawn.slick.Input; //import key if finish delete
import org.newdawn.slick.geom.Rectangle;

public class TowerDefenseGame extends BasicGame{
	
	public static int Screen_Width = 1920;
	public static int Screen_Height = 1080;
	public static int Stage_Width = 1560;
	public static int Stage_Height = 780;
	public static int Stage_x = 0;
	public static int Stage_y = 0;
	public static int Store_Height = 300;
	
	
	
	private float time = 0;
	public float timer = 0;
	
	public int currentWave = 2;
	public static int wave = 0;
	private boolean checkWave = true;  // check to release monster in next wave
	
	 private LinkedList<Entity> entities;
	
	 private TerrainDarkStage darkterrain;
	 private Store cell;
	 private fieldBuild filedbuild;
	 private Door doorHeart;
	 
	 
	 //monster
	 public static float monster_startX = -78;
	 public static float monster_startY = 156;
	 private boolean monster_checkTotal = false;
	private static int number_monster = 0;
	private static int max_monster = 20;
	private static float timerdelay_monster = (float) 3;
	private static float timer_monster = 0;
	 private ArrayList<Monster> monsterAll = new ArrayList<Monster>();

	
	//Background
	private static float changeResolutionBg = 1;
	private static int checkResolutionBg = 1;
	
	
	//Hp game use in Door
	public static int HpGame = 100;
	private int Heart_x = 1300;
	private int Heart_y = 920;
	private Rectangle hpLoaded;
	
	
	//Image Screen
	private Image darkstage;
	private Image Shopbackground;
	private Image Upgratebackground;

	
	//Store Tower
	private ArrayList<TowerDark> towerdark = new ArrayList<TowerDark>();
	public static boolean checkMouseClickCell = false;
	
	
	//Mouse
	private static float mouseError = 21;
	
	
	//GameStart and GameOver
	private boolean isGameStarted = false;
	private boolean isGameOver = false;

	public TowerDefenseGame(String title) throws SlickException {
		super(title);
		entities = new LinkedList<Entity>();
	}
	
	
	
	//SetBackGround
	public void setBackgroundinit(GameContainer container) throws SlickException{
		Color background = new Color(0, 0, 0);
		container.getGraphics().setBackground(background);
		darkstage = new Image("res/DarkStage.png");
		Shopbackground = new Image("res/Shop.png");
		Upgratebackground = new Image("res/Upgrate.png");

	}
	
	public void setBackgroundRender(Graphics g){
		g.setColor(new Color(0, 0, 0));
		darkstage.draw(Stage_x, Stage_y);
		Shopbackground.draw(30, Stage_Height+10);
		Upgratebackground.draw(Stage_Width+3, 0);
	}

	public void ChangeResolutionBg(){
		if(checkResolutionBg == 1){
			changeResolutionBg -= 0.05;
			if(changeResolutionBg <= 0){
				checkResolutionBg = 0;
			}
		}
		else if(checkResolutionBg == 0){
			changeResolutionBg += 0.05;
			if(changeResolutionBg >= 1){
				checkResolutionBg = 1;
			}
		}
	}
	
	public void Timer(int delta){
		//time in Game
				time += delta;
				if(time > 500){
					time = 0;
					timer += 0.5;
					ChangeResolutionBg();
					if(currentWave != 0 && Math.ceil(timer) - timer != 0.5){
						currentWave--;
					}
					if(checkWave && currentWave == 0){
						wave++;
						currentWave = 5;
					    checkWave = false;
					}
				}
	}
	
	public void releaseMonster() throws SlickException{
		if(wave == 1 && number_monster < max_monster){
			if(!monster_checkTotal){
				monsterAll.add(new MonsterLv1(monster_startX, monster_startY));
				monster_checkTotal = true;			// check monster release
				timer_monster = timer+timerdelay_monster;
				number_monster++;
			}
			else if(timer_monster == timer){
				monster_checkTotal = false;			// check monster release
			}
		}
	}
	
	//Draw textHp
	public void HpLoaded(Graphics g){
			g.setColor(new Color(255, 0, 0));
			g.drawString("" + HpGame + "/100",Heart_x +75 , Heart_y-7);
			hpLoaded = new Rectangle(Heart_x +70 , Heart_y +13,4*HpGame,25);
			g.setColor(new Color(0,0,0));
			g.draw(hpLoaded);
			g.setColor(new Color(100f,0f,0f,0.3f+HpGame/100f));
			g.fill(hpLoaded);
	}
	
	public void death(){
		if(HpGame == 0){
			isGameOver = true;
			isGameStarted = false;
		}
		else if(HpGame < 0){
			HpGame = 0;
		}
	}
	
	//buy tower in shop
	public void mouseClickBuyItemShop(int button, int x, int y){
		if(Store.checkMouseTower(x,y) && !checkMouseClickCell && button == 0){		//click tower in shop
			checkMouseClickCell = true;
		}
		else if(checkMouseClickCell && button == 0 &&
				fieldBuild.checkCol_mouseXRectY-1 != -1 &&
				fieldBuild.checkCol_mouseXRectX != -1 &&
				fieldBuild.checkCol_mouseXRectY != -1 &&
				fieldBuild.checkCol_mouseXRectX+1 != -1){
					if(fieldBuild.fieldTerrain[fieldBuild.checkCol_mouseXRectY-1][fieldBuild.checkCol_mouseXRectX] == 0 &&
					   fieldBuild.fieldTerrain[fieldBuild.checkCol_mouseXRectY][fieldBuild.checkCol_mouseXRectX] == 0 &&
			           fieldBuild.fieldTerrain[fieldBuild.checkCol_mouseXRectY-1][fieldBuild.checkCol_mouseXRectX+1] == 0 &&
					   fieldBuild.fieldTerrain[fieldBuild.checkCol_mouseXRectY][fieldBuild.checkCol_mouseXRectX+1] == 0){
						
						//add tower
						
						try {
							towerdark.add(new TowerDark(fieldBuild.checkCol_mouseXRectX * fieldBuild.sizeRect,
									(fieldBuild.checkCol_mouseXRectY-1)* fieldBuild.sizeRect));
							}	 catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//give red field
						fieldBuild.fieldTerrain[fieldBuild.checkCol_mouseXRectY-1][fieldBuild.checkCol_mouseXRectX] = 99;
						fieldBuild.fieldTerrain[fieldBuild.checkCol_mouseXRectY][fieldBuild.checkCol_mouseXRectX] = 99;
						fieldBuild.fieldTerrain[fieldBuild.checkCol_mouseXRectY-1][fieldBuild.checkCol_mouseXRectX+1] = 99;
						fieldBuild.fieldTerrain[fieldBuild.checkCol_mouseXRectY][fieldBuild.checkCol_mouseXRectX+1] = 99;
						
						//remove terrain and click
						fieldBuild.checkCol_mouseXRectX = -1;
						fieldBuild.checkCol_mouseXRectY = -1;
						checkMouseClickCell = false;
					}
		}
		else if(checkMouseClickCell && button == 1){
			//remove buy
				checkMouseClickCell = false;
				fieldBuild.checkCol_mouseXRectX = -1;
				fieldBuild.checkCol_mouseXRectY = -1;
		}
	}
	
	
	@Override
	  public void keyPressed(int key, char c) {
		if(isGameStarted && !isGameOver){
		 if (key == Input.KEY_T) {
		 }
		}
	  }
	
	//Update Render and init
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		setBackgroundRender(g);
		
		//draw textWave
		g.drawString("Wave   " + wave, 1300, 850);
		g.drawString("Next Wave  " + currentWave, 1300, 870);
		
		//draw Hp and TextHp
		HpLoaded(g);
		
		for(Entity entity : entities) {
			entity.render(g);
		}
		for(Monster monster : monsterAll){
			monster.render(g);
		}	
		for(TowerDark tower : towerdark){
			tower.render(g);
		}	
	}

	
	@Override
	public void init(GameContainer container) throws SlickException {
		isGameStarted = true;
		
		setBackgroundinit(container);
		darkterrain = new TerrainDarkStage(Stage_x, Stage_y);
		cell = new Store();
		filedbuild = new fieldBuild();
		 doorHeart = new Door((int)32*fieldBuild.sizeRect, 0, Heart_x, Heart_y);
		 entities.add(darkterrain);
		 entities.add(cell);
		 entities.add(filedbuild);
		 entities.add(doorHeart);
		 
	}

	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if(isGameStarted && !isGameOver){
			releaseMonster();
			Timer(delta);
			if(wave >= 1){
				darkstage.setColor((int) 1f, 1f, 1f, changeResolutionBg);
			}
			for (Entity entity : entities) {
			      entity.update(container, delta);
			    }
			for(Monster monster : monsterAll){
				monster.update(container, delta);
			}
			for(TowerDark tower : towerdark){
				tower.update(container, delta);
			}
			death();
			for (int i =0; i < monsterAll.size(); i++) {
				Monster temp = monsterAll.get(i);
				for(TowerDark tower : towerdark){
					tower.rangeCheck(temp,i);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		try {
		      TowerDefenseGame game = new TowerDefenseGame("TowerDefenseGame");
		      AppGameContainer appgc = new AppGameContainer(game);
		      appgc.setDisplayMode(Screen_Width, Screen_Height ,false);
		      appgc.start();
		    } catch (SlickException e) {
		      e.printStackTrace();
		    }
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy){
		//mouse drag on cell
		Store.checkMouseInCell(newx, newy);
		
		//mouse drag on buildField
		if(checkMouseClickCell){
				fieldBuild.checkCol_mouseXRectX = fieldBuild.checkMouseMoveX(newx-mouseError);
				fieldBuild.checkCol_mouseXRectY = fieldBuild.checkMouseMoveY(newy+mouseError/3);
		}
		else{
			fieldBuild.checkCol_mouseXRectX = -1;
			fieldBuild.checkCol_mouseXRectY = -1;
		}
	}
	
	
	@Override
	public void mousePressed(int button, int x, int y){
		mouseClickBuyItemShop(button, x, y);
	}
}
