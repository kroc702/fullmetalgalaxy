package com.fullmetalgalaxy.server.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.CompanyStatistics;
import com.fullmetalgalaxy.model.persist.PlayerGameStatistics;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.fullmetalgalaxy.server.GlobalVars;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.googlecode.objectify.Query;

@Path("/stats")
public class StatService {

	@Path("/global")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getGlobal() {
		Map<String, Object> global = new HashMap<>();

		global.put("Nombre de compte classé", GlobalVars.getActiveAccount());
		global.put("Nombre d'inscrit", GlobalVars.getAccountCount());
		global.put("Niveau TS maximum", GlobalVars.getMaxLevel());

		global.put("Nombre de partie en cours", GlobalVars.getCurrentGameCount());
		global.put("Nombre de partie terminée", GlobalVars.getFinishedGameCount());

		global.put("Nombre de partie terminées time Standard",
				GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.Standard));
		global.put("Nombre de partie terminées time StandardAsynch",
				GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.StandardAsynch));
		global.put("Nombre de partie terminées time QuickTurnBased",
				GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.QuickTurnBased));
		global.put("Nombre de partie terminées time QuickAsynch",
				GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.QuickAsynch));
		global.put("Nombre de partie terminées time Custom",
				GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.Custom));
		global.put("Nombre de partie terminées d'initiation", GlobalVars.getFGameInitiationCount());

		global.put("Nombre d'hexagon ", GlobalVars.getFGameNbOfHexagon());
		global.put("Nombre de joueurs ", GlobalVars.getFGameNbPlayer());
		global.put("Somme des scores ", GlobalVars.getFGameFmpScore());

		return new Gson().toJson(global);
	}

	@Path("/company")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getCompany(@DefaultValue("0") @QueryParam("year") int year,
			@DefaultValue("-m_profit") @QueryParam("orderby") String orderby) {

		if (year == 0) {
			year = GregorianCalendar.getInstance().get(Calendar.YEAR) - 1;
			if (GregorianCalendar.getInstance().get(Calendar.MONTH) <= 1) {
				year--;
			}
		}

		Query<CompanyStatistics> companyQuery = FmgDataStore.dao().query(CompanyStatistics.class);
		companyQuery.filter("m_year", year).order(orderby);

		List<Object> list = new ArrayList<Object>();
		for (CompanyStatistics game : companyQuery) {
			list.add(game);
		}
		return new Gson().toJson(list);
	}

	@Path("/playergame")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getPlayerGame(@Context UriInfo uriInfo, @DefaultValue("0") @QueryParam("from") int from,
			@DefaultValue("20") @QueryParam("to") int to,
			@DefaultValue("-m_gameEndDate") @QueryParam("orderby") String orderby) {
		Query<PlayerGameStatistics> playerGameQuery = FmgDataStore.dao().query(PlayerGameStatistics.class);

		for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
			switch (entry.getKey()) {
			default:
				if (entry.getValue().size() == 1) {
					playerGameQuery.filter(entry.getKey(), entry.getValue().get(0));
				} else if (entry.getValue().size() > 1) {
					playerGameQuery.filter(entry.getKey() + " in", entry.getValue().toArray());
				}
				break;
			case "orderby":
			case "from":
			case "to":
			}

		}
		if (to <= from)
			to = from + 20;
		playerGameQuery.order(orderby);

		List<Object> list = new ArrayList<Object>();
		for (PlayerGameStatistics game : playerGameQuery) {
			list.add(game);
		}
		return new Gson().toJson(list);
	}

}
