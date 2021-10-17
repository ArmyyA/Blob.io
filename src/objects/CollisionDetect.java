package objects;
//checks for collision between two objects
public class CollisionDetect {
	
	float x, y;
	float width, height;
	//constructor for this class
	public CollisionDetect(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}
	//moves this object alongside its corresponding object
	public void move(float x, float y) {
		this.x = x;
		this.y = y;
	}
	//changes size of object if corresponding ojbect side changes
	public void sizeChange(float s) {
		width = s;
		height = s;
	}
	//checks for collisions between itself and other object
	public boolean collidesWith(CollisionDetect d) {
		return x < d.x + d.width && y < d.y + d.height && x + width > d.x && y + height > d.y;
	}
}
