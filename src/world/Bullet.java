package world;

import objects.CollisionDetect;

//bullet class is used as an object that blobs can shoot
public class Bullet {
	public float speedX = 0;
	public float speedY = 0;
	public float bulletNumber;
	
	private float x, y;
	private float width, height = 12;
	CollisionDetect dtct;
	
	public boolean remove;
	//constructor of the bullet class
	public Bullet(float x, float y, float speedX, float speedY, float bN) {
		this.x = x;
		this.y = y;
		
		this.speedX = speedX;
		this.speedY = speedY;
		
		bulletNumber = bN;
		
		dtct = new CollisionDetect(x, y, width, height);
		
	}
	//updates the bullets x and y coordinates
	public void update () {
		x += speedX;
		y += speedY;
		
		if (x > 8000 || x < 0 || y > 8000 || y < 0) {
			remove = true;
		}
		dtct.move(x,y);
	}
	//sets the bullets x and y values
	public void setX(float a) {
		x += a;
	}
	public void setY(float b) {
		y += b;
	}
	//returns the bullets x and y values
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	//gets speed x or speed y
	public float getSpeedX() {
		return speedX;
	}
	public float getSpeedY() {
		return speedY;
	}
	//returs the bullets collision detect object
	public CollisionDetect getCollisionDetect() {
		return dtct;
	}
	
}