/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.crossvalidation;

import Structures.Data;

/**
 *
 * @author root
 */
public class Evaluator {
    
    public double[][] confusionMatrix;
    
    public void evaluateClassifier(Learning model, Data testData) {
        int nClass = testData.getNumClasses();
        confusionMatrix = new double[nClass][nClass];
        
    }
    
}
