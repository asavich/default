package etf.crossword.sa120481d;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class Gui {
	private JFrame frame;
	
	private CrosswordPanel crossword;
	
	private JTextPane text;
	
	private JButton run;
	private JButton step;
	private JButton reset;
	
	private List<String> dictionary;
	
	private boolean loaded = false;
	
	Algorithm algorithm;
	FinalSolution finalSolution;
	StepByStep stepByStep;
	
	public Gui() {
		crossword = new CrosswordPanel();
		
		JPanel up = new JPanel();
		up.add(crossword);
		
		text = new JTextPane();
		
		JScrollPane scroll = new JScrollPane(text);
		scroll.setWheelScrollingEnabled(true);					
		
		run = new JButton("Run");
		
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				finalSolution.run();
			}			
		});
		
		step = new JButton("Step");		
		step.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stepByStep.nextStep();				
			}			
		});
		
		reset = new JButton("RESET");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();				
			}	
		});
		
		disableButtons();
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(6, 1));
		buttons.add(run);
		buttons.add(step);
		
		JButton dummy1 = new JButton(); dummy1.setEnabled(false);
		JButton dummy2 = new JButton(); dummy2.setEnabled(false);
		JButton dummy3 = new JButton(); dummy3.setEnabled(false);
		
		buttons.add(dummy1);
		buttons.add(dummy2);
		buttons.add(dummy3);
		
		buttons.add(reset);
		
		JPanel down = new JPanel();
		down.setLayout(new BorderLayout());
		down.add(buttons, BorderLayout.WEST);
		down.add(scroll, BorderLayout.CENTER);
		
		frame = new JFrame("Crossword");
		frame.setSize(600, 600);		
		frame.setLayout(new BorderLayout());
				
		frame.add(up, BorderLayout.CENTER);
		frame.add(down, BorderLayout.SOUTH);
				
		JMenuBar menu = createMenuBar();
		frame.setJMenuBar(menu);
				
	    frame.setVisible(true);
	    frame.setResizable(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
	
	private void reset() {
		loaded=false;
		dictionary=null;
		algorithm = null;
		finalSolution = null;
		stepByStep = null;
		crossword.reset();
		resetText();
		disableButtons();
	}
	
	private void enableButtons() {
		run.setEnabled(true);
		step.setEnabled(true);
	}
	
	public void disableButtons() {
		run.setEnabled(false);
		step.setEnabled(false);
	}
	
	public void disableChangingCrossword() { crossword.disableChangingCells(); }
	
	public void printText(String message){
		StyledDocument doc = text.getStyledDocument();
		try {
			AttributeSet style = null;
			doc.insertString(doc.getLength(), message, style);
		} catch (BadLocationException e) {	e.printStackTrace(); }
	}
	
	public void resetText() { 
		text.setText("");
	}

	private void loadFromFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt", "text"));
		
		int dialog = chooser.showOpenDialog(null);		
		if(dialog == JFileChooser.APPROVE_OPTION){
			File file = chooser.getSelectedFile();			
			List<String> dict = new ArrayList<String>();
			
			try(BufferedReader br = new BufferedReader(new FileReader(file))) {
		        String line = br.readLine();

		        while (line != null) {
		            dict.add(line);
		            line = br.readLine();
		        }
		        
		        dictionary = dict;		        
		        loaded = true;
		        printText("Dictionary loaded.\n");
		    } catch (IOException e1) { e1.printStackTrace(); }			
		} 
	}
	
	private void loadFromTextArea() {
		List<String> dict = new ArrayList<String>();		
		String text = this.text.getText();

		String split[] = text.split("\\W+");
		
		for(String s: split)
			dict.add(s);
		
		dictionary = dict;
		loaded = true;
		printText("Dictionary loaded.\n");
	}
	
	private JMenuBar createMenuBar(){		
		JMenuItem ff = new JMenuItem("From File");		
		ff.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {				
				if(!loaded)
					loadFromFile();					
			}
		});
		
		JMenuItem ft = new JMenuItem("From TextArea");
		ft.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!loaded)
					loadFromTextArea();
			}
		});		
		
		JMenu file = new JMenu("File");		
		file.add(ff);
		file.add(ft);		

		JMenuItem newGame = new JMenuItem("New Game");		
		newGame.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		
		JMenu simulation = new JMenu("Simulation");	
		simulation.add(newGame);
		
		JMenuBar menuBar = new JMenuBar();		
		menuBar.add(file);
		menuBar.add(simulation);
		
		return menuBar;
	}
	
	public void newGame() {
		if (loaded) {
			algorithm = new Algorithm(this);
			finalSolution = new FinalSolution(this, algorithm);
			stepByStep = new StepByStep(this, algorithm);
			
			algorithm.printPossibleSolutions();
			printText("\n\n\n");
			
			crossword.disableChangingCells();		
			enableButtons();
		} else printText("\n *******You must load the dictionary first.******* \n");
	}
	
	public void repaintCrossword() { crossword.repaint(); }
	
	public Cell[][] getCells() { return crossword.getCells(); }
	public int getRowsNum() { return crossword.getRowsNum(); }
	public int getColsNum() { return crossword.getColsNum(); }
	public List<String> getDictionary() { return dictionary; }
	public JTextPane getText() { return text; }
}
