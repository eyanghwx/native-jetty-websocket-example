package org.eclipse.jetty.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Shell {
  
  private static Process process;
  private Pair pair;
  
  public Shell() {
  }
  
  public boolean isAlive() {
    try {
      process.exitValue();
      return false;
    } catch (IllegalThreadStateException e) {
      return true;
    }
  }

  public Pair run() throws IOException {
    List<String> args = new ArrayList<String>();
    args.add("/usr/local/hadoop-3.2.0-SNAPSHOT/bin/container-executor2");
    args.add("--run-docker");
    args.add("/tmp/test.cmd");
    //args.add("bash");
    //args.add("-i");
    ProcessBuilder build = new ProcessBuilder(args);
    build.redirectErrorStream(true);
    process = build.start();
    pair = new Pair(process.getInputStream(), process.getOutputStream());
    return pair;
  }
  
  public void waitFor() throws InterruptedException {
    process.waitFor();
  }

  public static void main(String[] args) {
    try {
      Shell shell = new Shell();
      Pair pair = shell.run();
      byte[] buffer = new byte[4000];
      while (shell.isAlive()) {
        int no = pair.input.available();
        if (no > 0) {
          int n = pair.input.read(buffer, 0, Math.min(no,  buffer.length));
          System.out.print(new String(buffer, 0, n));
        }
        int ni = System.in.available();
        if (ni > 0) {
          int n = System.in.read(buffer,  0, Math.min(ni,  buffer.length));
          pair.output.write(buffer, 0, n);
          pair.output.flush();
        }
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
        }
      }
      
    } catch (Exception e) {
      
    }
  }

  public void close() {
    process.destroy();
  }
}
