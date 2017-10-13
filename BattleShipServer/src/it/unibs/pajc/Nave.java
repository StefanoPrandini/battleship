package it.unibs.pajc;

import java.awt.Point;
import java.io.Serializable;
import java.util.Vector;

public class Nave implements Serializable
{	
	private static final long serialVersionUID = 1L;
	String id;
	Vector<Point> posizione;
	int lunghezza;
	
	public Nave (String id, Vector<Point> posizione, int lunghezza)
	{
		this.id = id;
		this.posizione = posizione;
		this.lunghezza = lunghezza;
	}	
		
}
