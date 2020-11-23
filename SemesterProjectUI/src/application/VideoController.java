package application;

import java.util.ArrayList;
import java.util.List;
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
	
	//the FXML grayscale checkbox
	@FXML
	private CheckBox grayscale;
	
	//the FXML YUV colorspace checkbox
	@FXML
	private CheckBox yuv;
	
	//the FXML HSV colorspace checkbox
	@FXML
	private CheckBox hsv;
	
	//the FXML checkbox for vertical sobel edge detection
	@FXML
	private CheckBox vertSobel;
	
	//the FXML checkbox for horizonal sobel edge detection
	@FXML
	private CheckBox horiSobel;
	
	//the FXML checkbox for diagonal sobel edge detection
	@FXML
	private CheckBox diagSobel;
	
	//the FXML checkbox for the Hough Transform algorithm
	@FXML
	private CheckBox houghTrans;
	
	//the FMXL checkbox for simple tresholding
	@FXML
	private CheckBox thresh;
	
	//the FMXL checkbox for color thresholding
	@FXML
	private CheckBox colorThresh;
	
	//the FXML checkbox for applying a blur
	@FXML
	private CheckBox blur;
	
	//the FXML checkbox for apllying opening
	@FXML
	private CheckBox open;
	
	//the FXML checkbox for applying closing
	@FXML
	private CheckBox close;
	
	//the FXML checkbox for applying BLOB detection
	@FXML
	private CheckBox blob;
	
	//the FXML slider used to set the value of simple thresholding
	@FXML
	private Slider threshVal;
	
	//the FXML slider used to set the minimum value of the hue for HSV color thresholding
	@FXML
	private Slider hueStart;
	
	//the FXML slider used to set the maximum value of the hue for HSV color thresholding
	@FXML
	private Slider hueStop;
	
	//the FXML slider used to set the minimum value of the saturation for HSV color thresholding
	@FXML
	private Slider saturationStart;
	
	//the FXML slider used to set the maximum value of the saturation for HSV color thresholding
	@FXML
	private Slider saturationStop;
	
	//the FXML slider used to set the minimum value of the value for HSV color thresholding
	@FXML
	private Slider valueStart;
	
	//the FXML slider used to set the maximum value of the value for HSV color thresholding
	@FXML
	private Slider valueStop;

	// the FXML logo checkbox
	//@FXML
	//private CheckBox logoCheckBox;
	// the FXML grayscale checkbox
	//@FXML
	//private ImageView histogram;
	// the FXML area for showing the current frame
	
	@FXML
	private ImageView currentFrame;
	
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

	/**
	 * Initialize method, automatically called by @{link FXMLLoader}
	 */
	public void initialize()
	{
		this.capture = new VideoCapture();
		this.cameraActive = false;
	}
	
	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	protected void startCamera()
	{
		// set a fixed width for the frame
		this.currentFrame.setFitWidth(900);
		// preserve image ratio
		this.currentFrame.setPreserveRatio(true);
		
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
					
					//below are if-statements that check whether the different checkboxes are checked
					//and if that is the case the effect listed beside the checkbox will be applied
					
					if (grayscale.isSelected())
					{
						
						//Convert to grayscale using the static cvtColor() method from the Imgproc class
						Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
						
					}
					
					
					if(yuv.isSelected()) {
						
						//Convert to YUV color space using the static cvtColor() method from the Imgproc class
						Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2YUV);
						
					}
					
					
                    if(hsv.isSelected()) {
						
						//Convert to HSV color space using the static cvtColor() method from the Imgproc class
						Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
						
					}
					
				
					if(horiSobel.isSelected()) {
						
						//Horizontal sobel edge detection using the static Sobel() method from the Imgproc class
						Imgproc.Sobel(frame, frame, -1, 0, 1);
						
					}
						
					if(vertSobel.isSelected()) {
						
						//Vertical sobel edge detection using the static Sobel() method from the Imgproc class
						Imgproc.Sobel(frame, frame, -1, 1, 0);
						 
					}
						 
				    if(diagSobel.isSelected()) {
					    //Diagonal sobel edge detection using the static Sobel() method from the Imgproc class
						Imgproc.Sobel(frame, frame, -1, 1, 1);
						
					}
					
			     	if(houghTrans.isSelected()) {
					
						  //Implement a Hough Transform
					
						  //Converting the image to Gray
					      Mat gray = new Mat();
					      Imgproc.cvtColor(frame, gray, Imgproc.COLOR_RGBA2GRAY);
					      
					      //Detecting the edges
					      Mat edges = new Mat();
					      Imgproc.Canny(gray, edges, 60, 60*3, 3, false);
					      
					      // Changing the color of the canny
					      Mat cannyColor = new Mat();
					      Imgproc.cvtColor(edges, cannyColor, Imgproc.COLOR_GRAY2BGR);
					      
					      //Detecting the hough lines from (canny)
					      Mat lines = new Mat();
					      Imgproc.HoughLines(edges, lines, 1, Math.PI/180, 150);
					      for (int i = 0; i < lines.rows(); i++) {
					         double[] data = lines.get(i, 0);
					         double rho = data[0];
					         double theta = data[1];
					         double a = Math.cos(theta);
					         double b = Math.sin(theta);
					         double x0 = a*rho;
					         double y0 = b*rho;
					         
					         //Drawing lines on the image
					         Point pt1 = new Point();
					         Point pt2 = new Point();
					         pt1.x = Math.round(x0 + 1000*(-b));
					         pt1.y = Math.round(y0 + 1000*(a));
					         pt2.x = Math.round(x0 - 1000*(-b));
					         pt2.y = Math.round(y0 - 1000 *(a));
					         Imgproc.line(frame, pt1, pt2, new Scalar(0, 0, 255), 3);
					      }
					      
				}
					       
					  	if(thresh.isSelected()) {
					  		
					  		//sets the thresholdValue variable to the current value of the threshVal slider
					  		thresholdValue = (int) threshVal.getValue();
					  		
                            //using the static OpenCV function threshold() from the Imgproc class to perform thresholding on the video feed
					  		Imgproc.threshold(frame, frame, thresholdValue, MAX_BINARY_VALUE, thresholdType);
					  		
					  	}
					  	
					  	
                        if(colorThresh.isSelected()) {
					  		
                        	//converts the video feed to HSV color space using the static method cvtColor() from the Imgproc class
    						Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
    						
    						//creates two scalars with one containing the minimum and the other containing the maximum values of the HSV colorspace treshold
    						Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
    								this.valueStart.getValue());
    						Scalar maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(),
    								this.valueStop.getValue());

    						//a static method for thresholding called inRange() from the Core class from the OpenCV library is used
    						Core.inRange(frame, minValues, maxValues, frame);
					  		
					  	}
					  	
					  	
					  	if(blur.isSelected()) {

					  		//the static blur() method from the Imgproc class form OpenCV is used to apply a blur of kernel size 5x5 to the video feed
							Imgproc.blur(frame, frame, new Size(5, 5));
							
					  	}
					  	
					  	if(open.isSelected()) {
					  		
					  		//two matrices of type Mat from the OpevCV library is created to be used for dilation and erosion respectively
					  		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9));
							Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9));
							
							//a new matrix of type Mat from the OpevCV library is created for the output image after erosion has been applied
							Mat erodeOutput = new Mat();
							
							//the erode() and dilate() methods are used respectively in order to apply a opening to the video feed
							Imgproc.erode(frame, erodeOutput, erodeElement);
							Imgproc.dilate(erodeOutput, frame, dilateElement);
					  		
					  	}
					  	
					  	if(close.isSelected()) {
					
					     	//two matrices of type Mat from the OpevCV library is created to be used for erosion and dilation respectively
					  		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9));
							Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9));
							
							//a new matrix of type Mat from the OpevCV library is created for the output image after dilation has been applied
							Mat dilateOutput = new Mat();
							
							//the dilate() and erode() methods are used respectively in order to apply a opening to the video feed
							Imgproc.dilate(frame, dilateOutput, dilateElement);
							Imgproc.erode(dilateOutput, frame, erodeElement);
							
					  	}
  
					  	
	                    if(blob.isSelected()) {
	                    	
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
					  	}
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
	
}
