package com.yazid;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import com.chart.LineView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class network extends Activity{    
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private TextView mTitle;
    private LinearLayout playerLayout = null;
    private ImageButton connectButton = null;   
    private TextView txtsuhu1, txtsuhu2, txtkelembaban1, txtkelembaban2, 
    txtasap1, txtasap2, txtapi1, txtapi2;
    
    String mReceived = "";
    ArrayList<HashMap<String, String>> dataMap = new ArrayList<HashMap<String, String>>();
    boolean FINISH_STATE;  
    View  mView;
    double s=0;   
    float value[],sum_value[];
   
    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    
    long startTime; 
    double runtime;
    private Handler myHandler = new Handler();
    LinearLayout lnView;
    ImageView imgMenu;
    boolean view;
    
    int humidity = 0;
    int temp = 0;
    int smoke = 0;
    int flame = 0;
    boolean RecieveUpdate1, RecieveUpdate2;
    private int H1, T1, S1, H2, T2, S2 ;
    
    /* Network
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
        
    private long data_size = 0;
    private long avg_data = 0;
    DataPoint dataPoint;
    GraphView graphHum, graphTemp, graphSmoke, graphNetwork, graphNetworkDelay;
    private LineGraphSeries<DataPoint> mSeriesHum1;
    private LineGraphSeries<DataPoint> mSeriesHum2;
    private LineGraphSeries<DataPoint> mSeriesTemp1;
    private LineGraphSeries<DataPoint> mSeriesTemp2;
    private LineGraphSeries<DataPoint> mSeriesSmoke1;
    private LineGraphSeries<DataPoint> mSeriesSmoke2;
    
    private BarGraphSeries<DataPoint> mSeriesNet1;
    private BarGraphSeries<DataPoint> mSeriesNet2;
    
    private LineGraphSeries<DataPoint> mSeriesNetThroughput1;
    private LineGraphSeries<DataPoint> mSeriesNetThroughput2;
    
    private LineGraphSeries<DataPoint> mSeriesNetDelay1;
    private LineGraphSeries<DataPoint> mSeriesNetDelay2;
    
    
    private double graphHum1LastXValue = 0d;
    private double graphHum2LastXValue = 0d;
    private double graphNetworkLastValue = 0d;
    
    int thickness = 1;
    int radius = 3;
    long packet_size1 = 0;
    long packet_size2 = 0;
    
    /* sniffing */
    private final static String HCI_FILE_PATH = "/sdcard/btsnoop_hci.log";
    public native String decodeHci(String snoopFile);
    TextView txtCapture;
    
    long throughput1 = 0;
    long throughput2 = 0;
    long sum_throughput1 = 0;
    long sum_throughput2 = 0;
    
    long avg_throughput1 = 0;
    long avg_throughput2 = 0;
    long avg_delay1 = 0;
    long avg_delay2 = 0;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        value=new float[10];
        sum_value=new float[10];
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        graphHum 	= (GraphView) findViewById(R.id.graphHum);
        graphTemp 	= (GraphView) findViewById(R.id.graphTemp);
        graphSmoke 	= (GraphView) findViewById(R.id.graphSmoke);
        graphNetwork = (GraphView) findViewById(R.id.graphNetwork );
        graphNetworkDelay = (GraphView) findViewById(R.id.graphNetworkDelay );
        
        
        mSeriesNetDelay1 =  new LineGraphSeries<>();
        mSeriesNetDelay1.setColor(Color.RED);
        mSeriesNetDelay1.setTitle("Delay 1");
        mSeriesNetDelay1.setDrawDataPoints(true);
        mSeriesNetDelay1.setDataPointsRadius(radius);
        mSeriesNetDelay1.setThickness(thickness); 
        
        mSeriesNetDelay2 =  new LineGraphSeries<>();
        mSeriesNetDelay2.setColor(Color.YELLOW);
        mSeriesNetDelay2.setTitle("Delay 2");
        mSeriesNetDelay2.setDrawDataPoints(true);
        mSeriesNetDelay2.setDataPointsRadius(radius);
        mSeriesNetDelay2.setThickness(thickness); 
        
        mSeriesNet1 = new BarGraphSeries<>();
        mSeriesNet1.setColor(Color.BLUE);
        mSeriesNet1.setTitle("Packet size 1");
       // mSeriesNet1.setDrawDataPoints(true);
        //mSeriesNet1.setDataPointsRadius(radius);
        //mSeriesNet1.setThickness(thickness);   
                
        mSeriesNet2 = new BarGraphSeries<>();
        mSeriesNet2.setColor(Color.GREEN);
        mSeriesNet2.setTitle("Packet size 2");
        //mSeriesNet2.setDrawDataPoints(true);
        //mSeriesNet2.setDataPointsRadius(radius);
        //mSeriesNet2.setThickness(thickness);    
        
        mSeriesNetThroughput1 =  new LineGraphSeries<>();
        mSeriesNetThroughput1.setColor(Color.RED);
        mSeriesNetThroughput1.setTitle("Throughput 1");
        mSeriesNetThroughput1.setDrawDataPoints(true);
        mSeriesNetThroughput1.setDataPointsRadius(radius);
        mSeriesNetThroughput1.setThickness(thickness); 
        
        mSeriesNetThroughput2 =  new LineGraphSeries<>();
        mSeriesNetThroughput2.setColor(Color.YELLOW);
        mSeriesNetThroughput2.setTitle("Throughput 2");
        mSeriesNetThroughput2.setDrawDataPoints(true);
        mSeriesNetThroughput2.setDataPointsRadius(radius);
        mSeriesNetThroughput2.setThickness(thickness); 
        
        
        
        mSeriesHum1 = new LineGraphSeries<>();
        mSeriesHum1.setColor(Color.RED);
        mSeriesHum1.setTitle("Humidity 1");
        mSeriesHum1.setDrawDataPoints(true);
        mSeriesHum1.setDrawDataPoints(true);
        mSeriesHum1.setDataPointsRadius(radius);
        mSeriesHum1.setThickness(thickness);
        
        mSeriesTemp1 = new LineGraphSeries<>();
        mSeriesTemp1.setColor(Color.RED);
        mSeriesTemp1.setTitle("Temp 1");
        mSeriesTemp1.setDrawDataPoints(true);
        mSeriesTemp1.setDrawDataPoints(true);
        mSeriesTemp1.setDataPointsRadius(radius);
        mSeriesTemp1.setThickness(thickness);
        
        mSeriesSmoke1 = new LineGraphSeries<>();
        mSeriesSmoke1.setColor(Color.RED);
        mSeriesSmoke1.setTitle("Smoke 1");
        mSeriesSmoke1.setDrawDataPoints(true);
        mSeriesSmoke1.setDrawDataPoints(true);
        mSeriesSmoke1.setDataPointsRadius(radius);
        mSeriesSmoke1.setThickness(thickness);
        
        mSeriesHum2 = new LineGraphSeries<>();
        mSeriesHum2.setColor(Color.YELLOW);
        mSeriesHum2.setTitle("Humidity 2");
        mSeriesHum2.setDrawDataPoints(true);
        mSeriesHum2.setDrawDataPoints(true);
        mSeriesHum2.setDataPointsRadius(radius);             
        mSeriesHum2.setThickness(thickness);
        
        mSeriesTemp2 = new LineGraphSeries<>();
        mSeriesTemp2.setColor(Color.YELLOW);
        mSeriesTemp2.setTitle("Temp 2");
        mSeriesTemp2.setDrawDataPoints(true);
        mSeriesTemp2.setDrawDataPoints(true);
        mSeriesTemp2.setDataPointsRadius(radius);
        mSeriesTemp2.setThickness(thickness);
        
        mSeriesSmoke2 = new LineGraphSeries<>();
        mSeriesSmoke2.setColor(Color.YELLOW);
        mSeriesSmoke2.setTitle("Smoke 2");
        mSeriesSmoke2.setDrawDataPoints(true);
        mSeriesSmoke2.setDrawDataPoints(true);
        mSeriesSmoke2.setDataPointsRadius(radius);
        mSeriesSmoke2.setThickness(thickness);
        
        
        graphNetwork.addSeries(mSeriesNet1);
        graphNetwork.addSeries(mSeriesNet2);
        graphNetwork.addSeries(mSeriesNetThroughput1);
        graphNetwork.addSeries(mSeriesNetThroughput2);
        
        graphNetwork.getViewport().setXAxisBoundsManual(true);
        graphNetwork.getViewport().setMinX(0);
        graphNetwork.getViewport().setMaxX(100);
        graphNetwork.getViewport().setScalable(true);
        graphNetwork.getViewport().setScrollable(true);
        
        graphNetworkDelay.addSeries(mSeriesNetDelay1);
        graphNetworkDelay.addSeries(mSeriesNetDelay2);
        
        graphNetworkDelay.getViewport().setXAxisBoundsManual(true);
        graphNetworkDelay.getViewport().setMinX(0);
        graphNetworkDelay.getViewport().setMaxX(50);
        graphNetworkDelay.getViewport().setScalable(true);
        graphNetworkDelay.getViewport().setScrollable(true);
        
        graphHum.addSeries(mSeriesHum1);
        graphHum.addSeries(mSeriesHum2);
        graphHum.getViewport().setXAxisBoundsManual(true);
        graphHum.getViewport().setMinX(0);
        graphHum.getViewport().setMaxX(100);
        graphHum.getViewport().setScalable(true);
        graphHum.getViewport().setScrollable(true);
        
               
        graphTemp.addSeries(mSeriesTemp1);
        graphTemp.addSeries(mSeriesTemp2);
        graphTemp.getViewport().setXAxisBoundsManual(true);
        graphTemp.getViewport().setMinX(0);
        graphTemp.getViewport().setMaxX(100);
        graphTemp.getViewport().setScalable(true);
        graphTemp.getViewport().setScrollable(true);
        
                  
        graphSmoke.addSeries(mSeriesSmoke1);
        graphSmoke.addSeries(mSeriesSmoke2);
        graphSmoke.getViewport().setXAxisBoundsManual(true);
        graphSmoke.getViewport().setMinX(0);
        graphSmoke.getViewport().setMaxX(100);
        graphSmoke.getViewport().setScalable(true);
        graphSmoke.getViewport().setScrollable(true);
        
               
        graphHum.getViewport().setMaxY(100);
        graphHum.getViewport().setMinY(0);
        graphTemp.getViewport().setMaxY(100);
        graphTemp.getViewport().setMinY(0);       
        graphSmoke.getViewport().setMaxY(1200);
        graphSmoke.getViewport().setMinY(0);
        
        graphNetwork.getViewport().setMaxY(1000);
        graphNetwork.getViewport().setMinY(0);
        
               
        graphHum.setTitle("Humidity Sensor (%) Red = sensor 1, Yellow = sensor 2");
        graphTemp.setTitle("Temperature Sensor (°C) Red = sensor 1, Yellow = sensor 2");        
        graphSmoke.setTitle("Smoke Sensor (PPM) Red = sensor 1, Yellow = sensor 2");
        graphNetwork.setTitle("B = data 1 (b) , G = data 2 (b), R = T 1 (b/s) , Y = T 2 (b/s) ");
        graphNetworkDelay.setTitle(" Red = delay 1 (ms), Yellow = delay 2 (ms)");
        
        
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);
        txtsuhu1 = (TextView) findViewById(R.id.txtsuhu1);
        txtsuhu2 = (TextView) findViewById(R.id.txtsuhu2);
        txtkelembaban1 = (TextView) findViewById(R.id.txtkelembaban1);          
        txtkelembaban2 = (TextView) findViewById(R.id.txtkelembaban2); 
        txtasap1 = (TextView) findViewById(R.id.txtasap1);
        txtasap2 = (TextView) findViewById(R.id.txtasap2);
        txtapi1 = (TextView) findViewById(R.id.txtapi1);
        txtapi2 = (TextView) findViewById(R.id.txtapi2);
        lnView = (LinearLayout) findViewById (R.id.lnView);
        lnView.setVisibility(View.GONE);
        
        txtCapture = (TextView) findViewById(R.id.txtCapture);
        txtCapture.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				moveRawFile(R.raw.btsnoophci, HCI_FILE_PATH);
				final String response = decodeHci(HCI_FILE_PATH);
				 runOnUiThread(new Runnable() {
	                    @Override
	                    public void run() {
	                    	txtCapture.setText(response);
	                    }
				 });
			}
		});
             
        imgMenu = (ImageView) findViewById (R.id.imgMenu);
        imgMenu.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(!view) {
					lnView.setVisibility(View.VISIBLE);
					view = true;
				} else {
					view = false;
					lnView.setVisibility(View.GONE);
				}
				
			}
		});
        
        
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }        
        playerLayout = (LinearLayout) findViewById(R.id.playerLayout);        
        connectButton = (ImageButton) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				scanForDevices();
			}
		});
              	
		updateInterfaceState();
		myHandler.removeCallbacks(mPlottingTask);
		myHandler.postDelayed(mPlottingTask, 1000);
		startTime = SystemClock.uptimeMillis();
    }
  
       
  private void ChartClear() {
	  	/*series1.clear();
	  	series2.clear();
	  	series3.clear();
	  	series4.clear();
	  	series5.clear();
	  	series6.clear();*/
  }

  int randomint = 9;
  long time_out1 = 0;
  long time_out2 = 0;
  long last_packet_size1 = 0;
  long last_packet_size2 = 0;
  private Runnable mPlottingTask = new Runnable() {  
	   public void run() {
		   long now = SystemClock.uptimeMillis();
		   double t = ( now - startTime )/10; 
		   if (t > 10000) { 
			   startTime = now;
			   t = 0.0;
			   mSeriesNet1.resetData(new DataPoint[]{});
			   mSeriesNet2.resetData(new DataPoint[]{});
			   mSeriesHum1.resetData(new DataPoint[]{});
			   mSeriesHum2.resetData(new DataPoint[]{});
			   mSeriesTemp1.resetData(new DataPoint[]{});
			   mSeriesTemp2.resetData(new DataPoint[]{});
			   mSeriesSmoke1.resetData(new DataPoint[]{});
			   mSeriesSmoke2.resetData(new DataPoint[]{});
			   mSeriesNetThroughput1.resetData(new DataPoint[]{});
			   mSeriesNetThroughput2.resetData(new DataPoint[]{});
			   mSeriesNetDelay1.resetData(new DataPoint[]{});
			   mSeriesNetDelay2.resetData(new DataPoint[]{});
		   }
		   runtime = t ;
		   if ( now - time_out1 > 2000 ) {
			    if ( !RecieveUpdate1 ) {
			    	TextView RX = (TextView)findViewById(R.id.txtRX1);
		    	    RX.setText(formatDecimal(0) + " b/s");
		    	    packet_size1 = 0;
			    }
			    time_out1 = now;
		   }
		   
		   if ( now - time_out2 > 2000 ) {
			    if ( !RecieveUpdate2 ) {
			    	TextView RX = (TextView)findViewById(R.id.txtRX2);
		    	    RX.setText(formatDecimal(0) + " b/s");	
		    	    packet_size2 = 0;
			    }
			    time_out2 = now;
		   }
		   
		   
		   
		   if (RecieveUpdate1 || RecieveUpdate2) {
			   RecieveUpdate1 = false;
			   RecieveUpdate2 = false;
			   graphHum1LastXValue+=5d;
			   graphHum2LastXValue+=5d;
			   mSeriesHum1.appendData( new DataPoint( graphHum1LastXValue , H1 ) , true, 100);
			   mSeriesHum2.appendData( new DataPoint( graphHum2LastXValue , H2 ) , true, 100);
			   mSeriesTemp1.appendData( new DataPoint( graphHum1LastXValue , T1 ) , true, 100);
			   mSeriesTemp2.appendData( new DataPoint( graphHum2LastXValue , T2 ) , true, 100);
			   mSeriesSmoke1.appendData( new DataPoint( graphHum1LastXValue , S1 ) , true, 100);
			   mSeriesSmoke2.appendData( new DataPoint( graphHum2LastXValue , S2 ) , true, 100);
			   mSeriesNet1.appendData( new DataPoint( graphHum2LastXValue , packet_size1 ) , true, 100);	
			   mSeriesNet2.appendData( new DataPoint( graphHum2LastXValue , packet_size2 ) , true, 100);
			   mSeriesNetThroughput1.appendData( new DataPoint( graphHum2LastXValue , avg_throughput1 ) , true, 100);
			   mSeriesNetThroughput2.appendData( new DataPoint( graphHum2LastXValue , avg_throughput2 ) , true, 100);
			   mSeriesNetDelay1.appendData( new DataPoint( graphHum2LastXValue , avg_delay1 ) , true, 100);	
			   mSeriesNetDelay2.appendData( new DataPoint( graphHum2LastXValue , avg_delay2 ) , true, 100);	
		   }
		   myHandler.postAtTime(this, now + 1000); 
	   }   
	};

   
	private void updateInterfaceState(){
    	boolean enabled = mChatService != null && mChatService.getState() == BluetoothChatService.STATE_CONNECTED;
    	if (enabled){
    		playerLayout.setVisibility(View.VISIBLE);
    		connectButton.setVisibility(View.GONE);
    	}
    	else{
    		playerLayout.setVisibility(View.GONE);
    		connectButton.setVisibility(View.VISIBLE);
    	}
    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
         } else {
            if (mChatService == null) setupChat();
        }
        /*LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        if (mChartView == null) {
        	mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer); //line
        	layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        } else {
            mChartView.repaint();
        } */      
        updateInterfaceState();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              mChatService.start();
            }
        }
        /*LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        if (mChartView == null) {
        	mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer); //line
        	layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        } else {
            mChartView.repaint();
        } */      
        updateInterfaceState();
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        mChatService = new BluetoothChatService(this, mHandler);
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        myHandler.removeCallbacks(mPlottingTask);
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
        myHandler.removeCallbacks(mPlottingTask);

    }

    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    private void sendMessage(String message) {
    	if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }      
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }
    
    @SuppressWarnings("unused")
	private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };

	
	String Temp ="";
	boolean ready;
	long time_throughput1 = 0;
    long time_throughput2 = 0;
    long start_t = 0;
    
    String buffer = "";
    boolean start;
    //BOARD_0 = 7
    //5,6
    private final Handler mHandler = new Handler() {    	
    	private void processReceivedMessage(String readMessage){    		
    		String allReceived = mReceived.concat(readMessage);
    		long now = SystemClock.uptimeMillis();
    		if ( readMessage.equals("B") ) {
    			start_t = now ;
    		}
    		
    		
    		
    		
    		if ( allReceived.length() == 7) {
    			String j = allReceived.substring(allReceived.length()-2, allReceived.length()-1);
    			int board = Integer.parseInt(j);
    			if ( board == 0 ) {
    				if ( time_throughput1 == 0 ) {
    					time_throughput1 = now - start_t ;
    	    		}    				
    			} else if ( board == 1 ) {
    				if ( time_throughput2 == 0 ) {
    					time_throughput2 = now - start_t ;
    	    		}  
    			}
    		}
    		
    		int index;    		
    		while((index = allReceived.indexOf('B')) != -1){
    			String command = allReceived.substring(0, index);
    			allReceived = allReceived.substring(index+1); 
    			if ( command.contains("BOARD_")) {
    				command = command.replace("BOARD_", "");
    			}
    			if ( command.contains("OARD_")){
    				command = command.replace("OARD_", "");
    			} 
    			 			 			
    			ProcessingParseData(command);    			
    		}   	    		
    		mReceived =allReceived;   		  		
    	}
    	
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);
                    //mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    mTitle.setText(R.string.title_not_connected);
                    break;
                }
                updateInterfaceState();
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:                
                String readMessage = (String)msg.obj;                
                processReceivedMessage(readMessage);
                break;
            case MESSAGE_DEVICE_NAME:                
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            if (resultCode == Activity.RESULT_OK) {
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                mChatService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            
            if (resultCode == Activity.RESULT_OK) {
                
                setupChat();
            } else {
                
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private int index_count_val;
    int count_max = 2;    
    int [] counter = new int[2];
    float [] val = new float [8];
    float [] avg = new float [8];
    
    byte [] incomming_data ;
    long start_time1 = 0;
    long start_time2 = 0;
    long start_timer3 = 0;
    long sum_byte_size1 = 0;
    long counter_byte1 = 0;
    long sum_byte_size2 = 0;
    long counter_byte2 = 0;
    
    int time_out_1 = 0;
    int time_out_2 = 0;
    /*
    throughput = Jumlah data yang dikirim / waktu pengiriman data    
    */
    
    
    long sum_delay1 = 0;
    long sum_delay2 = 0;
    long start_delay1 = 0;
    long start_delay2 = 0;
       
   
    
    protected void ProcessingParseData(String data) {
    	  	
    	data.trim();
    	int board = 2;
    	String [] parsing = data.split(",");
    	try {
	    	board = Integer.parseInt(parsing[0].trim()); 
	    	data_size = data.getBytes().length; 
    	}catch (Exception ex){
    		
    	}
    	long now = SystemClock.uptimeMillis();
    	if ( board == 0 ) {
    		 time_out1 = now;   		
    	} else if ( board == 1) {
    		 time_out2 = now;
    		 //throughput2 = (long)((float)data_size / ( now - time_throughput2 )); 
    		 //time_throughput2 = now;
    	}
    	
    	if ( start_delay1 == 0 ) {
    		 start_delay1 = now ;
    	}
    	
    	if ( start_delay2 == 0 ) {
   		 	 start_delay2 = now ;
    	}
    	
    	if ( board == 0 ) {
    		 //sum_delay1 = now - start_delay1;
    		 start_delay1 = now ;
    	}
    	
    	if ( board == 1 ) {
	   		 //sum_delay2 = now - start_delay2;
	   		 start_delay2 = now ;
    	}
    	    	
    	try {
    	
    	if (board == 0 ) {
	    	if ( now - start_time1 >= 1000 ) { // 1 sec
	    		time_out_2++;
	    		avg_data = sum_byte_size1 ;
		    	packet_size1 = sum_byte_size1;
		    	avg_throughput1 = (long)((float)( packet_size1 * 500 ) / ( now - time_throughput1 )) ; 
	    		time_throughput1 = now;
		    	sum_byte_size1 = 0;
		    	counter_byte1 = 0;
		    	sum_throughput1 = 0;		    	
		    	avg_delay1 = ( long )((float) sum_delay1 / ( packet_size1 ))  ;		    	
		    	sum_delay1 = 0;	    	
		    	TextView RX = (TextView)findViewById(R.id.txtRX1);
		    	if ( avg_data > 1000 ) {
		    	   	RX.setText(formatDecimal(packet_size1 / 1024) + " kb/s");
		    	} else {
		    	    	RX.setText(formatDecimal(packet_size1) + " b/s");
		    	}	    		
	    		start_time1 = now ;
	    	} else {
	    		sum_byte_size1+= data_size;	
	    		sum_delay1+= now - time_throughput1 ; 
	    	}
    	} else if (board == 1 ) {
	    	if ( now - start_time2 >= 1000 ) { // 1 sec
	    		packet_size2 = sum_byte_size2;
	    		sum_byte_size2 = 0;
	    		avg_throughput2 = sum_throughput2;
	    		avg_throughput2 = (long)((float) ( packet_size2 * 500) / ( now - time_throughput2 ))  ; 
	    		time_throughput2 = now;
	    		sum_throughput2 = 0;	
	    		avg_delay2 = ( long )((float) sum_delay2 / ( packet_size2  ))  ;		    	
		    	sum_delay2 = 0;	
	    		TextView RX = (TextView)findViewById(R.id.txtRX2);
		    	if ( avg_data > 1000 ) {
		    	   	RX.setText(formatDecimal(packet_size2 / 1024) + " kb/s");
		    	} else {
		    	  	RX.setText(formatDecimal(packet_size2) + " b/s");
		    	}	    				    	
	    		start_time2 = now ;
	    	} else {
	    		sum_byte_size2+= data_size;
	    		sum_throughput2+= throughput2;
	    		sum_delay2+= ( now - time_throughput2 );
	    		//counter_byte++;
	    	}
    	}
    	}catch (Exception e){
    		
    	}
    	if ( parsing.length >= 7 ) {
    	
    	    
			try {		
    		if ( board == 0) {
    			RecieveUpdate1=true;	
    			humidity = (int)Float.parseFloat(parsing[1].replace(".00", ""));
    			temp = (int)Float.parseFloat(parsing[2].replace(".00", ""));
    			smoke = Integer.parseInt(parsing[3].trim());
    			flame = Integer.parseInt(parsing[4].trim());
    			if ( counter[0] < count_max ) {
    				val[0]+= humidity;
    				val[1]+=temp;
    				val[2]+=smoke;
    				counter[0]++;    				
    			} else {
    				avg[0] = val[0] / count_max;
    				avg[1] = val[1] / count_max;
    				avg[2] = val[2] / count_max;
    				H1 = (int)avg[0];
        			T1 = (int)avg[1];
        			S1 = (int)avg[2];
        			txtkelembaban1.setText(H1 + " %");
        			txtsuhu1.setText(T1 + " °C");
        			txtasap1.setText(S1 + " PPM");
        			
        			if ( flame == 1 ) {
        				txtapi1.setText("Flame not detect!");
        			} else {
        				txtapi1.setText("Flame detect!");
        			}   				
    				counter[0] = 0;
    				val[0] = 0.0f;
    				val[1] = 0.0f;
    				val[2] = 0.0f;    				
    			}
    			
    			
    		}else if ( board == 1 ){
    			RecieveUpdate2 = true;	
    			humidity = (int)Float.parseFloat(parsing[1].replace(".00", ""));
    			temp = (int)Float.parseFloat(parsing[2].replace(".00", ""));
    			smoke = Integer.parseInt(parsing[3].trim());
    			flame = Integer.parseInt(parsing[4].trim());
    			if ( counter[1] < count_max ) {
    				val[4]+= humidity;
    				val[5]+= temp;
    				val[6]+= smoke;
    				counter[1]++;    				
    			} else {
    				avg[4] = val[4] / count_max;
    				avg[5] = val[5] / count_max;
    				avg[6] = val[6] / count_max;
    				H2 = (int)avg[4];
        			T2 = (int)avg[5];
        			S2 = (int)avg[6];
        			txtkelembaban2.setText(H2 + " %");
        			txtsuhu2.setText(T2 + " °C");
        			txtasap2.setText(S2 + " PPM");
        			
        			if ( flame == 0 ) {
        				txtapi2.setText("Flame not detect!");
        			} else {
        				txtapi2.setText("Flame detect!");
        			}   				
    				counter[1] = 0;
    				val[4] = 0.0f;
    				val[5] = 0.0f;
    				val[6] = 0.0f;    				
    			}   		
    		}
			}catch (Exception ex){
	    		
	    	}
    	 }
    	
    	
	}

	

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }
    
    public void scanForDevices(){
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.scan:
        	scanForDevices();
            return true;
        case R.id.debug:
            
            return true;
        }
        return false;
    }
    
    public static boolean isNumeric(String str)  
    {  
      try  
      {  
        double d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }
    
    public String formatDecimal(double number) {
        DecimalFormat nf = new DecimalFormat("###,###,###,##0");
        String formatted = nf.format(number);
        return formatted;
    } 
    
    public void moveRawFile(int fileId, String fileName) {
        try {
            InputStream in = getApplicationContext().getResources().openRawResource(fileId);
            FileOutputStream out = new FileOutputStream(fileName);
            byte[] buff = new byte[1024];
            int read = 0;

            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
}
    
   

}