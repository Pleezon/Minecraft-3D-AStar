package de.techgamez.pleezon

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class AStarNode {
    private int x;
    private int y;
    private int z;
    private AStarNode previous;

    public AStarNode(int x, int y,int z, AStarNode previous) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.previous = previous;
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
        return previous;
    }
    public BlockPos getBlockPos() {
    	return new BlockPos(this.getX(),this.getY(),this.getZ());
    }
    @Override
    public boolean equals(Object other) {
        return other instanceof AStarNode && (((AStarNode) other).getX() == x && ((AStarNode) other).getY() == y && ((AStarNode) other).getZ()==z);
    }
    public ArrayList<AStarNode> getNeighbours(ArrayList<BlockPos> available){
    	ArrayList<AStarNode> ret = new ArrayList<AStarNode>();
    	BlockPos pos = this.getBlockPos();
    	//NEVER TO BE LOOKED AT AGAIN!
    	if(available.contains(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ()))) {ret.add(new AStarNode(pos.getX()+1, pos.getY(), pos.getZ(), this));}
    	if(available.contains(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()))) {ret.add(new AStarNode(pos.getX()-1, pos.getY(), pos.getZ(), this));}
    	if(available.contains(new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1))) {ret.add(new AStarNode(pos.getX(), pos.getY(), pos.getZ()+1, this));}
		if(available.contains(new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1))) {ret.add(new AStarNode(pos.getX(), pos.getY(), pos.getZ()-1, this));}
		//UP
		if(Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(pos.getX(),pos.getY()+2,pos.getZ())).getBlock().getMaterial().equals(net.minecraft.block.material.Material.air)) {
			if(available.contains(new BlockPos(pos.getX()+1,pos.getY()+1,pos.getZ()))) {ret.add(new AStarNode(pos.getX()+1, pos.getY()+1, pos.getZ(), this));}
			if(available.contains(new BlockPos(pos.getX()-1,pos.getY()+1,pos.getZ()))) {ret.add(new AStarNode(pos.getX()-1, pos.getY()+1, pos.getZ(), this));}
			if(available.contains(new BlockPos(pos.getX(),pos.getY()+1,pos.getZ()+1))) {ret.add(new AStarNode(pos.getX(), pos.getY()+1, pos.getZ()+1, this));}
			if(available.contains(new BlockPos(pos.getX(),pos.getY()+1,pos.getZ()-1))) {ret.add(new AStarNode(pos.getX(), pos.getY()+1, pos.getZ()-1, this));}
		}
		
		//DOWN (HELP MEEEEEEEEE)
		if(available.contains(new BlockPos(pos.getX()+1,pos.getY()-1,pos.getZ()))&&Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(pos.getX()+1,pos.getY()+1,pos.getZ())).getBlock().getMaterial().equals(net.minecraft.block.material.Material.air))
		{ret.add(new AStarNode(pos.getX()+1, pos.getY()-1, pos.getZ(), this));}
		if(available.contains(new BlockPos(pos.getX()-1,pos.getY()-1,pos.getZ()))&&Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(pos.getX()-1,pos.getY()+1,pos.getZ())).getBlock().getMaterial().equals(net.minecraft.block.material.Material.air))
		{ret.add(new AStarNode(pos.getX()-1, pos.getY()-1, pos.getZ(), this));}
		if(available.contains(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ()+1))&&Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(pos.getX(),pos.getY()+1,pos.getZ()+1)).getBlock().getMaterial().equals(net.minecraft.block.material.Material.air))
		{ret.add(new AStarNode(pos.getX(), pos.getY()-1, pos.getZ()+1, this));}
		if(available.contains(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ()-1))&&Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(pos.getX(),pos.getY()+1,pos.getZ()-1)).getBlock().getMaterial().equals(net.minecraft.block.material.Material.air))
		{ret.add(new AStarNode(pos.getX(), pos.getY()-1, pos.getZ()-1, this));}
    	return ret;
    	
    }
    }