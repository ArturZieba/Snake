package siwy;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    //Variables
    static int speed = 5;
    static int pointcolor = 0;
    static int width = 20;
    static int height = 20;
    static int pointX = 0;
    static int pointY = 0;
    static int cornersize = 25;
    static List<Corner> snake = new ArrayList<>();
    static Dir direction = Dir.left;
    static boolean gameOver = false;
    static Random rand = new Random();

    public enum Dir{
        up,down,left,right
    }

    public static class Corner{
        int x;
        int y;

        public Corner(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    public void start(Stage primaryStage){
        try{
            newPoint();

            VBox root = new VBox();
            Canvas c = new Canvas(width*cornersize, height*cornersize);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer(){
                long lastTick = 0;

                public void handle(long now){
                    if (lastTick == 0){
                        lastTick = now;
                        tick(gc);
                        return;
                    }

                    if(now - lastTick > 1000000000 / speed){
                        lastTick = now;
                        tick(gc);
                    }
                }
            }.start();

            Scene scene = new Scene(root, width*cornersize, height*cornersize);

            //Control
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if(key.getCode() == KeyCode.W){
                    direction = Dir.up;
                }
                if(key.getCode() == KeyCode.A){
                    direction = Dir.left;
                }
                if(key.getCode() == KeyCode.S){
                    direction = Dir.down;
                }
                if(key.getCode() == KeyCode.D){
                    direction = Dir.right;
                }
            });

            //Add start snake
            snake.add(new Corner(width/2, height/2));
            snake.add(new Corner(width/2, height/2));
            snake.add(new Corner(width/2, height/2));

            primaryStage.setScene(scene);
            primaryStage.setTitle("Snake by Siwy");
            primaryStage.show();
        }catch (Exception e){}
    }

    //Tick
    public static void tick(GraphicsContext gc){
        if(gameOver){
            gc.setFill(Color.DARKRED);
            gc.setFont(new Font("",60));
            gc.fillText("GAME OVER", 100, 250);
            return;
        }

        for (int i = snake.size() - 1; i >= 1; i--){
            snake.get(i).x = snake.get(i-1).x;
            snake.get(i).y = snake.get(i-1).y;
        }

        switch (direction){
            case up:
                snake.get(0).y--;
                if (snake.get(0).y < 0){
                    gameOver = true;
                }
                break;

            case down:
                snake.get(0).y++;
                if (snake.get(0).y > height){
                    gameOver = true;
                }
                break;

            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0){
                    gameOver = true;
                }
                break;

            case right:
                snake.get(0).x++;
                if (snake.get(0).x > width){
                    gameOver = true;
                }
                break;
        }

        //Get points
        if(pointX == snake.get(0).x && pointY == snake.get(0).y){
            snake.add(new Corner(-1,-1));
            newPoint();
        }

        //Self-destruct
        for (int i = 1; i < snake.size(); i++){
            if(snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y){
                gameOver = true;
            }
        }

        //Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0, width*cornersize, height*cornersize);

        //Score
        gc.setFill(Color.LIGHTGREEN);
        gc.setFont(new Font("",25));
        gc.fillText("Score: " + (speed - 6), 10, 30);

        //Random point color
        Color cc = Color.WHITE;

        switch (pointcolor){
            case 0:
                cc = Color.DARKGREEN;
                break;
            case 1:
                cc = Color.LIGHTGREEN;
                break;
            case 2:
                cc = Color.SEAGREEN;
                break;
            case 3:
                cc = Color.NAVAJOWHITE;
                break;
            case 4:
                cc = Color.CRIMSON;
                break;
        }
        gc.setFill(cc);
        gc.fillOval(pointX*cornersize, pointY*cornersize, cornersize, cornersize);

        //Snake
        for(Corner c : snake){
            gc.setFill(Color.GREEN);
            gc.fillRect(c.x*cornersize, c.y*cornersize, cornersize-1, cornersize-1);
            gc.setFill(Color.DARKGREEN);
            gc.fillRect(c.x*cornersize, c.y*cornersize, cornersize-2, cornersize-2);
        }
    }


    //Points
    public static void newPoint(){
        start: while (true){
            pointX = rand.nextInt(width);
            pointY = rand.nextInt(height);

            for (Corner c : snake){
                if (c.x == pointX && c.y == pointY){
                    continue start;
                }
            }
            pointcolor = rand.nextInt(5);
            speed++;
            break;
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}