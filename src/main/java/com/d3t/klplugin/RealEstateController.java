package com.d3t.klplugin;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;

public class RealEstateController {

	public static final String[] WNames = { "Wohnen $", "Wohnen $$", "Wohnen $$$", "Wohnen $$$$", "Wohnen $$$$$" };
	public static final String[] WIGNames = { "Wohnen allgemein", "Gewerbe", "Industrie" };

	public static final float[] WRatios = { 0.20f, 0.32f, 0.28f, 0.16f, 0.04f };
	public static final float[] WIGRatio = { 0.60f, 0.25f, 0.15f };

	public static float[] WRates = new float[] { 1f, 1f, 1f, 1f, 1f };
	public static float[] WIGRate = new float[] { 1f, 1f, 1f };

	public RealEstateController() {
		calculateRates();
	}

	public void assignMultipliers() {
		Objective objective = KLPlugin.INSTANCE.getServer().getScoreboardManager().getMainScoreboard()
				.getObjective("Int");
		// W1-5
		for (int i = 0; i < 5; i++) {
			int val = objective.getScore(String.format("W%sworth", i + 1)).getScore();
			objective.getScore(String.format("W%smul", i + 1)).setScore(Math.round(val * WRates[i]));
		}
		// G
		int gVal = objective.getScore("Gworth").getScore();
		objective.getScore("Gmul").setScore(Math.round(gVal * WIGRate[1]));
		// I
		int iVal = objective.getScore("Iworth").getScore();
		objective.getScore("Imul").setScore(Math.round(iVal * WIGRate[2]));
	}

	public void calculateRates() {
		int allWPoints = 0;
		for (String s : Scoreboards.AllWScores) {
			allWPoints += getTotalScore(s);
		}
		int allGPoints = getTotalScore(Scoreboards.G);
		int allIPoints = getTotalScore(Scoreboards.I);
		int allPoints = allWPoints + allGPoints + allIPoints;
		float min = lerp(0.9f, 0.5f, allPoints / 250f);
		float max = lerp(1.2f, 2.0f, allPoints / 250f);
		int[] typeDistr = new int[] { allWPoints, allGPoints, allIPoints };
		calculateOverallRates(allPoints, typeDistr, min, max);
		calculateWRates(allWPoints, min, max);
		assignMultipliers();
	}

	private void calculateOverallRates(int allPoints, int[] points, float min, float max) {
		float distr[] = new float[3];
		for (int i = 0; i < 3; i++) {
			distr[i] = points[i] / (float) allPoints;
			float deviation = WIGRatio[i] / distr[i];
			deviation = Math.max(min, Math.min(max, deviation));
			WIGRate[i] = deviation;
		}
	}

	private void calculateWRates(int allWPoints, float min, float max) {
		float distr[] = new float[Scoreboards.AllWScores.length];
		for (int i = 0; i < distr.length; i++) {
			distr[i] = getTotalScore(Scoreboards.AllWScores[i]) / (float) allWPoints;
			float deviation = WRatios[i] / distr[i];
			deviation = Math.max(min, Math.min(max, deviation));
			WRates[i] = deviation * WIGRate[0];
		}
	}

	public void showDemandGraph(CommandSender sender) {
		if (Commands.IsPlayer(sender)) {
			sender.sendMessage("§eNachfrage für Immobilien:");
			sender.sendMessage("§a" + WIGNames[0]);
			sender.sendMessage(drawGraph(WIGRate[0]));
			for (int i = 0; i < WRates.length; i++) {
				float rate = WRates[i];
				sender.sendMessage("§a   " + WNames[i]);
				sender.sendMessage("   " + drawGraph(rate));
			}
			sender.sendMessage("§9" + WIGNames[1]);
			sender.sendMessage(drawGraph(WIGRate[1]));
			sender.sendMessage("§6" + WIGNames[2]);
			sender.sendMessage(drawGraph(WIGRate[2]));
		} else {
			Server s = KLPlugin.INSTANCE.getServer();
			s.getLogger().info(String.format("Nachfrage für Immobilien (in %): W: %s G: %s I: %s", (int)(WIGRate[0]*100), (int)(WIGRate[1]*100), (int)(WIGRate[2]*100)));
			s.getLogger().info(String.format("davon Wohnen (W1-5): %s, %s, %s, %s, %s", (int)(WRates[0]*100), (int)(WRates[1]*100), (int)(WRates[2]*100), (int)(WRates[3]*100), (int)(WRates[4]*100)));
		}
	}

	private String drawGraph(float rate) {
		int scale = 0;
		if (rate < 1) {
			scale = Math.round((rate - 1f) * 40);
		} else {
			scale = Math.round((rate - 1f) * 20);
		}
		String graph = "§8";
		for (int g = 0; g < 20; g++)
			graph += "|";
		graph += "§7:§8";
		for (int g = 0; g < 20; g++)
			graph += "|";
		// add color & select operator
		String operator = "";
		if (scale < 0) {
			graph = insertString(graph, "§c", 21 + scale);
			operator = "-";
		} else if (scale > 0) {
			graph = insertString(graph, "§a", 26);
			graph = insertString(graph, "§8", 28 + scale);
			operator = "+";
		} else {
			graph = insertString(graph, "§6", 23);
			operator = "+/-";
		}
		graph += " §7(" + operator + Math.abs(Math.round(100 - rate * 100)) + "%)";
		return graph;
	}

	private int getTotalScore(String scorename) {
		int total = 0;
		Server server = KLPlugin.INSTANCE.getServer();
		for (OfflinePlayer p : server.getOfflinePlayers()) {
			total += server.getScoreboardManager().getMainScoreboard().getObjective(scorename).getScore(p.getName())
					.getScore();
		}
		return total;
	}

	public void modifyRate(int index, float newRate) {
		WRates[index] = newRate;
	}

	private float lerp(float a, float b, float f) {
		f = Math.max(0, Math.min(1, f));
		return a + f * (b - a);
	}

	public static String insertString(String originalString, String stringToBeInserted, int index) {
		// Create a new string
		String newString = new String();
		for (int i = 0; i < originalString.length(); i++) {
			// Insert the original string character
			// into the new string
			newString += originalString.charAt(i);
			if (i == index) {
				// Insert the string to be inserted
				// into the new string
				newString += stringToBeInserted;
			}
		}
		return newString;
	}
}
