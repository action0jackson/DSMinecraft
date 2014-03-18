package geometry;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import geometry.BlockSetType;
import geometry.BlockSetStairFace;

public class ThreadPyramid implements Runnable
{
	private Geometry plugin;
	private CommandSender sender;
	private String[] args;

	public ThreadPyramid(Geometry plugin, CommandSender sender, String[] args)
	{
		this.plugin = plugin;
		this.sender = sender;
		this.args = args;
	}
		
	@Override	
	public void run()
	{
		if (args.length == 6 || args.length == 5)
		{
			// Make sure first 4 parameters can be parsed as a integer
			int x;
			int y;
			int z;
			int baseLength;
			int inverted = 1;
			try
			{
				// Try block type as integer
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
				z = Integer.parseInt(args[2]);
				inverted = (int) Math.signum(Integer.parseInt(args[3]));
				baseLength = Math.abs(Integer.parseInt(args[3]));
			}
			catch (NumberFormatException ex)
			{
				sender.sendMessage("Unable to parse one of the first 4 parameters as integer!");
				return;
			}

			// Get Outer Block Type
			Material outerMaterial = Library.getMaterial(args[4]);

			if (outerMaterial == null)
			{
				sender.sendMessage(args[4].toUpperCase() + " is not a valid material or material ID!");
				return;
			}

			// Check to make sure block is a placeable block
			if (!outerMaterial.isBlock())
			{
				sender.sendMessage(args[4].toUpperCase() + " is not a placeable block!");
				return;
			}

			// Get Inner Block Type
			Material innerMaterial = null;
			if (args.length == 6)
			{
				innerMaterial = Library.getMaterial(args[5]);

				if (innerMaterial == null)
				{
					sender.sendMessage(args[5].toUpperCase() + " is not a valid material or material ID!");
					return;
				}

				// Check to make sure block is a placeable block
				if (!innerMaterial.isBlock())
				{
					sender.sendMessage(args[5].toUpperCase() + " is not a placeable block!");
					return;
				}
			}

			// All parameters are good Proceed building box
			World world = sender.getServer().getWorld(sender.getServer().getWorlds().get(0).getName());
			if (sender instanceof Player)
			{
				// Get current world of player
				Player player = (Player) sender;
				world = player.getWorld();
			}

			Block block = world.getBlockAt(x, y, z);

			// First iterate over height
			for (int h = 1; h <= (int) Math.ceil((baseLength / 2.0d)); h++)
			{
				// iterate over length
				for (int l = (x + h - 1); l < (x + (baseLength - (h - 1))); l++)
				{
					// iterate over width
					for (int w = (z + h - 1); w < (z + (baseLength - (h - 1))); w++)
					{
						block = world.getBlockAt(l, inverted * h + y - inverted * 1, w);
						
						// If we don't sleep here we get a read timeout
						try
						{
							Thread.sleep(0, 10);
						} 
						catch (InterruptedException ex) 
						{
							Thread.currentThread().interrupt();
						}

						// Determine if it is an inner block our outer block
						if (l == (x + h - 1) || l == (x + (baseLength - (h - 1)) - 1) || w == (z + h - 1)
								|| w == (z + (baseLength - (h - 1)) - 1) || h == 1
								|| h == (int) Math.ceil(baseLength / 2.0d))
						{
							// Check if block is already made of required material
							if(block.getType() == outerMaterial)
							{
								continue;
							}
							
							if (outerMaterial.name().contains("STAIRS"))
							{
								if (w == (z + h - 1) && inverted == 1) // Orientation1
								{
									BlockSetStairFace bssf = new BlockSetStairFace(block, outerMaterial, (byte) 2);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bssf);
								}
								else if (l == (x + h - 1) && inverted == 1) // Orientation2
								{
									BlockSetStairFace bssf = new BlockSetStairFace(block, outerMaterial, (byte) 0);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bssf);
								}
								else if (w == (z + (baseLength - (h - 1)) - 1) && inverted == 1) // Orientation3
								{
									BlockSetStairFace bssf = new BlockSetStairFace(block, outerMaterial, (byte) 3);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bssf);
								}
								else if (l == (x + (baseLength - (h - 1)) - 1) && inverted == 1) // Orientation4
								{
									BlockSetStairFace bssf = new BlockSetStairFace(block, outerMaterial, (byte) 1);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bssf);
								}
								else if (w == (z + h - 1) && inverted == -1) // Orientation5
								{
									BlockSetStairFace bssf = new BlockSetStairFace(block, outerMaterial, (byte) 6);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bssf);
								}
								else if (l == (x + h - 1) && inverted == -1) // Orientation6
								{
									BlockSetStairFace bssf = new BlockSetStairFace(block, outerMaterial, (byte) 4);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bssf);
								}
								else if (w == (z + (baseLength - (h - 1)) - 1) && inverted == -1) // Orientation7
								{
									BlockSetStairFace bssf = new BlockSetStairFace(block, outerMaterial, (byte) 7);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bssf);
								}
								else if (l == (x + (baseLength - (h - 1)) - 1) && inverted == -1) // Orientation8
								{
									BlockSetStairFace bssf = new BlockSetStairFace(block, outerMaterial, (byte) 5);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bssf);
								}
							}
							else
							{							
								BlockSetType bst = new BlockSetType(block, outerMaterial);
								this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
							}
						}
						else
						{
							if (args.length == 6 && innerMaterial != null)
							{
								// Check if block is already made of required material
								if(block.getType() == innerMaterial)
								{
									continue;
								}
								
								BlockSetType bst = new BlockSetType(block, innerMaterial);
								this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
							}
						}
					}
				}
			}

			sender.sendMessage("Pyramid successfully created!");
		}
	}
}
