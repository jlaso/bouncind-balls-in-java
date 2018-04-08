package es.joseluislaso.bouncingballs;

import java.awt.Color;

final class Config {

    final static boolean DEBUG  = false;
	// makes a little pause when two or more balls bounce
    final static boolean STOPBY  = false;

	// relatives to main window
    final static String TITLE   = "Bouncing balls by Joseluis Laso";
    final static int	WIDTH   = 650;
    final static int    HEIGHT  = 650;
    final static Color  BGCOLOR = new Color(200,200,200);

    final static int	MARGIN  = 10;
    final static int	BOTTOM_MARGIN  = 100;
    final static int    WIDTH_E = WIDTH-MARGIN;
    final static int    HEIGHT_E= HEIGHT-BOTTOM_MARGIN;

    final static int    CENTERX = (WIDTH_E-MARGIN)/2;
    final static int    CENTERY = (HEIGHT_E-MARGIN-MARGIN)/2;

	final static int    FRAME  = 40;  // milliseconds each frame  1000/40 = 25 fps

    final static int    MIN_TRANSP = 50;
    final static int    MAX_TRANSP = 255;

    final static int    MIN_RADIUS = 15;
    final static int    MAX_RADIUS = 25;

}
