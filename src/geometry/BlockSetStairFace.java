package geometry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockSetStairFace extends BukkitRunnable
{
	private Block block;
	private Material material;
	private byte data;
	
	public BlockSetStairFace(Block block, Material material, byte data)
	{
		this.block = block;
		this.material = material;
		this.data = data;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run()
	{
		this.block.setType(this.material);
		this.block.setData(this.data, true);
	}
}
