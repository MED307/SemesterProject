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

	public static final int GRASS= 0;
	public static final int TREE = 1;
	public static final int STONE = 2;
	public static final int WATER = 3;
	public static final int ENEMY = 4;
	public static final int PLAYER = 5;

	
	
	//layers of the finale image
	BufferedImage gridLayer;
	BufferedImage terrainLayer;
	BufferedImage waterLayer;
	BufferedImage entitiesLayer;
	
	//terrain
	BufferedImage tree;
	
	BufferedImage stone;
	
	BufferedImage[] water;
	
	BufferedImage grass;
	
	BufferedImage enemy;
	
	
	//class Icons
	
	BufferedImage fighter;
	
	BufferedImage bard;
	
	BufferedImage wizard;
	
	BufferedImage barb;
	
	BufferedImage druid;
	
	BufferedImage monk;
	
	BufferedImage rogue;
	
	BufferedImage bloodhunt;
	
	BufferedImage cleric;
	
	BufferedImage paladin;
	
	BufferedImage ranger;
	
	BufferedImage sorc;
	
	BufferedImage warlock;
	
	//2D arraylist for the coordinates of where each square starts on the image
	ArrayList<ArrayList<ArrayList<Integer>>> gridList = new ArrayList<>();
	
	//the grid in types
	ArrayList<ArrayList<Integer>> gridInTypes = new ArrayList<>();
	
	private int sizeX;
	private int sizeY;
	private int squareWidth = 100;
	private int strokeWidth = 1;

	public Grid(int x, int y)
	{
		try 
		{
			//loads the images in from the resource folder
			tree = ImageIO.read(getClass().getResourceAsStream("/tree.png"));
			stone = ImageIO.read(getClass().getResourceAsStream("/stone.png"));
			water[0] = ImageIO.read(getClass().getResourceAsStream("/water1.png"));
			grass = ImageIO.read(getClass().getResource("/grass1.png"));
			enemy = ImageIO.read(getClass().getResource("/enemy1.png"));
			
			//load class icons
			fighter = ImageIO.read(getClass().getResource("/fighter.png"));
			bard = ImageIO.read(getClass().getResource("/bard.png"));
			wizard = ImageIO.read(getClass().getResource("/wizard.png"));
			barb = ImageIO.read(getClass().getResource("/barb.png"));
			druid = ImageIO.read(getClass().getResource("/druid.png"));
			monk = ImageIO.read(getClass().getResource("/monk.png"));
			rogue = ImageIO.read(getClass().getResource("/rogue.png"));
			bloodhunt = ImageIO.read(getClass().getResource("/bloodhunt.png"));
			cleric = ImageIO.read(getClass().getResource("/cleric.png"));
			paladin = ImageIO.read(getClass().getResource("/paladin.png"));
			ranger = ImageIO.read(getClass().getResource("/ranger.png"));
			sorc = ImageIO.read(getClass().getResource("/sorc.png"));
			warlock = ImageIO.read(getClass().getResource("/warlock.png"));
		}
		catch(IOException e)
		{
			e.printStackTrace();	
		}
		
		//saves the size
		sizeX = y;
		sizeY = x;
		
		//creates an image of that size
		gridLayer = new BufferedImage(sizeX * squareWidth, sizeY * squareWidth, BufferedImage.TYPE_4BYTE_ABGR);
		terrainLayer = new BufferedImage(sizeX * squareWidth, sizeY * squareWidth, BufferedImage.TYPE_4BYTE_ABGR);
		entitiesLayer = new BufferedImage(sizeX * squareWidth, sizeY * squareWidth, BufferedImage.TYPE_4BYTE_ABGR);
		
		//creates a 2D arrayList of each squares x start position
		for (int j = 0; j < sizeY; j++)
		{
			gridList.add(new ArrayList<>());
			gridInTypes.add(new ArrayList<>());
			
			
			for (int i = 0; i < sizeX; i ++)
	    	{
				gridInTypes.get(j).add(GRASS);
				gridList.get(j).add(new ArrayList<>());
				gridList.get(j).get(i).add((squareWidth)*i);
				gridList.get(j).get(i).add((squareWidth)*j);
	    	}
		}

		set();
		
	}
	
	
	//creates the grid lines
	public void set()
	{	
		//goes through the whole grid
		for (int x = 0; x < gridLayer.getWidth(); x++) 
		{	
            for (int y = 0; y < gridLayer.getHeight(); y++) 
            {
            	// color of the grid
            	int rgb;
                
                //for each square plus the first and last square
            	for (int i = 0; i < sizeX+2; i ++)
            	{
            		//checks if pixel is within the borders for the lines, with a thickness of strikewidth
                	if((squareWidth) * i > x - strokeWidth && (squareWidth) * i < x + strokeWidth)
                	{
                		//changes the color of the pixel to black
                		rgb = new Color(0,0,0).getRGB();
                        gridLayer.setRGB(x, y, rgb);
                	}
            	}
            	
            	//does the same for the y Axis
            	for (int i = 0; i < sizeY+2; i ++)
            	{
	            	if((squareWidth) * i > y - strokeWidth && (squareWidth) * i < y + strokeWidth)
	            	{
	            		//changes the color of the pixel to black
	            		rgb = new Color(0,0,0).getRGB();
	                    gridLayer.setRGB(x, y, rgb); 
	            	}
            	}
            }
        }
		//goes through each grid square
		for (int i = 0; i < sizeX; i ++) 
		{
			for (int j = 0; j < sizeY; j++)
			{
				//set the square to grass
				setSquare(i, j, GRASS);
			}
		}
	}
	
	//reset the layers
	public void resetEntities()
	{
		entitiesLayer = new BufferedImage(sizeX * squareWidth, sizeY * squareWidth, BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	public void resetTerrain()
	{
		terrainLayer = new BufferedImage(sizeX * squareWidth, sizeY * squareWidth, BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	public void setSquare(int x, int y, int type)
	{
		gridInTypes.get(y).set(x, type);
	}
	
	//changes the visuals of a specific squares, by redrawing that square using another buffered image
	public void drawSquare(int x, int y, int type)
	{
		BufferedImage WaterStructure = new BufferedImage(water[0].getWidth(), water[0].getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		if (type == WATER)
		{
			for (int h = 0; h < 4; h++)
			{
					switch(h) {
						case 0:
							if (gridInTypes.get(y).get(x-1) == WATER)
							{
								if (gridInTypes.get(y-1).get(x) == WATER)
								{
									if (gridInTypes.get(y-1).get(x-1) == WATER)
									{
										//waterStructure = full water
									}
									else
									{
										//waterStructure = curved knob
									}
									
								}
								else
								{
									//waterStructure = line down side
								}
							}
							else if (gridInTypes.get(y-1).get(x) == WATER)
							{
								//waterStructure = line across top
							}
							else
							{
								//waterStructure = closed Corner
							}

							break;
						case 1:
							if (gridInTypes.get(y).get(x + 1) == WATER)
							{
								if (gridInTypes.get(y-1).get(x) == WATER)
								{
									if (gridInTypes.get(y-1).get(x + 1) == WATER)
									{
										//waterStructure = full water
									}
									else
									{
										//waterStructure = curved knob
									}
									
								}
								else
								{
									//waterStructure = line down side
								}
							}
							else if (gridInTypes.get(y-1).get(x) == WATER)
							{
								//waterStructure = line across top
							}
							else
							{
								//waterStructure = closed Corner
							}
							break;
						case 2:
							if (gridInTypes.get(y).get(x-1) == WATER)
							{
								if (gridInTypes.get(y+1).get(x) == WATER)
								{
									if (gridInTypes.get(y+1).get(x-1) == WATER)
									{
										//waterStructure = full water
									}
									else
									{
										//waterStructure = curved knob
									}
									
								}
								else
								{
									//waterStructure = line down side
								}
							}
							else if (gridInTypes.get(y+1).get(x) == WATER)
							{
								//waterStructure = line across top
							}
							else
							{
								//waterStructure = closed Corner
							}
							break;
						case 3:
							if (gridInTypes.get(y).get(x + 1) == WATER)
							{
								if (gridInTypes.get(y + 1).get(x) == WATER)
								{
									if (gridInTypes.get(y + 1).get(x + 1) == WATER)
									{
										//waterStructure = full water
									}
									else
									{
										//waterStructure = curved knob
									}
									
								}
								else
								{
									//waterStructure = line down side
								}
							}
							else if (gridInTypes.get(y + 1).get(x) == WATER)
							{
								//waterStructure = line across top
							}
							else
							{
								//waterStructure = closed Corner
							}
							break;
					}
				}
		}
		//goes through each y value of that specific square
		for(int j = gridList.get(y).get(x).get(1) + strokeWidth; j < ((squareWidth)*(y+1)) - strokeWidth + 1; j++) 
		{	
			//goes through each x value of that specific square
			for(int i = gridList.get(y).get(x).get(0) + strokeWidth ; i < ((squareWidth)*(x+1)) - strokeWidth + 1; i++) 
			{
				//basis color as a RGB integer
				int rgb = new Color(0,0,0).getRGB();
				
				//if type is tree
				if (type == TREE)
				{
					rgb = tree.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));

					//changes the color of the pixel
					terrainLayer.setRGB(i, j, rgb);
				}
				
				//if type is stone
				else if (type == STONE)
				{
					//changes the color to the corresponding color on the stone image
					rgb = stone.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));

					//changes the color of the pixel
					terrainLayer.setRGB(i, j, rgb);
				}
				
				//if type is water
				else if (type == WATER)
				{
					
					//changes the color to the corresponding color on the water image
					rgb = water[0].getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
					
					//changes the color of the pixel
					terrainLayer.setRGB(i, j, rgb);
					waterLayer.setRGB(i, j, rgb);
				}

				//if type is enemy
				else if (type == ENEMY)
				{
					//changes the color to the corresponding color on the water image
					rgb = enemy.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));

					//changes the color of the pixel
					entitiesLayer.setRGB(i, j, rgb);
				}
				
				//if invalid type is given, draw grass instead
				else
				{
					//changes the color to the corresponding color on the grass image
					rgb = grass.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
					
					//changes the color of the pixel
					gridLayer.setRGB(i, j, rgb);
				}
				

			}
		}
	}
	
	//Overloaded method to draw the characters as their respected class
		public void setSquare(int x, int y, int type, String classes)
		{
			String playerClass = classes.toLowerCase();
			//goes through each y value of that specific square
			for(int j = gridList.get(y).get(x).get(1) + strokeWidth; j < ((squareWidth)*(y+1)) - strokeWidth + 1; j++) 
			{	
				//goes through each x value of that specific square
				for(int i = gridList.get(y).get(x).get(0) + strokeWidth ; i < ((squareWidth)*(x+1)) - strokeWidth + 1; i++) 
				{
					//basis color as a RGB integer
					int rgb = new Color(0,0,0).getRGB();
					
					//if type is player
					if (type == PLAYER)
					{
						//checks the class
						if (playerClass.compareTo("fighter") == 0) 
						{
							rgb = fighter.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("bard") == 0)
						{
							rgb = bard.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("barbarian") == 0)
						{
							rgb = barb.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("wizard") == 0)
						{
							rgb = wizard.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("druid") == 0)
						{
							rgb = druid.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("monk") == 0)
						{
							rgb = monk.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("rogue") == 0)
						{
							rgb = rogue.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("bloodhunter") == 0)
						{
							rgb = bloodhunt.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("cleric") == 0)
						{
							rgb = cleric.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("paladin") == 0)
						{
							rgb = paladin.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("ranger") == 0)
						{
							rgb = ranger.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("sorcerer") == 0)
						{
							rgb = sorc.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						//checks the class
						else if (playerClass.compareTo("warlock") == 0)
						{
							rgb = warlock.getRGB(i - (gridList.get(y).get(x).get(0) + strokeWidth), j - (gridList.get(y).get(x).get(1) + strokeWidth));
						}
						
						//changes the color of the pixel
						entitiesLayer.setRGB(i, j, rgb);
					}
				}
			}
		}
	
	
	//returns the buffered images as a standard image, for FXML to display
	public Image Display()
	{
		
		//adds the 3 images on top of each other creating the single image to be displayed.
		BufferedImage c = new BufferedImage(gridLayer.getWidth(), gridLayer.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = c.getGraphics();
		g.drawImage(gridLayer,0,0,null);
		g.drawImage(terrainLayer,0,0,null);
		g.drawImage(waterLayer,0,0,null);
		g.drawImage(entitiesLayer,0,0,null);
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
