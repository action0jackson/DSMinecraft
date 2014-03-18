package geometry;

import geometry.Library.Direction;
import geometry.Library.Heading;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ThreadTunnel implements Runnable
{
	private Geometry plugin;
	private CommandSender sender;
	private String[] args;	

	public ThreadTunnel(Geometry plugin, CommandSender sender, String[] args)
	{
		this.plugin = plugin;
		this.sender = sender;
		this.args = args;
	}
		
	@Override
	public void run()
	{
		if (args.length > 3)
		{
			// Make sure first 3 parameters can be parsed as a integer
			int x;
			int y;
			int z;
			try
			{
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
				z = Integer.parseInt(args[2]);
			}
			catch (NumberFormatException ex)
			{
				sender.sendMessage("Unable to parse one of the first 3 parameters as integer!");
				return;
			}

			// Make sure remaining parameters are in correct format
			List<Library.Heading> headings = new ArrayList<Library.Heading>();
			for (int i = 3; i < args.length; i++)
			{
				String[] vals = args[i].split("-");
				if (vals.length != 3)
					return;

				try
				{
					Heading heading = new Heading();
					heading.set_y(Integer.parseInt(vals[0]));
					heading.set_length(Math.abs(Integer.parseInt(vals[1])));
					heading.set_direction(Direction.valueOf(vals[2]));

					headings.add(heading);
				}
				catch (Exception ex)
				{
					return;
				}
			}

			// +X ==>EAST
			// -X ==>WEST
			// +Z ==>SOUTH
			// -Z ==>NORTH

			// All parameters are good Proceed building box
			World world = sender.getServer().getWorld(sender.getServer().getWorlds().get(0).getName());
			if (sender instanceof Player)
			{
				// Get current world of player
				Player player = (Player) sender;
				world = player.getWorld();
			}

			Block block = world.getBlockAt(x, y, z);

			// Loop through headings
			for (int heading = 0; heading < headings.size(); heading++)
			{
				// Make sure length is greater or equal to difference in height
				if (headings.get(heading).get_length() - 1 < Math.abs(headings.get(heading).get_y() - y))
				{
					sender.sendMessage("Height diffential is greater than length!");
					return;
				}

				// Loop through length
				for (int l = 0; l < headings.get(heading).get_length(); l++)
				{
					// Determine y
					if (l > 1 && y != headings.get(heading).get_y())
					{
						y += (int) Math.signum(headings.get(heading).get_y() - y);
					}

					// Determine direction
					if (headings.get(heading).get_direction() == Direction.N)
					{
						if (l != 0)
							z--;

						// surround location
						for (int h = y; h <= y + 5; h++)
						{
							for (int w = x - 2; w <= x + 2; w++)
							{
								block = world.getBlockAt(w, h, z);
								
								// If we don't sleep here we get a read timeout
								try
								{
									Thread.sleep(0, 10);
								} 
								catch (InterruptedException ex) 
								{
									Thread.currentThread().interrupt();
								}

								if (heading != 0 && (l == 0 && (w < x) || l == 1 && (w == x - 2))
										&& headings.get(heading - 1).get_direction() == Direction.E)
								{
									// Do nothing
								}
								else if (heading != 0 && (l == 0 && (w > x) || l == 1 && (w == x + 2))
										&& headings.get(heading - 1).get_direction() == Direction.W)
								{
									// Do nothing
								}
								else if (w == x - 2 || w == x + 2 || h == y + 5)
								{
									// Check if block is already made of required material
									if(block.getType() == Material.GLASS)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLASS);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y && (w == x - 1 || w == x + 1))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.GLOWSTONE)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLOWSTONE);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y
										&& w == x
										&& ((heading == 0 && l == 0) || (heading == headings.size() - 1 && l == headings
												.get(heading).get_length() - 1)))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.DIRT)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.DIRT);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y && w == x && (heading != 0 || l != 0))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.REDSTONE_BLOCK)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.REDSTONE_BLOCK);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else
								{
									// Check if block is already made of required material
									if(block.getType() == Material.AIR)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.AIR);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
							}
						}

						// Place endcap
						if (l == headings.get(heading).get_length() - 1)
						{
							for (int h = y; h <= y + 5; h++)
							{
								for (int w = x - 2; w <= x + 2; w++)
								{
									block = world.getBlockAt(w, h, z - 1);

									if (w == x - 2 || w == x + 2 || h == y + 5)
									{
										// Check if block is already made of required material
										if(block.getType() == Material.GLASS)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.GLASS);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
									else if (h == y && (w == x - 1 || w == x + 1 || w == x))
									{
										// Check if block is already made of required material
										if(block.getType() == Material.GLOWSTONE)
										{
											continue;
										}
												
										BlockSetType bst = new BlockSetType(block, Material.GLOWSTONE);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
									else
									{
										// Check if block is already made of required material
										if(block.getType() == Material.AIR)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.AIR);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
								}
							}

							for (int h = y; h <= y + 5; h++)
							{
								for (int w = x - 2; w <= x + 2; w++)
								{
									block = world.getBlockAt(w, h, z - 2);
									// Check if block is already made of required material
									if(block.getType() == Material.GLASS)
									{
										continue;
									}

									BlockSetType bst = new BlockSetType(block, Material.GLASS);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);								}
							}
						}
					}
					else if (headings.get(heading).get_direction() == Direction.S)
					{
						if (l != 0)
							z++;

						// surround location
						for (int h = y; h <= y + 5; h++)
						{
							for (int w = x - 2; w <= x + 2; w++)
							{
								block = world.getBlockAt(w, h, z);

								if (heading != 0 && (l == 0 && (w < x) || l == 1 && (w == x - 2))
										&& headings.get(heading - 1).get_direction() == Direction.E)
								{
									// Do nothing
								}
								else if (heading != 0 && (l == 0 && (w > x) || l == 1 && (w == x + 2))
										&& headings.get(heading - 1).get_direction() == Direction.W)
								{
									// Do nothing
								}
								else if (w == x - 2 || w == x + 2 || h == y + 5)
								{
									// Check if block is already made of required material
									if(block.getType() == Material.GLASS)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLASS);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y && (w == x - 1 || w == x + 1))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.GLOWSTONE)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLOWSTONE);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y
										&& w == x
										&& ((heading == 0 && l == 0) || (heading == headings.size() - 1 && l == headings
												.get(heading).get_length() - 1)))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.DIRT)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.DIRT);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y && w == x && (heading != 0 || l != 0))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.REDSTONE_BLOCK)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.REDSTONE_BLOCK);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else
								{
									// Check if block is already made of required material
									if(block.getType() == Material.AIR)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.AIR);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
							}
						}

						// Place endcap
						if (l == headings.get(heading).get_length() - 1)
						{
							for (int h = y; h <= y + 5; h++)
							{
								for (int w = x - 2; w <= x + 2; w++)
								{
									block = world.getBlockAt(w, h, z + 1);

									if (w == x - 2 || w == x + 2 || h == y + 5)
									{
										// Check if block is already made of required material
										if(block.getType() == Material.GLASS)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.GLASS);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
									else if (h == y && (w == x - 1 || w == x + 1 || w == x))
									{
										// Check if block is already made of required material
										if(block.getType() == Material.GLOWSTONE)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.GLOWSTONE);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
									else
									{
										// Check if block is already made of required material
										if(block.getType() == Material.AIR)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.AIR);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
								}
							}

							for (int h = y; h <= y + 5; h++)
							{
								for (int w = x - 2; w <= x + 2; w++)
								{
									block = world.getBlockAt(w, h, z + 2);
									// Check if block is already made of required material
									if(block.getType() == Material.GLASS)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLASS);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
							}
						}
					}
					else if (headings.get(heading).get_direction() == Direction.E)
					{
						if (l != 0)
							x++;

						// surround location
						for (int h = y; h <= y + 5; h++)
						{
							for (int w = z - 2; w <= z + 2; w++)
							{
								block = world.getBlockAt(x, h, w);

								if (heading != 0 && (l == 0 && (w > z) || l == 1 && (w == z + 2))
										&& headings.get(heading - 1).get_direction() == Direction.N)
								{
									// Do nothing
								}
								else if (heading != 0 && (l == 0 && (w < z) || l == 1 && (w == z - 2))
										&& headings.get(heading - 1).get_direction() == Direction.S)
								{
									// Do nothing
								}
								else if (w == z - 2 || w == z + 2 || h == y + 5)
								{
									// Check if block is already made of required material
									if(block.getType() == Material.GLASS)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLASS);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y && (w == z - 1 || w == z + 1))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.GLOWSTONE)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLOWSTONE);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y
										&& w == z
										&& ((heading == 0 && l == 0) || (heading == headings.size() - 1 && l == headings
												.get(heading).get_length() - 1)))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.DIRT)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.DIRT);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y && w == z && (heading != 0 || l != 0))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.REDSTONE_BLOCK)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.REDSTONE_BLOCK);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else
								{
									// Check if block is already made of required material
									if(block.getType() == Material.AIR)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.AIR);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
							}
						}

						// Place endcap
						if (l == headings.get(heading).get_length() - 1)
						{
							for (int h = y; h <= y + 5; h++)
							{
								for (int w = z - 2; w <= z + 2; w++)
								{
									block = world.getBlockAt(x + 1, h, w);

									if (w == z - 2 || w == z + 2 || h == y + 5)
									{
										// Check if block is already made of required material
										if(block.getType() == Material.GLASS)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.GLASS);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
									else if (h == y && (w == z - 1 || w == z + 1 || w == z))
									{
										// Check if block is already made of required material
										if(block.getType() == Material.GLOWSTONE)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.GLOWSTONE);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
									else
									{
										// Check if block is already made of required material
										if(block.getType() == Material.AIR)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.AIR);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
								}
							}

							for (int h = y; h <= y + 5; h++)
							{
								for (int w = z - 2; w <= z + 2; w++)
								{
									block = world.getBlockAt(x + 2, h, w);
									// Check if block is already made of required material
									if(block.getType() == Material.GLASS)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLASS);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
							}
						}
					}
					else if (headings.get(heading).get_direction() == Direction.W)
					{
						if (l != 0)
							x--;

						// surround location
						for (int h = y; h <= y + 5; h++)
						{
							for (int w = z - 2; w <= z + 2; w++)
							{
								block = world.getBlockAt(x, h, w);

								if (heading != 0 && (l == 0 && (w > z) || l == 1 && (w == z + 2))
										&& headings.get(heading - 1).get_direction() == Direction.N)
								{
									// Do nothing
								}
								else if (heading != 0 && (l == 0 && (w < z) || l == 1 && (w == z - 2))
										&& headings.get(heading - 1).get_direction() == Direction.S)
								{
									// Do nothing
								}
								else if (w == z - 2 || w == z + 2 || h == y + 5)
								{
									// Check if block is already made of required material
									if(block.getType() == Material.GLASS)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLASS);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y && (w == z - 1 || w == z + 1))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.GLOWSTONE)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLOWSTONE);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y
										&& w == z
										&& ((heading == 0 && l == 0) || (heading == headings.size() - 1 && l == headings
												.get(heading).get_length() - 1)))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.DIRT)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.DIRT);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else if (h == y && w == z && (heading != 0 || l != 0))
								{
									// Check if block is already made of required material
									if(block.getType() == Material.REDSTONE_BLOCK)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.REDSTONE_BLOCK);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
								else
								{
									// Check if block is already made of required material
									if(block.getType() == Material.AIR)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.AIR);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
							}
						}

						// Place endcap
						if (l == headings.get(heading).get_length() - 1)
						{
							for (int h = y; h <= y + 5; h++)
							{
								for (int w = z - 2; w <= z + 2; w++)
								{
									block = world.getBlockAt(x - 1, h, w);

									if (w == z - 2 || w == z + 2 || h == y + 5)
									{
										// Check if block is already made of required material
										if(block.getType() == Material.GLASS)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.GLASS);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
									else if (h == y && (w == z - 1 || w == z + 1 || w == z))
									{
										// Check if block is already made of required material
										if(block.getType() == Material.GLOWSTONE)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.GLOWSTONE);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
									else
									{
										// Check if block is already made of required material
										if(block.getType() == Material.AIR)
										{
											continue;
										}
										
										BlockSetType bst = new BlockSetType(block, Material.AIR);
										this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
									}
								}
							}

							for (int h = y; h <= y + 5; h++)
							{
								for (int w = z - 2; w <= z + 2; w++)
								{
									block = world.getBlockAt(x - 2, h, w);
									// Check if block is already made of required material
									if(block.getType() == Material.GLASS)
									{
										continue;
									}
									
									BlockSetType bst = new BlockSetType(block, Material.GLASS);
									this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
								}
							}
						}
					}

					// Set powered Rail above
					Block block2 = world.getBlockAt(x, y + 1, z);
					if ((heading == 0 && l == 1)
							|| (heading == headings.size() - 1 && l == headings.get(heading).get_length() - 2)
							|| (heading != 0 && l == 0))
					{
						// Check if block is already made of required material
						if(block2.getType() == Material.RAILS)
						{
							continue;
						}
						
						BlockSetType bst = new BlockSetType(block2, Material.RAILS);
						this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
					}
					else if (heading == 0 && l == 0)
					{
						// Check if block is already made of required material
						if(block.getType() == Material.POWERED_RAIL)
						{
							continue;
						}
						
						// Drop minecart here
						BlockSetType bst = new BlockSetType(block2, Material.POWERED_RAIL);
						this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
						world.spawnEntity(block2.getLocation(), EntityType.MINECART);
					}
					else
					{
						// Check if block is already made of required material
						if(block.getType() == Material.POWERED_RAIL)
						{
							continue;
						}
						
						BlockSetType bst = new BlockSetType(block2, Material.POWERED_RAIL);
						this.plugin.getServer().getScheduler().runTask(this.plugin, bst);
					}
				}
			}

			sender.sendMessage("Tunnel successfully created!");
		}
	}
}
