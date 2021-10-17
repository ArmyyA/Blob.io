package world;

import com.badlogic.gdx.Game;

public class GameM extends Game{
	
	public void create() {
		Menu menu = new Menu(this);
		setScreen(menu);
	}
}