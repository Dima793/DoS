package project.dos;

/**
 * Created by Дмитрий on 05.12.2016.
 */

public class CreatureHandler {
    private Creature creature;

    CreatureHandler(Creature baseCreature) {
        creature = baseCreature;
    }

    public Creature get() {
        return creature;
    }
}
