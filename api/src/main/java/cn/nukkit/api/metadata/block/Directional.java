package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.util.data.BlockFace;

import java.util.Objects;

public class Directional implements Metadata {
    private final BlockFace face;

    Directional(BlockFace face) {
        this.face = face;
    }

    public BlockFace getFace() {
        return face;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directional that = (Directional) o;
        return face == that.face;
    }

    @Override
    public int hashCode() {
        return Objects.hash(face);
    }

    @Override
    public String toString() {
        return "Directional(" +
                "face=" + face +
                ')';
    }
}
