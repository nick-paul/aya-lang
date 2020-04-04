package aya.ext.graphics;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.IOException;

import aya.Aya;
import aya.exceptions.AyaRuntimeException;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.util.DictReader;
import aya.util.FileUtils;
import aya.variable.Variable;

public class CanvasInterface {
	private CanvasTable _table;
	
	// Keys
	private final long AUTOFLUSH = Variable.encodeString("autoflush");
	private final long EXTENT = Variable.encodeString("extent");
	private final long HEIGHT = Variable.encodeString("height");
	private final long ANGLE = Variable.encodeString("angle");
	private final long THETA = Variable.encodeString("theta");
	private final long CYCLE = Variable.encodeString("cycle");
	private final long COLOR = Variable.encodeString("color");
	private final long WIDTH = Variable.encodeString("width");
	private final long STYLE = Variable.encodeString("style");
	private final long START = Variable.encodeString("start");
	private final long SIZE = Variable.encodeString("size");
	private final long NAME = Variable.encodeString("name");
	private final long TYPE = Variable.encodeString("type");
	private final long FILE = Variable.encodeString("file");
	private final long TEXT = Variable.encodeString("text");
	private final long FILL = Variable.encodeString("fill");
	private final long SHOW = Variable.encodeString("show");
	private final long JOIN = Variable.encodeString("join");
	private final long END = Variable.encodeString("end");
	private final long CAP = Variable.encodeString("cap");
	private final long DH = Variable.encodeString("dh");
	private final long DV = Variable.encodeString("dv");
	private final long DX = Variable.encodeString("dx");
	private final long DY = Variable.encodeString("dy");
	private final long X1 = Variable.encodeString("xa");
	private final long Y1 = Variable.encodeString("ya");
	private final long X2 = Variable.encodeString("xb");
	private final long Y2 = Variable.encodeString("yb");
	private final long XS = Variable.encodeString("xs");
	private final long YS = Variable.encodeString("ys");
	private final long W = Variable.encodeString("w");
	private final long H = Variable.encodeString("h");
	private final long X = Variable.encodeString("x");
	private final long Y = Variable.encodeString("y");
	private final long R = Variable.encodeString("r");
	private final long G = Variable.encodeString("g");
	private final long B = Variable.encodeString("b");
	private final long A = Variable.encodeString("a");
	
	public CanvasInterface() {
		_table = new CanvasTable();
	}
	
	public int doCommand(int canvas_id, Symbol command, Dict d) throws AyaRuntimeException {
		DictReader params = new DictReader(d);
		params.setErrorName("MG (command: " + command.str() + ")");
		
		String cmd = command.name();
		
		switch (cmd) {
		case "new": return cmdNew(params);
		case "close": _table.close(canvas_id); return 1;
		case "isopen": {
			Canvas c = _table.getCanvas(canvas_id);
			return (c != null && c.isOpen()) ? 1 : 0;
		}
		case "_font_names":{
			GraphicsEnvironment ge = GraphicsEnvironment
			        .getLocalGraphicsEnvironment();
	        String[] font_names = ge.getAvailableFontFamilyNames();
	        for (int i = 0; i < font_names.length; i++) {
	        	Aya.getInstance().println(font_names[i]);
	        }
	        
	        return 1;
		}
		}
		
		Canvas cvs = _table.getCanvas(canvas_id);
		
		// Initial checks
		if (cvs == null) {
			throw new AyaRuntimeException("Canvas with id '" + canvas_id + "' does not exist");
		} else if (!cvs.isOpen()) {
			return 0;
		}
		
		switch (cmd) {
		case "clear": return cmdClear(params, cvs);
		case "clearrect": return cmdClearRect(params, cvs);
		case "line": return cmdLine(params, cvs);
		case "rect": return cmdRect(params, cvs);
		case "roundrect": return cmdRoundRect(params, cvs);
		case "oval": return cmdOval(params, cvs);
		case "ellipse": return cmdEllipse(params, cvs);
		case "arc": return cmdArc(params, cvs);
		case "path": return cmdPath(params, cvs);
		case "text": return cmdText(params, cvs);
		case "rotate": return cmdRotate(params, cvs);
		case "scale": return cmdScale(params, cvs);
		case "translate": return cmdTranslate(params, cvs);
		case "shear": return cmdShear(params, cvs);
		case "copy": return cmdCopy(params, cvs);
		case "set_bg": return cmdSetBackground(params, cvs);
		case "set_color": return cmdSetColor(params, cvs);
		case "set_font": return cmdSetFont(params, cvs);
		case "set_paint": return cmdSetPaint(params, cvs);
		case "set_alpha": return cmdSetAlpha(params, cvs);
		case "set_stroke": return cmdSetStroke(params, cvs);
		case "isopen": return cmdIsOpen(params, cvs);
		case "show": return cmdShow(params, cvs);
		case "save": return cmdSave(params, cvs);
		default:
			throw new AyaRuntimeException("Canvas: Unknown command '" + command.name() + "'.");
		}

	}
	
	
	///////////////////
	// Canvas        //
	///////////////////
	
	private int cmdNew(DictReader params) {
		int id = _table.newCanvas(params.getString(NAME, "Canvas"), params.getIntEx(WIDTH), params.getIntEx(HEIGHT));
		Canvas c = _table.getCanvas(id);
		c.setShowOnRefresh(params.getInt(AUTOFLUSH, 0) != 0);
		if (params.getInt(SHOW, 1) == 1) {
			c.show();
		}
		return id;
	}
	
	private int cmdShow(DictReader params, Canvas cvs) {
		cvs.show();
		return 1;
	}
	
	private int cmdIsOpen(DictReader params, Canvas cvs) {
		return cvs.isOpen() ? 1 : 0;
	}
	
	
	private int cmdSave(DictReader params, Canvas cvs) {
		String filename = params.getStringEx(FILE);
		
		try {
			File f = new File(FileUtils.workingRelative(filename));
			cvs.save(f);
		} catch (IOException e) {
			return 0;
		}
		
		return 1;
	}
	
	
	///////////////////
	// Shapes        //
	///////////////////
	
	private int cmdLine(DictReader params, Canvas cvs) {
		cvs.getG2D().drawLine(params.getIntEx(X1),
							  params.getIntEx(Y1),
							  params.getIntEx(X2),
							  params.getIntEx(Y2));
		cvs.refresh();
		return 1;
	}
	
	private int cmdRect(DictReader params, Canvas cvs) {
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fillRect(params.getIntEx(X),
					  params.getIntEx(Y),
					  params.getIntEx(W),
					  params.getIntEx(H));	
		} else {
			cvs.getG2D().drawRect(params.getIntEx(X),
								  params.getIntEx(Y),
								  params.getIntEx(W),
								  params.getIntEx(H));
		}
		
		cvs.refresh();
		return 1;
	}
	private int cmdClear(DictReader params, Canvas cvs) {
		cvs.getG2D().clearRect(0, 0, cvs.getWidth(), cvs.getHeight());
		cvs.refresh();
		return 1;
	}
	
	private int cmdClearRect(DictReader params, Canvas cvs) {
		cvs.getG2D().clearRect(params.getIntEx(X),
				 			   params.getIntEx(Y),
			 				   params.getIntEx(W),
		 					   params.getIntEx(H));
		
		cvs.refresh();
		return 1;
	}
	
	private int cmdRoundRect(DictReader params, Canvas cvs) {
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fillRoundRect(params.getIntEx(X),
									   params.getIntEx(Y),
									   params.getIntEx(W),
									   params.getIntEx(H),
									   params.getInt(DH, 0),
									   params.getInt(DV, 0));	
		} else {
			cvs.getG2D().drawRoundRect(params.getIntEx(X),
									   params.getIntEx(Y),
									   params.getIntEx(W),
									   params.getIntEx(H),
									   params.getInt(DH, 0),
									   params.getInt(DV, 0));		  
			}
		
		cvs.refresh();
		return 1;
	}
	
	private int cmdArc(DictReader params, Canvas cvs) {
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fillArc(params.getIntEx(X),
								 params.getIntEx(Y),
								 params.getIntEx(W),
								 params.getIntEx(H),
								 params.getInt(ANGLE, 0),
								 params.getInt(EXTENT, 0));	
		} else {
			cvs.getG2D().drawArc(params.getIntEx(X),
								 params.getIntEx(Y),
								 params.getIntEx(W),
								 params.getIntEx(H),
								 params.getInt(ANGLE, 0),
								 params.getInt(EXTENT, 0));		  
			}
		
		cvs.refresh();
		return 1;
	}
	
	private int cmdOval(DictReader params, Canvas cvs) {
		int x = params.getIntEx(X);
		int y = params.getIntEx(Y);
		int w = params.getIntEx(W);
		int h = params.getIntEx(H);
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fill(new Ellipse2D.Double(x,y,w,h));	
		} else {
			cvs.getG2D().draw(new Ellipse2D.Double(x,y,w,h));	
		}
		
		cvs.refresh();
		return 1;
	}
	
	private int cmdEllipse(DictReader params, Canvas cvs) {
		double cx = params.getDoubleEx(X);
		double cy = params.getDoubleEx(Y);
		double w = params.getDoubleEx(W);
		double h = params.getDoubleEx(H);
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fill(new Ellipse2D.Double(cx-w/2, cy-h/2, w, h));	
		} else {
			cvs.getG2D().draw(new Ellipse2D.Double(cx-w/2, cy-h/2, w, h));	
		}
		
		cvs.refresh();
		return 1;
	}
	
	private int cmdPath(DictReader params, Canvas cvs) {
		double[] xs = params.getDoubleArrayEx(XS);
		double[] ys = params.getDoubleArrayEx(YS);
		
		if (xs.length != ys.length) {
			throw new AyaRuntimeException(
					"MG: ::path 'xs' and 'ys' must be the same length. Got xs(" +
					xs.length + "), ys(" + ys.length + ")");
		}
		
		if (xs.length == 0) {
			throw new AyaRuntimeException(
					"MG: ::path 'xs' and 'ys' must contain at least 1 point.");
		}
		
		GeneralPath path = new GeneralPath();
		path.moveTo(xs[0], ys[0]);
		
		for (int i = 1; i < xs.length; i++) {
			path.lineTo(xs[i], ys[i]);
		}
		
		path.closePath();
		
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fill(path);
		} else {
			cvs.getG2D().draw(path);
		}
		
		return 1;
	}
	
	private int cmdText(DictReader params, Canvas cvs) {
		cvs.getG2D().drawString(params.getStringEx(TEXT),
							    params.getIntEx(X),
							    params.getIntEx(Y));	
		cvs.refresh();
		return 1;
	}
	

	////////////////////
	// Transformation //
	////////////////////
	
	private int cmdRotate(DictReader params, Canvas cvs) {
		cvs.getG2D().rotate(params.getDouble(THETA, 0.0));
		
		return 1;
	}
	
	private int cmdScale(DictReader params, Canvas cvs) {
		cvs.getG2D().scale(params.getDoubleEx(X), params.getDoubleEx(Y));
		
		return 1;
	}
	
	private int cmdTranslate(DictReader params, Canvas cvs) {
		cvs.getG2D().translate(params.getDoubleEx(X), params.getDoubleEx(Y));
		
		return 1;
	}
	
	private int cmdShear(DictReader params, Canvas cvs) {
		cvs.getG2D().shear(params.getDoubleEx(X), params.getDoubleEx(Y));
		
		return 1;
	}
	
	private int cmdCopy(DictReader params, Canvas cvs) {
		cvs.getG2D().copyArea(params.getIntEx(X),
				 params.getIntEx(Y),
				 params.getIntEx(W),
				 params.getIntEx(H),
				 params.getIntEx(DX),
				 params.getIntEx(DY));	
		
		return 1;
	}
	
	
	///////////////////
	// Configuration //
	///////////////////
	
	private int cmdSetColor(DictReader params, Canvas cvs) {
		Color col = params.getColor(COLOR);
		if (col != null) {
			cvs.getG2D().setColor(col);
		} else {
			cvs.getG2D().setColor(new Color(params.getInt(R, 0),
		  			params.getInt(G, 0),
		  			params.getInt(B, 0)));
		}
		return 1;
	}
	
	
	private int cmdSetBackground(DictReader params, Canvas cvs) {
		Color col = params.getColor(COLOR);
		if (col != null) {
			cvs.getG2D().setBackground(col);
		} else {
			cvs.getG2D().setBackground(new Color(params.getInt(R, 0),
		  			params.getInt(G, 0),
		  			params.getInt(B, 0)));
		}
		return 1;
	}
	
	
	private int cmdSetPaint(DictReader params, Canvas cvs) {
		String type = params.getSymStringEx(TYPE);
		
		switch (type) {
		case "grad": {
			float x1 = (float)(params.getDoubleEx(X1));
			float y1 = (float)(params.getDoubleEx(Y1));
			float x2 = (float)(params.getDoubleEx(X2));
			float y2 = (float)(params.getDoubleEx(Y2));
			boolean cycle = params.getInt(CYCLE, 1) == 1;
			Color col1 = params.getColorEx(START);
			Color col2 = params.getColorEx(END);
			
			cvs.getG2D().setPaint(new GradientPaint(x1, y1, col1, x2, y2, col2, cycle));
		}
		break;
		default:
			cmdSetColor(params, cvs);
		}
		
		
		return 1;
	}

	private int cmdSetAlpha(DictReader params, Canvas cvs) {
		double alpha = Double.max(0, Double.min(1.0, params.getDouble(A, 1.0)));
		AlphaComposite alcom = AlphaComposite.getInstance(
                 AlphaComposite.SRC_OVER, (float)alpha);
        cvs.getG2D().setComposite(alcom);
        return 1;
	}
	
	private int symToCap(String s) {
		switch (s) {
		case "butt": return BasicStroke.CAP_BUTT;
		case "round": return BasicStroke.CAP_ROUND;
		case "square": return BasicStroke.CAP_SQUARE;
		default: return -1;
		}
	}
	
	private int symToJoin(String s) {
		switch (s) {
		case "bevel": return BasicStroke.JOIN_BEVEL;
		case "miter": return BasicStroke.JOIN_MITER;
		case "round": return BasicStroke.JOIN_ROUND;
		default: return -1;
		}
	}
	
	
	private int symToStyle(String s) {
		switch (s) {
		case "plain": return Font.PLAIN;
		case "bold": return Font.BOLD;
		case "italic": return Font.ITALIC;
		case "bolditalic": return Font.BOLD + Font.ITALIC;
		default: return -1;
		}
	}
	
	private int cmdSetStroke(DictReader params, Canvas cvs) {
		BasicStroke prev = (BasicStroke)(cvs.getG2D().getStroke());
		int cap  = symToCap(params.getSymString(CAP, ""));
		int join = symToJoin(params.getSymString(JOIN, ""));
		cvs.getG2D().setStroke(new BasicStroke(
				params.getFloat(WIDTH, prev.getLineWidth()),
				cap  == -1 ? prev.getEndCap() : cap,
				join == -1 ? prev.getLineJoin() : join
				));
		return 1;
	}
	
	private int cmdSetFont(DictReader params, Canvas cvs) {
		Font current = cvs.getG2D().getFont();
		String name = params.getString(NAME, current.getName());
		int size = params.getInt(SIZE, current.getSize());
		int style = symToStyle(params.getSymString(STYLE,""));
        cvs.getG2D().setFont(new Font(name, style == -1 ? current.getStyle() : style, size));
        return 1;
	}

}