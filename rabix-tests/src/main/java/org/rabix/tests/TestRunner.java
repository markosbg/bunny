package org.rabix.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.rabix.common.helper.JSONHelper;

public class TestRunner {

  private static String testDirPath;
  private static String cmd_prefix;
  private static String resultPath = "/Users/marko/code/bunny/rabix-backend-local/target/result.yaml";

  public static void main(String[] commandLineArguments) {
    testDirPath = commandLineArguments[0];
    cmd_prefix = commandLineArguments[1];
    startTestExecution();
    //startTestExecutionWithAbsolutePaths(); --> test it.

  }
  

  public static ArrayList<String> command(final String cmdline, final String directory) {
    try {
      Process process = new ProcessBuilder(new String[] { "bash", "-c", cmdline }).redirectErrorStream(true)
          .directory(new File(directory)).start();

      ArrayList<String> output = new ArrayList<String>();
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line = null;
      while ((line = br.readLine()) != null)
        output.add(line);

      if (0 != process.waitFor())
        return null;

      return output;

    } catch (Exception e) {
      return null;
    }
  }

  static void executeCommand(String cmdline) {
    ArrayList<String> output = command(cmdline, "/Users/marko/code/bunny/rabix-backend-local/target");
    if (null == output)
      System.out.println("COMMAND FAILED: " + cmdline + "\n");
    else
      for (String line : output)
        System.out.println(line);
  }

  /**
   * Reads content from a file
   */
  static String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  private static void startTestExecution() {
    boolean passed = false;
    File dir = new File(testDirPath);
    File[] directoryListing = dir.listFiles();
    if (dir.isDirectory()) {
      if (directoryListing != null) {
        executeCommand("tar -zxvf rabix-backend-local-0.0.1-SNAPSHOT.tar.gz");
        executeCommand("cp -a /Users/marko/code/bunny/rabix-tests/testbacklog .");
//        System.out.println("idemo odma:");
//        executeCommand("./rabix.sh -e . /Users/marko/code/bunny/rabix-tests/testbacklog/boysha2_grep.cwl.yaml /Users/marko/code/bunny/rabix-tests/testbacklog/grep2.inputs.yaml > result.yaml");
        for (File child : directoryListing) {
          if (!child.toString().endsWith("test.yaml"))
            continue;
          try {
            String inputsText = readFile(child.getAbsolutePath(), Charset.defaultCharset());
            Map<String, Object> inputSuite = JSONHelper.readMap(JSONHelper.transformToJSON(inputsText));
            Iterator entries = inputSuite.entrySet().iterator();
            while (entries.hasNext()) {
              Entry thisEntry = (Entry) entries.next();
              Object test_name = thisEntry.getKey();
              Object test = thisEntry.getValue();
              System.out.println("Running test: " + test_name + "\nWith given parameters:");
              Map<String, Map<String, LinkedHashMap>> mapTest = (Map<String, Map<String, LinkedHashMap>>) test;
              System.out.println("  app: " + mapTest.get("app"));
              System.out.println("  inputs: " + mapTest.get("inputs"));
              System.out.println("  expected: " + mapTest.get("expected"));
              String cmd = cmd_prefix + " -e . " + mapTest.get("app") + " " + mapTest.get("inputs") + " > result.yaml";
              System.out.println("->Running cmd: " + cmd);
              executeCommand(cmd);
              File resultFile = new File(resultPath);
              String resultText = readFile(resultFile.getAbsolutePath(), Charset.defaultCharset());
              Map<String, Object> resultData = JSONHelper.readMap(JSONHelper.transformToJSON(resultText));
              System.out.println("Generated result file:");
              System.out.println(resultText);
              passed = validateTestCase(mapTest, resultData);
              System.out.println("Test result:");
              if (passed) {

                System.out.println("Test: " + test_name + " PASSED");
              } else {
                System.out.println("Test: " + test_name + " FAILED");
              }

            }

          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      } else {
        System.out.println("[ERROR] Test directory is empty.");
      }
    } else {
      System.out.println("[ERROR] Test directory path is not valid directory path.");
    }

  }

  private static boolean validateTestCase(Map<String, Map<String, LinkedHashMap>> mapTest,
      Map<String, Object> resultData) {
    String resultFileName;
    int resultFileSize;
    String resultFileClass;
    Map<String, Object> resultValues = ((Map<String, Object>) resultData.get("outfile"));
    resultFileName = resultValues.get("path").toString();
    resultFileName = resultFileName.split("/")[resultFileName.split("/").length - 1];
    resultFileSize = (int) resultValues.get("size");
    resultFileClass = resultValues.get("class").toString();
    System.out.println("Test validation:");
    System.out.println("result file name: " + resultFileName + ", expected file name: "
        + mapTest.get("expected").get("outfile").get("name"));
    System.out.println("result file size: " + resultFileSize + ", expected file size: "
        + mapTest.get("expected").get("outfile").get("size"));
    System.out.println("result file class: " + resultFileClass + ", expected file class: "
        + mapTest.get("expected").get("outfile").get("class"));

    if (resultFileName.equals(mapTest.get("expected").get("outfile").get("name"))) {
      if (resultFileSize == (int) mapTest.get("expected").get("outfile").get("size")) {
        if (resultFileClass.equals(mapTest.get("expected").get("outfile").get("class"))) {
          return true;
        } else {
          System.out.println("result and expected file class are not equal!");
        }
      } else {
        System.out.println("result and expected file size are not equal!");
      }
    } else {
      System.out.println("result and expected file name are not equal!");
    }

    return false;
  }

}
