/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.crossvalidation;

import Learning.Learning;
import Structures.Data;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author root
 */
public class Main {
    
    public static void main(String[] args) throws FileNotFoundException {
        
        Evaluator eval = new Evaluator();
        for(int i=0; i<IteraParams.RUNS; i++) {
           Data train = Loader.loadTrainFromCassandra();
           Data test = Loader.loadTestFromCassandra();
           
           Learning cls = new Learning();
           cls.buildClassifier(train);
           
           eval.evaluateClassifier(cls, test);
        }    
        
        PrintWriter pw = new PrintWriter("out");
        pw.print(eval.toSummaryString());
        pw.close();
    }
        
}
