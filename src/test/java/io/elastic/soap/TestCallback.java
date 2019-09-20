package io.elastic.soap;

import io.elastic.api.EventEmitter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCallback implements EventEmitter.Callback {

  private static final Logger logger = LoggerFactory.getLogger(TestCallback.class);

  private List<Object> list = new ArrayList<>();

  @Override
  public void receive(Object data) {
    logger.info("Emitted object: {}", data);
    list.add(data);
  }


  public List<Object> getCalls() {
    return list;
  }

  public Object getLastCall() {
    if(list.size()>0){
      return list.get(list.size() - 1);
    }
    return null;
  }

  public void reset() {
    list = new ArrayList<>();
  }
}


