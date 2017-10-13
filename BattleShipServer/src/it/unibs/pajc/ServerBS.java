package it.unibs.pajc;

import java.io.*;
import java.net.*;
import java.util.Vector;

public class ServerBS //extends Thread
{
	ServerSocket server;
	
	private Vector <Socket> players = new Vector<Socket>();
	
	public static void main(String[] args) 
	{
		new ServerBS();
	}
	
	public ServerBS()	//costruttore
	{
		int port = 1234;
				
		System.out.println("Starting server on port: " + port + "\n");
		try
		{
			server = new ServerSocket(port);
			int numPlayer = 0;
			
			//continua ad accettare connessioni e crea una partita ogni 2
			while(true)
			{
				// chiamata bloccante, in attesa di una nuova connessione
				Socket client = server.accept();
				
				numPlayer++;
				
				System.out.println("PLAYER " + numPlayer + " connected: ");
				System.out.println("Address: " + client.getInetAddress());
				System.out.println("Port: " + client.getPort() + "\n");
				
				//aggiunge la socket al vettore dei giocatori
				players.add(client);
				
				if(players.size() % 2 == 0)	//se clients pari faccio partire una partita
				{
					//se ho 2 giocatori: pos 0 e pos 1, se ho 4 giocatori: pos 2 e pos 3, ...
					Partita partita = new Partita(players.size()/2, players.get(players.size() - 2), players.get(players.size() - 1));
					//faccio partire la partita startando il thread
					Thread t = new Thread(partita);
					t.start();
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("\nErrore2: " + e.getMessage());
		}
	}
	
//	class Connection implements Runnable//oppure extends Thread 
//	{
//			private final int BOARD_SIZE = 10;
//			
//			// dichiarazione delle variabili socket e dei buffer
//			Socket client;
////			ObjectInputStream inputFromClient;			
////			ObjectOutputStream outputToClient;
//			
//			public Connection(Socket client)
//			{
//				this.client = client;
//			}
//		
//			public void run()
//			{
//				//le due connessioni hanno la loro matrice e la loro flotta
//				ModelFromServer model = new ModelFromServer(this.BOARD_SIZE);
//				
//				try	//METTO LA creazione oggetti tra tonde: così sono validi anche fuori dal BLOCCO TRY. TRY-WITH-RESOURCE
//				(
//						ObjectOutputStream outputToClient = new ObjectOutputStream(client.getOutputStream());
//						ObjectInputStream inputFromClient = new ObjectInputStream(client.getInputStream());
//				)
//				{
//					
//					System.out.printf("Connesso a: %s: %d\n", client.getInetAddress(), client.getPort());
//					
//					
//					
//					
//					
////					Point request = null;
////					Point oldRequest = null;
////					
////					while(inputFromClient.readObject() != null)
////					{
////						request = (Point)inputFromClient.readObject();
////						outputToClient.writeObject(new Boolean(true));
////
////						oldRequest = request;
////					}
//					
//					
//					
//					inputFromClient.close();
//		 			outputToClient.close();
//		 			client.close();
//				
////					 chiusura dei buffer e del socket
//					
//				}
//				catch(IOException e)
//				{
//					System.out.println("\nErrore3: " + e.getMessage());
//					e.printStackTrace();
//				}
//				
//				System.out.println("\n...un client si è disconnesso...\n");
//			}//run
//			
//			
//			
//		}//Connection

}
