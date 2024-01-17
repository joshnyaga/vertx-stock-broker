package com.joshua.tutorials.vertx_stock_broker.quotes;

import com.joshua.tutorials.vertx_stock_broker.MainVerticle;
import com.joshua.tutorials.vertx_stock_broker.assets.GetAssetsFromDatabaseHandler;
import com.joshua.tutorials.vertx_stock_broker.db.DbErrorHelper;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

public class GetQuoteFromDatabaseHandler implements Handler<RoutingContext> {
  private final SqlClient db;
  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsFromDatabaseHandler.class);
  public GetQuoteFromDatabaseHandler(SqlClient db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    final String assetParams = context.pathParam("asset");
    LOG.debug("Assets parameter: {}", assetParams);
    Map<String, Object> parameters = Collections.singletonMap("asset", assetParams);
    SqlTemplate.forQuery(
      db,
      "SELECT q.asset, q.bid, q.ask, q.last_price, q.volume FROM broker.quotes q where asset=#{asset}"
      )
      .mapTo(QuoteEntity.class)
      .execute(parameters)
      .onFailure(DbErrorHelper.errorHandler(context, "Failed to get quote for asset "+ assetParams))
      .onSuccess(quotes ->{
        if(!quotes.iterator().hasNext()){
          //no entry
          DbErrorHelper.NotFoundResponse(context, "Quote for asset "+ assetParams + " not found");
          return;
        }
        var res = quotes.iterator().next().toJsonObject();
        LOG.info("Path {} responds with {}", context.normalizedPath(), res);
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(res.toBuffer());
      });

  }
}
