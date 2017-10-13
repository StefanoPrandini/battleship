package it.unibs.pajc;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class Partita implements Runnable
{
	private final int BOARD_SIZE = 10;

	private int numeroPartita;
	private Socket socketP1;
	private Socket socketP2;
	private String nameP1;
	private String nameP2;
	
	public Partita(int numeroPartita, Socket p1, Socket p2)
	{
		this.numeroPartita = numeroPartita;
		this.socketP1 = p1;
		this.socketP2 = p2;
	}
	
	
	@Override
	public void run()
	{
		//le due connessioni hanno la loro matrice e la loro flotta
		//in pratica ci sono i dati dei 2 giocatori
		ModelFromServer modelP1 = new ModelFromServer(this.BOARD_SIZE);
		ModelFromServer modelP2 = new ModelFromServer(this.BOARD_SIZE);
		
		
		try	//METTO LA creazione oggetti tra tonde: così sono validi anche fuori dal BLOCCO TRY. TRY-WITH-RESOURCE
		(
				//Stream client 1
				ObjectOutputStream outputToPlayer1 = new ObjectOutputStream(socketP1.getOutputStream());
				ObjectInputStream inputFromPlayer1 = new ObjectInputStream(socketP1.getInputStream());
				//Stream client 2
				ObjectOutputStream outputToPlayer2 = new ObjectOutputStream(socketP2.getOutputStream());
				ObjectInputStream inputFromPlayer2 = new ObjectInputStream(socketP2.getInputStream());
		)
		{
			nameP1 = (String)inputFromPlayer1.readObject();
			nameP2 = (String)inputFromPlayer2.readObject();
			System.out.println("Game " + numeroPartita + " started:\n" 
				+ nameP1 + " (" + socketP1.getInetAddress()+ ":" + socketP1.getPort() + ") VS "+  nameP2 + " (" + socketP2.getInetAddress()+ ":"+ socketP2.getPort() + ") ");
			
			Vector<Nave> flottaP1 = modelP1.getFlotta();
			Vector<Nave> flottaP2 = modelP2.getFlotta();
			int[][] matColpitiP1 = modelP1.getMatColpiti();
			int[][] matColpitiP2 = modelP2.getMatColpiti();
			
			outputToPlayer1.writeInt(BOARD_SIZE);
			outputToPlayer1.writeObject(flottaP1);
			outputToPlayer1.writeObject(matColpitiP1);
			outputToPlayer1.writeObject(nameP2);
			
			//mi crea le finestre bianche
			
			outputToPlayer2.writeInt(BOARD_SIZE);
			outputToPlayer2.writeObject(flottaP2);
			outputToPlayer2.writeObject(matColpitiP2);
			outputToPlayer2.writeObject(nameP1);

			boolean partitaInCorso = true;
			boolean turnoP1 = true;
			
			
			
			while(partitaInCorso)
			{
			
				outputToPlayer1.writeObject(partitaInCorso);	//informa i client se invierà dati
				outputToPlayer2.writeObject(partitaInCorso);	//sarà true
				
				if(turnoP1)
				{
					outputToPlayer1.writeObject(true);	//dice al player1 di mandare la casella
					outputToPlayer2.writeObject(false);//dice al player 2 di non mandare la casella
					
					Point shot = (Point)inputFromPlayer1.readObject();	//aspetta un point dal client che ha il turno
					
					while(shot.equals(new Point(-1,-1)))	//se client non ha nuova cella invia -1,-1
					{
						outputToPlayer1.writeObject(partitaInCorso);	//informa che la partita è in corrso
						outputToPlayer1.writeObject(true);				//informa che è il suo turno
						shot = (Point)inputFromPlayer1.readObject();
					}
					
					if(shot.equals(new Point(9,9)))
					{
						partitaInCorso = false;
					}
					
					//controlla dal player2 se player1 ha colpito
					boolean colpito = modelP2.isGoodShot(shot);
					if(colpito)
					{
						outputToPlayer1.writeObject(true);//gli dice che ha colpito
						modelP1.setMatColpiti(BOARD_SIZE - shot.y, shot.x, ModelFromServer.COLPITO);
					}
					else//non colpito
					{
						outputToPlayer1.writeObject(false);//gli dice che non ha colpito
						modelP1.setMatColpiti(BOARD_SIZE - shot.y, shot.x, ModelFromServer.ACQUA);
					}
					
					turnoP1 = !turnoP1;	//switch turn
					
//					if(false)	//mettere condizione che fa finire partita
//					{
//						partitaInCorso = false;
//					}
				}
				else//turno p2
				{
					outputToPlayer1.writeObject(false);//dice al player 1 di non mandare casella
					outputToPlayer2.writeObject(true);	//dice al player2 di mandare la casella
					
					Point shot = (Point)inputFromPlayer2.readObject();
					
					while(shot.equals(new Point(-1,-1)))	//se client non ha nuova cella invia -1,-1
					{
						outputToPlayer2.writeObject(partitaInCorso);	//informa che la partita è in corrso
						outputToPlayer2.writeObject(true);				//informa che è il suo turno
						shot = (Point)inputFromPlayer2.readObject();
					}
					
					if(shot.equals(new Point(9,9)))	//condizione che termina partita
					{
						partitaInCorso = false;
					}
					
					//controlla dal player2 se player1 ha colpito
					boolean colpito = modelP1.isGoodShot(shot);
					if(colpito)
					{
						outputToPlayer2.writeObject(true);
						modelP2.setMatColpiti(BOARD_SIZE - shot.y, shot.x, ModelFromServer.COLPITO);
					}
					else 
					{
						outputToPlayer2.writeObject(false);
						modelP2.setMatColpiti(BOARD_SIZE - shot.y, shot.x, ModelFromServer.ACQUA);
					}
					
					turnoP1 = !turnoP1;	//switch turn
					
//					if(false)	//mettere condizione che fa finire partita
//					{
//						partitaInCorso = false;
//					}
				}
				
			}
			outputToPlayer1.writeObject(partitaInCorso);	//informa i client se invierà dati
			outputToPlayer2.writeObject(partitaInCorso);	//sarà false

			
			
			
//			chiusura dei buffer e del socket
			inputFromPlayer1.close();
			inputFromPlayer2.close();

 			outputToPlayer1.close();
 			outputToPlayer2.close();

 			socketP1.close();
 			socketP2.close();
		}
		catch(IOException | ClassNotFoundException e)
		{
			System.out.println("\nErrore3: " + e.getMessage());
//			e.printStackTrace();
			System.out.println("A client left the game");
		}		
	}

	public String getNameP1() 
	{
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
}
