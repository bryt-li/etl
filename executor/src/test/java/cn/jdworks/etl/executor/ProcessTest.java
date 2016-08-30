package cn.jdworks.etl.executor;

import org.junit.*;
import java.util.*;

public class ProcessTest {
    
//    @Test
    public void Test() {
        try {  
	    List<String> list = new ArrayList<String>();  
	    ProcessBuilder pb = null;  
	    Process p = null;  
	    list.add("java");  
	    list.add("-jar");  
	    list.add("/Users/lixin/git/etl/footask/target/footask.jar");
	    list.add("1234");
	    list.add("abcd");
	    list.add("hey");
	    list.add("jude");
	    pb = new ProcessBuilder(list);  
	    p = pb.start();  
  
	    // process error and output message  
	    StreamWatch errorWatch = new StreamWatch(p.getErrorStream(),  
						     "ERROR",true);  
	    StreamWatch outputWatch = new StreamWatch(p.getInputStream(),  
						      "OUTPUT",true);  
	    // start to watch  
	    errorWatch.start();
	    outputWatch.start();
	    //wait for exit  
	    int exitVal = p.waitFor();  
	    //print the content from ERROR and OUTPUT  
	    System.out.println("ERROR: " + errorWatch.getOutput());  
	    System.out.println("OUTPUT: " + outputWatch.getOutput());  
	    System.out.println("the return code is " + exitVal);  
	} catch (Throwable t) {  
	    t.printStackTrace();  
	} 
    }
}
