package org.rabix.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;




import org.rabix.common.helper.JSONHelper;
//import org.rabix.common.helper.JSONHelper;
//import org.rabix.common.helper.ResourceHelper;
import org.yaml.snakeyaml.Yaml;


public class App {
	
	public static void main(String[] commandLineArguments){
		String testDirPath = commandLineArguments[0];
		String cmd_prefix = commandLineArguments[1];
		App app = new App();
		boolean passed;
		int numOfTests = 0;
		numOfTests = getNumOfTestsInSuite(testDirPath);
		
		
		File dir = new File(testDirPath);
	    File[] directoryListing = dir.listFiles();
	    if (directoryListing != null) {
	    	for (File child : directoryListing) {
	    		if(!child.toString().endsWith("test.yaml")) continue;
	    		
	    		Yaml yaml = new Yaml();
	    		try {
					Map<String, Map<String, String>> mapFromYaml = (Map<String, Map<String, String>>) yaml.load(new FileInputStream(new File(child.toString())));


					Iterator entries = mapFromYaml.entrySet().iterator();
					while (entries.hasNext()) {
					  Entry thisEntry = (Entry) entries.next();
					  Object test_name = thisEntry.getKey();
					  Object test = thisEntry.getValue();
					  System.out.println("Running test: " + test_name +"\nWith given details:\n"+ test);
					  
					  passed = run_test(test, cmd_prefix);
					  
					  if(!passed) {
						  //test failed
					  }
					  
					  
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    } else {
	    System.out.println("---error ---> The test directory is empty <--- Marko ");
	  }
		//app.prepareTestSuite();
	}

	
	private static boolean run_test(Object test, String cmd_prefix) {
		boolean passed = true;
		System.out.println("---- in run_test method:");
		Map<String, Map<String, String>> testDetails = (Map<String, Map<String, String>>) test;
			
		System.out.println("\tapp: " + testDetails.get("app"));
		System.out.println("\tinputs: " + testDetails.get("inputs"));
		System.out.println("\truntime: " + String.valueOf(testDetails.get("runtime")));
		System.out.println("\texpected: " + testDetails.get("expected"));
		
		String cmd = cmd_prefix + " -e . " + testDetails.get("app") + " " + testDetails.get("inputs") +" > result.yaml";
		System.out.println("\tRunning cmd: "+cmd);
		Process cmdProc = null;
//		try {
//			cmdProc = Runtime.getRuntime().exec(cmd);
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		}
//		int retValue = cmdProc.exitValue();
//		
//		System.out.println("returned: " + retValue);
//		
//		
//		if(retValue != 0) passed = false;
		Yaml resultYML = new Yaml();
		Map<String, Map<String, String>> result = null;
		try {
			result = (Map<String, Map<String, String>>) resultYML.load(new FileInputStream(new File("/Users/marko/code/bunny/rabix-tests/testbacklog/result.yaml")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		passed = checkResult(result, testDetails.get("expected"));
		
		
		
		return passed;
	}


	private static boolean checkResult(Map<String, Map<String, String>> result, Map<String, String> expected) {
		
		String path = result.get("outfile").get("path");
		
		System.out.println("output file path: "+ path);
		String split[] = path.split("/");
		System.out.println("output file name: " + split[split.length -1]);
		
		
		
		
		
		return false;
	}


	private static int getNumOfTestsInSuite(String testDirPath) {
		int numOfTests = 0;
		File dir = new File(testDirPath);
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		    	if(!child.toString().endsWith("test.yaml")) continue;
		    	numOfTests++;
		    }
		  } else {
		    System.out.println("---error ---> The test directory is empty <--- Marko ");
		  }
		return numOfTests;
	}


//	private void prepareTestSuite() {
//		
//		String yamlData = null;
//		try {
//			yamlData = ResourceHelper.readResource(this.getClass(), "hello_world.yaml");
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		String jsonData = JSONHelper.transformToJSON(yamlData);
//		
//		
//		Map<String, Object> finalMap = JSONHelper.readMap(JSONHelper.transformToJSON(jsonData));
//		String pera = (String) finalMap.get("test1").toString();
//		
//
//		ArrayList<String> listaVals = new ArrayList<String>();
//		
//		for (Map.Entry<String, Object> entry : finalMap.entrySet())
//		{
//		    System.out.println("finalMap keys-values: "+entry.getKey() + " : " + entry.getValue());
//		    listaVals.add(entry.getValue().toString());
//		}
//		
//		Map<String,String> mapVals = null;
//		
//		System.out.println("Ispis liste:");
//		for(String elem : listaVals){
//			System.out.println(elem);
//			
//		}
//		
//		
//		System.out.println("yamlData:\n"+yamlData);
//		System.out.println("-----------------------");
//		System.out.println("jsonData:\n"+jsonData);
//		System.out.println("-----------------------");
//		System.out.println("finalMap:\n"+finalMap);
//		
//	}
   
}
