package particles;

import Engine.ModelLoadHandler;
import Entities.Camera;
import org.lwjglx.util.vector.Matrix4f;

import java.util.*;

/**
 * Created by Beau on 9/10/2016.
 */
public class ParticleHandler {
    private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;

    public static void init(ModelLoadHandler loader, Matrix4f projectionMatrix){
        renderer = new ParticleRenderer(loader, projectionMatrix);
    }

    public static void update(Camera camera){
        Iterator<Map.Entry<ParticleTexture,List<Particle>>> mapIterator = particles.entrySet().iterator();
        while(mapIterator.hasNext()){
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();
            while(iterator.hasNext()){
                Particle p = iterator.next();
                boolean stillAlive =  p.update(camera);
                if(!stillAlive){
                    iterator.remove();
                    if(list.isEmpty()){
                        mapIterator.remove();
                    }
                }
            }
            InsertionSort.sortHighToLow(list);
        }
    }

    public static void renderParticles(Camera camera){
        renderer.render(particles,camera);
    }

    public static void cleanUp(){
        renderer.cleanUp();
    }

    public static void addParticle(Particle particle){
        List<Particle> list = particles.get(particle.getTexture());
        if (list ==  null){
            list = new ArrayList<>();
            particles.put(particle.getTexture(),list);
        }
        list.add(particle);
    }
}