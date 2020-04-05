package com.d3t.klplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import com.d3t.klplugin.stocks.StockData;
import com.d3t.klplugin.stocks.StockMarketHandler;

import net.milkbowl.vault.economy.EconomyResponse;

public class Commands {

	private static boolean parseError;
	private Server server;

	public RealEstateController immo;

	public Commands(Server server) {
		this.server = server;
		immo = new RealEstateController();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String c = cmd.getName();
		parseError = false;
		if(c.equalsIgnoreCase("payscoreboardvalue")) {
			if(args.length == 3) {
				String player = args[0];
				String scoreboardObjective = args[1];
				double multiplier = ParseDouble(args[2], sender);
				if(!parseError) PayoutScoreboardValue(player, scoreboardObjective, multiplier);
			} else {
				ArgCountError(sender, args.length, 3);
			}
			return true;
		} else if(c.equalsIgnoreCase("listofflineplayers")) {
			sender.sendMessage("§aLIST OF ALL OFFLINEPLAYERS:");
			for (OfflinePlayer p : server.getOfflinePlayers()) {
				sender.sendMessage(p.getName());
			}
			return true;
		} else if(c.equalsIgnoreCase("cmdblockop")) {
			if(IsPlayer(sender)) {
				sender.sendMessage("§cOnly command blocks can run this command!");
			} else {
				if(args.length != 2) {
					server.broadcastMessage("§8Command block error (cmdblockop)!");
					return false;
				}
				OfflinePlayer p = KLPlugin.getOfflinePlayer(args[0]);
				if(p != null) {
					boolean op = args[1].equalsIgnoreCase("true");
					if(p.isOp() != op) {
						p.setOp(op);
						if(op) {
							server.broadcastMessage(String.format("§9%s has been made a server operator", p.getName()));
						} else {
							server.broadcastMessage(String.format("§9%s is no longer a server operator", p.getName()));
						}
					}
					return true;
				}
			}
		} else if(c.equalsIgnoreCase("cmdblockgamerule")) {
			if(IsPlayer(sender)) {
				sender.sendMessage("§cOnly command blocks can run this command!");
				return false;
			} else {
				//Handled by anticheat's onCommandServer
				return true;
			}
		} else if(c.equalsIgnoreCase("giveimmopoints")) {
			if(args.length != 3) {
				ArgCountError(sender, args.length, 2);
				return false;
			} else {
				int amount = ParseInt(args[2], sender);
				OfflinePlayer player = KLPlugin.getOfflinePlayer(args[0]);
				if(player != null) {
					return GiveImmoPoints(sender, player, args[1], amount);
				}
			}
		} else if(c.equalsIgnoreCase("tradeimmoep")) {
			if(!IsPlayer(sender)) {
				KLPlugin.log.info("Only Players can execute this command!");
				return true;
			}
			if(args.length != 2) {
				ArgCountError(sender, args.length, 2);
				return false;
			} else {
				OfflinePlayer offlinePlayer = KLPlugin.getOfflinePlayer(args[0]);
				int amount = ParseInt(args[1], sender);
				if(offlinePlayer != null) {
					TradeImmoPoints(KLPlugin.toOnlinePlayer(KLPlugin.getOfflinePlayer(sender.getName())), offlinePlayer, amount);
					return true;
				}
			}
		} else if(c.equalsIgnoreCase("calcimmodemand")) {
			immo.calculateRates();
			return true;
		} else if(c.equalsIgnoreCase("immostatus")) {
			if(IsPlayer(sender)) immo.showDemandGraph(sender);
			return true;
		} else if(c.equalsIgnoreCase("modifyrate")) {
			if(args.length == 2) {
				int index = ParseInt(args[0], sender);
				float newRate = (float)ParseDouble(args[1], sender);
				immo.modifyRate(index, newRate);
			}
			return true;
		} else if(c.equalsIgnoreCase("pvpklasse")) {
			if(KLPlugin.pvp.selectClass(sender, args)) return true;
		} else if(c.equalsIgnoreCase("isworld")) {
			if(args.length > 0) {
				if(IsPlayer(sender)) {
					String worldname = ((Player)sender).getWorld().getName();
					if(worldname.equalsIgnoreCase(args[0])) {
						sender.sendMessage("Yes, you are in world "+worldname);
					} else {
						sender.sendMessage("No, you are not in world "+args[0]);
					}
				} else {
					if(sender instanceof BlockCommandSender) {
						BlockCommandSender cmdblock = (BlockCommandSender)sender;
						String worldname = cmdblock.getBlock().getWorld().getName();
						boolean b = worldname.equalsIgnoreCase(args[0]);
						Location loc = cmdblock.getBlock().getLocation();
						loc = loc.add(0, 1, 0);
						cmdblock.getBlock().getWorld().getBlockAt(loc).setType(b ? Material.REDSTONE_BLOCK : Material.COAL_BLOCK);
					}
				}
				return true;
			}
		} else if(c.equalsIgnoreCase("pvptick")) {
			KLPlugin.pvp.doGameTick();
			return true;
		} else if(c.equalsIgnoreCase("applyloadouttoclass")) {
			if(sender instanceof BlockCommandSender) {
				if(args.length >= 4) {
					BlockCommandSender cmdblock = (BlockCommandSender)sender;
					int cl = ParseInt(args[0], sender);
					int x = ParseInt(args[1], sender);
					int y = ParseInt(args[2], sender);
					int z = ParseInt(args[3], sender);
					KLPlugin.pvp.applyLoadout(sender, cl, cmdblock.getBlock().getWorld(), x,y,z);
					sender.sendMessage("ok");
				} else {
					sender.sendMessage("§cNot enough args!");
				}
			} else {
				sender.sendMessage("§cOnly command blocks can run this command!");
			}
			return true;
		} else if(c.equalsIgnoreCase("calcvelocity")) {
			if(args.length > 0) {
				double multiplier = 1;
				if(args.length > 1) {
					multiplier = ParseDouble(args[1], sender);
				}
				calcPlayerVelocities(args[0], multiplier);
			} else {
				sender.sendMessage("§cNot enough args!");
			}
		} else if(c.equalsIgnoreCase("stockinfo")) {
			if(args.length > 0) {
				StockMarketHandler.requestStockInfo(args[0], sender);
				return true;
			} else {
				sender.sendMessage("§cCompany symbol required!");
				return false;
			}
		} else if(c.equalsIgnoreCase("stocks")) {
			if(sender instanceof Player) {
				StockData[] d = StockMarketHandler.getStocksForPlayer((OfflinePlayer)sender);
				if(d != null) {
					sender.sendMessage("Deine Aktien:");
					for(int i = 0; i < d.length; i++) {
						sender.sendMessage("§8"+(i+1)+"§f "+d[i].symbol+"("+d[i].amount+"x) "+d[i].worthAtPurchase+"$");
					}
					sender.sendMessage("§8§o(Die Werte beziehen sich auf den Wert beim Zeitpunkt des Kaufs pro Aktie)");
					sender.sendMessage("§8----------");
					return true;
				}
			} else {
				System.out.println("Only a player can do this!");
			}
		} else if(c.equalsIgnoreCase("buystocks")) {
			if(sender instanceof Player) {
				if(args.length >= 2) {
					int amount = ParseInt(args[1], sender);
					StockMarketHandler.buyStocks((Player)sender, args[0], amount);
					return true;
				} else {
					return false;
				}
			} else {
				System.out.println("Only a player can do this!");
			}
		} else if(c.equalsIgnoreCase("sellstocks")) {
			if(sender instanceof Player) {
				if(args.length >= 1) {
					int index = ParseInt(args[0], sender);
					StockMarketHandler.sellStocks((Player)sender, index);
					return true;
				} else {
					return false;
				}
			} else {
				System.out.println("Only a player can do this!");
			}
		}
		if(parseError) sender.sendMessage("§cExecution failed");
		return false;
	}

	private void Pay(String playerName, double amount) {
		OfflinePlayer player = KLPlugin.getOfflinePlayer(playerName);
		if (player == null) {
			KLPlugin.log.info("Failed to get player!");
			return;
		}
		EconomyResponse r = KLPlugin.econ.depositPlayer(player, amount);
		if (r.transactionSuccess()) {
			// Transaction successfull!
		} else {
			KLPlugin.log.warning("Failed to pay player!" + r.errorMessage);
		}
	}

	private boolean GiveImmoPoints(CommandSender sender, OfflinePlayer player, String immoclass, int amount) {
		String scoreName = "";
		if (immoclass.equalsIgnoreCase("w1"))
			scoreName = Scoreboards.W1;
		else if (immoclass.equalsIgnoreCase("w2"))
			scoreName = Scoreboards.W2;
		else if (immoclass.equalsIgnoreCase("w3"))
			scoreName = Scoreboards.W3;
		else if (immoclass.equalsIgnoreCase("w4"))
			scoreName = Scoreboards.W4;
		else if (immoclass.equalsIgnoreCase("w5"))
			scoreName = Scoreboards.W5;
		else if (immoclass.equalsIgnoreCase("g"))
			scoreName = Scoreboards.G;
		else if (immoclass.equalsIgnoreCase("i"))
			scoreName = Scoreboards.I;
		else if (immoclass.equalsIgnoreCase("e"))
			scoreName = Scoreboards.Extra;
		if (scoreName.isEmpty()) {
			sender.sendMessage(String.format("§cImmo class '%s' not recognized.", immoclass));
			return false;
		}
		Score s = server.getScoreboardManager().getMainScoreboard().getObjective(scoreName).getScore(player.getName());
		s.setScore(s.getScore() + amount);
		server.broadcastMessage(String.format("§e%s hat %s %s-Punkte erhalten!", player.getName(), amount, scoreName));
		Player receiverPlayer = server.getPlayer(player.getUniqueId());
		if (receiverPlayer != null) {
			receiverPlayer.sendMessage(String.format("§eDu hast jetzt %s %s-Punkte", s.getScore(), scoreName));
		}
		return true;
	}

	private void TradeImmoPoints(Player sender, OfflinePlayer receiver, int amount) {
		if (receiver == null)
			return;
		if (amount <= 0) {
			sender.sendMessage("§cThat's not how it works!");
			return;
		}
		Score senderScore = getScoreObject(sender.getName(), Scoreboards.Extra);
		Score receiverScore = getScoreObject(receiver.getName(), Scoreboards.Extra);
		if (senderScore.getScore() < amount) {
			sender.sendMessage("§cDu hast nicht genug Extra-Punkte!");
			return;
		}
		senderScore.setScore(senderScore.getScore() - amount);
		sender.sendMessage(String.format("§eDu hast %s Extra-Punkte an %s gegeben.", amount, receiver.getName()));
		receiverScore.setScore(receiverScore.getScore() + amount);
		Player onlineReceiver = KLPlugin.toOnlinePlayer(receiver);
		if (onlineReceiver != null) {
			onlineReceiver
					.sendMessage(String.format("§eDu hast %s Extra-Punkte von %s erhalten.", amount, sender.getName()));
		}
	}
	
	private void calcPlayerVelocities(String targetScore, double multiplier) {
		Objective objective = server.getScoreboardManager().getMainScoreboard().getObjective(targetScore);
		double speed = 0;
		for(Player player : server.getOnlinePlayers()) {
			if(player.isGliding()) {
				Vector v = player.getVelocity();
				speed = Math.sqrt(Math.pow(v.getX(),2) + Math.pow(v.getY(),2) + Math.pow(v.getZ(),2));
				speed *= multiplier;
			}
			objective.getScore(player.getName()).setScore((int)Math.floor(speed));
		}
	}

	private boolean PayoutScoreboardValue(String player, String objective, double multiplier) {
		try {
			int amount = getScoreObject(player, objective).getScore();
			if (isOnline(player))
				Pay(player, amount * multiplier);
		} catch (Exception e) {
			KLPlugin.log.warning("Failed to get scoreboard value!");
		}
		return true;
	}

	static void ArgCountError(CommandSender sender, int args, int reqArgs) {
		boolean tooMany = args > reqArgs;
		if (sender instanceof Player) {
			if (tooMany) {
				sender.sendMessage(String.format("§cToo many arguments. %s arguments required.", reqArgs));
			} else {
				sender.sendMessage(String.format("§cNot enough arguments. %s arguments required.", reqArgs));
			}
		}
	}

	public static int ParseInt(String input, CommandSender sender) {
		try {
			return new Integer(input);
		} catch (Exception e) {
			sender.sendMessage(String.format("§cCannot parse '%s' to integer", input));
			return 0;
		}
	}

	public static double ParseDouble(String input, CommandSender sender) {
		try {
			return new Double(input);
		} catch (Exception e) {
			sender.sendMessage(String.format("§cCannot parse '%s' to double", input));
			return 0;
		}
	}

	static boolean IsPlayer(CommandSender sender) {
		return sender instanceof Player;
	}

	private Score getScoreObject(String player, String scorename) {
		return server.getScoreboardManager().getMainScoreboard().getObjective(scorename).getScore(player);
	}

	private boolean isOnline(String player) {
		for (Player p : server.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(player))
				return true;
		}
		return false;
	}
}
