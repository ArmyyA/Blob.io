package objects;

//blob class that creates objects that fight each other
public class Blob {
	private float size = 50;

	private float blobX = 0;
	private float blobY = 0;

	public float shotTimer = 30;
	private float speed;
	private float speedX;
	private float speedY;
	public boolean enemy;
	public boolean spreadShot;
	public boolean twinShot;
	public int blobNumber;
	public boolean singleShot;
	Blob follow;
	CollisionDetect dtct;

	// constructor of the blob class
	public Blob(float x, float y, boolean e, float s, int bN) {
		blobX = x;
		blobY = y;
		enemy = e;
		speed = s;
		blobNumber = bN;

		dtct = new CollisionDetect(x, y, (float) size, (float) size);
	}

	// updates the movement of each blob object
	public void update() {
		blobX += speedX;
		blobY += speedY;
		dtct.move(blobX, blobY);
	}

	// sets the x and y speed of each blob
	public void moveRight() {
		if (speedX < speed) {
			speedX += 0.3;
		}
	}

	public void moveLeft() {
		if (speedX > (-1.0 * speed)) {
			speedX += -0.3;
		}
	}

	public void moveDown() {
		if (speedY > (-1.0 * speed)) {
			speedY += -0.3;
		}
	}

	public void moveUp() {
		if (speedY < speed) {
			speedY += 0.3;
		}
	}

	// returs the x or y coordinates of each blob
	public float getX() {
		return blobX;
	}

	public float getY() {
		return blobY;
	}

	// sets the x and y coordinates of each blob object
	public void setX(float s) {
		blobX = s;
		dtct.move(blobX, blobY);
	}

	public void setY(float s) {
		blobY = s;
		dtct.move(blobX, blobY);
	}

	// returns the size of each blob object
	public float getSize() {
		return size;
	}

	// sets size of blob object
	public void setSize(float s) {
		size = s;
		dtct.sizeChange(size);
	}

	// returns speed
	public float getSpeed() {
		return speed;
	}

	// sets speed
	public void setSpeed(float s) {
		speed = s;
	}

	// sets x speed
	public void setSpeedX(float s) {
		speedX = s;
	}

	// sets y speed
	public void setSpeedY(float s) {
		speedY = s;
	}

	// gets x and y spped
	public float getSpeedX() {
		return speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	// gets the detection for collision with another object
	public CollisionDetect getCollisionDetect() {
		return dtct;
	}
}
