package it.unibs.pajc;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

import javax.swing.*;

/*
 * 
 * 
 * ------------------------ CONTROLLER ----------------------------------
 * accetta l'input e lo converte in comandi per vista
 * 
 * 
 */

public class MainFormBS 
{
	private JFrame frmBattleship;
	private PnlPaintAreaBS pnlPaint;
	private ObjectOutputStream outputToServer;
	private ObjectInputStream inputFromServer;
	
	Socket server;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		//finestra iniziale che chiede un nome
		JFrame parent = new JFrame();
		String digitedName = JOptionPane.showInputDialog(parent, "Waiting for other players:\nWhat is your name?", null);
		
		JOptionPane.showMessageDialog(parent, "Matching players...\nThe game will start when an opponent will be found");
		
				EventQueue.invokeLater(new Runnable() 
				{
					@Override
					public void run() 
					{
						try 
						{
							MainFormBS window;
							if(digitedName.length()==0)
							{
								//non potevo modificare digitedName per scope variabile, allora ne creo una nuova
								String playerName = "Player" + (int)Math.round(Math.random()*9999);
								window = new MainFormBS(playerName);
							}
							else
							{
								window = new MainFormBS(digitedName);
							}						
							window.frmBattleship.setVisible(true);
							
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				});	
	}

	/**
	 * Create the application.
	 */
	public MainFormBS(String playerName) 
	{
		connect();
		initialize(playerName);
		manageTurns(playerName);
	}

	private void connect()
	{
		try		//METTO LA creazione oggetti tra tonde: così sono validi anche fuori dal BLOCCO TRY. TRY-WITH-RESOURCE
		{
			server = new Socket("127.0.0.1", 1234);	
			outputToServer = new ObjectOutputStream(server.getOutputStream());		
			inputFromServer = new ObjectInputStream(server.getInputStream());
		}
		catch(IOException e)
		{
			System.out.println("\nerrore 0: " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initialize(String playerName)
	{
		
        System.out.println(playerName);
		frmBattleship = new JFrame("BattleShip");
		frmBattleship.setBounds(100, 100, 463, 295);
		frmBattleship.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		//FUTURA implementazione
		JPanel pnlButtons = new JPanel();
		pnlButtons.setAlignmentY(Component.TOP_ALIGNMENT);
		pnlButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JButton btn1 = new JButton("New button");
		pnlButtons.add(btn1);
		JButton btn2 = new JButton("New button");
		pnlButtons.add(btn2);
		JButton btn3 = new JButton("New button");
		pnlButtons.add(btn3);
		
		frmBattleship.getContentPane().add(pnlButtons, BorderLayout.NORTH);
		
		Giocatore giocatore = null;
		try 
		{
			outputToServer.writeObject(playerName);
			
//			public Giocatore(Point selectedCell, int boardSize, Vector<Nave> vector, int[][] matColpiti)
			giocatore = new Giocatore(new Point(-1, -1), inputFromServer.readInt(), (Vector<Nave>)inputFromServer.readObject(), 
										(int[][])inputFromServer.readObject(), playerName, (String)inputFromServer.readObject());	
			
			pnlPaint = new PnlPaintAreaBS(giocatore);
			
			pnlPaint.setAlignmentY(Component.TOP_ALIGNMENT);
			pnlPaint.setAlignmentX(Component.LEFT_ALIGNMENT);

			frmBattleship.getContentPane().add(pnlPaint, BorderLayout.CENTER);	//aggiunge Panel al frame principale 
			frmBattleship.setVisible(true);
		
		} 
		
		catch (ClassNotFoundException | IOException e) 
		{	
			e.printStackTrace();
		}
		
	}
	
	private void manageTurns(String playerName)
	{		
		Runnable turnsThread = () -> {	
			try 
			{
				boolean partitaInCorso = (boolean)inputFromServer.readObject();
				
				while(partitaInCorso == true)	//finchè server dice che la partita è in corso
				{
					boolean isMyTurn = (boolean)inputFromServer.readObject();
					
					System.out.println("my turn = " + isMyTurn);
					
					if(isMyTurn)
					{
						while(isMyTurn == true)	
						//se è vero cicla fino a quando server dice che non è suo turno
						{
							while(!pnlPaint.getGiocatore().isCellChanged() || pnlPaint.getGiocatore().getSelectedCell().equals(new Point(-1,-1))) 
							{
								Thread.sleep(1000);
//								System.out.println("waiting");
							}
							
							//arriva qua quando cellChanged = true e cella nella scacchiera
							pnlPaint.getGiocatore().setCellChanged(false);// una volta letta la rimetto a false, sennò resta sempre true
							
							Point cell = pnlPaint.getGiocatore().getSelectedCell();
							System.out.println("cell changed: " + cell.x + "." + cell.y);
							
							Request request = new Request(cell, playerName);
							
							outputToServer.writeObject(request);
							
							boolean response = (boolean)inputFromServer.readObject();
							System.out.println("colpito: " + response + " \n");
							pnlPaint.addColpo(response);	//se ritorna true aggiungo colpo true 
							//non passo il punto perchè pnlpaint ha già la selected cell
														
							isMyTurn = false;
						}
					}
				}
			} 
			catch (ClassNotFoundException | IOException | InterruptedException e) 
			{	
				e.printStackTrace();
			}
		};
		
		Thread t = new Thread(turnsThread);
		t.start();
	}
}
