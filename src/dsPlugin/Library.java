package dsPlugin;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Library
{
	public static String concateLines(String[] s, String separator)
	{
		String result = "";
		if (s.length > 0)
		{
			result = s[0]; // start with the first element
			for (int i = 1; i < s.length; i++)
			{
				result = result + separator + s[i];
			}
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public static Material getMaterial(String input)
	{
		int blockType = 0;
		Material material = null;
		try
		{
			// Try block type as integer
			blockType = Integer.parseInt(input);

			material = Material.getMaterial(blockType);
		}
		catch (Exception e)
		{
			// Try block type as string
			material = Material.getMaterial(input.toUpperCase());
		}

		return material;
	}

	public static enum Direction
	{
		N, S, E, W
	}

	public static class Heading
	{
		private int _length;
		private int _y;
		private Direction _direction;

		public Heading()
		{

		}

		public int get_length()
		{
			return _length;
		}

		public void set_length(int _length)
		{
			this._length = _length;
		}

		public int get_y()
		{
			return _y;
		}

		public void set_y(int _y)
		{
			this._y = _y;
		}

		public Direction get_direction()
		{
			return _direction;
		}

		public void set_direction(Direction _direction)
		{
			this._direction = _direction;
		}
	}

	public static class Coordinate
	{
		public Coordinate()
		{
		}

		public Coordinate(int _x, int _y, int _z)
		{
			X = _x;
			Y = _y;
			Z = _z;
		}

		public int X = 0;
		public int Y = 0;
		public int Z = 0;
	}

	public static void createVerticalTriangle(World world, Coordinate startCoord, Material innerMaterial,
			Material outerMaterial, int distance, boolean isInverted, boolean includeBase)
	{
		for (int y = startCoord.Y, xCounter = distance; xCounter >= 0; xCounter--)
		{
			int minX = startCoord.X - xCounter;
			int maxX = startCoord.X + xCounter;

			for (int x = minX; x <= maxX; x++)
			{
				Block block = world.getBlockAt(x, y, startCoord.Z);

				// If last block, set to outerMaterial, otherwise innerMaterial.
				if (x == minX || x == maxX || (y == startCoord.Y && includeBase))
				{
					block.setType(outerMaterial);
				}
				else
				{
					if (innerMaterial != null)
					{
						block.setType(innerMaterial);
					}
				}
			}

			// If inverted, decrement, otherwise increment.
			if (isInverted == true)
			{
				y--;
			}
			else
			{
				y++;
			}
		}
	}

	public static boolean createPyramid(CommandSender sender, Command cmd, String label, String[] args,
			boolean includeBase)
	{
		if (args.length == 5 || args.length == 6)
		{
			// Make sure first 4 parameters can be parsed as a integer
			int x;
			int y;
			int z;
			int radius;
			boolean isInverted = false;

			try
			{
				// Try block type as integer
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
				z = Integer.parseInt(args[2]);
				radius = Math.abs(Integer.parseInt(args[3]));

				if (Integer.parseInt(args[3]) < 0)
				{
					isInverted = true;
				}
			}
			catch (NumberFormatException ex)
			{
				sender.sendMessage("Unable to parse one of the first 4 parameters as integer!");
				return true;
			}

			// Get Outer Block Type
			Material outerMaterial = Library.getMaterial(args[4]);

			if (outerMaterial == null)
			{
				sender.sendMessage(args[4].toUpperCase() + " is not a valid material or material ID!");
				return true;
			}

			// Check to make sure block is a placeable block
			if (!outerMaterial.isBlock())
			{
				sender.sendMessage(args[4].toUpperCase() + " is not a placeable block!");
				return true;
			}

			// Get Inner Block Type
			Material innerMaterial = null;

			if (args.length == 6)
			{
				innerMaterial = Library.getMaterial(args[5]);

				if (innerMaterial == null)
				{
					sender.sendMessage(args[5].toUpperCase() + " is not a valid material or material ID!");
					return true;
				}

				// Check to make sure block is a placeable block
				if (!innerMaterial.isBlock())
				{
					sender.sendMessage(args[5].toUpperCase() + " is not a placeable block!");
					return true;
				}
			}

			// All parameters are good, proceed building pyramid
			World world = sender.getServer().getWorld(sender.getServer().getWorlds().get(0).getName());

			if (sender instanceof Player)
			{
				// Get current world of player
				Player player = (Player) sender;
				world = player.getWorld();
			}

			// Create the center triangle.
			Library.createVerticalTriangle(world, new Library.Coordinate(x, y, z), innerMaterial, outerMaterial,
					radius, isInverted, includeBase);

			// Create consecutively smaller triangles in the +Z and -Z
			// directions.
			for (int r = radius - 1, zCounter = 1; r >= 0; r--, zCounter++)
			{
				createVerticalTriangle(world, new Library.Coordinate(x, y, z + zCounter), innerMaterial, outerMaterial,
						r, isInverted, includeBase);
				createVerticalTriangle(world, new Library.Coordinate(x, y, z - zCounter), innerMaterial, outerMaterial,
						r, isInverted, includeBase);
			}

			sender.sendMessage("Pyramid successfully created!");
			return true;
		}

		// If this hasn't happened, then a value of false will be returned.
		return false;
	}

	public static double minRequiredAngle(double radius)
	{
		try
		{
			return 2 * Math.asin(1 / (2 * radius));
		}
		catch (Exception ex)
		{
			return 1.0;
		}
	}
}
