package etf.crossword.sa120481d; 

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class CrosswordPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6453521845519125484L;

	private final int ROWS=5, COLS=5;
		
	private Cell[][] cells = new Cell[ROWS][COLS];
	private boolean loaded = false;
	
	private MouseAdapter mouse=null;
	
	public CrosswordPanel() {
		this.setBackground(Color.white);
		setPreferredSize(new Dimension(300,300));					
		
		addMouseListener(mouse = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				                
                int width = getWidth()/COLS;
    	        int height = getHeight()/ROWS;

                int column = e.getX() / width;
                int row = e.getY() / height;

                Cell cell = cells[row][column];
                //Crosswords.log.info("row: "+row+", column: "+column);
                
                if(cell.isWhite()) cell.setBlack();
                else cell.setWhite();                
                
                updateCellsNumbers();
                repaint();				
			}
		});
						
		repaint();
	}
	
	public void reset() {
		loadCells();
		if (mouse == null) 
			mouse = new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					                
	                int width = getWidth()/COLS;
	    	        int height = getHeight()/ROWS;

	                int column = e.getX() / width;
	                int row = e.getY() / height;

	                Cell cell = cells[row][column];
	                //Crosswords.log.info("row: "+row+", column: "+column);
	                
	                if(cell.isWhite()) cell.setBlack();
	                else cell.setWhite();                
	                
	                updateCellsNumbers();
	                repaint();				
				}
			};
		addMouseListener(mouse);
		repaint();
	}
	
	public void disableChangingCells() { this.removeMouseListener(mouse); }
	
	private void loadCells() {
		int width = getWidth()/COLS-1;
        int height = getHeight()/ROWS-1;
        
        int xoff = (getWidth() - COLS*width) / 2;
        int yoff = (getHeight() - ROWS*height) / 2;
		
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) { 
				cells[i][j] = new Cell(width, height, xoff + j*width, yoff + i*height);
			}
		}
		
		updateCellsNumbers();
		loaded=true;
	}
		
	public void paintComponent(Graphics g) {
		super.paintComponent(g);		
				
		if (!loaded) loadCells();
		
		Graphics2D g2d = (Graphics2D) g.create();

		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {		
				Cell current = cells[i][j];
				
				g2d.setColor(current.getFill());
        		g2d.fill(current.getRectangle());
        		
        		g2d.setColor(Color.BLACK);
        		g2d.draw(current.getRectangle());
        		
        		g2d.drawString(current.getHorizontal(), 
        				(float)(current.getRectangle().getX() + 5),
        				(float)(current.getRectangle().getY() + current.getRectangle().getHeight() - 5));
        		
                g2d.drawString(current.getVertical(), 
                		(float)(current.getRectangle().getX() + current.getRectangle().getWidth() - 15),
                		(float)(current.getRectangle().getY() + 12));
                
                Font original = g2d.getFont();                
                g2d.setFont(new Font(original.getFontName(), Font.PLAIN, original.getSize()*3));                
                g2d.drawString(""+current.getLetter(), (float)(current.getRectangle().getX() + 22), 
                				(float)(current.getRectangle().getY() + 50));
                g2d.setFont(original);
			}		
		}
	}
	
	private boolean leftBlack(int row, int column) {
		boolean flag = false;
		
		if (column > 0)
			if (cells[row][column-1].isBlack())
				flag = true;
				
		return flag;
	}
	
	private boolean upBlack(int row, int column) {
		boolean flag = false;
		
		if (row > 0)
			if (cells[row-1][column].isBlack())
				flag = true;
		
		return flag;
	}
	
	private void updateCellsNumbers() {
		for (int i=0; i<ROWS; i++) 
			for (int j=0; j<COLS; j++) 
				cells[i][j].resetNumbers();		
		
		int count = 1;
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (!cells[i][j].isBlack()) {
					if (j == 0 || leftBlack(i,j)) { cells[i][j].setHorizontal(Integer.toString(count)); count++; }
					if (i==0 || upBlack(i,j)) { cells[i][j].setVertical(Integer.toString(count)); count++; }					
				}
			}
		}
	}
	
	public Cell[][] getCells() { return cells; }
	public int getRowsNum() { return ROWS; }
	public int getColsNum() { return COLS; }
	
}
