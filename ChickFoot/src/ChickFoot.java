/**
 * 
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author bones
 *
 */
public class ChickFoot {
	public static final int MAX_PIPS = 15;
	public static final int START_X = 600;
	public static final int START_Y = 100;
	public static final int DOM_SIZE = 40;
	public static final int HAND_START_SIZE = 17;
	
	private JFrame gameFrame;
	private JPanel panel = new MyPanel();
	private JScrollPane scrollBar;
	private GridBagConstraints c;
	private int turnNo = 0;
	private int curDouble = 15;
	private JTextField fldTurnNo;
	private ArrayList<Domino> deck;
	private Domino root, pendingBone;
	private ArrayList<Domino> hand;
	
	private class MyPanel extends JPanel {
		private static final long serialVersionUID = 88L;

		public MyPanel() {
			addMouseListener(new MouseListener() {
				@Override
			    public void mouseClicked(MouseEvent e) {
		   			for (int i = 0; i < hand.size(); i++) {
		   				if (contains(e.getX(), e.getY(), 45*i+5, 5, DOM_SIZE, 2*DOM_SIZE)) {
		   					System.out.println("Clicked on Tile #" + i);
		   					pendingBone = hand.get(i);
		   				}
		   			}
		   			// Check if they clicked on the root
		   			Domino destinationBone = null;
	   				if (contains(e.getX(), e.getY(), START_X, START_Y, 2*DOM_SIZE, DOM_SIZE)) {
	   					if (pendingBone != null) {
	   						System.out.println("Clicked on Root");
	   						destinationBone = root;
	   					}
	   				} else {
	   					destinationBone = root.findClick(e.getX(), e.getY());
	   				}
	   				if (pendingBone != null & destinationBone != null) {
	   					// Both pendingBone and destinationBone have been selected
	   					System.out.println("Trying to add the selected bone to the board");
	   					int availPips = destinationBone.getAvailablePips();
	   					System.out.println("Available pips is " + availPips + "; Major is " + pendingBone.getMajorPips() + "; Minor is " + pendingBone.getMinorPips());
	   					if (pendingBone.getMajorPips() == availPips & pendingBone.getMinorPips() == availPips) {
	   						// We have a double, so play it sideways
	   						pendingBone.setNode();
	   					} 
	   					if (pendingBone.getMajorPips() == availPips) {
	   						int childNo = destinationBone.isAvailable();
	   						if (childNo == 1) {
	   							destinationBone.addChild1(pendingBone);
	   						} else if (childNo == 2) {
	   							destinationBone.addChild2(pendingBone);
	   						} else if (childNo == 3) {
	   							destinationBone.addChild3(pendingBone);
	   						}
   							int idx = findBoneInHand(pendingBone.getMajorPips(), pendingBone.getMinorPips());
   							if (idx >= 0) {
   								System.out.println("Removing bone from hand");
   								hand.remove(idx);
   							}
	   					} else if (pendingBone.getMinorPips() == availPips){
	   						pendingBone.setUpsideDown();
	   						int childNo = destinationBone.isAvailable();
	   						if (childNo == 1) {
	   							destinationBone.addChild1(pendingBone);
	   						} else if (childNo == 2) {
	   							destinationBone.addChild2(pendingBone);
	   						} else if (childNo == 3) {
	   							destinationBone.addChild3(pendingBone);
	   						}
   							int idx = findBoneInHand(pendingBone.getMajorPips(), pendingBone.getMinorPips());
   							if (idx >= 0) {
   								System.out.println("Removing bone from hand");
   								hand.remove(idx);
   							}
	   					}
	   					pendingBone = null;
	   					destinationBone = null;
	   					panel.repaint();
	   					scrollBar.repaint();
	   				}
			    }
		
				public boolean contains(int X, int Y, int ObjX, int ObjY, int ObjWidth, int ObjHeight) {
					boolean retVal = false;
					if (X >= ObjX & X <= ObjX + ObjWidth & Y >= ObjY & Y <= ObjY + ObjHeight) {
						retVal = true;
					}
					return retVal;
				}
				
				@Override
			    public void mousePressed(MouseEvent e) {
			    }
				@Override
			    public void mouseReleased(MouseEvent e) {
			    }
				@Override
			    public void mouseEntered(MouseEvent e) {
			    }
				@Override
			    public void mouseExited(MouseEvent e) {
			    }
			});
		}

		@Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setColor(Color.pink);
   	        g2d.setStroke(new BasicStroke(1,
	            BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
   			root.draw(g);
   			for (int i = 0; i < hand.size(); i++) {
   				Domino curDom = hand.get(i);
   				curDom.setX(45*i + 5);
   				curDom.setY(5);
   				curDom.draw(g);
   			}
        }
	}

    
	public ChickFoot() {

		gameFrame = new JFrame();
		
		//Create a scrollbar using JScrollPane and add panel into it's viewport  
		//Set vertical and horizontal scrollbar always show  
		scrollBar=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
		scrollBar.setMinimumSize(new Dimension(700, 2500));

		//Add JScrollPane into JFrame  
		gameFrame.add(scrollBar);  
		
	    // Content-pane sets layout
	    panel.setLayout(new GridBagLayout());

	    c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		setupBoard();
		setupDeck();
		playSolitaire();
		
	    // Exit the program when the close-window button clicked
	    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    // Call some super methods to set up the MainWindow attributes
	    gameFrame.setTitle("Chicken Foot Client");  // "super" JFrame sets title
	    gameFrame.setSize(1400, 700);   // "super" JFrame sets initial size
	    gameFrame.setVisible(true);    // "super" JFrame shows

	}
	
	// Create an ActionListener for ending the turn; it will just call the routine that handles all of the paperwork
	ActionListener drawListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JButton) {
        		drawBone();
        		panel.repaint();
           }
        }
    };

	private void setupBoard() {

		// Create an ActionListener for ending the turn; it will just call the routine that handles all of the paperwork
		ActionListener endTurnListener = new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if (e.getSource() instanceof JButton) {
	        		nextDouble();
	           }
	        }
	    };
	
		JTextField lblTurn = new JTextField("Double:");
		lblTurn.setEditable(false);
	    c.gridx = 0;
	    c.gridy = 0;
	    c.gridwidth = 1;
	    panel.add(lblTurn, c);		
	    fldTurnNo = new JTextField(curDouble + "");
	    fldTurnNo.setEditable(false);
	    c.gridx = 1;
	    panel.add(fldTurnNo, c);
	    
	    // Set up end turn button and instruction text area
	    c.gridx++;
		JButton endTurnButton = new JButton("End Turn");
		endTurnButton.setMnemonic(KeyEvent.VK_T);
		endTurnButton.setFont(new Font("Arial", Font.PLAIN, 10));
		endTurnButton.addActionListener(endTurnListener);
		panel.add(endTurnButton, c);
		
		c.gridx++;
		JButton drawButton = new JButton("Draw Bone");
		drawButton.setMnemonic(KeyEvent.VK_D);
		drawButton.setFont(new Font("Arial", Font.PLAIN, 10));
		drawButton.addActionListener(drawListener);
		panel.add(drawButton, c);
	}

	private void setupDeck() {
		deck = new ArrayList<Domino>();
		for (int majPip = 0; majPip <= MAX_PIPS; majPip++) {
			for (int minPip = 0; minPip <= majPip; minPip++) {
				Domino newDom = new Domino(majPip, minPip);
				if (majPip == curDouble & minPip == curDouble) {
					root = newDom;
					root.setNode();
					root.setX(START_X);
					root.setY(START_Y);
				} else {
					deck.add(newDom);
				}
			}
		}
		Collections.shuffle(deck);
	}
	
	public int findBoneInHand(int MajorPips, int MinorPips) {
		for (int i = 0; i < hand.size(); i++) {
			if (hand.get(i).getMajorPips() == MajorPips & hand.get(i).getMinorPips() == MinorPips) {
				return i;
			}
		}
		return -1;
	}

	public void playSolitaire() {
		Domino curBone;
		int curChildSpot, curPips;
		
		// Put together starting hand
		hand = new ArrayList<Domino>();
		for (int i = 1; i <= HAND_START_SIZE; i++ ) {
			hand.add(deck.get(0));
			deck.remove(0);
		}
		// We'll just go thru some number of turns for now...
		for (int turn = 1; turn <= 10; turn++) {
			// Look at each Domino in the hadnd
			for (int i = 0; i < hand.size(); i++) {
				curBone = hand.get(i);
				// First check if the root is available, if so, try to play on it
				curChildSpot = root.isAvailable();
				if (curChildSpot > 0) {
					curPips = root.getAvailablePips();
					if (curPips == curBone.getMajorPips()) {
						if (curChildSpot == 1) {
							root.addChild1(curBone);
						} else if (curChildSpot == 2) {
							root.addChild2(curBone);
						} else if (curChildSpot == 3) {
							root.addChild3(curBone);
						}
						hand.remove(i);
					} else if (curPips == curBone.getMinorPips()) {
						if (curChildSpot == 1) {
							root.addChild1(curBone);
							root.getChild1().setUpsideDown();
						} else if (curChildSpot == 2) {
							root.addChild2(curBone);
							root.getChild2().setUpsideDown();
						} else if (curChildSpot == 3) {
							root.addChild3(curBone);
							root.getChild3().setUpsideDown();
						}
						hand.remove(i);
					}
				} else {
					// root was not available, so loop thru children looking for a match
					if (root.getChild1() != null) {
						curChildSpot = root.getChild1().isAvailable();
						if (curChildSpot > 0) {
							curPips = root.getChild1().getAvailablePips();
						} else {
							
						}
					}
				}
			}
		}
	}
	
	public void drawBone() {
		if (deck.size() > 0) {
			Domino curBone = deck.get(0);
			hand.add(curBone);
			deck.remove(0);
		} else {
			System.out.println("Boneyard is empty");
		}
	}
	
	public void nextDouble() {
		if (curDouble == 0) {
			System.out.println("Game over");
		} else {
			curDouble--;
			fldTurnNo.setText(curDouble + "");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Run the GUI construction in the Event-Dispatching thread for thread-safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ChickFoot gamePanel =  new ChickFoot(); // Let the constructor do the job
			}
		});

	}

}
