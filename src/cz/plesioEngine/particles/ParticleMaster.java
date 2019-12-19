package cz.plesioEngine.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import cz.plesioEngine.entities.Camera;
import cz.plesioEngine.renderEngine.Loader;

public class ParticleMaster {

    private static final Map<ParticleTexture, List<Particle>> PARTICLES = new HashMap<ParticleTexture, List<Particle>>();
    private static ParticleRenderer particleRenderer;

    public static void init(Loader loader, Matrix4f projectionMatrix) {
        particleRenderer = new ParticleRenderer(loader, projectionMatrix);
    }

    public static void update(Camera camera) {
        Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = PARTICLES.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Entry<ParticleTexture, List<Particle>> entry = mapIterator.next();
            List<Particle> list = entry.getValue();
            Iterator<Particle> iterator = list.iterator();
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                boolean stillAlive = p.update(camera);
                if (!stillAlive) {
                    iterator.remove();
                    if (list.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }
            if (!entry.getKey().isGlowing()) {
                InsertionSort.sortHighToLow(list);
            }
        }
    }

    public static void renderParticles(Camera camera) {
        particleRenderer.render(PARTICLES, camera);
    }

    public static void cleanUp() {
        particleRenderer.cleanUp();
    }

    public static void addParticle(Particle particle) {
        List<Particle> list = PARTICLES.get(particle.getTexture());
        if (list == null) {
            list = new ArrayList<Particle>();
            PARTICLES.put(particle.getTexture(), list);
        }
        list.add(particle);
    }

}
