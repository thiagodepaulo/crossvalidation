package com.itera.crossvalidation;

import java.io.Serializable;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.cassandra.CassandraSQLContext;


public class IteraSpark implements Serializable {
    
    public transient static JavaSparkContext sc;
    public static CassandraSQLContext sqlContext;

    public static void configureContext(String jarLocation, String sparkHost, String appName, String cassandraHost, String cassandraUsername,
            String cassandraPassword, String keyspace) {
        System.out.println("Configuring Context ...");

        SparkConf conf = new SparkConf(true)
                .set("spark.cassandra.connection.host", cassandraHost)
                .set("spark.cassandra.auth.username", cassandraUsername)
                .set("spark.cassandra.auth.password", cassandraPassword);

        String[] jars = new String[1];
        jars[0] = jarLocation;

        //Class[] classes = new Class[8];
        //conf.registerKryoClasses(classes);
        
        conf.setJars(jars);

        sc = new JavaSparkContext(sparkHost, appName, conf);

        sqlContext = new CassandraSQLContext(sc.sc());
        sqlContext.setKeyspace(keyspace);
    }
    
    public static DataFrame queryCassandra(String tableName, String[] columns, String whereClause) {
        if (sqlContext == null){
            configureContext("", "local", "teste", "192.168.21.253", "itera", "itera2101@", "itera");
        }
        
        String query = "";

        if (columns != null) {
            if (columns.length > 0) {
                query = "SELECT " + String.join(",", columns) + " FROM " + tableName;
            } else {
                query = "SELECT * FROM " + tableName;
            }
        } else {
            query = "SELECT * FROM " + tableName;
        }

        if (!whereClause.isEmpty()){
            query += " WHERE " + whereClause + " ALLOW FILTERING";
        }
        
        DataFrame df = sqlContext.cassandraSql(query);
        
        return df;
    }
}
