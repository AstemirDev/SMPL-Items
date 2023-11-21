package org.astemir.uniblend.core.entity.action;

import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.python.antlr.ast.Str;

import java.util.ArrayList;
import java.util.List;

public class ActionController<T extends IActionListener> {

    public static final UJsonDeserializer<ActionController> DESERIALIZER = (json)->{
        JsonObject jsonObject = json.getAsJsonObject();
        JsonObject actionsJson = jsonObject.getAsJsonObject("actions");
        String name = jsonObject.get("name").getAsString();
        int delay = 0;
        if (jsonObject.has("delay")){
            delay = jsonObject.get("delay").getAsInt();
        }
        List<Action> actions = new ArrayList<>();
        int i = 0;
        for (String actionName : actionsJson.keySet()) {
            Action action = new Action(i,actionName,actionsJson.get(actionName).getAsInt());
            actions.add(action);
            i++;
        }
        return new ActionController(name,delay,actions.toArray(new Action[actions.size()]));
    };

    public static final Action NO_ACTION = new Action(-1,"noAction",-1);
    private Action[] actions;
    private Action currentAction = NO_ACTION;
    private T owner;
    private String name;
    private int actionTick = 0;
    private int delay;

    public ActionController(String name,Action... actions) {
        this(name,0,actions);
    }

    public ActionController(String name,int delay,Action... actions) {
        this.delay = delay;
        this.name = name;
        this.actions = actions;
    }

    public ActionController<T> owner(T owner){
        this.owner = owner;
        return this;
    }

    public void playAction(String actionName){
        Action action = getActionByName(actionName);
        if (action != null){
            playAction(action);
        }
    }

    public void playAction(Action action){
        playAction(action,0);
    }

    public void playAction(Action action,int ownDelay) {
        if (!is(action) || action.isCanOverrideSelf()) {
            if (action.getLength() > 0) {
                setActionWithoutSync(action, action.getLength() +delay+ownDelay);
            }else{
                setActionWithoutSync(action, -1);
            }
            action.onStart(this);
            getOwner().onActionBegin(action);
            onActionBegin(action);
        }
    }

    @Deprecated
    public void setAction(Action action,int ticks) {
        if (!is(currentAction) || currentAction.isCanOverrideSelf()) {
            if (ticks > 0) {
                setActionWithoutSync(action, ticks + delay);
            }else{
                setActionWithoutSync(action, -1);
            }
            action.onStart(this);
            getOwner().onActionBegin(action);
            onActionBegin(action);
        }
    }

    public void setActionWithoutSync(Action action,int ticks){
        this.currentAction = action;
        this.actionTick = ticks;
    }

    public String getName() {
        return name;
    }

    public int getTicks(){
        return actionTick;
    }

    public void setTick(int ticks){
        this.actionTick = ticks;
    }

    public void setNoState(){
        playAction(NO_ACTION);
    }

    public boolean isNoAction(){
        return getActionState() == NO_ACTION;
    }


    public boolean is(String actionName){
        Action action = getActionByName(actionName);
        if (action != null){
            return is(action);
        }
        return false;
    }

    public boolean is(String... actionNames){
        for (String actionName : actionNames) {
            if (is(actionName)){
                return true;
            }
        }
        return false;
    }

    public boolean is(Action state){
        return getActionState() == state;
    }

    public boolean is(Action... states){
        for (Action state : states) {
            if (is(state)){
                return true;
            }
        }
        return false;
    }


    public void update(){
        Action previous = currentAction;
        if (!isNoAction()) {
            if (actionTick > 0) {
                currentAction.onTick(this,actionTick);
                onActionTick(currentAction, actionTick);
                getOwner().onActionTick(currentAction,actionTick);
                actionTick--;
            }else {
                if (actionTick != -1) {
                    currentAction.onEnd(this);
                    onActionEnd(currentAction);
                    getOwner().onActionEnd(currentAction);
                    if (is(previous) && actionTick != -1) {
                        playAction(NO_ACTION);
                    }
                }
            }
        }
    }

    public Action[] getActions() {
        return actions;
    }

    public Action getActionByName(String name){
        for (Action action : actions) {
            if (action.getName().equals(name)){
                return action;
            }
        }
        return NO_ACTION;
    }

    public Action getActionById(int id){
        if (id == -1){
            return NO_ACTION;
        }
        for (Action action : actions) {
            if (action.getId() == id){
                return action;
            }
        }
        return NO_ACTION;
    }

    public int getDelay() {
        return delay;
    }

    public T getOwner() {
        return owner;
    }

    public Action getActionState() {
        return currentAction;
    }

    public void onActionBegin(Action state){};

    public void onActionEnd(Action state){};

    public void onActionTick(Action state,int ticks){};
}
