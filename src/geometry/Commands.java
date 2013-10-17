package geometry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands
{
	public static boolean encase(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player culprit = null;
		if ((sender instanceof Player))
		{
			culprit = (Player) sender;
		}

		if (args.length == 2 || args.length == 3)
		{
			Player target = (Bukkit.getServer().getPlayer(args[0]));
			Material outerMaterial = Library.getMaterial(args[1]);

			if (outerMaterial == null)
			{
				sender.sendMessage(args[1].toUpperCase() + " is not a valid material or material ID!");
				return true;
			}

			// Check to make sure block is a placeable block
			if (!outerMaterial.isBlock())
			{
				sender.sendMessage(args[1].toUpperCase() + " is not a placeable block!");
				return true;
			}

			// Get Inner Block Type
			Material innerMaterial = null;
			if (args.length == 3)
			{
				innerMaterial = Library.getMaterial(args[2]);

				if (innerMaterial == null)
				{
					sender.sendMessage(args[2].toUpperCase() + " is not a valid material or material ID!");
					return true;
				}

				// Check to make sure block is a placeable block
				if (!innerMaterial.isBlock())
				{
					sender.sendMessage(args[2].toUpperCase() + " is not a placeable block!");
					return true;
				}
			}
			else
			{
				innerMaterial = Material.AIR;
			}

			// Make sure user is online
			if (target == null)
			{
				sender.sendMessage(args[0] + " is not online!");
				return true;
			}
			else
			{
				try
				{
					// encase in block type
					Location loc = target.getLocation();

					// Adjust Starting Block
					loc.setX(loc.getX() - 2);
					loc.setY(loc.getY() - 2);
					loc.setZ(loc.getZ() - 2);

					String[] args2 = new String[8];
					args2[0] = Integer.toString(loc.getBlockX());
					args2[1] = Integer.toString(loc.getBlockY());
					args2[2] = Integer.toString(loc.getBlockZ());
					args2[3] = "5";
					args2[4] = "5";
					args2[5] = "6";
					args2[6] = outerMaterial.name();
					args2[7] = innerMaterial.name();

					cuboid(sender, cmd, label, args2);

					target.sendMessage(ChatColor.BLUE + (culprit == null ? "Server" : culprit.getDisplayName())
							+ ChatColor.WHITE + " has encased you in " + outerMaterial.name() + " and "
							+ innerMaterial.name());
				}
				catch (Exception ex)
				{
					sender.sendMessage("Unable to set block type as " + outerMaterial.name() + "!");
				}

				return true;
			}
		}

		// If this hasn't happened the a value of false will be returned.
		return false;
	}

	public static boolean square(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 5)
		{
			String[] args2 = new String[6];
			args2[0] = args[0];
			args2[1] = args[1];
			args2[2] = args[2];
			args2[3] = args[3];
			args2[4] = args[3]; // Width same as Length
			args2[5] = args[4];

			return rectangle(sender, cmd, label, args2);
		}

		// If this hasn't happened the a value of false will be returned.
		return false;
	}

	public static boolean cube(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 5 || args.length == 6)
		{
			String[] args2 = (args.length == 5 ? new String[7] : new String[8]);
			args2[0] = args[0];
			args2[1] = args[1];
			args2[2] = args[2];
			args2[3] = args[3];
			args2[4] = args[3]; // Width same as Length
			args2[5] = args[3]; // Height same as Length
			args2[6] = args[4];
			if (args.length == 6)
			{
				args2[7] = args[5];
			}

			return cuboid(sender, cmd, label, args2);
		}

		// If this hasn't happened the a value of false will be returned.
		return false;
	}

	public static boolean rectangle(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 6)
		{
			String[] args2 = new String[7];
			args2[0] = args[0];
			args2[1] = args[1];
			args2[2] = args[2];
			args2[3] = args[3];
			args2[4] = args[4];
			args2[5] = "1"; // Height of 1
			args2[6] = args[5];

			return cuboid(sender, cmd, label, args2);
		}

		// If this hasn't happened the a value of false will be returned.
		return false;
	}

	public static boolean cuboid(CommandSender sender, Command cmd, String label, String[] args)
	{
		Thread t = new Thread(new ThreadCuboid(sender, args));
		t.start();
		return true;
	}

	public static boolean pyramid(CommandSender sender, Command cmd, String label, String[] args)
	{
		Thread t = new Thread(new ThreadPyramid(sender, args));
		t.start();
		return true;
	}

	public static boolean circle(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 5)
		{
			String[] args2 = new String[8];
			args2[0] = args[0]; // x
			args2[1] = args[1]; // y
			args2[2] = args[2]; // z
			args2[3] = args[3]; // radius
			args2[4] = args[4]; // outer material
			args2[5] = args[4]; // Use same material for inside of circle
			args2[6] = "360"; // Have maxTh be 360
			args2[7] = "0"; // Have maxPhi be 0

			return sphere(sender, cmd, label, args2);
		}

		// If this hasn't happened the a value of false will be returned.
		return false;
	}

	public static boolean sphere(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length > 4 && args.length < 9)
		{
			String[] args2 = new String[args.length + 2];
			args2[0] = args[0]; // x
			args2[1] = args[1]; // y
			args2[2] = args[2]; // z
			args2[3] = args[3]; // a
			args2[4] = args[3]; // b same as a
			args2[5] = args[3]; // c same as a
			args2[6] = args[4]; // Outer material
			if (args.length == 6)
			{
				args2[7] = args[5];
			}
			if (args.length == 7)
			{
				args2[7] = args[5];
				args2[8] = args[6];
			}
			if (args.length == 8)
			{
				args2[7] = args[5];
				args2[8] = args[6];
				args2[9] = args[7];
			}

			return ellipsoid(sender, cmd, label, args2);
		}

		// If this hasn't happened a value of false will be returned.
		return false;
	}

	public static boolean tunnel(CommandSender sender, Command cmd, String label, String[] args)
	{
		Thread t = new Thread(new ThreadTunnel(sender, args));
		t.start();
		return true;
	}

	public static boolean cone(CommandSender sender, Command cmd, String label, String[] args)
	{
		Thread t = new Thread(new ThreadCone(sender, args));
		t.start();
		return true;
	}

	public static boolean cylinder(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 6 || args.length == 7)
		{
			String[] args2 = new String[args.length + 1];
			args2[0] = args[0]; // x
			args2[1] = args[1]; // y
			args2[2] = args[2]; // z
			args2[3] = args[3]; // height
			args2[4] = args[4]; // radius1
			args2[5] = args[4]; // radius2 = radius1
			args2[6] = args[5]; // Outer Material
			if (args.length == 7)
			{
				args2[7] = args[6]; // Inner Material
			}

			return cone(sender, cmd, label, args2);
		}

		// If this hasn't happened the a value of false will be returned.
		return false;
	}

	public static boolean pyramid2(CommandSender sender, Command cmd, String label, String[] args)
	{
		Thread t = new Thread(new ThreadPyramid2(sender, cmd, label, args));
		t.start();
		return true;
	}

	public static boolean octahedron(CommandSender sender, Command cmd, String label, String[] args)
	{
		Thread t = new Thread(new ThreadOctahedron(sender, cmd, label, args));
		t.start();
		return true;
	}

	public static boolean ellipsoid(CommandSender sender, Command cmd, String label, String[] args)
	{
		Thread t = new Thread(new ThreadEllipsoid(sender, args));
		t.start();
		return true;
	}

	public static boolean ellipse(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 6)
		{
			String[] args2 = new String[10];
			args2[0] = args[0]; // x
			args2[1] = args[1]; // y
			args2[2] = args[2]; // z
			args2[3] = args[3]; // a
			args2[4] = "1"; // b
			args2[5] = args[4]; // c
			args2[6] = args[5]; // outer material
			args2[7] = args[5]; // Use same material for inside of circle
			args2[8] = "360"; // Have maxTh be 360
			args2[9] = "0"; // Have maxPhi be 0

			return ellipsoid(sender, cmd, label, args2);
		}

		// If this hasn't happened the a value of false will be returned.
		return false;
	}

}
