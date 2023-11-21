package org.astemir.lib.meg;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.astemir.uniblend.core.entity.parent.UEntity;


public interface ModelBasedEntity {

    default void setupModel(String modelName){
        ActiveModel model = ModelEngineAPI.createActiveModel(modelName);
        ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(((UEntity)this).getHandle());
        modeledEntity.addModel(model,true);
        setModel(model);
        setModelEntity(modeledEntity);
    }

    default void removeModel(){
        if (getModel() != null) {
            getModel().destroy();
        }
        if (getModelEntity() != null) {
            getModelEntity().destroy();
        }
    }


    default void playAnimation(String animationName,float lerpIn,float lerpOut,float speed,boolean force){
        if (getModel() != null) {
            getModel().getAnimationHandler().playAnimation(animationName, lerpIn, lerpOut, speed, force);
        }
    }

    default void stopAnimation(String animationName){
        if (getModel() != null) {
            getModel().getAnimationHandler().stopAnimation(animationName);
        }
    }

    default boolean isPlayingAnimation(String animationName){
        if (getModel() != null) {
            return getModel().getAnimationHandler().isPlayingAnimation(animationName);
        }
        return false;
    }


    void setModelEntity(ModeledEntity modelEntity);
    void setModel(ActiveModel model);
    ActiveModel getModel();
    ModeledEntity getModelEntity();
}
