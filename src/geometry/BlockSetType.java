package geometry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockSetType extends BukkitRunnable
{
	private Block block;
	private Material material;
	
	public BlockSetType(Block block, Material material)
	{
		this.block = block;
		this.material = material;
	}
	
	@Override
	public void run()
	{
		this.block.setType(this.material);
	}
}