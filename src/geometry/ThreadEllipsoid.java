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

public class ThreadEllipsoid implements Runnable
{
	private Geometry plugin;
	private CommandSender sender;
	private String[] args;
	private List<Location> setLocations =  new ArrayList<Location >();

	public ThreadEllipsoid(Geometry plugin, CommandSender sender, String[] args)
	{
		this.plugin = plugin;
		this.sender = sender;
		this.args = args;
	}
	
	@Override
	public void run()
	{
		if (args.length > 6 && args.length < 11)
		{
			// Make sure first 6 parameters can be parsed as a int
			int x;
			int y;
			int z;
			int a;
			int b;
			int c;
			int maxTh = 360;
			int maxPhi = 360;
			try
			{
				// Try block type as integer
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
				z = Integer.parseInt(args[2]);
				a = Integer.parseInt(args[3]);
				b = Integer.parseInt(args[4]);
				c = Integer.parseInt(args[5]);
				if (args.length > 8)
					maxTh = Math.abs(Integer.parseInt(args[8]));
				if (args.length > 9)
					maxPhi = Math.abs(Integer.parseInt(args[9]));
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

			int maxChord = Math.max(Math.max(a, b), c);
			double aChordDivision = 1.0 * a / maxChord;
			double bChordDivision = 1.0 * b / maxChord;
			double cChordDivision = 1.0 * c / maxChord;

			// Figure minAngle for worst case scenario (take greatest chord length)
			double minAngle = Library.minRequiredAngle(maxChord);

			// First iterate th circle
			for (double th = 0; th <= maxTh; th += minAngle)
			{
				// Next iterate phi circle
				for (double phi = 0; phi <= maxPhi; phi += minAngle)
				{
					for (double chord = (innerMaterial != null ? 0 : maxChord); chord <= maxChord; chord+=0.5)
					{	
						location.setX(Math.rint(x + (aChordDivision * chord) * Math.cos(th * Math.PI / 180)
								* Math.cos(phi * Math.PI / 180)));

						location.setY(Math.rint(y + (bChordDivision * chord) * Math.sin(phi * Math.PI / 180)));

						location.setZ(Math.rint(z + (cChordDivision * chord) * Math.sin(th * Math.PI / 180)
								* Math.cos(phi * Math.PI / 180)));

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
						if ((int)Math.rint(aChordDivision * chord) == a || (int)Math.rint(bChordDivision * chord) == b
								|| (int)Math.rint(cChordDivision * chord) == c)
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
				
				// Clear after each th iteration -- otherwise lookup in list is too time consuming
				setLocations.clear();
			}

			sender.sendMessage("Ellipsoid successfully created!");
		}
	}	
}