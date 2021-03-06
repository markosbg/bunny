package org.rabix.bindings.protocol.draft3.bean;

import java.util.List;

import org.rabix.bindings.BindingException;
import org.rabix.bindings.Bindings;
import org.rabix.bindings.BindingsFactory;
import org.rabix.bindings.model.Application;
import org.rabix.bindings.model.ApplicationPort;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class Draft3EmbeddedApp extends Draft3JobApp {

  private Application application;
  private List<Draft3InputPort> inputs;
  private List<Draft3OutputPort> outputs;

  @JsonCreator
  public Draft3EmbeddedApp(String raw) {
    Bindings bindings = BindingsFactory.create(raw);
    if (bindings != null) {
      try {
        application = bindings.loadAppObject(raw);
        inputs = Lists.transform(application.getInputs(), new Function<ApplicationPort, Draft3InputPort>() {
          @Override
          public Draft3InputPort apply(ApplicationPort port) {
            return new Draft3InputPort(port.getId(), port.getDefaultValue(), port.getSchema(), null, port.getScatter(), null, port.getLinkMerge());
          }
        });
        outputs = Lists.transform(application.getOutputs(), new Function<ApplicationPort, Draft3OutputPort>() {
          @Override
          public Draft3OutputPort apply(ApplicationPort port) {
            return new Draft3OutputPort(port.getId(), port.getDefaultValue(), port.getSchema(), null, port.getScatter(), null, port.getLinkMerge());
          }
        });
      } catch (BindingException e) {
        // TODO implement
      }
    }
  }
  
  @Override
  public List<Draft3InputPort> getInputs() {
    return inputs;
  }
  
  @Override
  public List<Draft3OutputPort> getOutputs() {
    return outputs;
  }
  
  @Override
  public String serialize() {
    return application.serialize();
  }
  
  @Override
  public Draft3JobAppType getType() {
    return Draft3JobAppType.EMBEDDED;
  }

}
