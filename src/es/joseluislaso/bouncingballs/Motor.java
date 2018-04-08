package es.joseluislaso.bouncingballs;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.*;

class Motor extends JFrame implements Runnable {

    private static final long serialVersionUID = -4924123182844968930L;
    private static Motor motorSingleton;
    private final static int NO_CHANGE = 9999;

    /**
     * Factory method to get the canvas singleton object.
     */
    static Motor getInstance() {
        if (motorSingleton == null) {
            motorSingleton = new Motor(Config.TITLE, Config.WIDTH, Config.HEIGHT);
        }
        motorSingleton.setVisible(true);
        return motorSingleton;
    }

    // instance part
    private JFrame frame;
    private CanvasPane canvas;
    private Graphics2D graphic;
    private Color backgroundColor;
    private Image canvasImage;
    private ArrayList<Ball> balls;
    private int t;
    private int dir;
    private boolean move = true;
    private boolean stopby = true;
    private boolean debug = Config.DEBUG;
    private boolean process = true;
    private int frameDuration = Config.FRAME;
    private int transparency = 150;

    private Motor(String title, int aWidth, int height) {
        frame = new JFrame();
        canvas = new CanvasPane();
        frame.setContentPane(canvas);
        frame.setTitle(title);

        canvas.setPreferredSize(new Dimension(aWidth, height));
        setBackgroundColor();
        frame.pack();
        balls = new ArrayList<>();
        addKeyListener(new Listen());
        setVisible(true);
    }

    /**
     * Set the canvas visibility and brings canvas to the front of screen
     * when made visible. This method can also be used to bring an already
     * visible canvas to the front of other windows.
     *
     * @param visible boolean value representing the desired visibility of
     *                the canvas (true or false)
     */
    @Override
    public void setVisible(boolean visible) {
        if (graphic == null) {
            // first time: instantiate the offscreen image and fill it with
            // the background colour
            Dimension size = canvas.getSize();
            canvasImage = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D) canvasImage.getGraphics();
            erase();
        }
        frame.setVisible(visible);
    }


    /**
     * assign a KeyListener to the canvas
     */
    @Override
    public void addKeyListener(KeyListener listen) {
        frame.addKeyListener(listen);
    }

    private class Listen extends KeyAdapter implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            t = e.getKeyChar();
            // to vary first ball's direction 
            switch (t) {
                case 'O':
                case 'o':
                    dir = -1;
                    break;
                case 'P':
                case 'p':
                    dir = 1;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int k = e.getKeyChar();
            if (k == 'O' || k == 'o' || k == 'P' || k == 'p') {
                dir = 0;
            }

            if (k == 'd' || k == 'D') {
                debug = !debug;
            }
            if (k == '+') {
                if (frameDuration > 10) frameDuration--;
            }
            if (k == '-') {
                if (frameDuration < 100) frameDuration++;
            }
            if (k == 'T' || k == 't') {
                if (transparency < Config.MAX_TRANSP) transparency += 5;
                setBackgroundColor();
            }
            if (k == 'R' || k == 'r') {
                if (transparency > Config.MIN_TRANSP) transparency -= 5;
                setBackgroundColor();
            }

            t = 0;
            // to pause the animation
            if (k == ' ') {
                move = !move;
                stopby = !stopby;
            }

            if (k == 'Q' || k == 'q' || k == 27)
                process = false;
        }

    }

    void setBackgroundColor() {
        System.out.println("Setting transparency to " + transparency);
        backgroundColor = new Color(
                Config.BGCOLOR.getRed(),
                Config.BGCOLOR.getGreen(),
                Config.BGCOLOR.getBlue(),
                transparency
        );
    }

    /**
     * erases all canvas with the background color
     */
    private void erase() {
        Color original = graphic.getColor();
        graphic.setColor(backgroundColor);
        graphic.fill(new Rectangle(0, 0, Config.WIDTH, Config.HEIGHT));
        graphic.setColor(Color.lightGray);
        graphic.fill(new Rectangle(0, Config.HEIGHT_E + 2 * Config.MARGIN, Config.WIDTH, Config.HEIGHT));
        graphic.setColor(original);
    }


    void addBall(Ball ball) {
        balls.add(ball);
    }

    /**
     * Redraw all balls on the Canvas.
     */
    private void redraw() {
        erase();
        // work frame where balls bounce
        graphic.drawRect(Config.MARGIN, Config.MARGIN,
                Config.WIDTH - (2 * Config.MARGIN) - 1, Config.HEIGHT - Config.MARGIN - Config.BOTTOM_MARGIN - 1);
        // angle guide
        if (debug) {
            Color previousColor = graphic.getColor();
            graphic.setColor(Color.gray);
            for (int a = 0; a < 360; a += 15) {
                int x = Config.CENTERX + (int) (Math.cos(Math.toRadians(a)) * (Config.WIDTH_E - 40) / 2);
                int y = Config.CENTERY + (int) (Math.sin(Math.toRadians(a)) * (Config.HEIGHT_E - 40) / 2);
                graphic.drawLine(Config.CENTERX, Config.CENTERY, x, y);
                graphic.setColor(Color.darkGray);
                graphic.drawString("" + a, x, y);
            }
            graphic.setColor(previousColor);
        }
        // now draw each ball
        for (Ball b : balls) {
            b.draw(graphic);
            // print data of each ball
            if (debug) {
                graphic.drawString("r" + b.getRadius(), (int) b.getX(), (int) b.getY() + 30);
                int nn = 5;
                int col = (b.getNumber() - 1) / nn;
                int row = -1 + (b.getNumber() - 1) % nn;
                int x = Config.MARGIN + col * (Config.WIDTH_E / nn);
                int y = 10 + Config.HEIGHT_E + 2 * Config.MARGIN + row * (Config.BOTTOM_MARGIN / nn);
                graphic.drawString("" + b.getNumber() +
                        ": a=" + (int) Math.toDegrees(b.getAngle()) +
                        ", s=" + b.getSpeed(), x, y);
            }
            if (debug)
                drawDirectionGuides(b);
        }
        canvas.repaint();
    }


    /**
     * moves the ball in the right direction
     */
    private void move(Ball ball) {
        // just in case
        if (ballOutOfWorld(ball)) {
            ball.setCoords(Config.CENTERX, Config.CENTERY);
            ball.setSpeed(0); // stops it
            return;
        }
        ball.move();
        // checks if the balls bounces with margins
        double a = checkBouncingWithWall(ball);
        if (a != NO_CHANGE) {
            ball.setAngle(a);
            ball.move();
        }
        // check hits with other balls
        for (Ball b : balls) {
            if (b.getNumber() > ball.getNumber()) {
                double overlap = ball.overlappingDistance(b);
                if (overlap <= 0) {
                    if (overlap < 2) {
                        ball.interchangeOfMater(b);
                    }
                    // two balls bounce
                    // play with speeds, checked ball slows down and the other speed up
                    ball.slowDownWithoutStop();
                    b.accelerate();
                    // calculate angles of collision
                    double resultAngle = ball.collisionAngle(b);
                    b.setAngle(resultAngle);
                    if (Config.STOPBY)
                        move = false;
                }
            }
        }
    }

    private boolean ballOutOfWorld(Ball b) {
        return b.amIoutOf(0, 0, Config.WIDTH, Config.HEIGHT_E + 10);
    }

    private void drawDirectionGuides(Ball ball) {
        int dist = 2 * ball.getSpeed() * ball.getRadius();
        double a = ball.getAngle();
        int centerX = (int) Math.round(ball.getCenterX());
        int centerY = (int) Math.round(ball.getCenterY());
        int xDest = centerX + (int) Math.round(Math.cos(a) * dist);
        int yDest = centerY + (int) Math.round(Math.sin(a) * dist);

        graphic.drawLine(centerX, centerY, xDest, yDest);
    }

    private double checkBouncingWithWall(Ball b) {
        int x = (int) Math.round(b.getCenterX());
        int y = (int) Math.round(b.getCenterY());
        int r = b.getRadius();
        double ang = (360 + (int) Math.toDegrees(b.getAngle())) % 360;
        if ((ang >= 90) && (ang < 270) && (x - r <= Config.MARGIN)) {
            if (ang >= 180) ang = (540 - ang) % 360;
            else ang = (180 - ang) % 360;
            if (debug) System.out.print("|<");
            return Math.toRadians(ang);
        }
        if ((ang >= 270 || ang < 90) && (x + r >= Config.WIDTH_E)) {
            if (ang >= 270) ang = (540 - ang) % 360;
            else ang = (180 - ang) % 360;
            if (debug) System.out.print(">|");
            return Math.toRadians(ang);
        }
        if ((ang >= 180) && (ang < 360) && (y - r <= Config.MARGIN)) {
            ang = (360 - ang) % 360;
            if (debug) System.out.print("^");
            return Math.toRadians(ang);
        }
        if ((ang >= 0) && (ang < 180) && (y + r >= Config.HEIGHT_E)) {
            ang = (360 - ang) % 360;
            if (debug) System.out.print("v");
            return Math.toRadians(ang);
        }

        return NO_CHANGE;
    }

    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame. This is essentially a JPanel with added capability to
     * refresh the image drawn on it.
     */
    private class CanvasPane extends JPanel {
        /**
         *
         */
        private static final long serialVersionUID = 2755881253952166777L;

        @Override
        public void paint(Graphics g) {
            g.drawImage(canvasImage, 0, 0, null);
        }
    }

    @Override
    public void run() {
        while (process) {
            long t1 = System.currentTimeMillis();
            if (move)  // this is controlled by user with spacebar
            {
                for (Ball b : balls) {
                    if (b.getNumber() == 1 && dir > 0) b.setAngle(0);
                    if (b.getNumber() == 1 && dir < 0) b.setAngle(Math.PI);
                    // if (debug) System.out.print(">:" + b.getNumber() + ":  ");
                    move(b);
                    // if (debug) System.out.println("");
                    if (Config.STOPBY) {
                        if (!move) {
                            redraw();
                            while (stopby) {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException ex) {

                                }
                            }
                            stopby = true;
                        }
                    }
                }
            }
            redraw();
            long t2 = System.currentTimeMillis();
            long t = frameDuration - (t2 - t1);
            if (t > 0) {
                try {
                    Thread.sleep(t);
                } catch (InterruptedException ex) {

                }
            }
        }
        System.exit(0);
    }


}