package de.techgamez.pleezon


public class GoalNode {
	private int x;
    private int y;
    private int z;

    public GoalNode(int x, int y,int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getZ() {
    	return z;
    }

    public AStarNode getPrevious() {
        return null;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof GoalNode && (((GoalNode) other).getX() == x && ((GoalNode) other).getY() == y && ((GoalNode) other).getZ()==z);
    }
}

