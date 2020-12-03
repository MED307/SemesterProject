package application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Grid {
	
	BufferedImage grid;
	
	BufferedImage tree;
	
	BufferedImage stone;
	
	BufferedImage water;
	
	BufferedImage grass;
	
	ArrayList<ArrayList<Integer>> gridList = new ArrayList<>();
	
	private int sizeX;
	private int sizeY;
	private int squareWidth;
	private int strokeWidth = 1;

	public Grid(int x, int y)
	{
		try 
		{
			tree = ImageIO.read(getClass().getResource("/treegrass.png"));
			stone = ImageIO.read(getClass().getResource("/stonegrass.png"));
			water = ImageIO.read(getClass().getResource("/water1.png"));
			grass = ImageIO.read(getClass().getResource("/grass1.png"));
		}
		catch(IOException e)
		{
			e.printStackTrace();	
		}
		sizeX = x;
		sizeY = y;
		grid = new BufferedImage(sizeX * 100, sizeY * 100, BufferedImage.TYPE_3BYTE_BGR);
		squareWidth = grid.getWidth()/sizeY;
		for (int j = 0; j < sizeY; j++)
		{
			gridList.add(new ArrayList<>());
			
			for (int i = 0; i < sizeX; i ++)
	    	{
				gridList.get(j).add((squareWidth)*i);
	    	}
		}
		set();
		
	}
	
	
	public void set()
	{
		for (int x = 0; x < grid.getWidth(); x++) 
		{
            for (int y = 0; y < grid.getHeight(); y++) 
            {
            	
            	int rgb = new Color(0,120,0).getRGB();
                grid.setRGB(x, y, rgb);
                
            	for (int i = 0; i < sizeX+2; i ++)
            	{
                	if((squareWidth) * i > x - strokeWidth && (squareWidth) * i < x + strokeWidth)
                	{
                		rgb = new Color(0,0,0).getRGB();
                        grid.setRGB(x, y, rgb);
                	}
            	}
            	
            	for (int i = 0; i < sizeY+2; i ++)
            	{
	            	if((squareWidth) * i > y - strokeWidth && (squareWidth) * i < y + strokeWidth)
	            	{
	            		rgb = new Color(0,0,0).getRGB();
	                    grid.setRGB(x, y, rgb); 
	            	}
            	}
            }
        }
	}
	
	
	public void setSquare(int x, int y, String type)
	{
		for(int j = (y*(squareWidth) + strokeWidth); j < ((squareWidth)*(y+1)) - strokeWidth + 1; j++) 
		{
			for(int i = gridList.get(y).get(x) + strokeWidth ; i < ((squareWidth)*(x+1)) - strokeWidth + 1; i++) 
			{
				int rgb = new Color(0,255,122).getRGB();

				if (type.compareTo("tree") == 0)
				{
					rgb = tree.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));
				}
				else if (type.compareTo("stone") == 0)
				{
					rgb = stone.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));
				}
				else if (type.compareTo("water") == 0)
				{
					rgb = water.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));
				}
				else
				{
					rgb = grass.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));
				}
				
				
				if(rgb == 0)
				{
					rgb = grass.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));
				}
				
				grid.setRGB(i, j, rgb);
			}
		}
	}
	
	
	
	
	public Image Display()
	{
		return SwingFXUtils.toFXImage(grid, null);
	}
	
	
	public int getColoumns() {
		return sizeX;
	}
	
	public int getRows() {
		return sizeY;
	}
	
}
