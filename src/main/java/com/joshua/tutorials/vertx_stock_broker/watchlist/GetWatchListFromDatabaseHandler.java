package com.joshua.tutorials.vertx_stock_broker.watchlist;

import com.joshua.tutorials.vertx_stock_broker.db.DbErrorHelper;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

public class GetWatchListFromDatabaseHandler implements Handler<RoutingContext> {

  private final SqlClient db;
  private static final Logger LOG = LoggerFactory.getLogger(DeleteWatchListHandler.class);
  public GetWatchListFromDatabaseHandler(SqlClient db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    //get the account id from path params
    var accountId = context.pathParam("accountId");
    //show the path and account id param
    LOG.debug("{} for account {}", context.normalizedPath(), accountId);
    Map<String, Object> parameters=Collections.singletonMap("account_id", accountId);

    SqlTemplate.forQuery(
      db,
      "SELECT w.asset FROM broker.watchList w where account_id=#{account_id}"

    )
      .mapTo(Row::toJson)//convert row to json
      .execute(parameters)
      .onFailure(DbErrorHelper.errorHandler(context, "Failed to fetch watch list for account id: "+ accountId))
      .onSuccess(assets ->{
        final RowSet<JsonObject> rows = assets;
        if(!assets.iterator().hasNext()){
          DbErrorHelper.NotFoundResponse(context,  "Watchlist for accountId "+ accountId+ " is not available");
          return;
        }
        var res = new JsonArray();
        //add to the json array - java 11
        assets.forEach(res::add);
        LOG.info("Path {} responds with {}", context.normalizedPath(), res);
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(res.toBuffer());
      });
  }
}
