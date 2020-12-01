package application;



public class Blob {

	
	private String name; 
	
	private String type;
	
	private int[] locationXY = new int[2];
	
	private double color;
	
	
	int xPositions;
	int yPositions;
	

	int area;
	
	
	
	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getxPositions() {
		return xPositions;
	}

	public void setxPositions(int xPositions) {
		this.xPositions = xPositions;
	}

	public int getyPositions() {
		return yPositions;
	}

	public void setyPositions(int yPositions) {
		this.yPositions = yPositions;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLocationX() {
		return locationXY[0];
	}

	public void setLocationX(int locationX) {
		this.locationXY[0] = locationX;
	}
	
	
	public int getLocationY() {
		return locationXY[1];
	}

	public void setLocationY(int locationY) {
		this.locationXY[1] = locationY;
	}

	public double getColor() {
		return color;
	}

	public void setColor(double color) {
		this.color = color;
	}
	

	
}
