package geometry;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.*;

public final class Geometry extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		getLogger().info("Geometry Succesfully Loaded!");
	}

	@Override
	public void onDisable()
	{
		getLogger().info("Geometry Succesfully Unloaded!");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("encase"))
		{
			return Commands.encase(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("cuboid"))
		{
			return Commands.cuboid(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("pyramid"))
		{
			return Commands.pyramid(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("sphere"))
		{
			return Commands.sphere(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("tunnel"))
		{
			return Commands.tunnel(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("cone"))
		{
			return Commands.cone(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("cylinder"))
		{
			return Commands.cylinder(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("pyramid2"))
		{
			return Commands.pyramid2(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("square"))
		{
			return Commands.square(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("rectangle"))
		{
			return Commands.rectangle(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("cube"))
		{
			return Commands.cube(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("circle"))
		{
			return Commands.circle(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("octahedron"))
		{
			return Commands.octahedron(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("ellipsoid"))
		{
			return Commands.ellipsoid(this, sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("ellipse"))
		{
			return Commands.ellipse(this, sender, cmd, label, args);
		}

		// If this hasn't happened, then a value of false will be returned.
		return false;
	}
}
