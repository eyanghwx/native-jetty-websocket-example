package org.eclipse.jetty.demo;

import java.io.InputStream;
import java.io.OutputStream;

public class Pair {
  public final InputStream input;
  public final OutputStream output;
  public Pair(InputStream input, OutputStream output) {
    this.input = input;
    this.output = output;
  }
}
