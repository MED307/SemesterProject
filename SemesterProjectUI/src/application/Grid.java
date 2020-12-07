package application;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Grid {
	
	BufferedImage grid;
	BufferedImage terrain;
	BufferedImage entities;
	
	BufferedImage tree;
	
	BufferedImage stone;
	
	BufferedImage water;
	
	BufferedImage grass;

	BufferedImage fighter;
	
	BufferedImage bard;
	
	BufferedImage wizard;
	
	BufferedImage enemy;
	
	ArrayList<ArrayList<Integer>> gridList = new ArrayList<>();
	
	private int sizeX;
	private int sizeY;
	private int squareWidth = 100;
	private int strokeWidth = 1;

	public Grid(int x, int y)
	{
		try 
		{
			//loads the images in from the resource folder
			tree = ImageIO.read(getClass().getResource("/tree.png"));
			stone = ImageIO.read(getClass().getResource("/stone.png"));
			water = ImageIO.read(getClass().getResource("/water1.png"));
			grass = ImageIO.read(getClass().getResource("/grass1.png"));
			fighter = ImageIO.read(getClass().getResource("/fighter.png"));
			bard = ImageIO.read(getClass().getResource("/bard.png"));
			wizard = ImageIO.read(getClass().getResource("/wizard.png"));
			enemy = ImageIO.read(getClass().getResource("/enemy1.png"));
		}
		catch(IOException e)
		{
			e.printStackTrace();	
		}
		
		//saves the size
		sizeX = x;
		sizeY = y;
		
		//creates an image of that size
		grid = new BufferedImage(sizeX * squareWidth, sizeY * squareWidth, BufferedImage.TYPE_4BYTE_ABGR);
		terrain = new BufferedImage(sizeX * squareWidth, sizeY * squareWidth, BufferedImage.TYPE_4BYTE_ABGR);
		entities = new BufferedImage(sizeX * squareWidth, sizeY * squareWidth, BufferedImage.TYPE_4BYTE_ABGR);
		
		//creates a 2D arrayList of each squares x start position
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
	
	
	//creates the grid lines
	public void set()
	{
		//goes through the whole grid
		for (int x = 0; x < grid.getWidth(); x++) 
		{	
            for (int y = 0; y < grid.getHeight(); y++) 
            {
            	// color of the grid
            	int rgb = new Color(0,120,0).getRGB();
            	
            	//sets the pixel to be that color
                grid.setRGB(x, y, rgb);
                
                //for each square plus the first and last square
            	for (int i = 0; i < sizeX+2; i ++)
            	{
            		//checks if pixel is within the borders for the lines, with a thickness of strikewidth
                	if((squareWidth) * i > x - strokeWidth && (squareWidth) * i < x + strokeWidth)
                	{
                		//changes the color of the pixel to black
                		rgb = new Color(0,0,0).getRGB();
                        grid.setRGB(x, y, rgb);
                	}
            	}
            	
            	//does the same for the y Axis
            	for (int i = 0; i < sizeY+2; i ++)
            	{
	            	if((squareWidth) * i > y - strokeWidth && (squareWidth) * i < y + strokeWidth)
	            	{
	            		//changes the color of the pixel to black
	            		rgb = new Color(0,0,0).getRGB();
	                    grid.setRGB(x, y, rgb); 
	            	}
            	}
            }
        }
	}
	
	public void resetEntities()
	{
		entities = new BufferedImage(sizeX * squareWidth, sizeY * squareWidth, BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	public void resetTerrain()
	{
		terrain = new BufferedImage(sizeX * squareWidth, sizeY * squareWidth, BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	//changes the visuals of a specific squares, by redrawing that square using another buffered image
	public void setSquare(int x, int y, String type)
	{
		//goes through each y value of that specific square
		for(int j = (y*(squareWidth) + strokeWidth); j < ((squareWidth)*(y+1)) - strokeWidth + 1; j++) 
		{	
			//goes through each x value of that specific square
			for(int i = gridList.get(y).get(x) + strokeWidth ; i < ((squareWidth)*(x+1)) - strokeWidth + 1; i++) 
			{
				//basis color as a RGB integer
				int rgb = new Color(0,0,0).getRGB();
				
				//if type is tree
				if (type.compareTo("tree") == 0)
				{
					rgb = tree.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));

					//changes the color of the pixel
					terrain.setRGB(i, j, rgb);
				}
				
				//if type is stone
				else if (type.compareTo("stone") == 0)
				{
					//changes the color to the corresponding color on the stone image
					rgb = stone.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));

					//changes the color of the pixel
					terrain.setRGB(i, j, rgb);
				}
				
				//if type is water
				else if (type.compareTo("water") == 0)
				{
					//changes the color to the corresponding color on the water image
					rgb = water.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));
					
					//changes the color of the pixel
					terrain.setRGB(i, j, rgb);
				}

				//if type is enemy
				else if (type.compareTo("enemy") == 0)
				{
					//changes the color to the corresponding color on the water image
					rgb = enemy.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));

					//changes the color of the pixel
					entities.setRGB(i, j, rgb);
				}
				
				//if invalid type is given, draw grass instead
				else
				{
					//changes the color to the corresponding color on the grass image
					rgb = grass.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));
					
					//changes the color of the pixel
					grid.setRGB(i, j, rgb);
				}
				

			}
		}
	}
	
	//changes the visuals of a specific squares, by redrawing that square using another buffered image
		public void setSquare(int x, int y, String type, String playerClass)
		{
			//goes through each y value of that specific square
			for(int j = (y*(squareWidth) + strokeWidth); j < ((squareWidth)*(y+1)) - strokeWidth + 1; j++) 
			{	
				//goes through each x value of that specific square
				for(int i = gridList.get(y).get(x) + strokeWidth ; i < ((squareWidth)*(x+1)) - strokeWidth + 1; i++) 
				{
					//basis color as a RGB integer
					int rgb = new Color(0,0,0).getRGB();
					
					//if type is player
					if (type.compareTo("player") == 0)
					{
						if (playerClass.compareTo("fighter") == 0) 
						{
							rgb = fighter.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));
						}
						else if (playerClass.compareTo("bard") == 0)
						{
							rgb = bard.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));
						}
						else if (playerClass.compareTo("bard") == 0)
						{
							rgb = wizard.getRGB(i - (gridList.get(y).get(x) + strokeWidth), j - ((y*(squareWidth)) + strokeWidth));
						}
						
						//changes the color of the pixel
						entities.setRGB(i, j, rgb);
					}
				}
			}
		}
	
	
	//returns the buffered images as a standard image, for FXML to display
	public Image Display()
	{
		
		//adds the 3 images on top of each other creating the single image to be displayed.
		BufferedImage c = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = c.getGraphics();
		g.drawImage(grid,0,0,null);
		g.drawImage(terrain,0,0,null);
		g.drawImage(entities,0,0,null);
		g.dispose();

		return SwingFXUtils.toFXImage(c, null);
	}
	
	
	public int getColoumns() {
		return sizeX;
	}
	
	public int getRows() {
		return sizeY;
	}
	
}
