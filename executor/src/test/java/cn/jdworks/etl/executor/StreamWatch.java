package cn.jdworks.etl.executor;

import java.util.*;
import java.io.*;


public class StreamWatch extends Thread {  
    InputStream is;  
    String type;  
    List<String> output = new ArrayList<String>();  
    boolean debug = false;  

    StreamWatch(InputStream is, String type) {  
	this(is, type, false);  
    }  
  
    StreamWatch(InputStream is, String type, boolean debug) {  
	this.is = is;  
	this.type = type;  
	this.debug = debug;  
    }  
  
    public void run() {  
	try {  
	    InputStreamReader isr = new InputStreamReader(is);  
	    BufferedReader br = new BufferedReader(isr);  
	    String line = null;  
	    while ((line = br.readLine()) != null) {  
		output.add(line);  
		if (debug)  
		    System.out.println(type + ">" + line);  
	    }  
	} catch (IOException ioe) {  
	    ioe.printStackTrace();  
	}  
    }  
  
    public List<String> getOutput() {  
	return output;  
    }  
}

