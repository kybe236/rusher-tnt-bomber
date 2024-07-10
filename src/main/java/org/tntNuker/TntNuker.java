package org.tntNuker;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
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
		BlockPos blockPosBelowPlayer = new BlockPos((int) Math.floor(x), (int) Math.floor(y) - 1, (int) Math.floor(z));

		// get player rotation
		float yaw = mc.player.getYRot() % 360;
		if (yaw > 180) {
			yaw -= 360;
		} else if (yaw < -180) {
			yaw += 360;
		}

		BlockPos blockPosShortBeforeTouch = getBlockPlayerIsApproaching(0.15);

		if (redstone) {
			if (mc.level.getBlockState(blockPosBelowPlayer).getBlock() == Blocks.AIR) {
				placeBlock(blockPosBelowPlayer, Blocks.REDSTONE_BLOCK, yaw);
			}
			if (mc.level.getBlockState(blockPosShortBeforeTouch).getBlock() == Blocks.AIR) {
				placeBlock(blockPosShortBeforeTouch, Blocks.REDSTONE_BLOCK, yaw);
			}
			if (isDiagonalYawLeft(yaw)) {
				placeSupportBlockDiagonal(yaw, Direction.LEFT);
			}
			if (isDiagonalYawRight(yaw)) {
				placeSupportBlockDiagonal(yaw, Direction.RIGHT);
			}
		}
		return;
	}

	public static void placeBlock(BlockPos blockPos, Block block, Float yaw) {
		//find item in hotbar
		if (mc.level == null || mc.player == null) {
			return;
		}

		// check if the block is air
		if (mc.level.getBlockState(blockPos).getBlock() != Blocks.AIR) {
			return;
		}

		// get the item from the block
		Item item = Item.BY_BLOCK.getOrDefault(block, Items.AIR);
		if (item == Items.AIR) {
			return;
		}

		// finds the slot where the item is
		int slot = mc.player.getInventory().findSlotMatchingItem(block.asItem().getDefaultInstance());

		// if the item is not in the hotbar, return
		if (slot < 0 || slot > 8) {
			return;
		}

		// select the slot
		mc.player.getInventory().selected = slot;

		// place the block and return
		BlockHitResult blockHitResult = RusherHackAPI.interactions().getBlockPlaceHitResult(blockPos, false, false, 5.0);
		if (blockHitResult != null) {
			RusherHackAPI.getRotationManager().updateRotation(blockPos);
			if (RusherHackAPI.interactions().useBlock(blockHitResult, InteractionHand.MAIN_HAND, false)) {
				ChatUtils.print("[DEBUG] [TNTNUKER] Block placed");
			}
			else {
				ChatUtils.print("[DEBUG] [TNTNUKER] Block not placed");
			}
		}
	}

	private static boolean isDiagonalYawLeft(float yaw) {
		// Check if yaw is in the range for diagonal placement to the left (135° and 225°)
		return (yaw > 135 && yaw <= 225);
	}

	private static boolean isDiagonalYawRight(float yaw) {
		// Check if yaw is in the range for diagonal placement to the right (45° and 315°)
		return (yaw > 45 && yaw <= 135) || (yaw > 315 && yaw <= 360);
	}

	public static BlockPos getBlockPlayerIsApproaching(double fractionalThreshold) {
		if (mc.player == null || mc.level == null) {
			return null;
		}

		// Get player's precise position
		double playerX = mc.player.getX();
		double playerY = mc.player.getY();
		double playerZ = mc.player.getZ();

		// Calculate fractional part of player's position
		double fracX = playerX - Math.floor(playerX);
		double fracZ = playerZ - Math.floor(playerZ);

		// Get player's yaw
		float yaw = mc.player.getYRot() % 360;
		if (yaw > 180) {
			yaw -= 360;
		} else if (yaw < -180) {
			yaw += 360;
		}

		BlockPos blockPosApproaching = null;
		if (fracX > 1 - fractionalThreshold && fracZ >= fractionalThreshold && fracZ <= 1 - fractionalThreshold) {
			// Approaching the block to the east
			blockPosApproaching = new BlockPos((int) Math.floor(playerX) + 1, (int) Math.floor(playerY) - 1, (int) Math.floor(playerZ));
		} else if (fracX < fractionalThreshold && fracZ >= fractionalThreshold && fracZ <= 1 - fractionalThreshold) {
			// Approaching the block to the west
			blockPosApproaching = new BlockPos((int) Math.floor(playerX) - 1, (int) Math.floor(playerY) - 1, (int) Math.floor(playerZ));
		} else if (fracZ > 1 - fractionalThreshold && fracX >= fractionalThreshold && fracX <= 1 - fractionalThreshold) {
			// Approaching the block to the south
			blockPosApproaching = new BlockPos((int) Math.floor(playerX), (int) Math.floor(playerY) - 1, (int) Math.floor(playerZ) + 1);
		} else if (fracZ < fractionalThreshold && fracX >= fractionalThreshold && fracX <= 1 - fractionalThreshold) {
			// Approaching the block to the north
			blockPosApproaching = new BlockPos((int) Math.floor(playerX), (int) Math.floor(playerY) - 1, (int) Math.floor(playerZ) - 1);
		} else {
			// The player is not close enough to any block to be considered "approaching" it
			blockPosApproaching = new BlockPos((int) Math.floor(playerX), (int) Math.floor(playerY) - 1, (int) Math.floor(playerZ));
		}

		System.out.println("[DEBUG] [TNTNUKER] Player approaching block at " + blockPosApproaching);
		return blockPosApproaching;
	}

	private static boolean isApproximatelyEqual(float a, float b, float tolerance) {
		return Math.abs(a - b) <= tolerance;
	}

	private static void placeSupportBlockDiagonal(float yaw, Direction direction) {
		if (mc.player == null || mc.level == null) {
			return;
		}

		// Get player's precise position
		double playerX = mc.player.getX();
		double playerY = mc.player.getY();
		double playerZ = mc.player.getZ();

		BlockPos blockPos = null;

		switch (direction) {
			case LEFT:
				blockPos = getDiagonalBlockPosition(yaw, Direction.LEFT);
				break;
			case RIGHT:
				blockPos = getDiagonalBlockPosition(yaw, Direction.RIGHT);
				break;
		}
		}
	}

	private static BlockPos getDiagonalBlockPosition(float yaw, Direction direction) {
		if (mc.player == null || mc.level == null) {
			return null;
		}

		// Get player's precise position
		double playerX = mc.player.getX();
		double playerY = mc.player.getY();
		double playerZ = mc.player.getZ();

		BlockPos blockPos = null;

		// Calculate fractional part of player's position
		double fracX = playerX - Math.floor(playerX);
		double fracZ = playerZ - Math.floor(playerZ);

		// Determine block position based on yaw angle and direction
		if (direction == Direction.LEFT) {
			if (yaw > 135 && yaw <= 225) {
				// Approaching the block to the northwest
				if (fracX < 0.15 && fracZ < 0.15) {
					blockPos = new BlockPos((int) Math.floor(playerX) - 1, (int) Math.floor(playerY) - 1, (int) Math.floor(playerZ) - 1);
				}
			}
		} else if (direction == Direction.RIGHT) {
			if ((yaw > 45 && yaw <= 135) || (yaw > 315 && yaw <= 360)) {
				// Approaching the block to the northeast
				if (fracX > 0.85 && fracZ < 0.15) {
					blockPos = new BlockPos((int) Math.floor(playerX) + 1, (int) Math.floor(playerY) - 1, (int) Math.floor(playerZ) - 1);
				}
			}
		}

		return blockPos;
	}

	private enum Direction {
		LEFT,
		RIGHT
	}
}