package world;

import objects.CollisionDetect;

//creates a particle object that blobs can consume to increase size
public class Particles {
	private int pX;
	private int pY;
	private int size = 10;
	public int colour;

	CollisionDetect dtct;

	// constructor for this class
	public Particles(int x, int y, int c) {
		pX = x;
		pY = y;
		colour = c;

		dtct = new CollisionDetect(x, y, size, size);
	}

	// returs x and y coordinates of this object
	public int getX() {
		return pX;
	}

	public int getY() {
		return pY;
	}

	// returns its collisionsdetect object
	public CollisionDetect getCollisionDetect() {
		return dtct;
	}
}
