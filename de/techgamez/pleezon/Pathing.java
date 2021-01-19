package de.techgamez.pleezon


import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Pathing {
	private ArrayList<BlockPos> available = new ArrayList<>();
	private ArrayList<BlockPos> finalized = new ArrayList<>();
	private ArrayList<AStarNode> toDo = new ArrayList<>();
    private AStarNode current;
    private int GoalX;
    private int GoalY;
    private int GoalZ;
    private int timeoutRange;
    private BlockPos startFrom;
    private AStarNode result_node = null;
    
    private static ArrayList<BlockPos> toWalk = new ArrayList<>();
    private Thread toWalkCallback = null;
    
	public Pathing(GoalNode goalNode,BlockPos start, int timeoutRange) {
		this.GoalX = goalNode.getX();
		this.GoalY = goalNode.getY();
		this.GoalZ = goalNode.getZ();
		this.timeoutRange = timeoutRange;
		this.startFrom = start;
		//net.minecraft.entity.player.EntityPlayer
		// public float jumpMovementFactor = 0.02F;
	}
	public Pathing() {}
	
	public void startCalc(Thread callback) {
		new Thread(()-> {
			//check if player is essentially on the goal cause he might already be there.
			if(!(Math.sqrt(this.startFrom.distanceSq(new BlockPos(GoalX,GoalY,GoalZ)))<1.5)|| (Math.sqrt(this.startFrom.distanceSq(new BlockPos(GoalX,GoalY,GoalZ)))>this.timeoutRange)) {
				System.out.println("AIGHT, started.");
				
				this.result_node = null;
				EntityPlayer p = Minecraft.getMinecraft().thePlayer;
				BlockPos ppos = this.startFrom;
				System.out.println(Math.sqrt(ppos.distanceSq(new BlockPos(this.GoalX,this.GoalY,this.GoalZ))));
				System.out.println("START FROM: " + ppos.toString());
				BlockPos.getAllInBox(
						new BlockPos(p.posX-this.timeoutRange,p.posY-this.timeoutRange,p.posZ-this.timeoutRange),
						new BlockPos(p.posX+this.timeoutRange,p.posY+this.timeoutRange,p.posZ+this.timeoutRange))
				.forEach((pos)->{
					if(
							Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock().getMaterial().equals(net.minecraft.block.material.Material.air)&& // is the block air?
							Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(pos.getX(),pos.getY()+1,pos.getZ())).getBlock().getMaterial().equals(net.minecraft.block.material.Material.air)&& // is there air over it?
							isWalkable(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ()))){ // is the block under it walkable?
								this.available.add(pos); // put the bottom block into the map.
					}
				});
				current = new AStarNode(this.GoalX, this.GoalY, this.GoalZ, null);
				try {
					while(Math.sqrt(current.getBlockPos().distanceSq(ppos))>1) {
						toDo.remove(current);
						finalized.add(current.getBlockPos());
						
						ArrayList<AStarNode> add = new ArrayList<AStarNode>();
						current.getNeighbours(available).forEach((n)->{
							if(!finalized.contains(n.getBlockPos())) {
								add.add(n);
							}
						});
						toDo.addAll(add);
						AStarNode current_lowest = null;
						for(AStarNode node : toDo) {
							if(current_lowest == null || Math.sqrt(ppos.distanceSq(current_lowest.getBlockPos()))>Math.sqrt(ppos.distanceSq(node.getBlockPos()))) {
								current_lowest = node;
							}
						}
						current = current_lowest;
					}
				}catch(Exception e) {
					System.out.println("COULDNT FIND VALID PATH TO GOAL.");
					e.printStackTrace();
				}
				
				
				
			}else {
				System.out.println("COULDNT CALC PATH; TOO NEAR/FAR TO/FROM GOAL.");
			}
			
			
			this.result_node=current;

			toWalk.clear();
			System.out.println("Clearing");
			while(this.result_node!=null) {
				System.out.println("Adding " + this.result_node.getBlockPos().toString());
				toWalk.add(getBlockMitte(this.result_node.getBlockPos()));
				this.result_node = this.result_node.getPrevious();
			}
			callback.start();
			//Start runnable with finalResult
			
		}
		).start();
	}
	//public void walk(Thread callback) {
	//	toWalkCallback = callback;
	//	for(BlockPos p : toWalk) {
	//		
	//		Minecraft.getMinecraft().theWorld.setBlockState(p.add(0, -1, 0), Blocks.diamond_block.getDefaultState());
	//	}
	//}
	
	public static boolean isWalkable(BlockPos pos) {
		if(Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock().isBlockSolid(Minecraft.getMinecraft().theWorld, pos, EnumFacing.UP)){
			return true;
		}
		
		return false;
	}
	
	private static float getStandardGroundSpeed() {
		return 0.11785904894762855F / 2; //0.05
	}
	private static float getStandardAirSpeed() {
		return 0.01F;//0.01
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
//		only fired this when player is in game
		
		if(toWalk.isEmpty()) {
			if(toWalkCallback != null) {
				toWalkCallback.start();
				toWalkCallback = null;
			}
			return;
		}
		
		boolean onGround = event.player.onGround;
		BlockPos pos = toWalk.get(0);
		BlockPos current = event.player.getPosition();
		
		BlockPos sub = pos.subtract(current);
		if(Math.sqrt(pos.distanceSq(current)) > .2) {
			if(Math.abs(sub.getX()) > .2)
				event.player.motionX += (onGround ? getStandardGroundSpeed() : getStandardAirSpeed()) * (sub.getX() > 0 ? 1 : -1);
			if(Math.abs(sub.getZ()) > .2)
				event.player.motionZ += (onGround ? getStandardGroundSpeed() : getStandardAirSpeed()) * (sub.getZ() > 0 ? 1 : -1);
		} else {
			toWalk.remove(0);
		}
	}
	
	private static BlockPos getBlockMitte(BlockPos pos) {
		double x = pos.getX() + (pos.getX() > 0 ?  + .5 : - .5);
		double z = (Math.abs(pos.getZ()) + .5) * (pos.getZ() > 0 ? 1 : -1);
		return new BlockPos(x, pos.getY(), z);
	}
}

	