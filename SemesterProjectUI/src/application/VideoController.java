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
	private CheckBox open;
 	
 	@FXML
	private CheckBox close;
	
	
	@FXML
	private Slider hueStart;
	
	@FXML
	private Slider hueStop;
	
	@FXML
	private Slider saturationStart;
	
	@FXML
	private Slider saturationStop;
	
	@FXML
	private Slider valueStart;
	
	@FXML
	private Slider valueStop;
	
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
	
	    private static int MAX_BINARY_VALUE = 255;
	    private int thresholdValue = 127;
	    private int thresholdType = 3;
	    
	    
	    Mat thisFrame;
	    
	    ArrayList<Double> mineFarver= new ArrayList<>();
	    
	    boolean gridFound = false;
	    
	    
	    Mat savedGrid = new Mat();
	    
	   Grid grid; 
	   
	   
	   Blob[] blobs;
	  
	
	    
	/**
	 * Initialize method, automatically called by @{link FXMLLoader}
	 */
	public void initialize()
	{
		this.capture = new VideoCapture();
		this.cameraActive = false;
		/*Grid grid = new Grid(10,10);
		grid.setSquare(2, 2, "tree");
		grid.setSquare(2, 5, "tree");
		grid.setSquare(7, 5, "tree");
		grid.setSquare(7, 2, "tree");
		grid.setSquare(3, 6, "tree");
		grid.setSquare(4, 6, "tree");
		grid.setSquare(5, 6, "tree");
		grid.setSquare(6, 6, "tree");
		currentFrame.setImage(grid.Display());*/
	}
	
	/**
	 * The action triggered by pushing the button on the GUI
	 */
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
		
		getGrid(thisFrame,mineFarver);
		
		
	}
	
	

	@FXML
	protected void gridFoundOnClick()
	{
		
		if(!gridFound) {
		gridFound= true;
		}
		else {
			gridFound = false;
		}
		
	}
	
	
	@FXML
	protected void getBlobOnClick()
	{
		
		getBlob(thisFrame,mineFarver);
		
		
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
						Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
								this.valueStart.getValue());
						Scalar maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(),
								this.valueStop.getValue());
					
						//Mat thresholdOutput = new Mat();
						Core.inRange(hsvOutput, minValues, maxValues, frame);

						//Mat hsvToBGROutput = new Mat();
						//Imgproc.cvtColor(thresholdOutput, hsvToBGROutput, Imgproc.COLOR_HSV2BGR);
						//Imgproc.cvtColor(hsvToBGROutput, frame, Imgproc.COLOR_BGR2GRAY);
						
			
					}
					
					
					
					if(!gridFound) {
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
					
					//creates two scalars with one containing the minimum and the other containing the maximum values of the HSV colorspace treshold
					Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
							this.valueStart.getValue());
					Scalar maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(),
							this.valueStop.getValue());

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
				    
				    ArrayList<Double> farver = new ArrayList<>(uniqueColors);
				    
				    
				    mineFarver = farver;
					}

			
		if(open.isSelected()) {
				  		
				  		//two matrices of type Mat from the OpevCV library is created to be used for dilation and erosion respectively
				  		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
						Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
						
						//a new matrix of type Mat from the OpevCV library is created for the output image after erosion has been applied
						Mat erodeOutput = new Mat();
						
						//the erode() and dilate() methods are used respectively in order to apply a opening to the video feed
						Imgproc.erode(frame, erodeOutput, erodeElement);
						Imgproc.dilate(erodeOutput, frame, dilateElement);
				  		
				  	}
				  	
				  	if(close.isSelected()) {
				
				     	//two matrices of type Mat from the OpevCV library is created to be used for erosion and dilation respectively
				  		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
						Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
						
						//a new matrix of type Mat from the OpevCV library is created for the output image after dilation has been applied
						Mat dilateOutput = new Mat();
						
						//the dilate() and erode() methods are used respectively in order to apply a opening to the video feed
						Imgproc.dilate(frame, dilateOutput, dilateElement);
						Imgproc.erode(dilateOutput, frame, erodeElement);
						
				  	}


                    
				    
                   //finding the grid size
                 /*   int[] point = new int[2];
                    
                    for (int y = 0; y < frame.height(); y++){
  				      for(int x = 0; x < frame.width(); x++){
  				      if(frame.get(y,x)[0] != 0){
  				    		  point[0] = y;
  				    	      point[1] = x;
  				    		 break;
  				    		  }
  				    }
  				    } 
                    
                    
                 // then move to mid point and then move down
   				 //  int midpoint = (contours.get(1).width())/2;
                    
                    ArrayList<Double> list = new ArrayList<>();
                    
                    
                    for (int y = point[0] ; y > 0 ; y--){
                    	
                    	if (frame.get(point[0]-y,point[1])[0] != 0) {
                    		list.add(frame.get(point[0]-y,point[1])[0]);
                    	}
                    	
                    }
                    	
                    	Set<Double> uniqueColoumns = new HashSet<Double>(list);
    				    
    				    ArrayList<Double> uColoumns = new ArrayList<>(uniqueColoumns);
                    	
         
    				    int coloumns = uColoumns.size();
    	                
                        int rows = farver.size() / coloumns;*/
 
				   
				 //   for (int i = 0; i < 50; ++i) System.out.println();
				    
				  //  System.out.print("søjler: " + coloumns + "   rækker: " + rows + "   felter: " + farver.size() );
                  
                  //  System.out.print("y = " + point[0] + "    x = " + point[1]);
				    
				   // System.out.print(uColoumns.size());
                  
                     
				    
				    //Print the grid to console
                   /* List<List<String>> blobs = new ArrayList<>();
                    char[] letters = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P'};

                  //  for (int i = 0; i < 50; ++i) System.out.println();   
                    
                    
                    
                 
                    for (int y = 0; y < coloumns; y++) {
                    	blobs.add(new ArrayList<>());
                    	
                    	for (int x = 0; x < rows; x++) {
                        String name = "" + letters[x] + (y+1);
                    	blobs.get(y).add(name);

                     //   System.out.print("  ");
	                   // System.out.print(contours.get(j).size().height);
                    }
                    }
                  
                   
                    for(int i = 0; i < blobs.size(); i++) {
                   System.out.println(blobs.get(i));
				}*/
                    

                    
					
				  
                //    frame = deskew(frame,2);
	            	
thisFrame = frame;
					
	
					

					       


					  	

				}
				
			}
			catch (Exception e)
			{
				// log the error
				System.err.println("Exception during the frame elaboration: " + e);
			}
		}
		
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
	

	
	 private static Mat deskew(Mat src, double angle) {
		    Point center = new Point(src.width() / 2, src.height() / 2);
		    Mat rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0);
		        Size size = new Size(src.width(), src.height());

		        Imgproc.warpAffine(src, src, rotImage, size, Imgproc.INTER_LINEAR
		                + Imgproc.CV_WARP_FILL_OUTLIERS);
		        return src;
		    }
	 
	 
	 public void getGrid (Mat frame, ArrayList<Double> farver) {
		 
         int[] point = new int[2];
         
         for (int y = 0; y < frame.height(); y++){
		      for(int x = 0; x < frame.width(); x++){
		      if(frame.get(y,x)[0] != 0){
		    		  point[0] = y;
		    	      point[1] = x;
		    		 break;
		    		  }
		    }
		    } 
         
         
      // then move to mid point and then move down
		 //  int midpoint = (contours.get(1).width())/2;
         
         ArrayList<Double> list = new ArrayList<>();
         
         
         for (int y = point[0] ; y > 0 ; y--){
         	
         	if (frame.get(point[0]-y,point[1])[0] != 0) {
         		list.add(frame.get(point[0]-y,point[1])[0]);
         	}
         	
         }
         	
         	Set<Double> uniqueColoumns = new HashSet<Double>(list);
			    
			    ArrayList<Double> uColoumns = new ArrayList<>(uniqueColoumns);
         	

			    int coloumns = uColoumns.size();
             
             int rows = farver.size() / coloumns;
             
             
             
             grid = new Grid(rows,coloumns);
             currentFrame1.setImage(grid.Display());
             savedGrid = frame;
             
             
             Collections.sort(farver, Collections.reverseOrder());
             
          blobs = new Blob[farver.size()];
  
          for(int i = 0; i < farver.size(); i++) {
        	    blobs[i] = new Blob();
           		blobs[i].setColor(farver.get(i));
                blobs[i].setLocationX(i % rows);
                blobs[i].setLocationY(i / rows);
           }

          for(int i = 0; i < farver.size(); i++){
         System.out.println("blob color: "+ blobs[i].getColor() + "   blob x: " + blobs[i].getLocationX() + "   blob y: " + blobs[i].getLocationY());
        
	 }
    
	 }
	 
	 public void getBlob (Mat frame, ArrayList<Double> farver) {
	 int[] point = new int[2];
     
     for (int y = 0; y < frame.height(); y++){
	      for(int x = 0; x < frame.width(); x++){
	      if(frame.get(y,x)[0] != 0){
	    		  point[0] = y;
	    	      point[1] = x;
	    		 break;
	    		  }
	    }
	    }
    
     double squareColor = savedGrid.get(point[0],point[1])[0];
    		 
  System.out.print(squareColor);

  
  int x = 0;
  int y = 0;
  
  for(int i = 0; i < farver.size(); i++) {
	if(blobs[i].getColor() == squareColor)
		{
		 x = blobs[i].getLocationX();
		 y = blobs[i].getLocationY(); 	
		 break;
		}
  }

     
     grid.set();
     grid.setSquare(x, y, "tree");
     currentFrame1.setImage(grid.Display());

     
    
	 }    
	 
}
