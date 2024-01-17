package com.joshua.tutorials.vertx_stock_broker.watchlist;

import com.joshua.tutorials.vertx_stock_broker.db.DbErrorHelper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class DeleteWatchListDatabaseHandler implements Handler<RoutingContext> {
  private final SqlClient db;
  private static final Logger LOG = LoggerFactory.getLogger(DeleteWatchListHandler.class);
  public DeleteWatchListDatabaseHandler(SqlClient db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOG.debug("{} for account {}", context.normalizedPath(), accountId);


    SqlTemplate.forUpdate(
      db,
      "DELETE FROM broker.watchlist where account_id=#{account_id}"
    ).execute(Collections.singletonMap("account_id", accountId))
      .onFailure(DbErrorHelper.errorHandler(context, "Failed to delete account with account id: "+ accountId))
      .onSuccess(result ->{
        LOG.debug("Deleted {} rows for account id {}", result.rowCount(), accountId);
          context.response()
            .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
            .end();
      });

  }
}
