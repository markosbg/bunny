package org.rabix.bindings.protocol.draft3.bean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.rabix.bindings.protocol.draft3.expression.Draft3ExpressionException;
import org.rabix.bindings.protocol.draft3.expression.helper.Draft3ExpressionBeanHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Draft3CommandLineTool extends Draft3JobApp {

  public static final String KEY_ARGUMENT_VALUE = "valueFrom";

  @JsonProperty("stdin")
  private Object stdin;
  @JsonProperty("stdout")
  private Object stdout;
  @JsonProperty("baseCommand")
  private Object baseCommand;
  @JsonProperty("arguments")
  private List<Object> arguments;
  

  public Draft3CommandLineTool() {
    super();
    this.baseCommand = new ArrayList<>();
    this.arguments = new ArrayList<>();
  }

  @SuppressWarnings("unchecked")
  public List<Object> getBaseCmd(Draft3Job job) throws Draft3ExpressionException {
    List<Object> result = new LinkedList<>();

    if (baseCommand instanceof List<?>) {
      for (Object baseCmd : ((List<Object>) baseCommand)) {
        Object transformed = transformBaseCommand(job, baseCmd);
        if (transformed != null) {
          result.add(transformed);
        }
      }
    } else if (baseCommand instanceof String) {
      Object transformed = transformBaseCommand(job, baseCommand);
      if (transformed != null) {
        result.add(transformed);
      }
    }
    return result;
  }
  
  private Object transformBaseCommand(Draft3Job job, Object baseCommand) throws Draft3ExpressionException {
    if (Draft3ExpressionBeanHelper.isExpression(baseCommand)) {
      return Draft3ExpressionBeanHelper.evaluate(job, baseCommand);
    } else {
      return baseCommand;
    }
  }

  public String getStdin(Draft3Job job) throws Draft3ExpressionException {
    if (Draft3ExpressionBeanHelper.isExpression(stdin)) {
      return Draft3ExpressionBeanHelper.evaluate(job, stdin);
    }
    return stdin != null ? stdin.toString() : "";
  }

  public String getStdout(Draft3Job job) throws Draft3ExpressionException {
    if (Draft3ExpressionBeanHelper.isExpression(stdout)) {
      return Draft3ExpressionBeanHelper.evaluate(job, stdout);
    }
    return stdout != null ? stdout.toString() : "";
  }

  public String getStderr(Draft3Job job) throws Draft3ExpressionException {
    String stdout = getStdout(job);
    return changeExtension(stdout, "err");
  }

  @JsonIgnore
  public boolean hasArguments() {
    return arguments != null;
  }

  public List<Object> getArguments() {
    return arguments;
  }

  @JsonIgnore
  public Object getArgument(Draft3Job job, Object binding) throws Draft3ExpressionException {
    if (binding instanceof Map<?, ?>) {
      Object value = ((Map<?, ?>) binding).get(KEY_ARGUMENT_VALUE);
      if (value == null) {
        return null;
      }
      if (Draft3ExpressionBeanHelper.isExpression(value)) {
        return Draft3ExpressionBeanHelper.evaluate(job, value);
      }
      return value;
    }
    return null;
  }

  /**
   * Replaces extension if there is any
   */
  private String changeExtension(String fileName, String extension) {
    if (fileName == null) {
      return null;
    }
    int lastIndexOf = fileName.lastIndexOf(".");
    return lastIndexOf != -1 ? fileName.substring(0, lastIndexOf + 1) + extension : fileName + "." + extension;
  }

  @Override
  @JsonIgnore
  public Draft3JobAppType getType() {
    return Draft3JobAppType.COMMAND_LINE_TOOL;
  }

  @Override
  public String toString() {
    return "CommandLineTool [stdin=" + stdin + ", stdout=" + stdout + ", baseCommands=" + baseCommand + ", arguments="
        + arguments + ", successCodes=" + successCodes + ", id=" + id + ", context=" + context + ", description="
        + description + ", label=" + label + ", contributor=" + contributor + ", owner=" + owner + ", inputs=" + getInputs()
        + ", outputs=" + getOutputs() + ", requirements=" + requirements + "]";
  }

}
