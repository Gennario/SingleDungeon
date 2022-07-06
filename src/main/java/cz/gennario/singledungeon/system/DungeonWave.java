package cz.gennario.singledungeon.system;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DungeonWave {

    private final String id, name;
    private final List<DungeonGang> gangs;

    public DungeonWave(String id, String name, List<DungeonGang> gangs) {
        this.id = id;
        this.name = name;
        this.gangs = gangs;
    }

}
