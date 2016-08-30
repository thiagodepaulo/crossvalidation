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

    static DataFrame dataFrame; 
    
    public static void loadData(String tableName, String[] columns, String whereClause){
        dataFrame = IteraSpark.queryCassandra(tableName, columns, whereClause);
    }
    public static Data loadTrainFromCassandra(String language, String w2vDir) {
        PreProcessing_Configuration configuration = new PreProcessing_Configuration(language, 2, true, false, true, true);

        List<InputPattern> conteudos = dataFrame.javaRDD().map(new Function<Row, InputPattern>() {
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

    public static Data loadTestFromCassandra() {
        return null;
    }
    
    public static void loadDataFromCassandra(){
        DataFrame[] dataFrames = dataFrame.randomSplit(new double[]  { 0.8, 0.2 } );
    }
    
    
}
