package geometry;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ThreadCuboid implements Runnable
{
	private CommandSender sender;
	private String[] args;	

	public ThreadCuboid(CommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = args;
	}
		
	@Override
	public void run()
	{
		if (args.length == 8 || args.length == 7)
		{
			// Make sure first 6 parameters can be parsed as a integer
			int x;
			int y;
			int z;
			int xLength;
			int zLength;
			int yLength;
			try
			{
				// Try block type as integer
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
				z = Integer.parseInt(args[2]);
				xLength = Integer.parseInt(args[3]);
				zLength = Integer.parseInt(args[4]);
				yLength = Integer.parseInt(args[5]);
			}
			catch (NumberFormatException ex)
			{
				sender.sendMessage("Unable to parse one of the first 6 parameters as integer!");
				return;
			}

			// Get Outer Block Type
			Material outerMaterial = Library.getMaterial(args[6]);

			if (outerMaterial == null)
			{
				sender.sendMessage(args[6].toUpperCase() + " is not a valid material or material ID!");
				return;
			}

			// Check to make sure block is a placeable block
			if (!outerMaterial.isBlock())
			{
				sender.sendMessage(args[6].toUpperCase() + " is not a placeable block!");
				return;
			}

			// Get Inner Block Type
			Material innerMaterial = null;
			if (args.length == 8)
			{
				innerMaterial = Library.getMaterial(args[7]);

				if (innerMaterial == null)
				{
					sender.sendMessage(args[7].toUpperCase() + " is not a valid material or material ID!");
					return;
				}

				// Check to make sure block is a placeable block
				if (!innerMaterial.isBlock())
				{
					sender.sendMessage(args[7].toUpperCase() + " is not a placeable block!");
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

			for (int l = x; Math.signum(xLength) == -1.0 ? l > (x + xLength) : l < (x + xLength); l += Math.rint(Math
					.signum(xLength)))
			{
				for (int w = z; Math.signum(zLength) == -1.0 ? w > (z + zLength) : w < (z + zLength); w += Math
						.rint(Math.signum(zLength)))
				{
					for (int h = y; Math.signum(yLength) == -1.0 ? h > (y + yLength) : h < (y + yLength); h += Math
							.rint(Math.signum(yLength)))
					{
						block = world.getBlockAt(l, h, w);

						// Determine if it is an inner block our outer block
						if (l == x || l == (x + xLength - Math.rint(Math.signum(xLength))) || w == z
								|| w == (z + zLength - Math.rint(Math.signum(zLength))) || h == y
								|| h == (y + yLength - Math.rint(Math.signum(yLength))))
						{
							block.setType(outerMaterial);
						}
						else
						{
							if (innerMaterial != null)
								block.setType(innerMaterial);
						}
					}
				}
			}
			sender.sendMessage("Cuboid successfully created!");
		}
	}
}
