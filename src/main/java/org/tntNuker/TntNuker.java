package org.tntNuker;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;
import org.rusherhack.client.api.utils.ChatUtils;
import org.tntNuker.modules.TntNukerModule;

/**
 * TNT NUKER
 *
 * @author kybe236
 */
public class TntNuker extends Plugin {
	static Minecraft mc = Minecraft.getInstance();

	@Override
	public void onLoad() {
		this.getLogger().info("[TNTNUKER] Started!");

		final TntNukerModule tntNukerModule = new TntNukerModule();
		RusherHackAPI.getModuleManager().registerFeature(tntNukerModule);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("[TNTNUKER] Stopped!");
	}

	public static void onTick(boolean left, boolean right, boolean below, boolean redstone, boolean front, boolean back) {
		// get the player's position
		double x = mc.player.getX();
		double y = mc.player.getY();
		double z = mc.player.getZ();
		// get the block below the player
		BlockPos blockPosBelowPlayer = new BlockPos((int) Math.floor(x),(int) Math.floor(y)-1, (int) Math.floor(z));
		//get player rotation
		float yaw = mc.player.getYRot() % 360;
		if (yaw > 180) {
			yaw -= 360;
		} else if (yaw < -180) {
			yaw += 360;
		}

// Determine the block positions to the left and right of the player based on the yaw
		BlockPos blockPosLeftOfPlayer = blockPosBelowPlayer;
		BlockPos blockPosRightOfPlayer = blockPosBelowPlayer;
		BlockPos blockPosFrontOfPlayer = blockPosBelowPlayer;
		BlockPos blockPosBackOfPlayer = blockPosBelowPlayer;
		BlockPos blockBellowRedstone = blockPosBelowPlayer.offset(0, -1, 0);

		if (yaw > -45 && yaw <= 45) {
			// Facing positive X
			blockPosRightOfPlayer = blockPosBelowPlayer.offset(-1, 0, 0);
			blockPosLeftOfPlayer = blockPosBelowPlayer.offset(1, 0, 0);
			blockPosFrontOfPlayer = blockPosBelowPlayer.offset(0, 0, 1);
			blockPosBackOfPlayer = blockPosBelowPlayer.offset(0, 0, -1);
		} else if (yaw > 45 && yaw <= 135) {
			// Facing positive Z
			blockPosRightOfPlayer = blockPosBelowPlayer.offset(0, 0, -1);
			blockPosLeftOfPlayer = blockPosBelowPlayer.offset(0, 0, 1);
			blockPosFrontOfPlayer = blockPosBelowPlayer.offset(-1, 0, 0);
			blockPosBackOfPlayer = blockPosBelowPlayer.offset(1, 0, 0);
		} else if (yaw > 135 || yaw <= -135) {
			// Facing negative X
			blockPosRightOfPlayer = blockPosBelowPlayer.offset(1, 0, 0);
			blockPosLeftOfPlayer = blockPosBelowPlayer.offset(-1, 0, 0);
			blockPosFrontOfPlayer = blockPosBelowPlayer.offset(0, 0, -1);
			blockPosBackOfPlayer = blockPosBelowPlayer.offset(0, 0, 1);
		} else if (yaw > -135 && yaw <= -45) {
			// Facing negative Z
			blockPosRightOfPlayer = blockPosBelowPlayer.offset(0, 0, 1);
			blockPosLeftOfPlayer = blockPosBelowPlayer.offset(0, 0, -1);
			blockPosFrontOfPlayer = blockPosBelowPlayer.offset(1, 0, 0);
			blockPosBackOfPlayer = blockPosBelowPlayer.offset(-1, 0, 0);
		}


		// see if you can place an block below the player
		if (redstone) placeBlock(blockPosBelowPlayer, Blocks.REDSTONE_BLOCK);
		if (below) placeBlock(blockBellowRedstone, Blocks.TNT);
		if (right) placeBlock(blockPosRightOfPlayer, Blocks.TNT);
		if (left) placeBlock(blockPosLeftOfPlayer, Blocks.TNT);
		if (front) placeBlock(blockPosFrontOfPlayer, Blocks.TNT);
		if (back) placeBlock(blockPosBackOfPlayer, Blocks.TNT);
	}
	public static void placeBlock(BlockPos blockPos, Block block) {
		// Check if the block position below the player is air
		if (mc.level.getBlockState(blockPos).isAir()) {
			// Find the block in the player's inventory
			Item item = Item.BY_BLOCK.getOrDefault(block, Items.AIR);
			if (item != Items.AIR) {
				// Get the slot containing this item in the player's inventory
				int slot = mc.player.getInventory().findSlotMatchingItem(item.asItem().getDefaultInstance());
				if (slot != -1) {
					mc.player.getInventory().selected = slot;

					// Create a hit result for the block placement
					BlockHitResult hitResult = new BlockHitResult(
							new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), // Position
							mc.player.getDirection(), // Facing direction
							blockPos, // Block position
							false // Inside block
					);

					// Use the item (right-click) at the specified position
					mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hitResult);
				}
			}
		}
	}

	
}