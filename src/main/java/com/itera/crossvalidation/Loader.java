package com.itera.crossvalidation;

import Configuration.PreProcessing_Configuration;
import IO.ListFiles;
import Preprocessing.Cleaner;
import Preprocessing.Preprocessing;
import Preprocessing.StemPt.OrengoStemmer;
import Preprocessing.StemPt.Stemmer;
import Preprocessing.StemmerEn;
import Preprocessing.StopWords;
import static Preprocessing.TextRepresentation.RepresentTM;
import Structures.Data;
import Structures.FeatureList;
import Structures.IndexValue;
import Structures.InputPattern;
import Word2Vec.JavaWord2Vec;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import scala.Tuple2;

public class Loader {

    static DataFrame[] dataFrames; 
    
    public static Data loadTrainFromCassandra(String language, String w2vDir) {
        return loadData(dataFrames[0], language, w2vDir);
    }

    public static Data loadTestFromCassandra(String language, String w2vDir) {
        return loadData(dataFrames[1], language, w2vDir);
    }
    
    public static void loadDataFromCassandra(String tableName, String[] columns, String whereClause){
        DataFrame dataFrame = IteraSpark.queryCassandra(tableName, columns, whereClause);
        dataFrames = dataFrame.randomSplit(new double[]  { 0.8, 0.2 } );
    }
    
    private static Data loadData(DataFrame df, String language, String w2vDir){
        PreProcessing_Configuration configuration = new PreProcessing_Configuration(language, 2, true, false, true, true);

        List<InputPattern> conteudos = df.javaRDD().map(new Function<Row, InputPattern>() {
            @Override
            public InputPattern call(Row row) throws Exception {
                String id = row.getAs(row.fieldIndex("rsegda_lin_extrt"));
                String classe = row.getAs(row.fieldIndex("category"));
                String texto = row.getAs(row.fieldIndex("rsegda_lin_extrt"));

                return new InputPattern(id, texto, classe);
            }
        }).collect();

        Data data = RepresentTM(conteudos, configuration, w2vDir);

        return data;
    }
}
