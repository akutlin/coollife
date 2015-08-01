package coollife.classicalConwayModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import coollife.core.bio.Animate;
import coollife.core.mapper.AbstractVisualMapper;
import firststep.Canvas;
import firststep.Color;
import firststep.DoubleXY;

public class DigitalFlatSquareThorMapper extends AbstractVisualMapper {
	
	private final int[] size;
	private Canvas cnv;
	private float k, x0, y0;
	private Map[] maps = new Map[4];
	
	public DigitalFlatSquareThorMapper( DigitalFlatSquareThor tp ) {
		super(tp);
		size = tp.getSize();
		maps[0] = new FirstMap();
		maps[1] = new SecondMap();
		maps[2] = new ThirdMap();
		maps[3] = new FourthMap();
	}
	
	private void fillFrameSize(int winWidth, int winHeight) {
		float wpw = (float)winWidth /size[0];
		float hph = (float)winHeight /size[1];
		if (wpw < hph) {
			k = wpw;
		} else {
			k = hph;
		}
		x0 = winWidth / 2 - (size[0] * k / 2);
	    y0 = winHeight / 2 - (size[1] * k / 2);
	}
	
	private abstract class GeneralMap extends Map {

		public GeneralMap(DoubleXY relativePos, DoubleXY relativeSize) {
			super(relativePos, relativeSize);
			// TODO Auto-generated constructor stub
		}
		
        protected int X0 = (int) (size[0] * relativePos.getX());
        protected int Y0 = (int) (size[1] * relativePos.getY());
        protected int width = (int) (size[0] * relativeSize.getX());
        protected int hight = (int) (size[1] * relativeSize.getY());

		@Override
		public void drawMap() {
			
			//draw frame
			cnv.beginPath();
	        cnv.strokeColor(new Color(200, 0, 0));
			cnv.rect(	x0 + X0 * k,
						y0 + Y0 * k, 
						width * k,
						hight * k);
	        cnv.stroke();
			
			//draw lines
	        double[] p = new double[2];
	        double[] vx = new double[2];
	        double[] vy = new double[2];
	        Color c = new Color(64, 64, 64, 255);
	        
			for ( int i = X0 + 1; i < X0 + width; i++ ) {
				p[0] = i; p[1] = Y0;
				vy[0] = 0; vy[1] = 1;
				drawGeodesic(cnv, c, p , vy , hight, 1);
			}
			for ( int i = Y0 + 1; i < Y0 + hight; i++) {
				p[0] = X0; p[1] = i;
				vx[0] = 1; vx[1] = 0;
				drawGeodesic(cnv, c, p , vx , width, 1);
			}
		}
	}
	
	private class FirstMap extends GeneralMap {

		public FirstMap() {
			super(new DoubleXY(0.5,0), new DoubleXY(0.5,0.5));
		}

		@Override
		public void it(double[] p, DoubleXY pos) {
			tp.transform(p);
			if ( p[0] >= X0 && p[1] <= Y0 + hight ) { 
				pos.setX(x0 + k * p[0]);
				pos.setY(y0 + k * p[1]);
			} else if( p[0] == 0 && p[1] <= Y0 + hight ) {
				pos.setX(x0 + k * size[0]);
				pos.setY(y0 + k * p[1]);
			} else
				throw new MapMismatchException();
		}
	}
	
	private class SecondMap extends GeneralMap {

		public SecondMap() {
			super(new DoubleXY(0,0), new DoubleXY(0.5,0.5));
		}

		@Override
		public void it(double[] p, DoubleXY pos) {
			tp.transform(p);
			if ( p[0] <= width &&
					p[1] <= hight ) { 
				pos.setX(x0 + k * p[0]);
				pos.setY(y0 + k * p[1]);
			} else
				throw new MapMismatchException();
		}
	}
	
	private class ThirdMap extends GeneralMap {

		public ThirdMap() {
			super(new DoubleXY(0,0.5), new DoubleXY(0.5,0.5));
		}

		@Override
		public void it(double[] p, DoubleXY pos) {
			tp.transform(p);
			if ( p[0] <= width && p[1] >= Y0 ) { 
				pos.setX(x0 + k * p[0]);
				pos.setY(y0 + k * p[1]);
			} else if( p[1] == 0 && p[0] <= width ) {
				pos.setX(x0 + k * p[0]);
				pos.setY(y0 + k * size[1]);
			} else
				throw new MapMismatchException();
		}
	}
	
	private class FourthMap extends GeneralMap {

		public FourthMap() {
			super(new DoubleXY(0.5,0.5), new DoubleXY(0.5,0.5));
		}

		@Override
		public void it(double[] p, DoubleXY pos) {
			tp.transform(p);
			if ( p[0] >= X0 && p[1] >= Y0 ) { 
				pos.setX(x0 + k * p[0]);
				pos.setY(y0 + k * p[1]);
			} else if ( p[0] == 0 && p[1] >= Y0 ) {
				pos.setX(x0 + k * size[0]);
				pos.setY(y0 + k * p[1]);
			} else if ( p[0] >= X0 && p[1] == 0 ) {
				pos.setX(x0 + k * p[0]);
				pos.setY(y0 + k * size[1]);
			} else if ( p[0] == 0 && p[1] == 0 ) {
				pos.setX(x0 + k * size[0]);
				pos.setY(y0 + k * size[1]);
			} else
				throw new MapMismatchException();
		}
	}

	@Override
	public void drawAtlas(Canvas cnv, int winWidth, int winHeight) {
		
		this.cnv = cnv;
		
		fillFrameSize( winWidth, winHeight );
	    
	    // Painting dark font
        cnv.beginPath();
        cnv.fillColor(new Color(0, 0, 0));
        cnv.rect(x0, y0, k * size[0], k * size[1]);
        cnv.fill();
		
        maps[0].drawMap();
        maps[1].drawMap();
        maps[2].drawMap();
        maps[3].drawMap();
		
	}

	@Override
	public void drawBiosphere(Canvas cnv, int winWidth, int winHeight, Set<Animate> pool) {
		
		cnv.beginPath();
		cnv.fillColor(new Color( 0, 0, 255));
		
		for ( Animate a : pool ) {
			for( DoubleXY pos : preparePoint( a.getPosition() ) )
				if ( pos.getX() < x0 + size[0] * k && pos.getY() < y0 + size[1] * k )
				cnv.rect((float)pos.getX(), (float)pos.getY(), k, k);
		}
		
		cnv.fill();
	}

	@Override
	public List<double[]> preparePosition(DoubleXY pos) {
		double x = pos.getX();
		double y = pos.getY();
		List<double[]> arr = new ArrayList<>();
		if ( x > x0 && x < x0 + size[0] * k &&
				y > y0 && y < y0 + size[1] * k ) {
			double[] p = new double[2];
			p[0] = (x - x0) /k;
			p[1] = (y - y0) /k;
			tp.transform(p);
			arr.add(p);
		}
		return arr;
	}

	@Override
	public List<DoubleXY> preparePoint(double[] p) {
		tp.transform(p);
		List<DoubleXY> arr = new ArrayList<>();
		for ( Map map : maps) {
			try {
				DoubleXY pos = new DoubleXY(0,0);
				map.it(p, pos);
				arr.add(pos);
			} catch ( RuntimeException ex) {}
		}
		return arr;
	}
}
