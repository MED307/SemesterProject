package application;

import java.util.Comparator;

public class PlayerSorter implements Comparator<Player> 
{
    @Override
    public int compare(Player o1, Player o2) {
        return o2.getInitiative() - o1.getInitiative();
    }
}