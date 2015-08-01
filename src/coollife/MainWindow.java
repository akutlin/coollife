package coollife;

import coollife.core.mapper.VisualMapper;
import firststep.Canvas;
import firststep.Color;
import firststep.Image;
import firststep.Paint;
import firststep.Window;
import firststep.Framebuffer;


public class MainWindow extends Window {
	
	private static final String APPNAME = "Cool Life";
	
	private boolean isPaused;
	private boolean isDown;
    private VisualMapper mapper;

	public MainWindow( VisualMapper mapper ) {
		super (APPNAME, 600, 400, new Color(0.2f, 0.2f, 0.2f, 1.0f));
		this.mapper = mapper;
	}
	
	@Override
	public void key(Key key, int scancode, KeyState state, Modifiers modifiers) {
		if (key == Key.ESCAPE && state == KeyState.PRESS) {
			close();
		} else if (key == Key.SPACE && state == KeyState.PRESS) {
			isPaused = !isPaused;
	        updateTitle();
	    }
	}
	
	@Override
	public void mouseButton(MouseButton button, MouseButtonState state, Modifiers modifiers) {
	    if (button == MouseButton.LEFT && state == MouseButtonState.PRESS)
	    	isDown = true;
	    if (button == MouseButton.LEFT && state == MouseButtonState.RELEASE)
	    	isDown = false;
	}
	
	private void updateTitle() {
	    if (isPaused)
	    	setTitle(APPNAME + " [ Pause ]");
	    else
	    	setTitle(APPNAME);
	}
	
	int winWidth = 0;
	int winHeight = 0;
	Framebuffer fontFB;
	Paint fbPaint;
	
	@Override
	protected void frame(Canvas cnv) {
		
		boolean changed = false;
		
		changed = winWidth != getWidth() || winHeight != getHeight() ? true : false;
		
		int winWidth = getWidth();
		int winHeight = getHeight();
 		
 		if ( changed ) {
 			fontFB = cnv.createFramebuffer(winWidth, winHeight, Image.Flags.of(Image.Flag.REPEATX, Image.Flag.REPEATY));
 			fontFB.beginDrawing(1.0f);
 			mapper.drawAtlas(cnv, winWidth, winHeight);
 			fontFB.endDrawing();
 			fbPaint = cnv.imagePattern(0, 0, winWidth, winHeight, 0.f, fontFB.getImage(), 1.5f);
 		}
 		
		Framebuffer mainFb = cnv.getMainFramebuffer(); 
		mainFb.beginDrawing(1.0f);
		
		cnv.beginPath();
		cnv.rect(0, 0, winWidth, winHeight);
		cnv.fillPaint(fbPaint);
		cnv.fill();
		
		mapper.drawBiosphere(cnv, winWidth, winHeight);
		if ( !isPaused )
			mapper.getBiosphere().turn();
		if ( isDown )
			mapper.updatePosition( getCursorPos() );
		
		mainFb.endDrawing();
	}
	
}