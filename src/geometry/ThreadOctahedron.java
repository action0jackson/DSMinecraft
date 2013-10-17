package geometry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ThreadOctahedron implements Runnable
{
	private CommandSender sender;
	private Command cmd;
	private String label;
	private String[] args;	

	public ThreadOctahedron(CommandSender sender, Command cmd, String label, String[] args)
	{
		this.sender = sender;
		this.cmd = cmd;
		this.label = label;
		this.args = args;
	}
		
	@Override
	public void run()
	{
		if (args.length < 5)
		{
			return;
		}

		Library.createPyramid(sender, cmd, label, args, false);

		// Flip the sign on the radius to draw the other half of the pyramid.
		args[3] = Integer.toString(Integer.parseInt(args[3]) * -1);
		Library.createPyramid(sender, cmd, label, args, false);
	}
}
