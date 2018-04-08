package es.joseluislaso.bouncingballs;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


class Ball extends BufferedImage {

    private static int sequence = 1;
    private int number;
    private static BufferedImage CLEAR = null;

    private double x;
    private double y;
    private double radius;
    private int speed;
    private Color color;

    /**
     * angle of movement in radians
     */
    private double angle;

    final static int MAX_SPEED = 7;
    final static int MIN_SPEED = 3;

    Ball(Color color, int radius) {
        super(Config.MAX_RADIUS * 2, Config.MAX_RADIUS * 2, BufferedImage.TYPE_INT_ARGB);
        // store calculated radius and speed in the instance
        this.radius = radius;
        this.color = color;
        this.speed = 5;
        number = sequence;
        // increase sequence for the next ball, just to keep them numbered
        sequence++;
        _draw();
    }

    private void _draw() {
        Graphics g = this.getGraphics();
        if (null == CLEAR) {
            CLEAR = new BufferedImage(Config.MAX_RADIUS * 2, Config.MAX_RADIUS * 2, BufferedImage.TYPE_INT_ARGB);
        }
        this.setData(CLEAR.getRaster());
//        // sprite's shape, box model
//        if (Config.DEBUG) {
//            g.setColor(Color.gray);
//            g.drawRect(0, 0, 2 * radius - 1, 2 * radius - 1);
//        }
        // filling the ball
        g.setColor(color);
        int radius = getRadius();
        g.fillArc(0, 0, 2 * radius - 1, 2 * radius - 1, 0, 360);
        // external perimeter
        g.setColor(new Color(0, 0, 0, 80));
        g.drawArc(0, 0, 2 * radius - 1, 2 * radius - 1, 0, 360);
        // shine
        //g.setColor(shinyColor(color));
        g.setColor(color.brighter());
        int width = radius / 2;
        int x = radius + radius / 4;
        int y = radius - (2 * radius / 3);
        g.fillArc(x, y, width, width, 0, 360);
//        // cross in the center of the ball
//        g.setColor(new Color(0, 0, 0, 80));
//        g.drawLine(radius - 5, radius, radius + 5, radius);
//        g.drawLine(radius, radius - 5, radius, radius + 5);
        // ball's label
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics f = g.getFontMetrics();
        String str = "" + getNumber();
        int w = f.stringWidth(str);
        int h = f.getHeight();
        g.drawString(str, radius - (int) (w / 2.5), radius - (int) (h / 2.5));
    }

    private static Color shinyColor(Color color) {

        float factor = 1.11f;
        int r = (int) (factor * color.getRed());
        int g = (int) (factor * color.getGreen());
        int b = (int) (factor * color.getBlue());

        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }

        return new Color(r, g, b);
    }

    int getNumber() {
        return number;
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    void setCoords(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double getCenterX() {
        return x + radius;
    }

    double getCenterY() {
        return y + radius;
    }

    int getRadius() {
        return (int) Math.round(radius);
    }

    private void setRadius(double r) {
        if (r >= Config.MIN_RADIUS && r <= Config.MAX_RADIUS) {
            radius = r;
        }
    }

    double getAngle() {
        return angle;
    }


    void setAngle(double angle) {
        this.angle = Math.toRadians(Math.toDegrees(angle) % 360);
    }

    int getSpeed() {
        return speed;
    }

    void setSpeed(int speed) {
        this.speed = speed;
    }

    void accelerate() {
        if (speed < MAX_SPEED) setSpeed(speed + 1);
    }

    void slowDownWithoutStop() {
        if (speed > MIN_SPEED) setSpeed(speed - 1);
    }

    void slowDown() {
        if (speed > 0) setSpeed(speed - 1);
    }

    void draw(Graphics2D graphic) {
        int xx = (int) Math.round(x);
        int yy = (int) Math.round(y);
        graphic.drawImage(this, xx, yy, null);
    }

    private Point center() {
        int xx = (int) Math.round(x + radius);
        int yy = (int) Math.round(y + radius);
        return new Point(xx, yy);
    }

    private double distance(Ball ball) {
        int xx = (int) Math.round(x + radius);
        int yy = (int) Math.round(y + radius);
        Point2D center1 = new Point(xx, yy);
        return center1.distance(ball.center());
    }

    double collisionAngle(Ball ball) {
        double theAngle = getAngle();
        double dx = getX() - ball.getX();
        double dy = getY() - ball.getY();
        double atan2 = Math.atan2(dx, dy);
        setAngle(atan2 + getAngle());
        return theAngle;
    }

    void interchangeOfMater(Ball ball) {
        setRadius(radius-0.1);
        _draw();
        ball.setRadius(ball.getRadius()+0.1);
        ball._draw();
    }

    boolean amIoutOf(int x1, int y1, int x2, int y2) {
        return (x < x1) || (x + 2 * radius > x2) || (y < y1) || (y + 2 * radius > y2);
    }

    void move() {
        int dist = getSpeed();
        double xx = getX() + (Math.cos(angle) * dist);
        double yy = getY() + (Math.sin(angle) * dist);
        setCoords(xx, yy);
    }

    /**
     * returns  0 if the current ball is in contact with passed ball
     * returns <0 if the balls are overlapped
     * returns >0 if the balls are not touching neither overlapping
     */
    double overlappingDistance(Ball ball) {
        return (distance(ball) - (radius + ball.getRadius()));
    }
}
