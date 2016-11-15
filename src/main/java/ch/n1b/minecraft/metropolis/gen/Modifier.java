package ch.n1b.minecraft.metropolis.gen;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

/**
 * Created by thomas on 11/15/2016.
 */
public class Modifier implements WorldGeneratorModifier {

    @Override
    public String getId() {
        return "metropolis:default";
    }

    @Override
    public String getName() {
        return "Metropolis Modifier";
    }

    @Override
    public void modifyWorldGenerator(WorldProperties world, DataContainer settings, WorldGenerator worldGenerator) {
        worldGenerator.setBaseGenerationPopulator(new VoronoiGenerationPopulator(world.getSeed()));
    }

}
