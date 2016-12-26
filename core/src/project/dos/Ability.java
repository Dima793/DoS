package project.dos;

/**
 * Created by ASUS on 28.11.2016.
 */

public class Ability {
    public int number;
    public String name;
    public Ability(int abNumber, String abName) {
        number = abNumber;
        name = abName;
    }

    @Override
    public int hashCode() {
        return 11500 * number + name.hashCode();
    }

    @Override
    public boolean equals(Object a) {//maybe should check the class of parameter
        return (number == ((Ability) a).number) && (name == ((Ability) a).name);
    }
}
