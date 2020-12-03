package application;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
	//the FXML button for opening the camera
	@FXML
	private Button button;

	//	@FXML
	//private CheckBox grayscale;


	@FXML
	private Boolean open = false;

	@FXML
	private Boolean close = false;
	
	private Boolean newFrame = true;


	@FXML
	private Slider hueStart;

	@FXML
	private Slider hueStop;

	@FXML
	private int saturationStart = 127;

	@FXML
	private int saturationStop = 127;

	@FXML
	private int valueStart = 127;

	@FXML
	private int valueStop = 127;

	@FXML
	private ImageView currentFrame;

	@FXML
	private ImageView currentFrame1;

	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
	private VideoCapture capture;
	// a flag to change the button behavior
	private boolean cameraActive;
	// the logo to be loaded

	Mat thisFrame;

	ArrayList<Double>gridColour;

	boolean gridFound = false;


	Mat gridImage = new Mat();

	Grid grid; 


	Blob[] gridSquares;

	Scalar minValues;
	Scalar maxValues;


	public void initialize()
	{
		this.capture = new VideoCapture();
		this.cameraActive = false;
	}

	@FXML
	protected void startCamera()
	{
		// set a fixed width for the frame
		//this.currentFrame.setFitWidth(900);
		// preserve image ratio
		//this.currentFrame.setPreserveRatio(true);

		if (!this.cameraActive)
		{
			// start the video capture
			this.capture.open(0);

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

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				// update the button content
				this.button.setText("Stop Camera");
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
			this.button.setText("Start Camera");

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

		if(!gridFound) {
			gridFound= true;
			saturationStart = 50;
			saturationStop = 220;
			valueStart = 0;
			valueStop = 255;
			open = true;
			close = true;
		}
		else {
			gridFound = false;
		}
	}
 
	@FXML
	protected void getBlobOnClick()
	{
		for (int i = 0; i < grid.getColoumns(); i ++) 
		{
			for (int j = 0; j < grid.getRows(); j++)
			{
				grid.setSquare(i, j, "");
			}
		}
			
		
		hueStart.setValue(80);
		grabFrame();
		getBlob(thisFrame,gridColour, "tree");
		
		hueStart.setValue(105);
		grabFrame();
		getBlob(thisFrame,gridColour, "water");
		
		hueStart.setValue(160);
		grabFrame();
		getBlob(thisFrame,gridColour, "stone");
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

					if(gridFound) {

						Mat hsvOutput = new Mat();

						Imgproc.cvtColor(frame, hsvOutput, Imgproc.COLOR_BGR2HSV);

						//creates two scalars with one containing the minimum and the other containing the maximum values of the HSV colorspace treshold
						minValues = new Scalar(this.hueStart.getValue(), this.saturationStart, this.valueStart);
						maxValues = new Scalar(this.hueStart.getValue() + 20, this.saturationStop, this.valueStop);

						//Mat thresholdOutput = new Mat();
						Core.inRange(hsvOutput, minValues, maxValues, frame);

					}

					if(!gridFound) {
						Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);

						//creates two scalars with one containing the minimum and the other containing the maximum values of the HSV colorspace treshold
						minValues = new Scalar(this.hueStart.getValue(), this.saturationStart,
								this.valueStart);
						maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop,
								this.valueStop);

						//a static method for thresholding called inRange() from the Core class from the OpenCV library is used
						Core.inRange(frame, minValues, maxValues, frame);

						// Create the CV_8U version of the distance image
						// It is needed for findContours()
						Mat dist_8u = new Mat();

						frame.convertTo(dist_8u, CvType.CV_8U);

						// Find total markers
						List<MatOfPoint> contours = new ArrayList<>();
						Mat hierarchy = new Mat();
						Imgproc.findContours(dist_8u, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

						// Create the marker image for the watershed algorithm
						Mat markers = Mat.zeros(frame.size(), CvType.CV_32S);

						// Draw the foreground markers
						for (int i = 0; i < contours.size(); i++) {
							Imgproc.drawContours(markers, contours, i, new Scalar(i + 1), -1);

						} 

						// Draw the background marker
						Mat markersScaled = new Mat();
						markers.convertTo(markersScaled, CvType.CV_32F);
						Core.normalize(markersScaled, markersScaled, 0.0, 255.0, Core.NORM_MINMAX);
						Imgproc.circle(markersScaled, new Point(5, 5), 3, new Scalar(255, 255, 255), -1);
						Mat markersDisplay = new Mat();
						markersScaled.convertTo(markersDisplay, CvType.CV_8U);
						Imgproc.circle(markers, new Point(5, 5), 3, new Scalar(255, 255, 255), -1);

						frame = markersDisplay;

						ArrayList<Double> colors = new ArrayList<>();

						for (int y = 0; y < frame.height(); y++){
							for(int x = 0; x < frame.width(); x++){
								if(/*frame.get(y,x)[0] != 255 &&*/ frame.get(y,x)[0] != 0){
									colors.add(frame.get(y,x)[0]);
								}
							}
						} 

						Set<Double> uniqueColors = new HashSet<Double>(colors);
						gridColour = new ArrayList<>(uniqueColors);

		
					}

					if(open) {

						//two matrices of type Mat from the OpevCV library is created to be used for dilation and erosion respectively
						Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
						Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));

						//a new matrix of type Mat from the OpevCV library is created for the output image after erosion has been applied
						Mat erodeOutput = new Mat();

						//the erode() and dilate() methods are used respectively in order to apply a opening to the video feed
						Imgproc.erode(frame, erodeOutput, erodeElement);
						Imgproc.dilate(erodeOutput, frame, dilateElement);
			
					}

					if(close) {

						//two matrices of type Mat from the OpevCV library is created to be used for erosion and dilation respectively
						Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
						Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));

						//a new matrix of type Mat from the OpevCV library is created for the output image after dilation has been applied
						Mat dilateOutput = new Mat();

						//the dilate() and erode() methods are used respectively in order to apply a opening to the video feed
						Imgproc.dilate(frame, dilateOutput, dilateElement);
						Imgproc.erode(dilateOutput, frame, erodeElement);

					}

					thisFrame = frame;


				}

			}
			catch (Exception e)
			{
				// log the error
				System.err.println("Exception during the frame elaboration: " + e);
			}
		}
		System.out.println(hueStart.getValue());
		return frame;
	}

	private void stopAcquisition()
	{
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

	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image)
	{
		Utils.onFXThread(view.imageProperty(), image);
	}

	/**
	 * On application close, stop the acquisition from the camera
	 */
	protected void setClosed()
	{
		this.stopAcquisition();
	}

	public void getGrid (Mat frame, ArrayList<Double>gridColour) 
	{
		int[] point = new int[2];
		
		ArrayList<Double> list = new ArrayList<>();

		for (int y = 0; y < frame.height(); y++)
		{
			for(int x = 0; x < frame.width(); x++)
			{
				if(frame.get(y,x)[0] != 0)
				{
					point[0] = y;
					point[1] = x;
					break;
				}
			}
		} 

		

		for (int y = point[0] ; y > 0 ; y--)
		{
			if (frame.get(point[0]-y,point[1])[0] != 0) 
			{
				list.add(frame.get(point[0]-y,point[1])[0]);
			}
		}

		Set<Double> uniqueColoumns = new HashSet<Double>(list);

		ArrayList<Double> uColoumns = new ArrayList<>(uniqueColoumns);

		int coloumns = uColoumns.size();

		int rows =gridColour.size() / coloumns;

		grid = new Grid(rows,coloumns);
		currentFrame1.setImage(grid.Display());
		gridImage = frame;

		Collections.sort(gridColour, Collections.reverseOrder());

		gridSquares = new Blob[gridColour.size()];

		for(int i = 0; i < gridColour.size(); i++) 
		{
			gridSquares[i] = new Blob();
			gridSquares[i].setColor(gridColour.get(i));
			gridSquares[i].setLocationX(i % rows);
			gridSquares[i].setLocationY(i / rows);
		}
	}

	public void getBlob (Mat frame, ArrayList<Double>gridColour, String type) 
	{
		
		
		Mat dist_8u = new Mat();
		frame.convertTo(dist_8u, CvType.CV_8U);
		// Find total markers
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(dist_8u, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		
		// Create the marker image for the watershed algorithm
		Mat markers = Mat.zeros(frame.size(), CvType.CV_32S);

		// Draw the foreground markers
		for (int i = 0; i < contours.size(); i++) {
			Imgproc.drawContours(markers, contours, i, new Scalar(i + 1), -1);

		} 

		// Draw the background marker
		Mat markersScaled = new Mat();
		markers.convertTo(markersScaled, CvType.CV_32F);
		Core.normalize(markersScaled, markersScaled, 0.0, 255.0, Core.NORM_MINMAX);
		Imgproc.circle(markersScaled, new Point(5, 5), 3, new Scalar(255, 255, 255), -1);
		Mat markersDisplay = new Mat();
		markersScaled.convertTo(markersDisplay, CvType.CV_8U);
		Imgproc.circle(markers, new Point(5, 5), 3, new Scalar(255, 255, 255), -1);

		frame = markersDisplay;
		
		
		ArrayList<Double> terrainColors = new ArrayList<>();
		
		for (int y = 0; y < frame.height(); y++){
			for(int x = 0; x < frame.width(); x++){
				if(frame.get(y,x)[0] != 0){
					terrainColors.add(frame.get(y,x)[0]);
				}
			}
		} 

		Set<Double> setTerrainColors = new HashSet<Double>(terrainColors);
		ArrayList<Double> uniqueTerrainColors = new ArrayList<>(setTerrainColors);

		//System.out.println(uniqueTerrainColors);
		//System.out.println(uniqueTerrainColors.size());
		
		ArrayList<Blob> terrainBlobs = new ArrayList<>();
		
		
		
		
		for (int c = 0; c < uniqueTerrainColors.size(); c++ ) {
					
				int area = 0;
				Blob blob = new Blob();
				terrainBlobs.add(blob);
				
				for (int y = 0; y < frame.height(); y++)
				{
					for(int x = 0; x < frame.width(); x++)
					{
						if(frame.get(y,x)[0] == uniqueTerrainColors.get(c)){
							area +=1 ;
							terrainBlobs.get(c).setArea(area);
							
						}
					}
				}
				
				
				System.out.println("Area number " + c + ": " + terrainBlobs.get(c).getArea());
			}

			
		for (int c = 0; c < uniqueTerrainColors.size(); c++ ) {
			int xPos = 0;
			int yPos = 0;
			
			for (int y = 0; y < frame.height(); y++)
			{
				for(int x = 0; x < frame.width(); x++)
				{
					if(frame.get(y,x)[0] == uniqueTerrainColors.get(c)){
						
						xPos += x;
						yPos += y;
						
						terrainBlobs.get(c).setxPositions(xPos);
						terrainBlobs.get(c).setyPositions(yPos);

					}
				}
			}
			
			int xMid;
			int yMid;

			xMid = terrainBlobs.get(c).getxPositions() / terrainBlobs.get(c).getArea();
			yMid = terrainBlobs.get(c).getyPositions() / terrainBlobs.get(c).getArea();
			System.out.println(c);
			System.out.println("x:" + terrainBlobs.get(c).getxPositions() + "     " + terrainBlobs.get(c).getArea());
			System.out.println("y:" + terrainBlobs.get(c).getyPositions() + "     " + terrainBlobs.get(c).getArea());
			System.out.println(" ");
			terrainBlobs.get(c).setLocationX(xMid); 
			terrainBlobs.get(c).setLocationY(yMid);
			
		}

		double[] squareColors = new double[uniqueTerrainColors.size()];
		
		

		for(int i = 0; i < uniqueTerrainColors.size(); i++ ) 
		{		
			squareColors[i] = gridImage.get(terrainBlobs.get(i).getLocationY(),terrainBlobs.get(i).getLocationX())[0];
		}
		

		for (int i = 0; i < gridSquares.length;i++) 
		{	
			for(int j = 0; j < squareColors.length; j++) 
			{
				if((gridSquares[i].getColor() - squareColors[j]) < 0.5 && (gridSquares[i].getColor() - squareColors[j]) > -0.5 ){

					int gridX = gridSquares[i].getLocationX();
					int gridY = gridSquares[i].getLocationY();
					
					System.out.println("blob " + j + " x position: " + gridX + "x pixelPos: " + terrainBlobs.get(j).getLocationX());
					System.out.println("blob " + j + " y position: " + gridY + "y pixelPos: " + terrainBlobs.get(j).getLocationY());
					System.out.println("");
					grid.setSquare(gridX, gridY, type);
					currentFrame1.setImage(grid.Display());
				}
			}
		}
	}    
}
