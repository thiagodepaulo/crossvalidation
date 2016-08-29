/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.crossvalidation;

/**
 *
 * @author root
 */
public class Validator {
    
    public static void main(String[] args) {
        
        for(int i=0; i<IteraParams.RUNS; i++) {
           Data train = Loader.loadFromCassandra();
           Data test = Loader.loadTrainFromCassandra();
           
           Learning model = buildClassifier();
           double[] confidence = model.classify(test);           
        }    
        
    }
    
    public static Learning buildClassifier() {
        return null;
    }
    
}
