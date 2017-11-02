package it.unibs.pajc;

import java.awt.Point;
import java.util.Vector;

/*
 * 
 * 
 * ------------------------ piccolo MODEL per racchiudere le cose che servono al client per disegnare il pnlPaint ----------------------------------
 * 
 * 
 * 
 */

public class Giocatore 
{
	static final int COLPITO = 1;
	static final int ACQUA = 2;
	
	private Point selectedCell;
	private int boardSize;
	private Vector<Nave> flotta;
	private int[][] matColpiti;	//al client serve per disegnare i colpi
	private String nameP1;
	private String nameP2;
	
	private boolean cellChanged;
	
	public Giocatore(Point selectedCell, int boardSize, Vector<Nave> vector, int[][] matColpiti, String p1, String p2)
	{
		this.selectedCell = selectedCell;
		this.boardSize = boardSize;
		this.flotta = vector;
		this.matColpiti = matColpiti;
		this.nameP1 = p1;
		this.nameP2 = p2;
	}

	public Vector<Nave> getFlotta() 
	{
		return flotta;
	}
	public void setFlotta(Vector<Nave> flotta) 
	{
		this.flotta = flotta;
	}
	public int[][] getMatColpiti() 
	{
		return matColpiti;
	}
	public void setMatColpiti(int[][] matColpiti) 
	{
		this.matColpiti = matColpiti;
	}
	public int getBoardSize() 
	{
		return boardSize;
	}
	public void setBoardSize(int boardSize) 
	{
		this.boardSize = boardSize;
	}
	public int getMatColpiti(int riga, int colonna) 
	{
		return matColpiti[riga][colonna];
	}
	public void setMatColpiti(int riga, int colonna, int val)
	{
		matColpiti[riga][colonna] = val;
	}
	public String getNameP1() {
		return nameP1;
	}
	public void setNameP1(String nameP1) 
	{
		this.nameP1 = nameP1;
	}
	public String getNameP2() 
	{
		return nameP2;
	}
	public void setNameP2(String nameP2) 
		{
		this.nameP2 = nameP2;
	}

	public Point getSelectedCell() 
	{
		return selectedCell;
	}

	public void setSelectedCell(Point selectedCell) 
	{
		if(!selectedCell.equals(this.selectedCell)) 
		{
			this.selectedCell = selectedCell;
			this.cellChanged = true;
			System.out.println("cell changed: " + this.cellChanged);
		}
		else
		{
			this.cellChanged = false;
		}
		
	}
	public boolean isCellChanged() 
	{
		return cellChanged;
	}
	public void setCellChanged(boolean cellChanged) 
	{
		this.cellChanged = cellChanged;
	}
}
