package cn.nukkit.server.block.behavior;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.metadata.block.Liquid;
import cn.nukkit.server.entity.BaseEntity;
import com.flowpowered.math.vector.Vector3i;

import java.util.List;
import java.util.Optional;

public abstract class LiquidBlockBehavior extends SimpleBlockBehavior {
    private static final int[] FLOW_COST = new int[]{1000, 1000, 1000, 1000};
    private static final int DECAY_PER_BLOCK = 1;

    @Override
    public boolean onUpdate(Block block) {
        BlockState state = block.getBlockState();
        Liquid liquid = (Liquid) state.getBlockData();
        byte level = liquid.getLevel();
        int multiplier = getFlowDecayPerBlock();

        if (level > 0) {

        }
        return false;
    }

    @Override
    public boolean onEntityCollision(BaseEntity entity) {
        return false;
    }

    protected boolean canFlowInto(Block block) {
        BlockState state = block.getBlockState();
        return state.getBlockType().isFloodable() &&
                !(state.getBlockData() instanceof Liquid && ((Liquid) state.getBlockData()).getLevel() == 0);
    }

    protected int getFlowDecayPerBlock() {
        return DECAY_PER_BLOCK;
    }

    protected Vector3i getFlowVector(Block liquid) {
        Vector3i flowVector = Vector3i.ZERO;

        int level = ((Liquid) liquid.getBlockState().getBlockData()).getLevel();

        List<Block> neighbors = liquid.getNeighboringBlocksIfLoaded();
        for (Block neighbor : neighbors) {
            if (neighbor.getBlockPosition().getY() != liquid.getBlockPosition().getY()) {
                continue;
            }

            int flowLevel = getFlowLevel(liquid, neighbor);
            if (flowLevel < 0) {
                if (!neighbor.getBlockState().getBlockType().isFloodable()) {
                    continue;
                }
                Optional<Block> below = liquid.getLevel().getBlockIfChunkLoaded(liquid.getBlockPosition().sub(0, -1, 0));
                flowLevel = below.map(block -> getFlowLevel(liquid, block)).orElse(-1);

                if (flowLevel >= 0) {
                    int realLevel = flowLevel - (level - 8);
                    flowVector = flowVector.add(neighbor.getBlockPosition().sub(liquid.getBlockPosition()).mul(realLevel));
                }
            } else {
                int realLevel = flowLevel - level;
                flowVector = flowVector.add(neighbor.getBlockPosition().sub(liquid.getBlockPosition()).mul(realLevel));
            }
        }

        if (level >= 8) {
            boolean floodable = false;
            for (Block neighbor : neighbors) {
                if (neighbor.getBlockState().getBlockType().isFloodable()) {
                    floodable = true;
                    break;
                }
            }
            if (floodable) {
                flowVector = flowVector.add(0, -6, 0);
            }
        }

        return flowVector;
    }

    protected int getFlowLevel(Block block, Block neighbor) {
        if (block.getBlockState().getBlockType() != neighbor.getBlockState().getBlockType()) {
            return -1;
        } else {
            return ((Liquid) neighbor.getBlockState().getBlockData()).getLevel();
        }
    }
}
