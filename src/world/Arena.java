package world;

import java.util.ArrayList;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import objects.Blob;
import objects.PowerUp;

//Arena class is responsible for adding the games objects, centering the camera on the player, calling player and enemy moves, generating particles
//generating powerups, checking for collisions, ect.
//basically main class where everything happens
public class Arena implements Screen {

	private OrthographicCamera cam;
	BitmapFont size;
	BitmapFont enemLeft;
	BitmapFont leaderboard;
	private int numberOfEnemies = 50;
	private int numberOfEnemiesLeft = 1;
	private static SpriteBatch batch;
	private static SpriteBatch hudBatch;
	private Texture eBlobSkin;
	private Texture mapTexture;
	private Texture particleTexture;
	private Texture bulletT;
	private Texture W;
	private Texture L;
	Sprite blobSk;
	int randX;
	int randY;
	int enemyNumber = 0;
	int playerShotTimer = 5;
	private Texture blobSkin;
	int particlesLeft = 0;
	int particlesAdded = 0;
	int powerUpNumber = 0;
	public static float mapSize = 8000;
	private int mapTimer = 5;
	private int enemiesPopped = 0;
	float timeElapsed;
	final int mapWidth = 1600;
	final int mapHeight = 900;
	final int viewWidth = 1600;
	final int viewHeight = 900;
	int rockint = 0;
	int i = 0;

	ArrayList<Particles> particles;
	ArrayList<Bullet> bullets;
	ArrayList<Blob> blob;
	ArrayList<PowerUp> powerUps;

	Random rand = new Random();
	private Blob b1 = new Blob((float) rand.nextInt(8000), (float) rand.nextInt(8000), false, (float) 4.0, 0);
	Blob follow = b1;

	Game game;

	public Arena(Game g) {
		game = g;
		create();
	}

	// creates the worlds objects at the start of the game and creates the array lists that are needed and also creates iamges of the objects as textures
	public void create() {
		cam = new OrthographicCamera(30, 30);
		cam.zoom = 10;

		// cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		resize(1, 1);
		cam.update();
		// handleInput();

		batch = new SpriteBatch();
		hudBatch = new SpriteBatch();
		size = new BitmapFont();
		enemLeft = new BitmapFont();
		leaderboard = new BitmapFont();

		bullets = new ArrayList<Bullet>();
		particles = new ArrayList<Particles>();
		blob = new ArrayList<Blob>();
		powerUps = new ArrayList<PowerUp>();

		blobSkin = new Texture(Gdx.files.internal("assets/PlayerSkin.png"));

		eBlobSkin = new Texture(Gdx.files.internal("assets/agarioskin.png"));

		bulletT = new Texture("assets/Blullet.png");
		particleTexture = new Texture(Gdx.files.internal("assets/particles.png"));
		mapTexture = new Texture(Gdx.files.internal("assets/background.jpg"));
		L = new Texture(Gdx.files.internal("assets/L.png"));
		W = new Texture(Gdx.files.internal("assets/W.jpg"));

		Blob follow;

		b1.singleShot = true;
		enemyGenerator();
		powerUpGenerator();
		b1.setSize(70);
	}

	// renders enemy and player movements, collisions, draws objects, draws hud,
	// ect.
	public void render(float dt) {

		playerMove();
		for (Blob b : blob) {
			if (b.enemy) {
				enemyMove(b);
			}
		}
		collisions();
		cam.position.set((float) b1.getX() + b1.getSize() / 2, (float) b1.getY() + b1.getSize() / 2, 0);
		resize(1, 1);
		batch.setProjectionMatrix(cam.combined);

		particleGenerator();

		// clear screen and draw graphics
		Gdx.gl.glClearColor(255 / 255f, 204 / 255f, 203 / 255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// draws all object images
		batch.begin();
		batch.draw(mapTexture, 0, 0, mapSize, mapSize);
		if (mapTimer == 0) {
			mapSize--;
			mapTimer = 5;
		}
		if (mapTimer > 0) {
			mapTimer--;
		}
		for (Particles p : particles) {
			if (p.colour == 1) {
				batch.draw(particleTexture, p.getX(), p.getY(), 10, 10);
			} else if (p.colour == 2) {
				batch.draw(bulletT, p.getX(), p.getY(), 11, 11);
			} else if (p.colour == 3) {
				batch.draw(eBlobSkin, p.getX(), p.getY(), 10, 10);
			} else if (p.colour == 4) {
				batch.draw(blobSkin, p.getX(), p.getY(), 10, 10);
			}
		}
		for (Bullet b : bullets) {
			batch.draw(bulletT, b.getX(), b.getY(), 15, 15);
		}
		for (PowerUp p : powerUps) {
			batch.draw(particleTexture, p.getX(), p.getY(), 50, 50);
		}
		for (Blob eB : blob) {
			if (eB.enemy)
				batch.draw(eBlobSkin, eB.getX(), eB.getY(), eB.getSize(), eB.getSize());
		}
		batch.draw(blobSkin, b1.getX(), b1.getY(), b1.getSize(), b1.getSize());
		textDisplay();

		batch.end();

		// draws hud at the top right screen
		hudBatch.begin();
		hud();
		if (b1.getSize() < 30) {
			hudBatch.draw(L, 0, 0, 1600, 900);
		}
		if (numberOfEnemiesLeft == 0) {
			hudBatch.draw(W, 0, 0, 1600, 900);
		}
		hudBatch.end();
	}

	// gets the enemy move
	// move can be dodging, shooting, or following and even a combination of all
	// three
	public void enemyMove(Blob eB) {
		// checks if a bullet is too close and dodges it
		for (Bullet b : bullets) {
			if (b.bulletNumber != eB.blobNumber) {
				if (b.getSpeedX() == 0) {
					if (b.getX() > eB.getX() && Math.abs(b.getY() - eB.getY()) > -eB.getSize() * 2
							&& Math.abs(b.getY() - eB.getY()) < eB.getSize() * 2) {
						eB.moveLeft();
					} else if (b.getX() < eB.getX() && Math.abs(b.getY() - eB.getY()) > -eB.getSize() * 2
							&& Math.abs(b.getY() - eB.getY()) < eB.getSize() * 2) {
						eB.moveRight();
					}
				} else if (b.getSpeedY() == 0) {
					if (b.getY() < eB.getY() && Math.abs(b.getX() - eB.getX()) > -eB.getSize() * 2
							&& Math.abs(b.getX() - eB.getX()) < eB.getSize() * 2) {
						eB.moveUp();
					} else if (b.getY() > eB.getY() && Math.abs(b.getX() - eB.getX()) > -eB.getSize() * 2
							&& Math.abs(b.getX() - eB.getX()) < eB.getSize() * 2) {
						eB.moveDown();
					}
				}
			}
		}
		// finds the closest enemy
		float shortestDist = 1000;
		if (numberOfEnemiesLeft > 2) {
			for (Blob EB : blob) {
				float dist = (Math.abs(eB.getX() - EB.getX()) + Math.abs(eB.getY() - EB.getY()));

				if (dist < shortestDist && eB.blobNumber != EB.blobNumber) {
					shortestDist = dist;
					follow = EB;
				}

			}
		}
		if (numberOfEnemiesLeft <= 2) {
			follow = b1;
		}
		// shoots blob if in its range
		if (eB.shotTimer == 0 && eB.getSize() > 30) {
			if (eB.getX() < follow.getX()
					&& (eB.getY() + eB.getSize() / 2) - (follow.getY() + follow.getSize() / 2) < 30
					&& (eB.getY() + eB.getSize() / 2) - (follow.getY() + follow.getSize() / 2) > -30) {
				if (b1.getSize() > 329) {
					bullets.add(new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, 8, 1,
							eB.blobNumber));
					bullets.add(new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, 8, -1,
							eB.blobNumber));
				}
				bullets.add(
						new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, 8, 0, eB.blobNumber));
				eB.setSpeed((float) (eB.getSpeed() + 0.005));
				eB.setSize(eB.getSize() - 1);
			}
			if (eB.getX() > follow.getX()
					&& (eB.getY() + eB.getSize() / 2) - (follow.getY() + follow.getSize() / 2) < 30
					&& (eB.getY() + eB.getSize() / 2) - (follow.getY() + follow.getSize() / 2) > -30) {
				if (b1.getSize() > 329) {
					bullets.add(new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, -8, 1,
							eB.blobNumber));
					bullets.add(new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, -8, -1,
							eB.blobNumber));
				}
				bullets.add(
						new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, -8, 0, eB.blobNumber));
				eB.setSpeed((float) (eB.getSpeed() + 0.005));
				eB.setSize(eB.getSize() - 1);
			}
			if (eB.getY() < follow.getY()
					&& (eB.getX() + eB.getSize() / 2) - (follow.getX() + follow.getSize() / 2) < 30
					&& (eB.getX() + eB.getSize() / 2) - (follow.getX() + follow.getSize() / 2) > -30) {
				if (b1.getSize() > 329) {
					bullets.add(new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, 1, 8,
							eB.blobNumber));
					bullets.add(new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, -1, 8,
							eB.blobNumber));
				}
				bullets.add(
						new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, 0, 8, eB.blobNumber));
				eB.setSpeed((float) (eB.getSpeed() + 0.005));
				eB.setSize(eB.getSize() - 1);
			}
			if (eB.getY() > follow.getY()
					&& (eB.getX() + eB.getSize() / 2) - (follow.getX() + follow.getSize() / 2) < 30
					&& (eB.getX() + eB.getSize() / 2) - (follow.getX() + follow.getSize() / 2) > -30) {
				if (eB.getSize() > 329) {
					bullets.add(new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, 1, -8,
							eB.blobNumber));
					bullets.add(new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, -1, -8,
							eB.blobNumber));
				}
				bullets.add(
						new Bullet(eB.getX() + eB.getSize() / 2, eB.getY() + eB.getSize() / 2, 0, -8, eB.blobNumber));
				eB.setSpeed((float) (eB.getSpeed() + 0.005));
				eB.setSize(eB.getSize() - 1);
			}
			if (eB.getSize() < 130) {
				eB.shotTimer = 20;
			}
			if (eB.getSize() > 129 && eB.getSize() < 230) {
				eB.shotTimer = 8;
			}
			if (eB.getSize() > 229 && eB.getSize() < 330) {
				eB.shotTimer = 5;
			}
			if (eB.getSize() > 329) {
				eB.shotTimer = 3;
			}
		}

		// moves this blob closer to the blob it is targeting/following based of x and y
		// coordinates
		if (eB.shotTimer > 0) {
			eB.shotTimer--;
		}
		if (((follow.getX() + follow.getSize() / 2) - (eB.getX() + follow.getSize() / 2)) > 0) {
			eB.moveRight();
		}
		if (((follow.getX() + follow.getSize() / 2) - eB.getX()) < 0) {
			eB.moveLeft();
		}
		if (((follow.getY() + follow.getSize() / 2) - (eB.getY() + eB.getSize() / 2)) < 0) {
			eB.moveDown();
		}
		if (((follow.getY() + follow.getSize() / 2) - (eB.getY() + eB.getSize() / 2)) > 0) {
			eB.moveUp();
		}
		if (eB.getX() < 0) {
			eB.setX(rand.nextInt((int) mapSize));
		}
		if (eB.getX() > mapSize) {
			eB.setX(rand.nextInt((int) mapSize));
		}
		if (eB.getY() < 0) {
			eB.setY(rand.nextInt((int) mapSize));
		}
		if (eB.getY() > mapSize) {
			eB.setY(rand.nextInt((int) mapSize));
		}
		eB.update();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	// resizes the camera as player size increases and decreases
	public void resize(int arg0, int arg1) {
		if (cam.zoom < 110 && cam.zoom < b1.getSize() / 4) {
			cam.zoom += 0.5;
		}
		if (cam.zoom > b1.getSize() / 4 && cam.zoom > 15) {
			cam.zoom += -0.5;
		}
		cam.update();
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
	}

	// gets the players movement
	public void playerMove() {
		// if player meets criteria, player can shoot a bullet
		if (b1.getSize() > 20) {
			if (playerShotTimer == 0) {
				if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyJustPressed(Keys.W)) {
					if (b1.singleShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 0, 10,
								b1.blobNumber));
					}
					if (b1.twinShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2 + 10, b1.getY() + b1.getSize() / 2, 0, 50,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2 - 10, b1.getY() + b1.getSize() / 2, 0, 50,
								b1.blobNumber));
					}
					if (b1.spreadShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 0, 8,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 1, 8,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, -1, 8,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 2, 8,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, -2, 8,
								b1.blobNumber));

					}
					b1.setSize(b1.getSize() - 1);
					b1.setSpeed((float) (b1.getSpeed() + 0.005));
				}
				if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyJustPressed(Keys.D)) {
					if (b1.singleShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 10, 0,
								b1.blobNumber));
					}
					if (b1.twinShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2 + 10, 50, 0,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2 - 10, 50, 0,
								b1.blobNumber));
					}
					if (b1.spreadShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 8, 0,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 8, 1,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 8, -1,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 8, 2,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 8, -2,
								b1.blobNumber));
					}
					b1.setSize(b1.getSize() - 1);
					b1.setSpeed((float) (b1.getSpeed() + 0.005));
				}
				if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyJustPressed(Keys.A)) {
					if (b1.singleShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, -10, 0,
								b1.blobNumber));
					}
					if (b1.twinShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2 + 10, -50, 0,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2 - 10, -50, 0,
								b1.blobNumber));
					}
					if (b1.spreadShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, -8, 0,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, -8, 1,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, -8, -1,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, -8, 2,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, -8, -2,
								b1.blobNumber));
					}
					b1.setSize(b1.getSize() - 1);
					b1.setSpeed((float) (b1.getSpeed() + 0.005));
				}
				if (Gdx.input.isKeyJustPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.S)) {
					if (b1.singleShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 0, -10,
								b1.blobNumber));
					}
					if (b1.twinShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2 + 10, b1.getY() + b1.getSize() / 2, 0, -50,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2 - 10, b1.getY() + b1.getSize() / 2, 0, -50,
								b1.blobNumber));
					}
					if (b1.spreadShot == true) {
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 0, -8,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 1, -8,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, -1, -8,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, 2, -8,
								b1.blobNumber));
						bullets.add(new Bullet(b1.getX() + b1.getSize() / 2, b1.getY() + b1.getSize() / 2, -2, -8,
								b1.blobNumber));

					}
					b1.setSize(b1.getSize() - 1);
					b1.setSpeed((float) (b1.getSpeed() + 0.005));
				}
				if (b1.getSize() < 130) {
					playerShotTimer = 15;
				} else if (b1.getSize() > 129 && b1.getSize() < 230) {
					playerShotTimer = 12;
				} else if (b1.getSize() > 229 && b1.getSize() < 330) {
					playerShotTimer = 9;
				} else if (b1.getSize() > 329 && b1.getSize() < 430) {
					playerShotTimer = 5;
				} else if (b1.getSize() > 429) {
					playerShotTimer = 2;
				}
			}
		}
		// decreases playerShotTimer
		if (playerShotTimer > 0) {
			playerShotTimer--;
		}
		// gets the players movement
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			b1.moveRight();
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			b1.moveLeft();
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			b1.moveDown();
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			b1.moveUp();
		}
		if (b1.getX() < 0) {
			b1.setX(rand.nextInt((int) mapSize));
		}
		if (b1.getX() + b1.getSize() > mapSize) {
			b1.setX(rand.nextInt((int) mapSize));
		}
		if (b1.getY() < 0) {
			b1.setY(rand.nextInt((int) mapSize));
		}
		if (b1.getY() + b1.getSize() > mapSize) {
			b1.setY(rand.nextInt((int) mapSize));
		}
		b1.update();
	}

	// generates particles if they do not meet the given criteria
	public void particleGenerator() {
		while (particlesAdded < 7000) {
			particles.add(new Particles(rand.nextInt(8000), rand.nextInt(8000), i));
			particlesAdded++;
			particlesLeft = particlesAdded;
			if (i == 4) {
				i = 0;
			}
			i++;
		}

		while (particlesLeft < mapSize * 0.5) {
			particles.add(new Particles(rand.nextInt((int) mapSize), rand.nextInt((int) mapSize), i));
			particlesLeft++;
			if (i == 4) {
				i = 0;
			}
			i++;
		}

	}
	// generates enemies at the start of the game inside the range of the map
	public void enemyGenerator() {
		while (enemyNumber < numberOfEnemies) {
			blob.add(new Blob(rand.nextInt(8000), rand.nextInt(8000), true, 2, enemyNumber + 1));
			blob.add(b1);
			enemyNumber++;
		}
	}

	// generates four different powerups at the start of the game
	public void powerUpGenerator() {
		while (powerUpNumber < 1) {
			powerUps.add(new PowerUp(rand.nextInt(8000), rand.nextInt(8000), 1));
			powerUps.add(new PowerUp(rand.nextInt(8000), rand.nextInt(8000), 2));
			powerUps.add(new PowerUp(rand.nextInt(8000), rand.nextInt(8000), 3));
			powerUps.add(new PowerUp(rand.nextInt(8000), rand.nextInt(8000), 4));
			powerUpNumber++;
		}
	}

	// checks for collisons between blobs and particles or blobs and bullets
	public void collisions() {
		ArrayList<Bullet> removeBullets = new ArrayList<Bullet>();
		ArrayList<Blob> removeBlob = new ArrayList<Blob>();
		// checks for collisions of blobs with particles
		for (Bullet b : bullets) {
			b.update();
			if (b.remove) {
				removeBullets.add(b);
			}
			if (b1.blobNumber != b.bulletNumber && b.getCollisionDetect().collidesWith(b1.getCollisionDetect())) {
				removeBullets.add(b);
				b1.setSize(b1.getSize() - 4);
				b1.setSpeed((float) (b1.getSpeed() + 0.02));
			}
			for (Blob eB : blob) {
				if (eB.blobNumber != b.bulletNumber && b.getCollisionDetect().collidesWith(eB.getCollisionDetect())
						&& eB.enemy) {
					removeBullets.add(b);
					eB.setSize(eB.getSize() - 2);
					eB.setSpeed((float) (b1.getSpeed() + 0.01));
					if (eB.getSize() < 31) {
						removeBlob.add(eB);
						if (b.bulletNumber == 0) {
							b1.setSize(b1.getSize() + 50);
							enemiesPopped++;
						}
						for (Blob bb : blob) {
							if (b.bulletNumber == bb.blobNumber && bb.enemy) {
								bb.setSize(bb.getSize() + 50);
							}
						}

					}
				}
			}
		}
		bullets.removeAll(removeBullets);
		blob.removeAll(removeBlob);

		ArrayList<Particles> removeParticles = new ArrayList<Particles>();
		// checks of blobs with particles
		for (Particles p : particles) {
			if (p.getCollisionDetect().collidesWith(b1.getCollisionDetect()) && b1.getSize() < 1030) {
				removeParticles.add(p);
				if (b1.getSize() < 500) {
					b1.setSize((float) (b1.getSize() + 0.5));
					if (b1.getSpeed() > 0.75) {
						b1.setSpeed((float) (b1.getSpeed() - 0.00125));
					}
				}
				if (b1.getSize() > 499) {
					b1.setSize((float) (b1.getSize() + 0.25));
					if (b1.getSpeed() > 0.75) {
						b1.setSpeed((float) (b1.getSpeed() - 0.000625));
					}
				}
				particlesLeft--;
			}
			for (Blob B : blob) {
				if (p.getCollisionDetect().collidesWith(B.getCollisionDetect()) && B.enemy && B.getSize() < 1030) {
					removeParticles.add(p);
					if (B.getSize() < 500) {
						B.setSize((float) (B.getSize() + 1));
						if (B.getSpeed() > 0.5) {
							B.setSpeed((float) (B.getSpeed() - 0.01));
						}
					}
					if (B.getSize() > 499) {
						B.setSize((float) (B.getSize() + 0.25));
						if (B.getSpeed() > 0.5) {
							B.setSpeed((float) (B.getSpeed() - 0.0025));
						}
					}
					particlesLeft--;
				}
			}
		}
		particles.removeAll(removeParticles);

		// checks for collision of human with powerups
		ArrayList<PowerUp> removePowerUps = new ArrayList<PowerUp>();
		for (PowerUp p : powerUps) {
			if (p.getCollisionDetect().collidesWith(b1.getCollisionDetect())) {
				removePowerUps.add(p);
				if (p.getType() == 1) {
					b1.setSize(b1.getSize() + 100);
				}
				if (p.getType() == 2) {
					b1.setSpeed((float) (b1.getSpeed() + 2));
				}
				if (p.getType() == 3) {
					b1.spreadShot = true;
					b1.singleShot = false;
					b1.twinShot = false;
				}
				if (p.getType() == 4) {
					b1.twinShot = true;
					b1.singleShot = false;
					b1.spreadShot = false;
				}
			}
		}
		powerUps.removeAll(removePowerUps);
	}

	// displays the blobs size
	public void textDisplay() {
		CharSequence bHealth = String.valueOf(b1.getSize() - 30);
		size.getData().setScale(b1.getSize() / 100);
		size.setUseIntegerPositions(false);
		size.draw(batch, bHealth, b1.getX() + b1.getSize() / 3 + b1.getSize() / 55,
				b1.getY() + b1.getSize() / 2 + b1.getSize() / 30);

		for (Blob eB : blob) {
			if (eB.enemy) {
				size.getData().setScale(eB.getSize() / 80);
				CharSequence eHealth = String.valueOf(eB.getSize() - 30);
				size.draw(batch, eHealth, eB.getX() + eB.getSize() / 3,
						eB.getY() + eB.getSize() / 2 + eB.getSize() / 30);
			}
		}
	}

	// displays the number of enemies left, the highest sized enemy, its size and
	// number of enemies destroyed
	public void hud() {
		numberOfEnemiesLeft = 0;
		Blob leaderBlob = b1;
		int biggestSize = 0;
		for (Blob b : blob) {
			int temp = 0;
			if (b.enemy) {
				numberOfEnemiesLeft++;
			}
			if (b.getSize() > biggestSize) {
				biggestSize = (int) b.getSize();
				leaderBlob = b;
			}
		}
		if (leaderBlob.getSize() < b1.getSize()) {
			leaderBlob = b1;
		}

		CharSequence enemiesLeft = "Enemies Left: " + String.valueOf(numberOfEnemiesLeft);
		enemLeft.setUseIntegerPositions(false);
		enemLeft.setColor(Color.BLACK);
		enemLeft.getData().setScale((float) 1.8);
		enemLeft.draw(hudBatch, enemiesLeft, 1290, 830);

		CharSequence leader = null;
		CharSequence size = null;
		CharSequence popped = "Enemies Popped: " + String.valueOf(enemiesPopped);
		if (leaderBlob.blobNumber != 0) {
			leader = "Leader: Enemy Blob " + String.valueOf(leaderBlob.blobNumber);
			size = "Size: " + String.valueOf(leaderBlob.getSize() - 30);

		}
		if (leaderBlob.blobNumber == 0) {
			leader = "Leader: Human Blob";
			size = "Size: " + String.valueOf(b1.getSize() - 30);
		}
		leaderboard.getData().setScale((float) 1.6);

		leaderboard.setColor(Color.BLUE);
		leaderboard.draw(hudBatch, leader, 1290, 795);
		leaderboard.draw(hudBatch, size, 1290, 760);
		enemLeft.draw(hudBatch, popped, 20, 830);

	}

}