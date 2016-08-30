/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.crossvalidation;

import Configuration.PreProcessing_Configuration;
import Learning.Learning;
import SemiSupervisedLearning.TCHN_DocTerm;
import Structures.Data;
import SupervisedLearning.IMHN_DocTerm;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author root
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        PreProcessing_Configuration configuration = new PreProcessing_Configuration(IteraParams.LANGUAGE, 2, true, false, true, true);

        PrintWriter pw = new PrintWriter("out");

        for (int i = 0; i < IteraParams.RUNS; i++) {
            Evaluator eval = new Evaluator();

            Loader.loadDataFromCassandra("event_cc_pareto", new String[]{}, "");
            Data train = Loader.loadTrainFromCassandra(IteraParams.LANGUAGE, "");
            Data test = Loader.loadTestFromCassandra(IteraParams.LANGUAGE, "");

            Learning cls = new TCHN_DocTerm(train, configuration, 0.05, 0.01, 10,100);
            cls.buildClassifier(train);

            eval.evaluateClassifier(cls, test);

            pw.print(eval.toSummaryString());

        }

        pw.close();
    }

}
