package me.DSMinecraftServer.x64.craigj.DSPlugin;

import org.bukkit.Material;

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
}
