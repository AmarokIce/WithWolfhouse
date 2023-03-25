package club.someoneice.wolfhouse

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.ShapedOreRecipe
import org.apache.logging.log4j.LogManager

@Mod(modid = WolfHouse.MODID)
class WolfHouse {
    companion object {
        lateinit var WarehouseManager: Block
        const val MODID = "wolfhouser"
        val LOGGER = LogManager.getLogger(MODID)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        Blocks.chest
        WarehouseManager = Warehouse()
        GameRegistry.addRecipe(ShapedOreRecipe(WarehouseManager, "WPW", "WCW", "DDD", 'W', "plankWood", 'P', Items.ender_pearl, 'C', Blocks.chest, 'D', Items.diamond))
    }
}

fun Block.asItemStack(): ItemStack {
    return ItemStack(this)
}

fun ItemStack.sameAsItem(stack: ItemStack): Boolean {
    return this.item.equals(stack.item)
}

fun IInventory.isEmpty(): Boolean {
    for (i in 0 until this.sizeInventory) if (this.getStackInSlot(i) != null) return false
    return true
}

fun IInventory.hasEmpty(): Boolean {
    for (i in 0 until this.sizeInventory) if (this.getStackInSlot(i) == null) return true
    return false
}

fun IInventory.getEmptySlot(): Int {
    for (i in 0 until this.sizeInventory) if (this.getStackInSlot(i) == null) return i
    return -1
}