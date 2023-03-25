package club.someoneice.wolfhouse

import alexsocol.asjlib.get
import alexsocol.asjlib.set
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World

class Warehouse: Block(Material.wood) {
    init {
        this.setCreativeTab(CreativeTabs.tabDecorations)
        this.setBlockName("warehouse_manager_block")
        GameRegistry.registerBlock(this, "warehouse_manager_block")
    }

    override fun getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): ArrayList<ItemStack> {
        val list = ArrayList<ItemStack>()
        list.add(this.asItemStack())
        return list
    }

    override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, size: Int, fx: Float, fy: Float, fz: Float): Boolean {
        if (world.isRemote) return true
        if (world.getTileEntity(x, y + 1, z) !is IInventory) return false

        val baseInv = world.getTileEntity(x, y + 1, z) as IInventory
        val heldItem = player.heldItem
        if (heldItem == null) {
            if (baseInv.isEmpty()) return false

            for (i in 0..3) for (o in -8..8) for (p in -8..8) {
                if (x + o == x && y + i == y + 1 && z + p == z) continue
                val tile = world.getTileEntity(x + o, y + i, z + p)
                if (tile is IInventory) {
                    val inv = tile as IInventory
                    for (slotBase in 0 until baseInv.sizeInventory) {
                        baseInv[slotBase] ?: continue
                        for (slot in 0 until inv.sizeInventory) {
                            if (inv[slot] == null) {
                                inv[slot] = baseInv[slotBase]!!.copy()
                                baseInv[slotBase] = null
                                inv.markDirty()
                                break
                            } else if (inv[slot]!!.sameAsItem(baseInv[slotBase]!!) && inv[slot]!!.stackSize < 64) {
                                val size = baseInv[slotBase]!!.copy().stackSize
                                if (inv[slot]!!.stackSize + size <= 64) {
                                    inv[slot]!!.stackSize += size
                                    baseInv[slotBase] = null
                                    inv.markDirty()
                                    break
                                } else {
                                    baseInv[slotBase]!!.stackSize = inv[slot]!!.stackSize - size
                                    inv[slot]!!.stackSize = 64
                                    inv.markDirty()
                                    continue
                                }
                            }
                        }
                    }

                    inv.markDirty()
                }
            }
            return true
        } else {
            if (!baseInv.hasEmpty()) return false

            if (player.isSneaking) {
                for (i in 0..3) for (o in -8..8) for (p in -8..8) {
                    val tile = world.getTileEntity(x + o, y + i, z + p)
                    if (tile is IInventory) {
                        val inv = tile as IInventory
                        for (slot in 0 until inv.sizeInventory) {
                            inv[slot] ?: continue
                            if (inv[slot]!!.sameAsItem(heldItem)) {
                                baseInv[baseInv.getEmptySlot()] = inv[slot]
                                inv[slot] = null
                                inv.markDirty()
                                return true
                            }
                        }
                    }
                }
            } else {
                for (i in 0..3) for (o in -8..8) for (p in -8..8) {
                    val tile = world.getTileEntity(x + o, y + i, z + p)
                    if (tile is IInventory) {
                        val inv = tile as IInventory
                        for (slot in 0 until inv.sizeInventory) {
                            val slotEmpty = baseInv.getEmptySlot()
                            if (slotEmpty == -1) return true
                            inv[slot] ?: continue
                            if (inv[slot]!!.sameAsItem(heldItem)) {
                                baseInv[slotEmpty] = inv[slot]
                                inv[slot] = null
                                inv.markDirty()
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    private lateinit var top: IIcon
    private lateinit var sides: IIcon

    @SideOnly(Side.CLIENT)
    override fun registerBlockIcons(iconRegister: IIconRegister) {
        top = iconRegister.registerIcon(WolfHouse.MODID + ":manager_top")
        sides = iconRegister.registerIcon(WolfHouse.MODID + ":manager_side")
    }

    @SideOnly(Side.CLIENT)
    override fun getIcon(side: Int, metadata: Int): IIcon {
        return if (side == 1) top else sides
    }
}