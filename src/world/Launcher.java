package world;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Launcher {
	public static void main(String[] args) {
		GameM arena = new GameM();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1600;
		config.height = 900;
		LwjglApplication launcher = new LwjglApplication(arena, config);
	}

}
