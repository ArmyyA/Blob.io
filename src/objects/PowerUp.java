package objects;
//powerUp object grants human blob 1 of 4 powers
public class PowerUp {
	private float pX;
	private float pY;
	private float size = 30;
	private int Type;
	
	CollisionDetect dtct;
	//constructor of the class
	public PowerUp(float x, float y, int Type) {
		pX = x;
		pY = y;
		this.Type = Type;
		
		dtct = new CollisionDetect(x, y, size, size);
	}
	//return x and y coordinates
	public float getX() {
		return pX;
	}
	public float getY() {
		return pY;
	}
	//returns collision detect object
	public CollisionDetect getCollisionDetect() {
		return dtct;
	}
	//returns its power type
	public int getType() {
		return Type;
	}
}
