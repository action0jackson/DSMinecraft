package geometry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import geometry.BlockSetType;

public class ThreadCone implements Runnable
{
	private Geometry plugin;
	private CommandSender sender;
	private String[] args;
	private List<Location> setLocations =  new ArrayList<Location >();

	public ThreadCone(Geometry plugin, CommandSender sender, String[] args)
	{
		this.plugin = plugin;
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
			double inverted = 1.0;
			try
			{
				// Try block type as integer
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
				z = Integer.parseInt(args[2]);
				inverted = Math.signum(Integer.parseInt(args[3]));
				height = Math.abs(Integer.parseInt(args[3]));
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

			Location location = new Location(world, x, y, z);
			Block block = location.getBlock();	

			// First iterate height
			for (int h = 1; h <= height; h++)
			{
				double maxRadius = ((double) (radius2 - radius1) / (height - 1)) * (h - 1) + radius1;
				double minAngle = Library.minRequiredAngle(maxRadius);

				// Next iterate th circle
				for (double th = 0; th <= 360; th += minAngle)
				{
					// Next iterate over radius
					for (double r = 0; r <= maxRadius; r+=0.5)
					{
						location.setX(Math.rint(x + r * Math.cos(th * Math.PI / 180)));
						location.setY(inverted * h + y - inverted * 1);
						location.setZ(Math.rint(z + r * Math.sin(th * Math.PI / 180)));

						block = location.getBlock();
						
						// Make sure we haven't already set this block
						if(setLocations.contains(block.getLocation()))
						{
							continue;
						}
						
						// Haven't set, add to list
						setLocations.add(block.getLocation());
						
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
						if (r == Math.rint(maxRadius) || h == y || h == y + height - 1)
						{
							// Check if block is already made of required material
							if(block.getType() == outerMaterial)
							{
								continue;
							}
							
							BlockSetType bst = new BlockSetType(block, outerMaterial);
							this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
						}
						else
						{
							if (innerMaterial != null)
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
				setLocations.clear();
			}

			sender.sendMessage("Cone successfully created!");
		}
	}
}
