package knightminer.tcomplement.common;

import javax.annotation.Nonnull;

import knightminer.tcomplement.library.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.tconstruct.shared.client.BakedTableModel;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit() {
		super.preInit();

		MinecraftForge.EVENT_BUS.register(this);
	}

	protected void registerItemModelDynamic(ItemMetaDynamic item) {
		if(item != null) {
			item.registerItemModels();
		}
	}

	public void registerFluidModels(Fluid fluid) {
		if(fluid == null) {
			return;
		}

		Block block = fluid.getBlock();
		if(block != null) {
			Item item = Item.getItemFromBlock(block);
			FluidStateMapper mapper = new FluidStateMapper(fluid);

			// item-model
			if(item != Items.AIR) {
				ModelLoader.registerItemVariants(item);
				ModelLoader.setCustomMeshDefinition(item, mapper);
			}
			// block-model
			ModelLoader.setCustomStateMapper(block, mapper);
		}
	}

	protected static void setModelStateMapper(Block block, IStateMapper mapper) {
		if(block != null) {
			ModelLoader.setCustomStateMapper(block, mapper);
		}
	}

	protected static void registerItemColors(ItemColors colors, IItemColor handler, Item... items) {
		for (Item item : items) {
			if (item != null) {
				colors.registerItemColorHandler(handler, item);
			}
		}
	}

	protected static void registerItemColors(ItemColors colors, IItemColor handler, Block... blocks) {
		for (Block block : blocks) {
			if (block != null) {
				colors.registerItemColorHandler(handler, block);
			}
		}
	}

	protected static void wrapTableModel(ModelBakeEvent event, ModelResourceLocation loc) {
		IBakedModel model = event.getModelRegistry().getObject(loc);
		if(model != null) {
			event.getModelRegistry().putObject(loc, new BakedTableModel(model, null, DefaultVertexFormats.BLOCK));
		}
	}

	private static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {
		public final ModelResourceLocation location;
		public FluidStateMapper(Fluid fluid) {
			// have each block hold its fluid per nbt? hm
			this.location = new ModelResourceLocation(Util.getResource("fluid_block"), fluid.getName());
		}

		@Nonnull
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
			return location;
		}

		@Nonnull
		@Override
		public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
			return location;
		}
	}
}
