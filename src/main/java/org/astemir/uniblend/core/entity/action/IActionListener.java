package org.astemir.uniblend.core.entity.action;


import java.util.ArrayList;
import java.util.List;

public interface IActionListener {

    List<ActionController> EMPTY = new ArrayList<>();

    default void onActionBegin(Action state){};

    default void onActionEnd(Action state){};

    default void onActionTick(Action state,int ticks){};


    default List<ActionController> getControllers(){return EMPTY;}
}
