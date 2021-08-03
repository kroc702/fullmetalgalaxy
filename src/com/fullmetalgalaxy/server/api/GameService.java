/* *********************************************************************
 *
 *  This file is part of Full Metal Galaxy.
 *  http://www.fullmetalgalaxy.com
 *
 *  Full Metal Galaxy is free software: you can redistribute it and/or 
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 *
 *  Full Metal Galaxy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public 
 *  License along with Full Metal Galaxy.  
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.server.FmgDataStore;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.googlecode.objectify.Query;

/**
 * 
 *
 */
@Path("/game")
public class GameService {

	@Path("/list")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getList(@Context UriInfo uriInfo, @DefaultValue("0") @QueryParam("from") int from,
			@DefaultValue("20") @QueryParam("to") int to,
			@DefaultValue("-m_creationDate") @QueryParam("orderby") String orderby) {

		Query<EbGamePreview> gameQuery = FmgDataStore.dao().query(EbGamePreview.class);

		for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
			switch (entry.getKey()) {
			default:
				if (entry.getValue().size() == 1) {
					gameQuery.filter(entry.getKey(), entry.getValue().get(0));
				} else if (entry.getValue().size() > 1) {
					gameQuery.filter(entry.getKey() + " in", entry.getValue().toArray());
				}
				break;
			case "orderby":
			case "from":
			case "to":
			}

		}
		if (to <= from)
			to = from + 20;
		gameQuery.order(orderby);

		List<Object> list = new ArrayList<Object>();
		for (EbGamePreview game : gameQuery.offset(from).limit(to - from)) {
			list.add(game);
		}

		return new Gson().toJson(list);
	}

}
