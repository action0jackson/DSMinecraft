package geometry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ThreadPyramid2 implements Runnable
{
	private Geometry plugin;
	private CommandSender sender;
	private Command cmd;
	private String label;
	private String[] args;	

	public ThreadPyramid2(Geometry plugin, CommandSender sender, Command cmd, String label, String[] args)
	{
		this.plugin = plugin;
		this.sender = sender;
		this.cmd = cmd;
		this.label = label;
		this.args = args;
	}
		
	@Override
	public void run()
	{
		Library.createPyramid(plugin, sender, cmd, label, args, true);
	}
}
