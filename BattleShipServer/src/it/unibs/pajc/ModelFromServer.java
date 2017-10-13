package it.unibs.pajc;

import java.awt.Point;
import java.util.Vector;

class ModelFromServer
{	
	public static final int COLPITO = 1;
	public static final int ACQUA = 2;
	private static final double MAX_RANDOM_SHIP_SIZE = 10;
	private static int NUM_NAVI = 10; //non final perchè se diverso lo modifico
	
	private int boardSize;
	public Vector <Nave> flotta = new Vector <Nave>();
	//la matrice delle mie navi
	public boolean[][] matPosizioni;	
	//la matrice con gli spari che ho effettuato
	//al server serve per sapere quando finisce la partita
	public int[][] matColpiti;	//int e non boolean così possono avere più stati (niente, colpito, acqua, affondato...)
	
	public ModelFromServer(int boardSize)
	{
		this.boardSize = boardSize;
		 createMat();
		 initializeFlotta();
	}
	
	public void createMat()
	{
		matPosizioni = new boolean[boardSize][boardSize];
		matColpiti = new int[boardSize][boardSize];
		
		for(int i = 0; i < boardSize; i++)
		{
			for(int j = 0; j < boardSize; j++)
			{
				matPosizioni[i][j] = false;	//mette tutto a 0
				matColpiti[i][j] = 0;
			}
		}
	}
	
	public void initializeFlotta()
	{
		int lunghezza = 0;
		
		if(NUM_NAVI < 10)
			NUM_NAVI = 10;
		
		for(int i = 0; i < NUM_NAVI; i++)	//10 navi da posizionare
		{
			if(i == 0)	//una nave lunga 4
			{
				lunghezza = 4;
			}
			else if(0 < i && i < 3)	//due navi lunghe 3
			{
				lunghezza = 3;
			}
			else if(3 <= i && i < 6)	//tre navi lunghe 2
			{
				lunghezza = 2;
			}
			else if(6 <= i && i < 10)	//4 navi lunghe 1
			{
				lunghezza = 1;
			}
			else	//i > 10, solo se decido di mettere più di 10 navi
			{
				lunghezza = (int)Math.round(Math.random()*MAX_RANDOM_SHIP_SIZE);	//dimensione casuale nave: tra 0 e dimensione max
			}
			
			Nave nave;
			Vector<Point> posizioneNave;
			
			do
			{
				posizioneNave = setPosizioneRandom(lunghezza);
				nave = new Nave("" + i, posizioneNave, lunghezza);	//nave da aggiungere a flotta quando ha le posizioneNave assegnata ( != null )
			}
			while(posizioneNave == null);	//se non la posiziona: posizioneNave = null -> rifà il posizionamento
			flotta.add(nave);
		}
	}//initializeFlotta	
	
	public Vector<Point> setPosizioneRandom(int lunghezza) 
	{
		//parte da posizione casuale
		int x = ((int)Math.round(Math.random() * (boardSize - 1))); //.random compreso tra 0 e 1 -> diventa tra 0 e size-1
		int y = ((int)Math.round(Math.random() * (boardSize - 1))); 
		int xi = x;	//valori iniziali di riferimento
		int yi = y;	//valori iniziali di riferimento
		
		Point pos0 = new Point(x, y);
		Vector<Point>posizioniNave = new Vector<Point>();
		
		boolean fatto=false;
		
		if(posNotAssigned(pos0))
		{
			posizioniNave.add(pos0);
			
			 int iterazione0 = 0, iterazione1 = 0, iterazione2 = 0, iterazione3 = 0;	//contatori: se ha già provato una direzione non lo fa più
			 
			do
			{
				int direzione = (int)Math.round(Math.random()*3); //direzione orientamento nave: casuale, tra 0 e 3
				
				switch(direzione)
				{
				case 0: //alto
				{
					if(iterazione0 == 0) //se è la prima volta prova, sennò no, ha già provato
					{
						iterazione0++;
						for(int i = 0; i < lunghezza - 1; i++) //controlla se sono libere tutte le posizioni. lunghezza -1 perchè c'è già pos0
						{
							y++;	//va in alto di uno
							if(y < boardSize)	//se è dentro al campo
							{
								if(posNotAssigned(new Point(x, y)))	//se la posizione è libera la aggiunge
								{
									posizioniNave.addElement(new Point(x, y));	//aggiunge un punto al vettore da restituire
								}
								else 
								{
									y = yi;	//faccio tornare y come prima, per evitare overflow in iterazioni successive
									posizioniNave.clear(); //toglie posizioni salvate ma che non vanno bene
								}
							}
							else
							{
								y = yi;	//faccio tornare y come prima, per evitare overflow in iterazioni successive
								posizioniNave.clear(); //toglie posizioni salvate ma che non vanno bene
							}
						}
						if(posizioniNave.size()==lunghezza)
						{
							fatto = true;	//se riempie tutte le posizioni
						}
					}
					
					break;
				}
				case 1: //destra
				{
					if(iterazione1 == 0) //se è la prima volta prova, sennò no, ha già provato
					{
						iterazione1++;
						for(int i = 0; i < lunghezza - 1; i++) //controlla se sono libere tutte le posizioni. lunghezza -1 perchè c'è già pos0
						{
							x++;	//va a destra di uno
							if(x < boardSize)	//se è dentro al campo
							{
								if(posNotAssigned(new Point(x, y)))	//se la posizione è libera la aggiunge
								{
									posizioniNave.addElement(new Point(x, y));	//aggiunge un punto al vettore da restituire
								}
								else 
								{
									x = xi;	//faccio tornare x come prima, per evitare overflow
									posizioniNave.clear(); //toglie posizioni salvate ma che non vanno bene
								}
							}
							else
							{
								x = xi;	//faccio tornare x come prima, per evitare overflow
								posizioniNave.clear(); //toglie posizioni salvate ma che non vanno bene
							}
						}
						if(posizioniNave.size()==lunghezza)
						{
							fatto = true;	//se riempie tutte le posizioni

						}
					}					
					
					break;
				}
				case 2: //basso
				{
					if(iterazione2 == 0) //se è la prima volta prova, sennò no, ha già provato
					{
						iterazione2++;
						for(int i = 0; i < lunghezza - 1; i++) //controlla se sono libere tutte le posizioni. lunghezza -1 perchè c'è già pos0
						{
							y--;	//va in basso di uno
							if(y >= 0)	//se è dentro al campo
							{
								if(posNotAssigned(new Point(x, y)))	//se la posizione è libera la aggiunge
								{
									posizioniNave.addElement(new Point(x, y));	//aggiunge un punto al vettore da restituire
								}
								else 
								{
									y = yi;	//faccio tornare y come prima, per evitare overflow
									posizioniNave.clear(); //toglie posizioni salvate ma che non vanno bene
								}
							}
							else
							{
								y = yi;	//faccio tornare y come prima, per evitare overflow
								posizioniNave.clear(); //toglie posizioni salvate ma che non vanno bene
							}
						}
						if(posizioniNave.size()==lunghezza)
						{
							fatto = true;	//se riempie tutte le posizioni

						}
					}					
					
					break;
				}
				case 3:	//sinistra
				{
					if(iterazione3 == 0) //se è la prima volta prova, sennò no, ha già provato
					{
						iterazione3++;
						for(int i = 0; i < lunghezza - 1; i++) //controlla se sono libere tutte le posizioni. lunghezza -1 perchè c'è già pos0
						{
							x--;	//va a sinistra di uno
							if(x >= 0)	//se è dentro al campo
							{
								if(posNotAssigned(new Point(x, y)))	//se la posizione è libera la aggiunge
								{
									posizioniNave.addElement(new Point(x, y));	//aggiunge un punto al vettore da restituire
								}
								else
								{
									x = xi;	//faccio tornare x come prima, per evitare overflow
									posizioniNave.clear(); //toglie posizioni salvate ma che non vanno bene
								}
							}
							else
							{
								x = xi;	//faccio tornare x come prima, per evitare overflow
								posizioniNave.clear(); //toglie posizioni salvate ma che non vanno bene
							}
						}
						if(posizioniNave.size()==lunghezza)
						{
							fatto = true;	//se riempie tutte le posizioni
						}
					}					
					break;
				}
				}//SWITCH
			}//DO
			while(!fatto && iterazione0 + iterazione1 + iterazione2 + iterazione3 != 4); //se ha fatto o se ha provato tutte le direzioni esce
		}//IF
				
		if(fatto)
		{
			for(Point p : posizioniNave)
			{
				matPosizioni[boardSize - p.y -1][p.x] = true;//modifica matPosizioni indicando caselle occupate. X NUMERO COLONNA, Y NUMERO RIGA e matrici sono righe x colonne
			}
			
			return posizioniNave;
		}
		else return null;
	}//setPosizioneRandom
	
	public boolean posNotAssigned(Point pos)
	{
		if(matPosizioni[boardSize - pos.y -1][pos.x]==false)	
			return true;
		
		else return false;
	}
	
	public boolean isGoodShot(Point selectedCell) //controlla se il colpo dell'avversario colpisce una nave del giocatore
	//da point a matrice inverto x e y: poi tolgo y da boardsize perchè una parte da sopra e l'altra da sotto
	{
		if(matPosizioni[boardSize - selectedCell.y - 1][selectedCell.x]==true)
			return true;
		else 
			return false;
	}
	
	public int getBoardSize() 
	{
		return this.boardSize;
	}

	public void setBoardSize(int size)
	{
		this.boardSize = size;
	}

	public Vector<Nave> getFlotta() 
	{
		return this.flotta;
	}

	public void setFlotta(Vector<Nave> flotta) 
	{
		this.flotta = flotta;
	}

	public boolean[][] getMatPosizioni()
	{
		return this.matPosizioni;
	}

	public void setMatPosizioni(boolean[][] mat)
	{
		this.matPosizioni = mat;
	}

	public int[][] getMatColpiti() 
	{
		return this.matColpiti;
	}

	public void setMatColpiti(int[][] mat) 
	{
		this.matColpiti = mat;
	}

	public boolean getMatPosizioni(int riga, int colonna) 
	{
		return this.matPosizioni[riga][colonna];
	}

	public int getMatColpiti(int riga, int colonna)
	{
		return this.matColpiti[riga][colonna];
	}

	public void setMatPosizioni(int riga, int colonna, boolean val)
	{
		this.matPosizioni[riga][colonna] = val;
	}

	public void setMatColpiti(int riga, int colonna, int val) 
	{
		this.matColpiti[riga][colonna] = val;
	}

}