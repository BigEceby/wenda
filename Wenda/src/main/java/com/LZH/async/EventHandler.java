package com.LZH.async;

import java.util.List;

/**
 * Created by asus on 2017/4/21.
 */
public interface EventHandler {
    void doHandle(EventModel eventModel);
    List<EventType> getSupportEventTypes();
}
