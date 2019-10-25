package com.sunirban.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ModelInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import static com.badlogic.gdx.Gdx.*;
import static com.badlogic.gdx.graphics.Color.*;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture gameover;
	Texture nametag;
	ShapeRenderer shaperenderer;

	Texture[] birds;
    int flapstate=0;
    float birdY=0;
    float velocity=0;
    Circle birdcircle;

    int gamestate=0;
    double gravity=1;
    int score=0;
    int scoringtube=0;
    BitmapFont font;

    Texture toptube;
    Texture bottomtube;
    float gap=600;
    float maxtubeoffset;
    Random randomgenerator;
    float tubevelocity=4;
    int numberOftubes=4;
    float[] tubeX=new float[numberOftubes];
    float[] tubeoffset=new float[numberOftubes];
    float distancebtwtubes;
    Rectangle[] toptubesrectangles;
    Rectangle[] bottomtubesrectangles;

	@Override
	public void create () {
		batch = new SpriteBatch();
        background= new Texture("bg.png");
        gameover= new Texture("gameover.png");
        nametag= new Texture("nametag.png");

        shaperenderer= new ShapeRenderer();
        birdcircle=new Circle();
        font=new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        birds =new Texture[2];
        birds[0]=new Texture("bird.png");
        birds[1]= new Texture("bird2.png");

        toptube=new Texture("toptube.png");
        bottomtube=new Texture("bottomtube.png");
        maxtubeoffset= graphics.getHeight()/2-gap/2-100;
        randomgenerator=new Random();
        distancebtwtubes= graphics.getWidth()*3/4;
        toptubesrectangles=new Rectangle[numberOftubes];
        bottomtubesrectangles=new Rectangle[numberOftubes];

        startGame();
	}

	public void startGame(){
        birdY= graphics.getHeight()/2 - birds[0].getHeight()/2;

        for(int i=0;i<numberOftubes;i++){

            tubeoffset[i]=(randomgenerator.nextFloat()-0.5f)*(graphics.getHeight()-1.5f*gap-200);

            tubeX[i]= graphics.getWidth()/2 - toptube.getWidth()/2+ graphics.getWidth()+ i*distancebtwtubes;

            toptubesrectangles[i]=new Rectangle();
            bottomtubesrectangles[i]=new Rectangle();
        }
    }

	@Override
	public void render () {//works continuously when app is processed

        batch.begin();//starts displaying sprite
        batch.draw(background, 0, 0, graphics.getWidth(), graphics.getHeight());//repeatedly displays background

        if(gamestate==1) {

            if(tubeX[scoringtube]< graphics.getWidth()/2){
                score++;
                Gdx.app.log("Score", String.valueOf(score));

                if(scoringtube<numberOftubes-1){
                    scoringtube++;
                }else {
                    scoringtube=0;
                }
            }

            if(input.justTouched()){
                velocity=-22;


            }

            for(int i=0;i<numberOftubes;i++) {

                if(tubeX[i]<-toptube.getWidth()){

                    tubeX[i]+=numberOftubes*distancebtwtubes;
                    tubeoffset[i]=(randomgenerator.nextFloat()-0.5f)*(graphics.getHeight()-1.5f*gap-200);


                }
                else {

                    tubeX[i] -= tubevelocity;

                }
                batch.draw(toptube, tubeX[i], graphics.getHeight() / 2 + gap / 2 + tubeoffset[i]);
                batch.draw(bottomtube, tubeX[i], graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeoffset[i]);

                toptubesrectangles[i]=new Rectangle(tubeX[i], graphics.getHeight() / 2 + gap / 2 + tubeoffset[i],toptube.getWidth(),toptube.getHeight());
                bottomtubesrectangles[i]=new Rectangle(tubeX[i], graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeoffset[i],bottomtube.getWidth(),bottomtube.getHeight());
            }

            if(birdY>0) {
                velocity += gravity;
                birdY -= velocity;
            }else {
                gamestate=2;
            }

        }
        else if(gamestate==0){
            if(input.justTouched()){
                gamestate=1;
            }
        }

        else if(gamestate==2){

            batch.draw(gameover, graphics.getWidth()/2-gameover.getWidth()/2,graphics.getHeight()/2-gameover.getHeight()/2);
            batch.draw(nametag, graphics.getWidth()/2-nametag.getWidth()/2,100);
            if(input.justTouched()){
                gamestate=1;
                startGame();
                scoringtube=0;
                score=0;
                velocity=0;
            }

        }

        if (flapstate == 0) {
            flapstate = 1;
        } else {
            flapstate = 0;
        }


        batch.draw(birds[flapstate], graphics.getWidth() / 2 - birds[flapstate].getWidth() / 2, birdY);
        font.draw(batch,String.valueOf(score),75,200);

        batch.end();

        birdcircle.set(graphics.getWidth()/2,birdY+birds[flapstate].getHeight()/2,birds[flapstate].getWidth()/2);

        //shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shaperenderer.setColor(RED);
        //
        //shaperenderer.circle(birdcircle.x,birdcircle.y,birdcircle.radius);
        for(int i=0;i<numberOftubes;i++) {

            // shaperenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeoffset[i],toptube.getWidth(),toptube.getHeight());
            //shaperenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeoffset[i],bottomtube.getWidth(),bottomtube.getHeight());
            if(Intersector.overlaps(birdcircle,toptubesrectangles[i])||Intersector.overlaps(birdcircle,bottomtubesrectangles[i])){
                Gdx.app.log("collision","yay!");
                gamestate=2;
            }

        }
	    //shaperenderer.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		birds[0].dispose();
        birds[1].dispose();
        toptube.dispose();
        bottomtube.dispose();
        shaperenderer.dispose();
    }
}
