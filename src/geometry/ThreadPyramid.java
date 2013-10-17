package geometry;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ThreadPyramid implements Runnable
{
	private CommandSender sender;
	private String[] args;	

	public ThreadPyramid(CommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = args;
	}
		
	@SuppressWarnings("deprecation")
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

						// Determine if it is an inner block our outer block
						if (l == (x + h - 1) || l == (x + (baseLength - (h - 1)) - 1) || w == (z + h - 1)
								|| w == (z + (baseLength - (h - 1)) - 1) || h == 1
								|| h == (int) Math.ceil(baseLength / 2.0d))
						{
							if (outerMaterial.name().contains("STAIRS"))
							{
								if (w == (z + h - 1) && inverted == 1) // Orientation1
								{
									block.setType(outerMaterial);
									block.setData((byte) 2, true);
								}
								else if (l == (x + h - 1) && inverted == 1) // Orientation2
								{
									block.setType(outerMaterial);
									block.setData((byte) 0, true);
								}
								else if (w == (z + (baseLength - (h - 1)) - 1) && inverted == 1) // Orientation3
								{
									block.setType(outerMaterial);
									block.setData((byte) 3, true);
								}
								else if (l == (x + (baseLength - (h - 1)) - 1) && inverted == 1) // Orientation4
								{
									block.setType(outerMaterial);
									block.setData((byte) 1, true);
								}
								else if (w == (z + h - 1) && inverted == -1) // Orientation5
								{
									block.setType(outerMaterial);
									block.setData((byte) 6, true);
								}
								else if (l == (x + h - 1) && inverted == -1) // Orientation6
								{
									block.setType(outerMaterial);
									block.setData((byte) 4, true);
								}
								else if (w == (z + (baseLength - (h - 1)) - 1) && inverted == -1) // Orientation7
								{
									block.setType(outerMaterial);
									block.setData((byte) 7, true);
								}
								else if (l == (x + (baseLength - (h - 1)) - 1) && inverted == -1) // Orientation8
								{
									block.setType(outerMaterial);
									block.setData((byte) 5, true);
								}
							}
							else
							{
								block.setType(outerMaterial);
							}
						}
						else
						{
							if (args.length == 6)
							{
								block.setType(innerMaterial);
							}
						}
					}
				}
			}

			sender.sendMessage("Pyramid successfully created!");
		}
	}
}
