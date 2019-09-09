package io.elastic.soap;

import io.elastic.api.EventEmitter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCallback implements EventEmitter.Callback {

  private static final Logger logger = LoggerFactory.getLogger(TestCallback.class);

  private static List<Object> list = new ArrayList<>();

  @Override
  public void receive(Object data) {
    logger.info("Emitted object: {}", data);
    list.add(data);
  }


  public static List<Object> getCalls() {
    return list;
  }

  public static Object getLastCall() {
    return list.get(list.size() - 1);
  }

  public void reset() {
    list = new ArrayList<>();
  }
}


