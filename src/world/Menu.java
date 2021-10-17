package world;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;

import objects.Terrain;

public class Menu implements Screen{
	
	private Stage mainStage;
	private Stage stage;
	private ShapeRenderer shape;
	
	private Terrain background;
	private Terrain startgame;
	private Terrain exit;
	
	Game game;
	
	public Menu(Game game) {
		this.game = game;
		
		//calls the create method to intialize the  menu
		create();
	}
	public void create() {
		mainStage = new Stage();
		stage = new Stage();
		
		background = new Terrain();
	 	background.setTexture(new Texture(Gdx.files.internal("assets/Untitled-2.png")));
	 	background.setPosition(0, 0);
	 	mainStage.addActor(background);
	 	
	 	startgame = new Terrain();
	 	startgame.setPosition(650, 400);
	 	stage.addActor(startgame);
	 	
	 	exit = new Terrain();
	 	exit.setPosition(650, 250);
	 	stage.addActor(exit);
		
		shape = new ShapeRenderer();
	}
	
	public void render(float dt) {
		
		mainStage.act(dt);
		Gdx.gl.glClearColor(15, 0, 15, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		shape.begin(ShapeType.Filled);
		

		
		exit.setTexture(new Texture(Gdx.files.internal("assets/whiteexit.png")));
		startgame.setTexture(new Texture(Gdx.files.internal("assets/whitestart.png")));
		
		if(Gdx.input.getX() >= 700 && Gdx.input.getX() <= 900 && Gdx.input.getY() >= 375 && Gdx.input.getY() <= 500) {
			startgame.setTexture(new Texture(Gdx.files.internal("assets/start.png")));
			if(Gdx.input.isTouched())
				game.setScreen(new Arena(game));
		}
		if(Gdx.input.getX() >= 700 && Gdx.input.getX() <= 900 && Gdx.input.getY() >= 550 && Gdx.input.getY() <= 650) {
			exit.setTexture(new Texture(Gdx.files.internal("assets/exit.png")));
			if(Gdx.input.isTouched())
				Gdx.app.exit();
		}
		
		mainStage.draw();
		
		shape.end();
		
		stage.draw();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	
}
