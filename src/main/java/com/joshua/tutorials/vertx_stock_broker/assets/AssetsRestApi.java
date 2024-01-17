package com.joshua.tutorials.vertx_stock_broker.assets;

import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {
  public static final List<String> ASSETS = Arrays.asList("ASSL","AMZN","FB", "GOOG", "MSFT", "NFLX", "TSLA");

  public static void attach(Router parent, SqlClient db){

    parent.get("/assets").handler(new GetAssetsHandler());
    parent.get("/pg/assets").handler(new GetAssetsFromDatabaseHandler(db));
  }
}
