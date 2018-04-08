
package es.joseluislaso.bouncingballs;

import java.awt.Color;

public class Program {

    public static void main(String[] args) {

        System.out.println(Config.TITLE + " started.");

        Motor motor = Motor.getInstance();

        int numballs = 10;
        Ball[] ball = new Ball[numballs];

        Color[] color = {
                Color.orange, Color.gray, Color.cyan,
                Color.green, Color.lightGray, Color.yellow,
                Color.magenta, Color.pink, Color.red,
                Color.white, Color.blue, Color.darkGray,
        };

        int dx = 65;
        int dy = 65;
        double y = Config.MARGIN + 15;
        double x = Config.MARGIN + 15;
        // now generating the random balls
        for (int i = 0; i < numballs; i++) {
            int radius = Config.MIN_RADIUS + ((int) (Math.random() * (Config.MAX_RADIUS - Config.MIN_RADIUS)));
            ball[i] = new Ball(color[i % color.length], radius);
            ball[i].setCoords(x, y);
            x += dx;
            if (x > Config.WIDTH_E - dx) {
                x = Config.MARGIN + 15;
                y += dy;
            }
            ball[i].setAngle(Math.toRadians(45 + (int) (Math.random() * 90)));
            ball[i].setSpeed(Ball.MIN_SPEED + (int) (Math.random() * (Ball.MAX_SPEED - Ball.MIN_SPEED)));
            motor.addBall(ball[i]);
        }

        motor.run();
    }
}

