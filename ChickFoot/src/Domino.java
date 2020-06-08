import java.awt.*;

public class Domino {
	private int majorPips;
	private int minorPips;
	private boolean isNode = false;		// indicates this domino is currently a node on the board, rather than an extension
	private boolean upsideDown = false;	// upside down means the minor side is on top, instead of the major side
	private Domino child1;
	private Domino child2;
	private Domino child3;
	private int x, y;		// x and y location of domino, once it's on the board
	private int childDrift = 0;
	
	private static Color[] pipColors = {Color.magenta, Color.red, Color.orange, Color.yellow,
			Color.green, Color.cyan, Color.blue, Color.lightGray,
			Color.magenta, Color.red, Color.orange, Color.yellow,
			Color.green, Color.cyan, Color.blue, Color.lightGray};
	
	public Domino(int MajorPips, int MinorPips) {
		majorPips = MajorPips;
		minorPips = MinorPips;
	}

	public Domino getChild1() {
		return child1;
	}
	public Domino getChild2() {
		return child2;
	}
	public Domino getChild3() {
		return child3;
	}
	public int getMajorPips() {
		return majorPips;
	}
	public int getMinorPips() {
		return minorPips;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public void setNode() {
		isNode = true;
	}
	public void setX(int X) {
		x = X;
	}
	public void setY(int Y) {
		y = Y;
	}
	public void setUpsideDown() {
		upsideDown = true;
	}
	public void setChildDrift(int Drift) {
		childDrift = Drift;
	}
	public int isAvailable() {
		if (child1 == null) {
			return 1;
		} else if (isNode) {
			if (child2 == null) {
				return 2;
			} else if (child3 == null) {
				return 3;
			}
		}
		return 0;
	}
	
	public int getAvailablePips() {
		if (upsideDown) {
			return majorPips;
		} else {
			return minorPips;
		}
	}
	
	public Domino addChild1(Domino NewDom) {
		int newX, newY;
		if (isNode) {
			newX = x-(int)(1*ChickFoot.DOM_SIZE) + this.childDrift;
			newY = y+ChickFoot.DOM_SIZE;
		} else {
			newX = x + this.childDrift;
			newY = y+2*ChickFoot.DOM_SIZE;
		}
		child1 = NewDom;
		child1.setX(newX);
		child1.setY(newY);
		if (isNode) {
			child1.setChildDrift(-ChickFoot.DOM_SIZE/2);
		} else {
			child1.setChildDrift(this.childDrift / 2);
		}
		return child1;
	}
	public Domino addChild2(Domino NewDom) {
		child2 = NewDom;
		child2.setX(x+(int)(0.5*ChickFoot.DOM_SIZE));
		child2.setY(y+ChickFoot.DOM_SIZE);
		child2.setChildDrift(0);
		return child2;
	}
	public Domino addChild3(Domino NewDom) {
		child3 = NewDom;
		child3.setX(x+(int)(2*ChickFoot.DOM_SIZE) + this.childDrift);
		child3.setY(y+ChickFoot.DOM_SIZE);
		child3.setChildDrift(ChickFoot.DOM_SIZE/2);
		return child3;
	}
	
	public void draw(Graphics g) {
		int firstPips, secondPips;

		// Draw the first square, then the second square, with the second square being to the right if a node
		if (upsideDown) {
			firstPips = minorPips;
			secondPips = majorPips;
		} else {
			firstPips = majorPips;
			secondPips = minorPips;
		}

		Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
		g.drawRect(x, y, ChickFoot.DOM_SIZE, ChickFoot.DOM_SIZE);
        g2d.setColor(pipColors[firstPips]);
		for (int p = 1; p <= firstPips; p++) {
			g.fillOval(x+8*(p%4)+6, y+8*p/4, 6, 6);
		}

        g2d.setColor(Color.black);
		if (isNode) {
			g.drawRect(x+ChickFoot.DOM_SIZE, y, ChickFoot.DOM_SIZE, ChickFoot.DOM_SIZE);
	        g2d.setColor(pipColors[secondPips]);
			for (int p = 1; p <= secondPips; p++) {
				g.fillOval(x+ChickFoot.DOM_SIZE+8*(p%4)+6, y+8*p/4, 6, 6);
			}
			drawFoot(g);
		} else {
			g.drawRect(x, y+ChickFoot.DOM_SIZE, ChickFoot.DOM_SIZE, ChickFoot.DOM_SIZE);
	        g2d.setColor(pipColors[secondPips]);
			for (int p = 1; p <= secondPips; p++) {
				g.fillOval(x+8*(p%4)+6, y+ChickFoot.DOM_SIZE+8*p/4, 6, 6);
			}
			if (child1 != null) {
				child1.draw(g);
			}
		}
	}
	
	public void drawFoot(Graphics g) {
		if (child1 != null) {
			child1.draw(g);
			if (child2 != null) {
				child2.draw(g);
				if (child3 != null) {
					child3.draw(g);
				}
			}
		}
	}
	
	public Domino findClick(int MouseX, int MouseY) {
		int height, width;
		Domino curDomCheck;
		
		if (child1 != null) {
			if (child1.isNode) {
				height = ChickFoot.DOM_SIZE;
				width = 2*ChickFoot.DOM_SIZE;
			} else {
				height = 2*ChickFoot.DOM_SIZE;
				width = ChickFoot.DOM_SIZE;
			}
			if (MouseX >= child1.getX() & MouseX <= child1.getX() + width & MouseY >= child1.getY() & MouseY <= child1.getY() + height) {
				System.out.println("Clicked on a Child 1; Major is " + child1.getMajorPips() + "; Minor is " + child1.getMinorPips());
				return child1;
			}
			curDomCheck = child1.findClick(MouseX, MouseY);
			if (curDomCheck != null) {
				return curDomCheck;
			}
		}
		if (child2 != null) {
			height = 2*ChickFoot.DOM_SIZE;
			width = ChickFoot.DOM_SIZE;
			if (MouseX >= child2.getX() & MouseX <= child2.getX() + width & MouseY >= child2.getY() & MouseY <= child2.getY() + height) {
				System.out.println("Clicked on a Child 2");
				return child2;
			}
			curDomCheck = child2.findClick(MouseX, MouseY);
			if (curDomCheck != null) {
				return curDomCheck;
			}
		}
		if (child3 != null) {
			height = 2*ChickFoot.DOM_SIZE;
			width = ChickFoot.DOM_SIZE;
			if (MouseX >= child3.getX() & MouseX <= child3.getX() + width & MouseY >= child3.getY() & MouseY <= child3.getY() + height) {
				System.out.println("Clicked on a Child 3");
				return child3;
			}
			curDomCheck = child3.findClick(MouseX, MouseY);
			if (curDomCheck != null) {
				return curDomCheck;
			}
		}
		return null;
	}
}
