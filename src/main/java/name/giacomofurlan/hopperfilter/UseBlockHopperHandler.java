package name.giacomofurlan.hopperfilter;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class UseBlockHopperHandler {
    public static void registerHandlers() {
        HopperFilter.LOGGER.info("Registering use block handler");
        UseBlockCallback.EVENT.register(UseBlockHopperHandler::handleUseBlockHopper);
    }

    public static ActionResult handleUseBlockHopper(PlayerEntity player, World world, Hand hand, HitResult hitResult) {
        if (hand != Hand.MAIN_HAND || !player.getStackInHand(hand).isOf(Items.STICK)) {
            return ActionResult.PASS;
        }

        Vec3d vec3d;
        BlockPos pos;

        if (hitResult instanceof BlockHitResult) {
            pos = ((BlockHitResult) hitResult).getBlockPos();
         } else {
            vec3d = hitResult.getPos();
            pos = BlockPos.ofFloored(vec3d);
         }

         BlockEntity block = world.getBlockEntity(pos);

        if (block instanceof HopperBlockEntity) {
            openFilterGui(player, (HopperBlockEntity) block);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public static void openFilterGui(PlayerEntity player, HopperBlockEntity hopper) {
        player.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.literal("Hopper Filter");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new HopperFilterScreenHandler(syncId, inv, hopper);
            }
        });
    }
}
