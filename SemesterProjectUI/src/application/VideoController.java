package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * The controller associated with the only view of our application. The
 * application logic is implemented here. It handles the button for
 * starting/stopping the camera, the acquired video stream, the relative
 * controls and the histogram creation.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @version 2.0 (2017-03-10)
 * @since 1.0 (2013-11-20)
 * 
 */
public class VideoController
{
	//the FXML elements
	@FXML
	private Button cameraControlBtn;

	@FXML
	private Slider hueStart;
	
	@FXML
	private Slider nextCamera;
	
	@FXML
	private Text SliderNumber;

	@FXML
	private ImageView currentFrame;

	@FXML
	private ImageView currentFrame1;
	
	@FXML
	private Pane helpPane;
	
	@FXML
	private Pane cameraControlPane;
	
	@FXML
	private ListView<Player> playerListVIew;

	@FXML
	private 
	Label rollResultValueLabel;
	
	@FXML 
	Label rollResultsLabel;
	
	
	//List of players on the map
	private ArrayList<Player> players =  new ArrayList<>();
	
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	
	// the OpenCV object that 
	private VideoCapture capture;
	
	// a flag to change the button behavior
	private boolean cameraActive;
	
	// the image to be processed
	Mat thisFrame;

	ArrayList<Double>gridColour;

	boolean gridFound = false;
	
	boolean controlsActive = false;
	
	private Boolean open = false;

	private Boolean close = false;
	
	boolean helpActive = false;
	
	
	//HSV values for thresholding the incoming image
	private int saturationStart = 127;

	private int saturationStop = 127;

	private int valueStart = 127;

	private int valueStop = 127;

	private int hueStop = 40;

	
	//saved image of the grid found, before changing the HSV values and begin thresholding for colors
	Mat gridImage = new Mat();
	
	//the virtual Grid
	Grid grid; 

	//list of all the squares in the grid as blobs
	Blob[] gridSquares;
	
	//scalar values used for thresholding
	Scalar minValues;
	Scalar maxValues;
	
	//FXML functions for rolling the different types of dices
	@FXML
	private void rollD20(){
		dieRoll(20);
	}
	
	@FXML
	private void rollD12(){
		dieRoll(12);
	}
	
	@FXML
	private void rollD10(){
		dieRoll(10);
	}
	
	@FXML
	private void rollD8(){
		dieRoll(8);
	}
	
	@FXML
	private void rollD6(){
		dieRoll(6);
	}
	
	@FXML
	private void rollD4(){
		dieRoll(4);
	}

	//function for terminating the program
	@FXML
	protected void exitProgram() 
	{
		System.exit(0);
	}

	//die roller
	public void dieRoll(int die)
	{
		//gets a random number between 0.0 and 1.0 then time if the size of the die, and plus one, then cast it to an int
		int roll = (int) (Math.random() * die) + 1;
		
		//display the result
		rollResultsLabel.setText("Roll Results of D"+ die);
		rollResultValueLabel.setText(" " + roll);
	}

	//run right as the program starts
	public void initialize()
	{
		this.capture = new VideoCapture();
		this.cameraActive = false;
		playerListVIew.setCellFactory(chatRoomListView -> new PlayerListCellController());
		nextCamera.valueProperty().addListener((obs, oldval, newVal) ->
			{nextCamera.setValue(Math.round(newVal.doubleValue()));
			double test = nextCamera.getValue();
			SliderNumber.setText("" + test);
			});
	}

	//open and close camera window
	@FXML
	protected void showCamera() {
		if (controlsActive){
			cameraControlPane.setVisible(false);
			controlsActive = false;
		}
		else {
			cameraControlPane.setVisible(true);
			controlsActive = true;
		}
	}
	
	//open and close help window
	@FXML
	protected void showHelp() {
		if (helpActive){
			helpPane.setVisible(false);
			helpActive = false;
		}
		else {
			helpPane.setVisible(true);
			helpActive = true;
		}
	}
	
	boolean test;
	@FXML
	protected void addPlayer()
	{
		PopUp pop = new PopUp();
		Player p = null;
		try {
			p = pop.addPlayer("New Player", "addPlayer.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (p != null)
		{
			players.add(p);
			playerListVIew.getItems().add(p);
		}

	}
	
	@FXML
	protected void startCamera()
	{
		if (!this.cameraActive)
		{
			// start the video capture
			this.capture.open((int)this.nextCamera.getValue());

			// is the video stream available?
			if (this.capture.isOpened())
			{
				this.cameraActive = true;

				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run()
					{
						// effectively grab and process a single frame
						Mat frame = grabFrame();
						// convert and show the frame
						Image imageToShow = Utils.mat2Image(frame);
						updateImageView(currentFrame, imageToShow);
					}
				};
				
				//Adds the frameGrabber to an ExecutorService, that will execute the code on a timer
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				// update the button content
				this.cameraControlBtn.setText("Stop Camera");
			}
			else
			{
				// log the error
				System.err.println("Impossible to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.cameraControlBtn.setText("Start Camera");

			// stop the timer
			this.stopAcquisition();
		}
	}


	@FXML
	protected void gridOnClick()
	{
		getGrid(thisFrame,gridColour);
	}

	@FXML
	protected void gridFoundOnClick()
	{
		
		//changes the threshold values when the grid has been found
		if(!gridFound) {
			gridFound= true;
			saturationStart = 50;
			saturationStop = 220;
			valueStart = 0;
			valueStop = 255;
			hueStop = 10;
			open = true;
			close = true;
		}
		//return the values back to normal
		else {
			gridFound = false;
			saturationStart = 125;
			saturationStop = 125;
			valueStart = 125;
			valueStop = 125;
			hueStop = 30;
			open = false;
			close = false;
		}
	}
 
	@FXML
	protected void getBlobOnClick()
	{
		
		//if grid is found
		if(gridFound)
		{		
			grid.resetEntities();
			//finds all the objects with a hue of green
			hueStart.setValue(87);
			grabFrame();
			getBlob(thisFrame,gridColour, Grid.TREE);
			
			//finds all objects with a hue of blue
			hueStart.setValue(105);
			grabFrame();
			getBlob(thisFrame,gridColour, Grid.WATER);
			
			//red
			hueStart.setValue(170);
			grabFrame();
			getBlob(thisFrame,gridColour, Grid.STONE);
				
			//yellow
			hueStart.setValue(14);
			grabFrame();
			getBlob(thisFrame,gridColour, Grid.ENEMY);
			
			//purple
			//hueStart.setValue(162);
			//grabFrame();
			//getBlob(thisFrame,gridColour, Grid.PLAYER);
		}
	}
	
	
	@FXML
	protected void setValuesOnClick()
	{
        //Values for specific blob
		minValues = new Scalar(0, 0, 150);
		maxValues = new Scalar(180, 250, 255);


	}


	private Mat grabFrame()
	{
		Mat frame = new Mat();

		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				// read the current frame
				this.capture.read(frame);

				// if the frame is not empty, process it
				if (!frame.empty())
				{
					//If the grid has already been found run this
					if(gridFound) 
					{
						Mat hsvOutput = new Mat();
						
						// Converts the frame grabbed from using RGB to HSV
						Imgproc.cvtColor(frame, hsvOutput, Imgproc.COLOR_BGR2HSV);

						//creates two scalars with one containing the minimum and the other containing the maximum values of the HSV color space threshold
						minValues = new Scalar(this.hueStart.getValue(), this.saturationStart, this.valueStart);
						maxValues = new Scalar(this.hueStart.getValue() + hueStop, this.saturationStop, this.valueStop);

						//Checks if the Image is within those thresholds
						Core.inRange(hsvOutput, minValues, maxValues, frame);

					}
					// if Grid has not yet been found
					if(!gridFound) {
						Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);

						//creates two scalars with one containing the minimum and the other containing the maximum values of the HSV colorspace treshold
						minValues = new Scalar(this.hueStart.getValue(), this.saturationStart, this.valueStart);
						maxValues = new Scalar(255, this.saturationStop, this.valueStop);

						//a static method for thresholding called inRange() from the Core class from the OpenCV library is used
						Core.inRange(frame, minValues, maxValues, frame);

						// Create the CV_8U version of the distance image
						// It is needed for findContours()
						Mat dist_8u = new Mat();
						
						//Converts the mat to using 8 bit unsigned color meaning colors between 0-255
						frame.convertTo(dist_8u, CvType.CV_8U);

						// Find total grid using the build in blob detection from openCV
						List<MatOfPoint> contours = new ArrayList<>();
						
						//not used, but the openCV requires this for the function to run
						Mat hierarchy = new Mat();
						
						//blob detection
						Imgproc.findContours(dist_8u, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

						// Create the marker image for the watershed algorithm
						Mat markers = Mat.zeros(frame.size(), CvType.CV_32S);

						// Draw the foreground markers
						for (int i = 0; i < contours.size(); i++) {
							Imgproc.drawContours(markers, contours, i, new Scalar(i + 1), Core.FILLED);

						} 

						// Draw the background marker
						Mat markersScaled = new Mat();
						
						//Converts the mat to using 32 bit floats color meaning colors between 0.0-1.0
						markers.convertTo(markersScaled, CvType.CV_32F);
						
						//normalizes the values to be between 0 and 255
						Core.normalize(markersScaled, markersScaled, 0.0, 255.0, Core.NORM_MINMAX);
						
						//converts the image back to 8bit unsigned
						markersScaled.convertTo(markersScaled, CvType.CV_8U);
						
						//sets frame to the image
						frame = markersScaled;
						
						//creates a list of all the contours colors, to be used to determine the amount of blobs found
						ArrayList<Double> colors = new ArrayList<>();
						
						//add all colors not black to the list
						for (int y = 0; y < frame.height(); y++){
							for(int x = 0; x < frame.width(); x++){
								if( frame.get(y,x)[0] != 0){
									colors.add(frame.get(y,x)[0]);
								}
							}
						} 
						
						//A HashSet is a collection where every item is unique, therefore if the same color pops up twice it is only added once.
						Set<Double> uniqueColors = new HashSet<Double>(colors);
						
						//this gives us a list of each unique color used to describe a specific blob in the image, which is added to a global variable
						gridColour = new ArrayList<>(uniqueColors);		
					}
					
					//kernel for use to do open and close
					Mat Element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9));

					if(open) {
						//a new matrix of type Mat from the OpevCV library is created for the output image after erosion has been applied
						Mat erodeOutput = new Mat();

						//the erode() and dilate() methods are used respectively in order to apply a opening to the video feed
						Imgproc.erode(frame, erodeOutput, Element);
						Imgproc.dilate(erodeOutput, frame, Element);
			
					}

					if(close) {
						//a new matrix of type Mat from the OpevCV library is created for the output image after dilation has been applied
						Mat dilateOutput = new Mat();

						//the dilate() and erode() methods are used respectively in order to apply a opening to the video feed
						Imgproc.dilate(frame, dilateOutput, Element);
						Imgproc.erode(dilateOutput, frame, Element);
					}
					
					//adds the frame to the global variable thisFrame
					thisFrame = frame;
				}
			}
			catch (Exception e)
			{
				System.err.println("Exception during the frame elaboration: " + e);
			}
		}
		return frame;
	}

	private void stopAcquisition()
	{
		//checks if the timer exists and if it has already been shut down
		if (this.timer != null && !this.timer.isShutdown())
		{
			try
			{
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}

		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
	}

	// updates the viewed image through a runnable (runLater)
	private void updateImageView(ImageView view, Image image)
	{
		Platform.runLater(() -> {
			view.setImage(image);
		});
	}


	 //On application close, stop the acquisition from the camera
	protected void setClosed()
	{
		this.stopAcquisition();
	}

	public void getGrid (Mat frame, ArrayList<Double>gridColour) 
	{
		
		ArrayList<Double> list = new ArrayList<>();
		
		//goes through the image finding how many different colored blobs there are in one x value to determine the grid ratio
		for (int y = frame.height() ; y > 0 ; y--)
		{
			
			// if the color is not equal to a line
			if (frame.get(frame.height()-y,5)[0] != 0) 
			{
				list.add(frame.get(frame.height()-y,5)[0]);
			}
		}
		
		//A HashSet is a collection where every item is unique, therefore if the same color pops up twice it is only added once.
		Set<Double> uniqueRows = new HashSet<Double>(list);
		
		//creates an array list of the values
		ArrayList<Double> uRows = new ArrayList<>(uniqueRows);
		
		//calculates the amount of rows and coloums from the colors in one line and the known amount of grid squares (gridColour)
		int rows = uRows.size();
		int coloumns =gridColour.size() / rows;
		
		//creates a virtual grid with those ratios
		grid = new Grid(rows,coloumns);
		
		//displays the grid
		currentFrame1.setImage(grid.Display());
		
		//saves the image of the grid
		gridImage = frame;
		
		//sort gridColour from highest to lowest, as the first grid square, has the highest color
		Collections.sort(gridColour, Collections.reverseOrder());
		
		//creates an array of the blobs
		gridSquares = new Blob[gridColour.size()];
		
		// for each element in the array, create a new blob and fill out the information on where in the grid the blob is and what color the blob is.
		for(int i = 0; i < gridColour.size(); i++) 
		{
			gridSquares[i] = new Blob();
			gridSquares[i].setColor(gridColour.get(i));
			gridSquares[i].setLocationX(i % coloumns);
			gridSquares[i].setLocationY(i / coloumns);
		}
	}

	public void getBlob (Mat frame, ArrayList<Double>gridColour, int type) 
	{	
		//does the same kind of blob detection as when finding the grid
		Mat dist_8u = new Mat();
		frame.convertTo(dist_8u, CvType.CV_8U);
		// Find total markers
		List<MatOfPoint> contours = new ArrayList<>();
		
		//not used, but needed for finding Contours
		Mat hierarchy = new Mat();
		Imgproc.findContours(dist_8u, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		// Create the marker image for the watershed algorithm
		Mat markers = Mat.zeros(frame.size(), CvType.CV_32S);

		// Draw the contours onto a matrix
		for (int i = 0; i < contours.size(); i++) {
			Imgproc.drawContours(markers, contours, i, new Scalar(i + 1), -1);
		} 

		// convert the matrix into floats with the values from markers
		Mat markersScaled = new Mat();
		markers.convertTo(markersScaled, CvType.CV_32F);
		
		//normalizes the values to be between 0.0 and 255.0
		Core.normalize(markersScaled, markersScaled, 0.0, 255.0, Core.NORM_MINMAX);
		Mat markersDisplay = new Mat();
		
		//converts it to use integers between 0 and 255
		markersScaled.convertTo(markersDisplay, CvType.CV_8U);
		
		//sets the value of frame
		frame = markersDisplay;
		
		//finds the colors of the blobs
		ArrayList<Double> terrainColors = new ArrayList<>();
		
		for (int y = 0; y < frame.height(); y++)
		{
			for(int x = 0; x < frame.width(); x++)
			{
				if(frame.get(y,x)[0] != 0)
				{
					terrainColors.add(frame.get(y,x)[0]);
				}
			}
		} 
		
		//makes sure each color is only added once
		Set<Double> setTerrainColors = new HashSet<Double>(terrainColors);
		ArrayList<Double> uniqueTerrainColors = new ArrayList<>(setTerrainColors);
		
		//creates a list of blobs
		ArrayList<Blob> terrainBlobs = new ArrayList<>();	
		
		//gets the area of the blob, by check each pixel and seeing if they are the same color as the blob, if so add it to the blob
		for (int c = 0; c < uniqueTerrainColors.size(); c++ ) 
		{	
			int area = 0;
			int xPos = 0;
			int yPos = 0;
			
			Blob blob = new Blob();
			terrainBlobs.add(blob);
			
			//double for loop to check through the whole picture
			for (int y = 0; y < frame.height(); y++)
			{
				for(int x = 0; x < frame.width(); x++)
				{
					//check if the pixel and the blob is the same color
					if(frame.get(y,x)[0] == uniqueTerrainColors.get(c))
					{
						//increases the value of the blobs area
						area +=1;
						terrainBlobs.get(c).setArea(area);
						
						//increases the total x and y location, which is used with the area later to find the Center Of Mass
						xPos += x;
						yPos += y;
						terrainBlobs.get(c).setxPositions(xPos);
						terrainBlobs.get(c).setyPositions(yPos);
					}
				}
			}
			
			int xMid;
			int yMid;
			
			// finds the Center Of Mass
			xMid = terrainBlobs.get(c).getxPositions() / terrainBlobs.get(c).getArea();
			yMid = terrainBlobs.get(c).getyPositions() / terrainBlobs.get(c).getArea();
			
			//adds the coordinates of it to the blob
			terrainBlobs.get(c).setLocationX(xMid); 
			terrainBlobs.get(c).setLocationY(yMid);
		}
		
		//list of colors on the grid corresponding with the position of the blob
		double[] squareColors = new double[uniqueTerrainColors.size()];
		
		for(int i = 0; i < squareColors.length; i++ ) 
		{
			//adds the color of the grid using the coordinates of the blobs center of mass
			squareColors[i] = gridImage.get(terrainBlobs.get(i).getLocationY(),terrainBlobs.get(i).getLocationX())[0];
		}
		
		int newPlayerX = 1000;
		int newPlayerY = 1000;
		//goes through each square of the grid
		for (int i = 0; i < gridSquares.length;i++) 
		{	
			//goes through each color determined on line 571
			for(int j = 0; j < squareColors.length; j++) 
			{
				// if they differ less than 0.1 from each other, then make that square on the grid a version of the blob currently being detected for (Type)
				if((gridSquares[i].getColor() - squareColors[j]) < 0.1 && (gridSquares[i].getColor() - squareColors[j]) > -0.1 )
				{
					//set the grid to be of type
					grid.setSquare(gridSquares[i].getLocationX(), gridSquares[i].getLocationY(), type);
					System.out.println(gridSquares[i].getLocationX() + " " + gridSquares[i].getLocationY());
					
					//if type is player
					if (type == Grid.PLAYER)
					{
						//look through each player
						for (Player e : players)
						{
							//if the player is not already placed and another player does not already have this position
							if (!e.isPlaced() && Utils.checkPlayerPos(players, gridSquares[i].getLocationX(), gridSquares[i].getLocationY()))
							{
								//set the position of the player to be the found square and set it to have been placed
								e.setPos(gridSquares[i].getLocationX(), gridSquares[i].getLocationY());
								e.setMoved(false);
								e.setPlaced(true);
								break;
							}
							
							//if players position is the same as the found square set the player to not have been moved
							if (e.getPos()[0] == gridSquares[i].getLocationX() && e.getPos()[1] == gridSquares[i].getLocationY())
							{
								e.setMoved(false);
							}

						} 
						
						//if no one has this position set them to be the newPlayerPos
						if (Utils.checkPlayerPos(players, gridSquares[i].getLocationX(), gridSquares[i].getLocationY()))
						{
							newPlayerX = gridSquares[i].getLocationX();
							newPlayerY = gridSquares[i].getLocationY();
						}

						
					}
				}
			}
		}
		
		//then after checking through all squares and the type is player
		if (type == Grid.PLAYER)
		{
			//for each player
			for (Player e : players)
			{
				System.out.println(e.getName() + ": " + " x: "+ e.getPos()[0] + " y: " + e.getPos()[1]);
				
				//check if a player is missing, and that is not moved
				if (players.size() > terrainBlobs.size())
				{
					//if the player has not yet been placed, tell it so
					e.setPlaced(false);
				}
				
				// if a player has moved
				if (e.isMoved())
				{
					// give it the position of the square with no existing player
					e.setPos(newPlayerX, newPlayerY);
				}
				else
				{
					//set the moved to true, as this will change if the position is found
					e.setMoved(true);
				}
				//as a square not placed is set to be 1000, this only places squares which has been places
				if (e.getPos()[0] < 500)
				{
					//sets the grid squares and displays the image
					grid.setSquare(e.getPos()[0], e.getPos()[1], Grid.PLAYER, e.getClasses());
					
				}
			}
		}
		currentFrame1.setImage(grid.Display());
	}    
}
