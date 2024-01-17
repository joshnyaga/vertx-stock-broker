package com.joshua.tutorials.vertx_stock_broker.watchlist;

import com.joshua.tutorials.vertx_stock_broker.db.DbErrorHelper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PutWatchListDatabaseHandler implements Handler<RoutingContext> {
   private final Pool db;
  private static final Logger LOG = LoggerFactory.getLogger(PutWatchListDatabaseHandler.class);
  public PutWatchListDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {

    var accountId = context.pathParam("accountId");
    LOG.debug("{} for account {}", context.normalizedPath(), accountId);
    // the get the body
    var json = context.getBodyAsJson();
    var watchList = json.mapTo(WatchList.class);

    var parameterBatch =  watchList.getAssets().stream().map(asset -> {
      final Map<String, Object> parameters = new HashMap<>();
      parameters.put("account_id", accountId);
      parameters.put("asset", asset.getName());
      return parameters;
    }).toList();

    //Transaction

    db.withTransaction(client ->{
      //1 - Delete all for account_id
      return SqlTemplate.forUpdate(client,"DELETE FROM broker.watchList w where w.account_id = #{account_id}")
        .execute(Collections.singletonMap("account_id", accountId))
        .onFailure(DbErrorHelper.errorHandler(context, "Failed to clear watchlist for account id: "+ accountId))
        .compose(deletionOne ->{
          return addAllForAccountId(client,context, parameterBatch);
        }) .onFailure(DbErrorHelper.errorHandler(context, "Failed to update watchlist for account id: "+ accountId))
        .onSuccess(result -> {
          //both executed successfully
          context.response()
            .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
            .end();

      });

    });
    //2








  }

  private Future<SqlResult<Void>> addAllForAccountId(SqlConnection client, RoutingContext context, List<Map<String, Object>> parameterBatch) {
    return SqlTemplate.forUpdate(
        client,
        "INSERT INTO broker.watchList VALUES (#{account_id}, #{asset})"
      ).executeBatch(parameterBatch)
      .onFailure(DbErrorHelper.errorHandler(context, "Failed to insert into watchlist"));
  }
}
