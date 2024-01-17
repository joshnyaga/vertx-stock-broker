package com.joshua.tutorials.vertx_stock_broker.assets;

import com.joshua.tutorials.vertx_stock_broker.db.DbErrorHelper;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAssetsFromDatabaseHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsFromDatabaseHandler.class);
  private final SqlClient db;
  public GetAssetsFromDatabaseHandler(SqlClient db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    db.query("SELECT a.value FROM broker.assets a")
      .execute()
      .onFailure(DbErrorHelper.errorHandler(context, "Failed to get assets from db!"))
      .onSuccess(result ->{
        var response  = new JsonArray();
        result.forEach(row->{
            response.add(row.getValue("value"));
        });
        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });

  }


}
