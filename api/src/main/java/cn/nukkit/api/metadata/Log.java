package cn.nukkit.api.metadata;

import cn.nukkit.api.metadata.data.LogDirection;
import cn.nukkit.api.metadata.data.TreeSpecies;
import com.google.common.base.Preconditions;

import java.util.Objects;

public class Log extends Wood {
    private final LogDirection direction;

    private Log(TreeSpecies species, LogDirection direction) {
        super(species);
        this.direction = direction;
    }

    public static Log of(TreeSpecies species, LogDirection direction) {
        Preconditions.checkNotNull(species, "species");
        Preconditions.checkNotNull(direction, "direction");
        return new Log(species, direction);
    }

    public LogDirection getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Log log = (Log) o;
        return direction == log.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), direction);
    }

    @Override
    public String toString() {
        return "Log{" +
                "species=" + getSpecies() + ',' +
                "direction=" + direction +
                '}';
    }
}
