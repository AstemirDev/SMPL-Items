package org.astemir.uniblend.core.particle;

import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.function.Predicate;

public class UParticleBeam {

    public static TraceResult sendDamageableBeam(Particle particle, Location begin, Vector direction, int distance, Predicate<LivingEntity> predicate, boolean ghost) {
        return sendDamageableBeam(new UParticleEffect(particle),begin,direction,distance,predicate,ghost);
    }

    public static TraceResult sendBeam(Particle particle, Location begin, Vector direction, int distance,boolean ghost) {
        return sendBeam(new UParticleEffect(particle),begin,direction,distance,ghost);
    }

    public static TraceResult sendDamageableBeam(UParticleEffect effect, Location begin, Vector direction, int distance, Predicate<LivingEntity> predicate, boolean ghost){
        for (double i = 0;i<distance;i+=0.25f){
            Location point = begin.clone().add(direction.clone().normalize().multiply(i));
            if (Float.isFinite((float)point.getX()) && Float.isFinite((float)point.getY()) && Float.isFinite((float)point.getZ())) {
                if (point.getBlock().isSolid() && !ghost){
                    return new TraceResult(point,true);
                }
                effect.play(point);
                point.getNearbyLivingEntities(0.25f, 0.25f, 0.25f, (entity) -> {
                    predicate.test(entity);
                    return true;
                });
            }
        }
        return new TraceResult(begin.clone().add(direction.clone().normalize().multiply(distance)),false);
    }

    public static TraceResult sendObservableBeam(UParticleEffect effect, Location begin, Vector direction, int distance, Predicate<Entity> predicate, boolean ghost){
        for (double i = 0;i<distance;i+=0.25f){
            Location point = begin.clone().add(direction.clone().normalize().multiply(i));
            if (Float.isFinite((float)point.getX()) && Float.isFinite((float)point.getY()) && Float.isFinite((float)point.getZ())) {
                if (point.getBlock().isSolid() && !ghost){
                    return new TraceResult(point,true);
                }
                effect.play(point);
                for (Entity nearbyEntity : point.getNearbyEntities(0.25f, 0.25f, 0.25f)) {
                    predicate.test(nearbyEntity);
                }
            }
        }
        return new TraceResult(begin.clone().add(direction.clone().normalize().multiply(distance)),false);
    }

    public static TraceResult sendBeam(UParticleEffect effect, Location begin, Vector direction, int distance, boolean ghost){
        for (double i = 0;i<distance;i+=0.25f){
            Location point = begin.clone().add(direction.clone().normalize().multiply(i));
            if (Float.isFinite((float)point.getX()) && Float.isFinite((float)point.getY()) && Float.isFinite((float)point.getZ())) {
                if (point.getBlock().isSolid() && !ghost){
                    return new TraceResult(point,true);
                }
                effect.play(point);
            }
        }
        return new TraceResult(begin.clone().add(direction.clone().normalize().multiply(distance)),false);
    }

    public static TraceResult sendParticleLightning(Particle particle, Location begin, Vector direction, int distance, Predicate<LivingEntity> predicate,int segmentsCount,float offset,boolean ghost) {
        return sendParticleLightning(new UParticleEffect(particle),begin,direction,distance,predicate,segmentsCount,offset,ghost);
    }

    public static TraceResult sendParticleLightning(UParticleEffect effect, Location begin, Vector direction, int distance, Predicate<LivingEntity> predicate, int segmentsCount, float offset, boolean ghost){
        Location loc = begin.clone();
        for (int i = 0;i<segmentsCount;i++){
            TraceResult result = sendDamageableBeam(effect,loc,direction.clone().add(new Vector(RandomUtils.randomFloat(-offset,offset),RandomUtils.randomFloat(-offset,offset),RandomUtils.randomFloat(-offset,offset))),distance,predicate,ghost);
            if (result.isMeetHardBlock()){
                return result;
            }else{
                loc = result.getLoc();
            }
        }
        return new TraceResult(loc,false);
    }

    public static TraceResult sendObservableParticleLightning(UParticleEffect effect, Location begin, Vector direction, int distance, Predicate<Entity> predicate, int segmentsCount, float offset, boolean ghost){
        Location loc = begin.clone();
        for (int i = 0;i<segmentsCount;i++){
            TraceResult result = sendObservableBeam(effect,loc,direction.clone().add(new Vector(RandomUtils.randomFloat(-offset,offset),RandomUtils.randomFloat(-offset,offset),RandomUtils.randomFloat(-offset,offset))),distance,predicate,ghost);
            if (result.isMeetHardBlock()){
                return result;
            }else{
                loc = result.getLoc();
            }
        }
        return new TraceResult(loc,false);
    }


    public static class TraceResult{

        private Location loc;
        private boolean meetHardBlock = false;

        public TraceResult(Location loc, boolean meetHardBlock) {
            this.loc = loc;
            this.meetHardBlock = meetHardBlock;
        }

        public Location getLoc() {
            return loc;
        }

        public boolean isMeetHardBlock() {
            return meetHardBlock;
        }
    }
}
