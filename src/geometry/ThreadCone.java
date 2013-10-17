package geometry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ThreadCone implements Runnable
{
	private CommandSender sender;
	private String[] args;	

	public ThreadCone(CommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = args;
	}
	
	@Override
	public void run()
	{
		if (args.length == 7 || args.length == 8)
		{
			// Make sure first 4 parameters can be parsed as int
			int x;
			int y;
			int z;
			int height;
			int radius1;
			int radius2;
			try
			{
				// Try block type as integer
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
				z = Integer.parseInt(args[2]);
				height = Integer.parseInt(args[3]);
				radius1 = Math.abs(Integer.parseInt(args[4]));
				radius2 = Math.abs(Integer.parseInt(args[5]));
			}
			catch (NumberFormatException ex)
			{
				sender.sendMessage("Unable to parse one of the parameters as integer!");
				return;
			}

			// Get Outer Block Material
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

			// Get Inner Block Material
			Material innerMaterial = null;
			if (args.length > 7)
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

			Location location = block.getLocation();

			// First iterate height
			for (int h = y; Math.signum(height) == -1.0 ? h >= y + height : h <= y + height; h += Math.rint(Math
					.signum(height)))
			{
				double maxRadius = ((double) (radius2 - radius1) / height) * (h - y) + radius1;
				double minAngle = Library.minRequiredAngle(maxRadius);

				// Next iterate th circle
				for (double th = 0; th <= 360.0; th += minAngle)
				{
					// Next iterate over radius
					for (int r = (int) (innerMaterial != null ? 0 : Math.rint(maxRadius)); r <= Math.rint(maxRadius); r++)
					{
						location.setX(Math.rint(x + r * Math.cos(th * Math.PI / 180)));
						location.setY(h);
						location.setZ(Math.rint(z + r * Math.sin(th * Math.PI / 180)));

						block = world.getBlockAt(location);

						if (r == Math.rint(maxRadius) || h == y || h == y + height)
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

			sender.sendMessage("Cone successfully created!");
		}
	}
}
