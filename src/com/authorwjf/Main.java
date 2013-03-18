package com.authorwjf;

import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
//import java.io.ObjectOutputStream;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Main extends Activity implements SensorEventListener {
	
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mLinearAccelerometer;
    private Sensor mGyro;
    private Sensor mMagnet;
    
    private ArrayList<float[]> mArrayList = new ArrayList<float[]>();
    private ArrayList<float[]> accList = new ArrayList<float[]>();
    private ArrayList<float[]> gyroList = new ArrayList<float[]>();
    private ArrayList<float[]> accMean = new ArrayList<float[]>();
    
    //for collecting data
    private ArrayList<float[]> linearAccList = new ArrayList<float[]>();
    private ArrayList<float[]> rawAccList = new ArrayList<float[]>();

    private ArrayList<float[]> gyroMean = new ArrayList<float[]>();
    private float[] previousGyro = new float[4];
    private float[] previousAngle = new float[4];
    
    private float[] rotationMatrix = new float[9];
    private float[] accMagOrientation = new float[3];
    
    private ArrayList<float[]> accMagOritentationList = new ArrayList<float[]>();

    private float[] magnet = new float[3];

    private float[] accel = new float[3];
    
    private Timer timer;
    
    private Button startINS;

    
    //private ArrayList<float[]> accList = new ArrayList<float[]>();
//    private ArrayList<float[]> gyroList = new ArrayList<float[]>();
    private ArrayList<float[]> distanceList = new ArrayList<float[]>();
    
//	private float[] accThree = new float[3];
//	private float[] gyroThree= new float[3];
//    
//	private	float[] mArray = new float[3];
	private static final String FILENAME = "gyro_data.txt";
	private String mString;
	private static final int sensorDelay = 2000000;
//    private final float NOISE = (float) 2.0;
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Log.d("myapp", "sorry your device don't support this function");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLinearAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
//        startINS = (Button)findViewById(R.id.startINS);
//        startINS.setOnClickListener(new OnClickListener(){
//        	@Override
//        	public void onClick(View v){
//        		startINS.setEnabled(false);
//        		
//        		//set the timer to collect data
//        		timer = new Timer(true);
//        		timer.schedule(new TimerTask(){
//        			@Override 
//        			public void run(){
//        				//get the data every 2s, then clear it
//        				writeToFile();
//        				//clear the array list 
//        				//the next line code is wrong, as sensorChanged may write to it
//        				mArrayList.clear();
//        			}
//        		}, 0, 2000);
//        	}
//        });
        
        
        if(mAccelerometer == null){
        	Log.d("myapp", "sorry your device don't support this function");
        }else{
        	Log.d("myapp", "device support this function!!");
        }
        
        if(mGyro == null){
        	Log.d("myapp", "sorry your device don't support this function");
        }else{
        	Log.d("myapp", "device support this function!!");
        }
        mSensorManager.registerListener(this, mLinearAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyro , SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnet , SensorManager.SENSOR_DELAY_NORMAL);
        
        
}

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnet, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}
	
	public void sensorPause(View view){
		// pause the sensor change
		writeToFile();
		onPause();
	}
	
	public String convertToString(){
		String str = "";
//		for(int i=0; i<linearAccList.size(); i++){
//			str += String.format("%f", linearAccList.get(i)[0])+" ";
//			str += String.format("%f", linearAccList.get(i)[1])+" ";
//			str += String.format("%f", linearAccList.get(i)[2])+" ";
//			str += String.format("%f", linearAccList.get(i)[3])+"\n";
//		}
		
		for(int i=0; i<rawAccList.size(); i++){
			str += String.format("%f", rawAccList.get(i)[0])+" ";
			str += String.format("%f", rawAccList.get(i)[1])+" ";
			str += String.format("%f", rawAccList.get(i)[2])+" ";
			str += String.format("%f", rawAccList.get(i)[3])+"\n";
		}
//		
//		for(int i=0; i<gyroList.size(); i++){
//			str += String.format("%f", gyroList.get(i)[0])+" ";
//			str += String.format("%f", gyroList.get(i)[1])+" ";
//			str += String.format("%f", gyroList.get(i)[2])+" ";
//			str += String.format("%f", gyroList.get(i)[3])+"\n";
//		}
		
//		for(int i=0; i<accMagOritentationList.size(); i++){
//			str += String.format("%f", accMagOritentationList.get(i)[0])+" ";
//			str += String.format("%f", accMagOritentationList.get(i)[1])+" ";
//			str += String.format("%f", accMagOritentationList.get(i)[2])+" ";
//			str += String.format("%f", accMagOritentationList.get(i)[3])+"\n";
//		}
//		for(String s2: gyroList){
//			str += s2;
//		}
//		}else{
//			str="not the same size"+String.format("%d", accList.size())+"  "+String.format("%d", gyroList.size());
//		}
		
//		
//		for(String s: accList){
//			str += s;
//		}
		return str;
	}
	
	public void writeToFile(){
		String myString = convertToString();
		File file = new File(Environment.getExternalStorageDirectory() + File.separator + FILENAME);
		try{
			file.createNewFile();
			OutputStream fo = new FileOutputStream(file);              
		    fo.write(myString.getBytes());
		    fo.close();
			
//			FileOutputStream fOut = openFileOutput(FILENAME, Context.MODE_PRIVATE);
//			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOut);
//			outputStreamWriter.write(myString);
//			outputStreamWriter.close();
//			fOut.close();
		}
		catch(IOException e){
			Log.e("acc", "File write failed: "+e.toString());
		}
		sendEmail();
	}
	
	public String getAppDir(){
//        PackageManager m = getPackageManager();
//		String s = getPackageName();
//		try {
//		    PackageInfo p = m.getPackageInfo(s, 0);
//		    s = p.applicationInfo.dataDir;
//		} catch (NameNotFoundException e) {
//		    Log.w("yourtag", "Error Package name not found ", e);
//		}	
//		Context context = (Context) this;
//		String s = context.getFilesDir().getAbsolutePath();
		String s = Environment.getExternalStorageDirectory().getAbsolutePath();
		return s;
	}
	
	public void sendEmail(){
		
		String dir = getAppDir();
		
		File f = new File(dir+"/gyro_data.txt");
		if(f.exists()){
			Log.d("myapp", "file exist!");
		}else{
			Log.d("myapp", "file not exist!");
		}	
		
//        String ret = "";
//        
//        try {
//            InputStream inputStream = openFileInput(FILENAME);
//            
//            if ( inputStream != null ) {
//            	InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            	String receiveString = "";
//            	StringBuilder stringBuilder = new StringBuilder();
//            	
//            	while ( (receiveString = bufferedReader.readLine()) != null ) {
//            		stringBuilder.append(receiveString);
//            	}
//            	
//            	inputStream.close();
//            	ret = stringBuilder.toString();
//            	Log.d("the string in this file", ret);
//            }
//        }
//        catch (FileNotFoundException e) {
//        	Log.e("myapp", "File not found: " + e.toString());
//		} catch (IOException e) {
//			Log.e("myapp", "Can not read file: " + e.toString());
//		}
        
		
		
		Log.d("the app-path", dir);
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"boji.hit@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject of Email");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+dir+"/gyro_data.txt"));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the mail device 6");
        startActivity(Intent.createChooser(sendIntent, "Email:"));   
	}
	
	/*public double getDistance(ArrayList<double[]> measures){
		double distance = 0;
		double velocityX = 0; 
		double velocityY = 0;
		double distanceX = 0; 
		double distanceY = 0;
		for(int i=0; i<measures.size()-1; i++){
			velocityX += measures.get(i)[0]+(measures.get(i+1)[0]-measures.get(i)[0])/2;
			velocityY += measures.get(i)[1]+(measures.get(i+1)[1]-measures.get(i)[1])/2;
			distanceX += ;
		}
		return distance;
	}*/
	
//	//get the angle 
//	public float[] getAngle(float currentGyro[]){
//		float dt = (currentGyro[0]-previousGyro[0])/1000000000;
//		float angle[] = new float[4];
//		
//		Log.d("myapp", "the time interval in the angle computation"+Float.toString(dt));
//		
//		//integration here
//		angle[0] = currentGyro[0];
//		currentGyro[1] -= 0.0160679; currentGyro[2] -= 0.00733585; currentGyro[3] += 0.0022238;
//		for(int i=1; i<4; i++){
//			angle[i] = previousAngle[i] + (previousGyro[i]+(currentGyro[i]-previousGyro[i])/2)*dt;
//		}
//		
//		System.arraycopy(angle, 0, previousAngle, 0, previousAngle.length);
//		return angle;
//	}
//	
	//from getRotationMatrix to get oritentation
	public void calculateAccMagOrientation(float time){
		float angle[] = new float[4];
		angle[0] = time;
		
	    if(SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
	        SensorManager.getOrientation(rotationMatrix, accMagOrientation);
	    }
	    
	    angle[1] = accMagOrientation[0]; angle[2] = accMagOrientation[1]; angle[3] = accMagOrientation[2];
	    accMagOritentationList.add(angle);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		float time = event.timestamp;
		String strTime = String.format("%f", time);
		

		TextView tvX= (TextView)findViewById(R.id.x_acc);
		TextView tvY= (TextView)findViewById(R.id.y_acc);
		TextView tvZ= (TextView)findViewById(R.id.z_acc);
		TextView gyro_X= (TextView)findViewById(R.id.x_gyro);
		TextView gyro_Y= (TextView)findViewById(R.id.y_gyro);
		TextView gyro_Z= (TextView)findViewById(R.id.z_gyro);
		TextView angle_X= (TextView)findViewById(R.id.x_angle);
		TextView angle_Y= (TextView)findViewById(R.id.y_angle);
		TextView angle_Z= (TextView)findViewById(R.id.z_angle);
		
//		int n = 0;
		if (!mInitialized) {
			tvX.setText("0.0");
			tvY.setText("0.0");
			tvZ.setText("0.0");
			gyro_X.setText("0.0");
			gyro_Y.setText("0.0");
			gyro_Z.setText("0.0");
			
			float initAcc[] = {time, (float)0, (float)0, (float)0};
			accList.add(initAcc);
			float initGyro[] = {time, (float)0, (float)0, (float)0};
			gyroList.add(initGyro);
			
			//for getAngle use
			previousGyro[0] = time;
			previousGyro[1] = (float)0; previousGyro[2] = (float)0; previousGyro[3] = (float)0;
			
			mInitialized = true;
		} 
		
//		Sensor sensor = event.sensor;
		
		synchronized (this) {
	        switch (event.sensor.getType()){
//	        case Sensor.TYPE_GYROSCOPE:
//	        	 //for sampling
//                //collect the data in the timespan
//				gyro_X.setText(Float.toString(event.values[0]));
//				gyro_Y.setText(Float.toString(event.values[1]));
//				gyro_Z.setText(Float.toString(event.values[2]));
//				
//    			float mGyro[] = {time, event.values[0], event.values[1], event.values[2]};
//				gyroList.add(mGyro);
				
				//for too many gyro data, get sample from them
//    			if(gyroList.isEmpty()){
//    				gyroList.add(mGyro);
//    			}else if((time-gyroList.get(0)[0])/1000000000 < 0.1){
//    				float internalGyro[] = {time, event.values[0], event.values[1], event.values[2]};
//	    			gyroList.add(internalGyro);
//    			}else{ //only use the mean of the data in this timespan
//    				float sum_x = 0; float sum_y = 0; float sum_z = 0;
//    				for(int i = 0; i < gyroList.size(); i++){
//    					sum_x += gyroList.get(i)[1];
//    					sum_y += gyroList.get(i)[2];
//    					sum_z += gyroList.get(i)[3];		    					
//    				}
//    				float mean_x = sum_x/gyroList.size();
//    				float mean_y = sum_y/gyroList.size();
//    				float mean_z = sum_z/gyroList.size();
//    				float mMean[] = {gyroList.get(0)[0], mean_x, mean_y, mean_z};
//    				accMean.add(mMean);
//    				
//    				//clear the accList data, and re-init accList
//    				gyroList.clear();
//    				float initAcc[] = {time, (float)0, (float)0, (float)0};
//    				accList.add(initAcc);
//    			}				
//			break;
			
	        case Sensor.TYPE_ACCELEROMETER:
                tvX.setText(Float.toString(event.values[0]));
                tvY.setText(Float.toString(event.values[1]));
                tvZ.setText(Float.toString(event.values[2]));
                
	        	float mAcc[] = {time, event.values[0], event.values[1], event.values[2]};
	        	rawAccList.add(mAcc);
	        break;
				
	        
//	        case Sensor.TYPE_LINEAR_ACCELERATION:
//                tvX.setText(Float.toString(event.values[0]));
//                tvY.setText(Float.toString(event.values[1]));
//                tvZ.setText(Float.toString(event.values[2]));
	            
                //for sampling
                //collect the data in the timespan
//	    			if((time-accList.get(0)[0])/1000000000 < 0.2){
//	    				float mAcc[] = {time, event.values[0], event.values[1], event.values[2]};
//		    			accList.add(mAcc);
//	    			}else{ //only use the mean of the data in this timespan
//	    				float sum_x = 0; float sum_y = 0; float sum_z = 0;
//	    				for(int i = 0; i < accList.size(); i++){
//	    					sum_x += accList.get(i)[1];
//	    					sum_y += accList.get(i)[2];
//	    					sum_z += accList.get(i)[3];		    					
//	    				}
//	    				float mean_x = sum_x/accList.size();
//	    				float mean_y = sum_y/accList.size();
//	    				float mean_z = sum_z/accList.size();
//	    				float mMean[] = {accList.get(0)[0], mean_x, mean_y, mean_z};
//	    				accMean.add(mMean);
//	    				
//	    				//clear the accList data, and re-init accList
//	    				accList.clear();
//	    				float initAcc[] = {time, (float)0, (float)0, (float)0};
//	    				accList.add(initAcc);
//	    			}
    			
//    			//for collecting data
//    			float mLinearAcc[] = {time, event.values[0], event.values[1], event.values[2]};
//				linearAccList.add(mLinearAcc);
//
//	        break;
	        
	        case Sensor.TYPE_MAGNETIC_FIELD:
		        System.arraycopy(event.values, 0, magnet, 0, 3);
		        break;
//		        case Sensor.TYPE_GYROSCOPE:
//
//	                gyro_X.setText(Float.toString(event.values[0]));
//	                gyro_Y.setText(Float.toString(event.values[1]));
//	                gyro_Z.setText(Float.toString(event.values[2]));
//
//	                //collect data in the timespan
//	    			if((time-gyroList.get(0)[0])/1000000000 < 0.2){
//	    				float mGyro[] = {time, event.values[0], event.values[1], event.values[2]};
//		    			gyroList.add(mGyro);
//	    			}else{ //we only use the average in the timespan
//	    				float sum_x = 0; float sum_y = 0; float sum_z = 0;
//	    				for(int i = 0; i < gyroList.size(); i++){
//	    					sum_x += gyroList.get(i)[1];
//	    					sum_y += gyroList.get(i)[2];
//	    					sum_z += gyroList.get(i)[3];		    					
//	    				}
//	    				float mean_x = sum_x/gyroList.size();
//	    				float mean_y = sum_y/gyroList.size();
//	    				float mean_z = sum_z/gyroList.size();
//	    				
//	    				float mMean[] = {gyroList.get(0)[0], mean_x, mean_y, mean_z};
//	    				gyroMean.add(mMean);
//	    				
//	    				//clear the gyroList, and re-init it
//	    				gyroList.clear();
//	    				float initGyro[] = {time, (float)0, (float)0, (float)0};
//	    				gyroList.add(initGyro);
//	    				
//		    			//get the angle
//		    			float angle[] = new float[4];
//		    			angle = getAngle(mMean);
//
//		    			;
//	    				Log.d("myapp", "the mMena value"+Float.toString(angle[1]));
//		    			//set the angle on the screen
//		                angle_X.setText(String.format("%.4f", angle[1]*57.2957795));
//		                angle_Y.setText(String.format("%.4f", angle[2]*57.2957795));
//		                angle_Z.setText(String.format("%.4f", angle[3]*57.2957795));
//		                
//		                Log.d("myapp", "value of angle"+String.format("%.4f", angle[1]*57.2957795)+"  "+String.format("%.4f", angle[2]*57.2957795)+"   "+String.format("%.4f", angle[3]*57.2957795));
//	    				//for getAngle use
//	    				System.arraycopy(mMean, 0, previousGyro, 0, mMean.length);
//	    			}
//
//		        break;
	        }
	    }
		

//		if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
//			float x = event.values[0];
//			float y = event.values[1];
//			float z = event.values[2];
//			
//			tvX.setText(Float.toString(x));
//			tvY.setText(Float.toString(y));
//			tvZ.setText(Float.toString(z));
//			
//					
//		}
//		if (sensor.getType() == Sensor.TYPE_GYROSCOPE){
//			float x2 = event.values[0];
//			float y2 = event.values[1];
//			float z2 = event.values[2];
//			
//			gyro_X.setText(Float.toString(x));
//			gyro_Y.setText(Float.toString(y));
//			gyro_Z.setText(Float.toString(z));
//			
//			gyroX = String.format("%f", x);
//			gyroY = String.format("%f", y);
//			gyroZ = String.format("%f", z);
//		}

//		mString = strTime+" "+accX+" "+accY+" "+accZ+" "+gyroX+" "+gyroY+" "+gyroZ+"\n";
//		mArrayList.add(mString);
		//ImageView iv = (ImageView)findViewById(R.id.image);
		

//		else {
//			Log.d("myapp","this is after accList");
//
//			//float deltaX = Math.abs(mLastX - x);
//			//float deltaY = Math.abs(mLastY - y);
//			//float deltaZ = Math.abs(mLastZ - z);
//			//if (deltaX < NOISE) deltaX = (float)0.0;
//			//if (deltaY < NOISE) deltaY = (float)0.0;
//			//if (deltaZ < NOISE) deltaZ = (float)0.0;
//			mLastX = x;
//			mLastY = y;
//			mLastZ = z;
//
//			/*mLastX = x-(float)0.0161;
//			mLastY = y-(float)0.0076;
//			mLastZ = z+(float)0.0044;*/
//			float[] measurement = new float[3];
//			measurement[0] = mLastX;
//			measurement[1] = mLastY;
//			measurement[2] = mLastZ;			
//			accList.add(measurement);
//			
//			if(!velocityList.isEmpty()){
//				velocity[0] = velocityList.get(velocityList.size()-1)[0] 
//						+(accList.get(accList.size()-1)[0]
//						+(mLastX-accList.get(accList.size()-1)[0])/2)*(float)0.2;
//				velocity[1] = velocityList.get(velocityList.size()-1)[1] 
//						+(accList.get(accList.size()-1)[1]
//						+(mLastY-accList.get(accList.size()-1)[1])/2)*(float)0.2;
//				velocity[2] = velocityList.get(velocityList.size()-1)[2] 
//						+(accList.get(accList.size()-1)[2]
//						+(mLastZ-accList.get(accList.size()-1)[2])/2)*(float)0.2;
//
//			}								
//
//			/*if(!distanceList.isEmpty()){
//				distance[0] = distanceList.get(distanceList.size()-1)[0] 
//						+velocityList.get(velocityList.size()-1)[0]
//						+(velocity[0]-velocityList.get(velocityList.size()-1)[0])/2;
//				distance[1] = distanceList.get(distanceList.size()-1)[1] 
//						+velocityList.get(velocityList.size()-1)[1]
//						+(velocity[1]-velocityList.get(velocityList.size()-1)[1])/2;
//				distance[2] = distanceList.get(distanceList.size()-1)[2] 
//						+velocityList.get(velocityList.size()-1)[2]
//						+(velocity[2]-velocityList.get(velocityList.size()-1)[2])/2;
//				distanceList.add(distance);
//			}*/
//
//
//			mArray[0] = x; //preprocess the sensor data
//			mArray[1] = y;
//			mArray[2] = z;
			



			
//			n += 1;
			//iv.setVisibility(View.VISIBLE);
			/*if (deltaX > deltaY) {
				iv.setImageResource(R.drawable.horizontal);
			} else if (deltaY > deltaX) {
				iv.setImageResource(R.drawable.vertical);
			} else {
				iv.setVisibility(View.INVISIBLE);
			}*/
		
	}
}