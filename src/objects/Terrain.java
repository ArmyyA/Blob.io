package objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Terrain extends Actor{
	
	public TextureRegion region;
	public Rectangle boundary;
	public float velocityX;
	public float velocityY;
	public float elapsedTime;
	public float health = 100;
	
	public Terrain() {
		super();
		region = new TextureRegion();
		boundary = new Rectangle();
		velocityX = 0;
		velocityY = 0;
	}
	
	public void setTexture(Texture t) {
		int w = t.getWidth();
		int h = t.getHeight();
		setWidth(w);
		setHeight(h);
		region.setRegion(t);
	}
	
	public void act(float dt) {
		super.act(dt);
		moveBy(velocityX*dt, velocityY * dt);
		
	}
	
    public Rectangle getBoundingRectangle()
    {
        boundary.set( getX(), getY(), getWidth(), getHeight() );
        return boundary;
    }

	public void draw(Batch batch, float parentAlpha) {
		Color c = getColor();
		batch.setColor(c.a, c.b, c.g, c.r);
		if (isVisible())
			batch.draw( region, getX(), getY(), getOriginX(), getOriginY(),
	                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation() );
	}
}
