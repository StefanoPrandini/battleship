package it.unibs.pajc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import java.awt.RenderingHints;
import java.awt.event.*;

/*
 * 
 * 
 * ------------------------ VIEW ----------------------------------
 *
 * 
 * 
 */


public class PnlPaintAreaBS extends JComponent implements MouseMotionListener, MouseListener //implements per fare listen azioni mouse
{
	private static final long serialVersionUID = 1L;
	
	private static final int SPACE_BETWEEN_BOARDS = 5;
	private Point mousePosition = null;
	private Giocatore giocatore;
	private int boardSize;
	private int cellSize;
	
	//Create the panel (Costruttore)
	public PnlPaintAreaBS() 
	{
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public PnlPaintAreaBS(Giocatore giocatore)	//per creare il panel devo passare un oggetto giocatore (dal controller) con i dati necessari
	{
		this();	//costruttore di default
		this.giocatore = giocatore;
		this.boardSize = giocatore.getBoardSize();
	}
	
	public void addColpo(boolean colpito)
	{
		if(colpito)
			giocatore.setMatColpiti(giocatore.getSelectedCell().y, giocatore.getSelectedCell().x, Giocatore.COLPITO);
		
		else 
			giocatore.setMatColpiti(giocatore.getSelectedCell().y, giocatore.getSelectedCell().x, Giocatore.ACQUA);
	}
	
	public int getBoardSize() 
	{
		return boardSize;
	}
	public void setBoardSize(int boardSize) 
	{
		this.boardSize = boardSize;
	}
	public Giocatore getGiocatore()
	{
		return this.giocatore;
	}
	public void setGiocatore(Giocatore giocatore)
	{
		this.giocatore = giocatore;
	}
	
	public int getBoardCellSize()
	{
		return Math.min(getHeight()/(boardSize +2) , getWidth()/(2*boardSize + SPACE_BETWEEN_BOARDS + 1));	//getboardsize nel model, dimensione assegnata per creare una partita
	}
	
	private Point screenToBoard(Point sc)		//da coordinate schermo a coordinate board
	{
		return new Point(sc.x / cellSize, sc.y / cellSize);
	}
	
	@Override
	protected void paintComponent(Graphics g) 	//graphics area in cui si disegna
	{
		super.paintComponent(g);
		
		//uso graphics2D per togliere l'ALIASING (seghettatura). imposto la scheda grafica. Degrado di performance
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		cellSize = getBoardCellSize();
		
		paintBoards(g);			//disegna scacchiera
		paintFlotta(g);			//disegna le navi
		paintActiveCell(g);		//indica cella sotto al mouse con mirino
		paintShots(g);			//disegna i colpi (colpito o no)
	}
	
	void paintBoards(Graphics g)	//disegna scacchiera
	{
		g.setColor(new Color(30, 144, 255));		//blu chiaro
		g.fillRect(cellSize, cellSize, cellSize*boardSize, cellSize*boardSize);
		
		g.setColor(new Color(58, 117, 196));	//blu scuro
		for(int i = 1; i <= boardSize; i++)			//ho numerato le celle (dimensione size x size)
		{
			for(int j = 1; j <= boardSize; j++)
			{
				if((i+j)%2 != 0)
				{
					int x = i * cellSize;
					int y = j * cellSize;
					g.fillRect(x, y, cellSize, cellSize);
				}
			}
		}
		
		g.setColor(new Color(30, 144, 255));		//blu chiaro
		g.fillRect(cellSize + (cellSize)*(boardSize + 4), cellSize, cellSize*boardSize, cellSize*boardSize);
		
		g.setColor(new Color(58, 117, 196));	//blu scuro
		for(int i = 1; i <= boardSize; i++)			//ho numerato le celle (dimensione size x size)
		{
			for(int j = 1; j <= boardSize; j++)
			{
				if((i+j)%2 != 0)
				{
					int x = (cellSize*(boardSize + 4)) + (i * cellSize);
					int y = j * cellSize;
					g.fillRect(x, y, cellSize, cellSize);
				}
			}
		}
		
		g.setColor(Color.red);
		g.setFont(new Font("label", Font.PLAIN, cellSize));
		g.drawString(giocatore.getNameP1(), cellSize, cellSize*3/4);
		g.drawString(giocatore.getNameP2(), (boardSize+5)*cellSize, cellSize*3/4);
	}
	
	void paintFlotta(Graphics g)
	{
		for(Nave nave : giocatore.getFlotta())
			paintNave(g, nave, Color.red, Color.yellow);	//red per la linea, yellow per le caselle
	}
	
	void paintNave(Graphics g, Nave nave, Color colorLine, Color colorShip)
	{
		
		Point firstPos = nave.posizione.elementAt(0);		//prima e ultima posizione della nave per gestire arrotondamento
		Point lastPos = nave.posizione.elementAt(nave.lunghezza-1);
		
		for(int i = 0; i < nave.posizione.size(); i++)
		{
			g.setColor(colorShip);	//sarà yellow
			
			if(nave.lunghezza == 1)	//se nave ha solo una casella -> cerchio
				g.fillOval(nave.posizione.elementAt(0).x*cellSize + cellSize, (boardSize - 1 - nave.posizione.elementAt(0).y)*cellSize + cellSize, cellSize, cellSize);
			
			else	//nave più lunga di 1
			{
				if(i == 0 || i == nave.lunghezza-1)	//prima e ultima sono cerchi
				{
					g.fillOval((nave.posizione.elementAt(i).x+1)*cellSize, ((boardSize - nave.posizione.elementAt(i).y))*cellSize, cellSize, cellSize);
					
					if(firstPos.x == lastPos.x)	//è verticale
					{
						if(firstPos.y > lastPos.y)	//firstPos in alto
						{
							g.fillRect((firstPos.x+1)*cellSize, ((boardSize - firstPos.y)*cellSize + cellSize/2), cellSize, cellSize);	//colora mezza casella sotto alla prima
							g.fillRect((lastPos.x+1)*cellSize, ((boardSize - lastPos.y)*cellSize - cellSize/2), cellSize, cellSize);	//colora mezza casella sopra all'ultima
						}
						else	//lastPos in alto
						{
							g.fillRect((firstPos.x+1)*cellSize, ((boardSize - firstPos.y)*cellSize - cellSize/2), cellSize, cellSize);	//colora mezza casella sopra alla prima
							g.fillRect((lastPos.x+1)*cellSize, ((boardSize - lastPos.y)*cellSize + cellSize/2), cellSize, cellSize);	//colora mezza casella sotto all'ultima
						}
					}
					
					else	//firstPos.y == lastPos.y	//è orizzontale
					{
						if(firstPos.x > lastPos.x)	//firstPos a destra
						{
							g.fillRect((firstPos.x+1)*cellSize - cellSize/2, (boardSize - firstPos.y)*cellSize, cellSize, cellSize);	//colora mezza casella sotto alla prima
							g.fillRect((lastPos.x+1)*cellSize + cellSize/2, (boardSize - lastPos.y)*cellSize, cellSize, cellSize);	//colora mezza casella sopra all'ultima
						}
						else	//lastPos a destra
						{
							g.fillRect((firstPos.x+1)*cellSize + cellSize/2, (boardSize - firstPos.y)*cellSize, cellSize, cellSize);	//colora mezza casella sopra alla prima
							g.fillRect((lastPos.x+1)*cellSize - cellSize/2, (boardSize - lastPos.y)*cellSize, cellSize, cellSize);	//colora mezza casella sotto all'ultima
						}
					}
				}
				else	//non la prima o l'ultima casella
					g.fillRect((nave.posizione.elementAt(i).x+1)*cellSize, (boardSize - nave.posizione.elementAt(i).y)*cellSize, cellSize, cellSize);
			}
			
			g.setColor(colorLine);	//sarà red
			g.drawLine(firstPos.x*cellSize + cellSize*3/2, (boardSize - 1 - firstPos.y)*cellSize + cellSize*3/2, 
							lastPos.x*cellSize + cellSize*3/2, (boardSize - 1 - lastPos.y)*cellSize + cellSize*3/2);
		}
	}
	
	void paintActiveCell(Graphics g) 	//mirino su casella sotto al mouse
	{
		if(mousePosition == null)
			return;
		
		int x = mousePosition.x *cellSize;		//perchè in mouseMoved c'è screenToBoard
		int y = mousePosition.y *cellSize;
		
		if(x >= (boardSize + SPACE_BETWEEN_BOARDS)*cellSize && x < (2* boardSize + SPACE_BETWEEN_BOARDS)*cellSize && y >= cellSize && y < (boardSize + 1)*cellSize)
		{
			g.setColor(Color.green);

			g.drawOval(x, y, cellSize, cellSize);
			g.drawLine(x + cellSize/2, y, x + cellSize/2, y + cellSize);
			g.drawLine(x, y + cellSize/2, x + cellSize, y + cellSize/2);
		}
	}
	
	void paintShots(Graphics g)	//colora miei spari nella scacchiera nemica	//colora casella se colpito, disegna X se non colpito
	//ci sono +/-1 e +/-5 perchè caselle iniziano una casella staccata, e tra le 2 scacchiere ci sono 5 caselle
	{		
		for(int i = 0; i < boardSize; i++)		//ricontrolla tutti i colpiti o no e li disegna tutti
		{
			for(int j = 0; j < boardSize; j++)
			{
				if(giocatore.getMatColpiti(i, j) == Giocatore.COLPITO)
				{
					g.setColor(Color.orange);
					g.fillRect((j+(boardSize + SPACE_BETWEEN_BOARDS))*cellSize, (i+1)*cellSize, cellSize, cellSize);
				}
				
				else if(giocatore.getMatColpiti(i, j) == Giocatore.ACQUA)
				{
					g.setColor(Color.white);
					g.drawLine
						((j+(boardSize + SPACE_BETWEEN_BOARDS))*cellSize, (i+1)*cellSize, 
								(j+(boardSize + SPACE_BETWEEN_BOARDS))*cellSize + cellSize, (i+1)*cellSize + cellSize);
					g.drawLine((j+(boardSize + SPACE_BETWEEN_BOARDS))*cellSize + cellSize, (i+1)*cellSize, 
							(j+(boardSize + SPACE_BETWEEN_BOARDS))*cellSize, (i+1)*cellSize + cellSize);
				}
			}	
		}		
	}	
	
	@Override
	public void mouseMoved(MouseEvent e) 
	{
		Point currentMousePosition = screenToBoard(e.getPoint());
		if (currentMousePosition == null || !currentMousePosition.equals(mousePosition))
		{
			mousePosition = screenToBoard(e.getPoint());
			repaint();		//non chiamo paintComponent, chiedo a java di aggiornare
		}
	}
	
	//quando clicco su usa casella setto selectedCell con le coordinate della seconda scacchiera:
	//tolgo 15 alla x e 1 alla y (bordi e spazio tra scacchiere)
		
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		Point boardPos = screenToBoard(e.getPoint());	
		
		System.out.println("click: " + boardPos.x + "." + boardPos.y);
		
		int x = boardPos.x;		//perchè c'è screenToBoard
		int y = boardPos.y;
		
		
		if(!giocatore.getSelectedCell().equals(new Point(x - boardSize - SPACE_BETWEEN_BOARDS, y - 1)))	//controllo che la cella selezionata sia cambiata
		{
			//deve essere nella scacchiera avversaria
			if(x >= (boardSize + SPACE_BETWEEN_BOARDS) && x < (2*boardSize + SPACE_BETWEEN_BOARDS) && y >= 1 && y < boardSize + 1)
			{
				//così ho coordinate pronte per essere confrontate con matrice posizioni
				giocatore.setSelectedCell(new Point(x - boardSize - SPACE_BETWEEN_BOARDS, y - 1));	
			}
			else
				giocatore.setSelectedCell(new Point(-1,-1));//se la cella non è nella scacchiera avversaria
		}
		
		System.out.println("selected cell: " + giocatore.getSelectedCell().x + "." + giocatore.getSelectedCell().y);
		
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
