package it.unibs.pajc;

import java.awt.Point;
import java.io.Serializable;

//Creo questa classe per dare un'identità definità ai colpi che il client manda al server
//Point è generico

public class Request implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Point point;
	private String player;
	
	public Request(Point point, String player)
	{
		this.point = point;
		this.player = player;
	}
	
	public void setPoint(Point point)
	{
		this.point = point;
	}
	public Point getPoint()
	{
		return this.point;
	}
	
	public void setPlayer(String player)
	{
		this.player = player;
	}
	public String getPlayer()
	{
		return this.player;
	}
}

