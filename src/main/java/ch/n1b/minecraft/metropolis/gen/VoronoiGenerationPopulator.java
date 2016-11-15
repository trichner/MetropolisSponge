package ch.n1b.minecraft.metropolis.gen;

import ch.n1b.minecraft.metropolis.noise.Voronoi2D;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.gen.GenerationPopulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 11/15/2016.
 */
public class VoronoiGenerationPopulator implements GenerationPopulator {

    private Voronoi2D voronoi2D;

    private Cause cause = Cause.source(this).build();

    private List<BlockType> types = ImmutableList.of(BlockTypes.STONE, BlockTypes.DIRT, BlockTypes.BRICK_BLOCK,
            BlockTypes.WOOL, BlockTypes.MOSSY_COBBLESTONE, BlockTypes.DIAMOND_BLOCK,
            BlockTypes.GOLD_BLOCK, BlockTypes.REDSTONE_BLOCK);

    public VoronoiGenerationPopulator(long seed) {
        this.voronoi2D = new Voronoi2D(seed, 0.1d, (x, y) -> x + y);
    }

    @Override
    public void populate(World world, MutableBlockVolume buffer, ImmutableBiomeArea biomes) {
        Vector3i min = buffer.getBlockMin();
        Vector3i max = buffer.getBlockMax();

        for(int x = min.getX(); x<=max.getX(); x++){
            for(int z = min.getZ(); z<=max.getZ(); z++){
                double field = voronoi2D.noise(x,z).field;
                buffer.setBlockType(x,1,z,toBlock(field), cause);
            }
        }
    }

    public BlockType toBlock(double noise){
        int iNoise = (int) Math.abs(Math.round(noise * 1000d));
        return types.get(iNoise % types.size());
    }
}
