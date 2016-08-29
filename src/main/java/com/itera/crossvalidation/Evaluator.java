/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.crossvalidation;

import Structures.Data;
import Structures.IndexValue;
import java.util.ArrayList;

/**
 *
 * @author root
 */
public class Evaluator {

    public double[][] confusionMatrix;
    public int nClass;
    public int nDocs;

    public void evaluateClassifier(Learning model, Data testData) {
        this.nClass = testData.getNumClasses();
        this.nDocs = testData.getNumDocs();

        ArrayList<IndexValue> adjList;
        confusionMatrix = new double[nClass][nClass];
        for (int docId = 0; docId < nDocs; docId++) {
            adjList = testData.getAdjListDoc(docId);
            int realClassDoc = testData.getClassDocument(docId);
            int predClassDoc = model.classify(adjList);
            confusionMatrix[realClassDoc][predClassDoc] += 1;
        }
    }

    public double truePositiveRate(int classIndex) {
        double correct = 0, total = 0;
        for (int j = 0; j < this.nClass; j++) {
            if (j == classIndex) {
                correct += this.confusionMatrix[classIndex][j];
            }
            total += this.confusionMatrix[classIndex][j];
        }
        if (total == 0) {
            return 0;
        }
        return correct / total;
    }

    /**
     * Calculates the weighted (by class size) true positive rate.
     *
     * @return the weighted true positive rate.
     */
    public double weightedTruePositiveRate() {
        double[] classCounts = new double[this.nClass];
        double classCountSum = 0;

        for (int i = 0; i < this.nClass; i++) {
            for (int j = 0; j < this.nClass; j++) {
                classCounts[i] += this.confusionMatrix[i][j];
            }
            classCountSum += classCounts[i];
        }

        double truePosTotal = 0;
        for (int i = 0; i < this.nClass; i++) {
            double temp = truePositiveRate(i);
            truePosTotal += (temp * classCounts[i]);
        }

        return truePosTotal / classCountSum;
    }

    /**
     * Calculate the true negative rate with respect to a particular class. This
     * is defined as<p/>
     * <
     * pre>
     * correctly classified negatives ------------------------------ total
     * negatives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the true positive rate
     */
    public double trueNegativeRate(int classIndex) {
        double correct = 0, total = 0;
        for (int i = 0; i < this.nClass; i++) {
            if (i != classIndex) {
                for (int j = 0; j < this.nClass; j++) {
                    if (j != classIndex) {
                        correct += this.confusionMatrix[i][j];
                    }
                    total += this.confusionMatrix[i][j];
                }
            }
        }
        if (total == 0) {
            return 0;
        }
        return correct / total;
    }

    /**
     * Calculates the weighted (by class size) true negative rate.
     *
     * @return the weighted true negative rate.
     */
    public double weightedTrueNegativeRate() {
        double[] classCounts = new double[this.nClass];
        double classCountSum = 0;

        for (int i = 0; i < this.nClass; i++) {
            for (int j = 0; j < this.nClass; j++) {
                classCounts[i] += this.confusionMatrix[i][j];
            }
            classCountSum += classCounts[i];
        }

        double trueNegTotal = 0;
        for (int i = 0; i < this.nClass; i++) {
            double temp = trueNegativeRate(i);
            trueNegTotal += (temp * classCounts[i]);
        }

        return trueNegTotal / classCountSum;
    }

    /**
     * Calculates the weighted (by class size) recall.
     *
     * @return the weighted recall.
     */
    public double weightedRecall() {
        return weightedTruePositiveRate();
    }

    /**
     * Calculate the precision with respect to a particular class. This is
     * defined as<p/>
     * <
     * pre>
     * correctly classified positives ------------------------------ total
     * predicted as positive
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the precision
     */
    public double precision(int classIndex) {

        double correct = 0, total = 0;
        for (int i = 0; i < this.nClass; i++) {
            if (i == classIndex) {
                correct += this.confusionMatrix[i][classIndex];
            }
            total += this.confusionMatrix[i][classIndex];
        }
        if (total == 0) {
            return 0;
        }
        return correct / total;
    }

    /**
     * Calculates the weighted (by class size) precision.
     *
     * @return the weighted precision.
     */
    public double weightedPrecision() {
        double[] classCounts = new double[this.nClass];
        double classCountSum = 0;

        for (int i = 0; i < this.nClass; i++) {
            for (int j = 0; j < this.nClass; j++) {
                classCounts[i] += this.confusionMatrix[i][j];
            }
            classCountSum += classCounts[i];
        }

        double precisionTotal = 0;
        for (int i = 0; i < this.nClass; i++) {
            double temp = precision(i);
            precisionTotal += (temp * classCounts[i]);
        }

        return precisionTotal / classCountSum;
    }

    /**
     * Calculate the F-Measure with respect to a particular class. This is
     * defined as<p/>
     * <
     * pre>
     * 2 * recall * precision ---------------------- recall + precision
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the F-Measure
     */
    public double fMeasure(int classIndex) {

        double precision = precision(classIndex);
        double recall = recall(classIndex);
        if ((precision + recall) == 0) {
            return 0;
        }
        return 2 * precision * recall / (precision + recall);
    }

    /**
     * Calculate the recall with respect to a particular class. This is defined
     * as<p/>
     * <
     * pre>
     * correctly classified positives ------------------------------ total
     * positives
     * </pre><p/>
     * (Which is also the same as the truePositiveRate.)
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the recall
     */
    public double recall(int classIndex) {

        return truePositiveRate(classIndex);
    }

    /**
     * Calculates the macro weighted (by class size) average F-Measure.
     *
     * @return the weighted F-Measure.
     */
    public double weightedFMeasure() {
        double[] classCounts = new double[this.nClass];
        double classCountSum = 0;

        for (int i = 0; i < this.nClass; i++) {
            for (int j = 0; j < this.nClass; j++) {
                classCounts[i] += this.confusionMatrix[i][j];
            }
            classCountSum += classCounts[i];
        }

        double fMeasureTotal = 0;
        for (int i = 0; i < this.nClass; i++) {
            double temp = fMeasure(i);
            fMeasureTotal += (temp * classCounts[i]);
        }

        return fMeasureTotal / classCountSum;
    }

    /**
     * Unweighted micro-averaged F-measure. If some classes not present in the
     * test set, they have no effect.
     *
     * Note: if the test set is *single-label*, then this is the same as
     * accuracy.
     *
     * @return unweighted micro-averaged F-measure.
     */
    public double unweightedMicroFmeasure() {
        double tp = 0;
        double fn = 0;
        double fp = 0;
        for (int c = 0; c < this.nClass; c++) {
            tp += numTruePositives(c);
            fn += numFalseNegatives(c);
            fp += numFalsePositives(c);
        }
        return 2 * tp / (2 * tp + fn + fp);
    }

    /**
     * Calculate the number of true positives with respect to a particular
     * class. This is defined as<p/>
     * <
     * pre>
     * correctly classified positives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the true positive rate
     */
    public double numTruePositives(int classIndex) {
        double correct = 0;
        for (int j = 0; j < this.nClass; j++) {
            if (j == classIndex) {
                correct += this.confusionMatrix[classIndex][j];
            }
        }
        return correct;
    }

    /**
     * Calculate number of false negatives with respect to a particular class.
     * This is defined as<p/>
     * <
     * pre>
     * incorrectly classified positives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the false positive rate
     */
    public double numFalseNegatives(int classIndex) {
        double incorrect = 0;
        for (int i = 0; i < this.nClass; i++) {
            if (i == classIndex) {
                for (int j = 0; j < this.nClass; j++) {
                    if (j != classIndex) {
                        incorrect += this.confusionMatrix[i][j];
                    }
                }
            }
        }
        return incorrect;
    }

    /**
     * Calculate number of false positives with respect to a particular class.
     * This is defined as<p/>
     * <
     * pre>
     * incorrectly classified negatives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the false positive rate
     */
    public double numFalsePositives(int classIndex) {
        double incorrect = 0;
        for (int i = 0; i < this.nClass; i++) {
            if (i != classIndex) {
                for (int j = 0; j < this.nClass; j++) {
                    if (j == classIndex) {
                        incorrect += this.confusionMatrix[i][j];
                    }
                }
            }
        }
        return incorrect;
    }

    /**
     * Returns value of kappa statistic if class is nominal.
     *
     * @return the value of the kappa statistic
     */
    public final double kappa() {

        double[] sumRows = new double[confusionMatrix.length];
        double[] sumColumns = new double[confusionMatrix.length];
        double sumOfWeights = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix.length; j++) {
                sumRows[i] += confusionMatrix[i][j];
                sumColumns[j] += confusionMatrix[i][j];
                sumOfWeights += confusionMatrix[i][j];
            }
        }
        double correct = 0, chanceAgreement = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            chanceAgreement += (sumRows[i] * sumColumns[i]);
            correct += confusionMatrix[i][i];
        }
        chanceAgreement /= (sumOfWeights * sumOfWeights);
        correct /= sumOfWeights;

        if (chanceAgreement < 1) {
            return (correct - chanceAgreement) / (1 - chanceAgreement);
        } else {
            return 1;
        }
    }

    /**
     * Outputs the performance statistics in summary form. Lists number (and
     * percentage) of instances classified correctly, incorrectly and
     * unclassified. Outputs the total number of instances classified, and the
     * number of instances (if any) that had no class value provided.
     *
     * @param title the title for the statistics
     * @param printComplexityStatistics if true, complexity statistics are
     * returned as well
     * @return the summary as a String
     */
    public String toSummaryString(String title,
            boolean printComplexityStatistics) {

        StringBuffer text = new StringBuffer();

        if (printComplexityStatistics && m_NoPriors) {
            printComplexityStatistics = false;
            System.err.println("Priors disabled, cannot print complexity statistics!");
        }

        text.append(title + "\n");
        try {
            if (m_WithClass > 0) {
                if (m_ClassIsNominal) {

                    text.append("Correctly Classified Instances     ");
                    text.append(Utils.doubleToString(correct(), 12, 4) + "     "
                            + Utils.doubleToString(pctCorrect(),
                                    12, 4) + " %\n");
                    text.append("Incorrectly Classified Instances   ");
                    text.append(Utils.doubleToString(incorrect(), 12, 4) + "     "
                            + Utils.doubleToString(pctIncorrect(),
                                    12, 4) + " %\n");
                    text.append("Kappa statistic                    ");
                    text.append(Utils.doubleToString(kappa(), 12, 4) + "\n");

                    if (m_CostMatrix != null) {
                        text.append("Total Cost                         ");
                        text.append(Utils.doubleToString(totalCost(), 12, 4) + "\n");
                        text.append("Average Cost                       ");
                        text.append(Utils.doubleToString(avgCost(), 12, 4) + "\n");
                    }
                    if (printComplexityStatistics) {
                        text.append("K&B Relative Info Score            ");
                        text.append(Utils.doubleToString(KBRelativeInformation(), 12, 4)
                                + " %\n");
                        text.append("K&B Information Score              ");
                        text.append(Utils.doubleToString(KBInformation(), 12, 4)
                                + " bits");
                        text.append(Utils.doubleToString(KBMeanInformation(), 12, 4)
                                + " bits/instance\n");
                    }
                } else {
                    text.append("Correlation coefficient            ");
                    text.append(Utils.doubleToString(correlationCoefficient(), 12, 4)
                            + "\n");
                }
                if (printComplexityStatistics && m_ComplexityStatisticsAvailable) {
                    text.append("Class complexity | order 0         ");
                    text.append(Utils.doubleToString(SFPriorEntropy(), 12, 4)
                            + " bits");
                    text.append(Utils.doubleToString(SFMeanPriorEntropy(), 12, 4)
                            + " bits/instance\n");
                    text.append("Class complexity | scheme          ");
                    text.append(Utils.doubleToString(SFSchemeEntropy(), 12, 4)
                            + " bits");
                    text.append(Utils.doubleToString(SFMeanSchemeEntropy(), 12, 4)
                            + " bits/instance\n");
                    text.append("Complexity improvement     (Sf)    ");
                    text.append(Utils.doubleToString(SFEntropyGain(), 12, 4) + " bits");
                    text.append(Utils.doubleToString(SFMeanEntropyGain(), 12, 4)
                            + " bits/instance\n");
                }

                text.append("Mean absolute error                ");
                text.append(Utils.doubleToString(meanAbsoluteError(), 12, 4)
                        + "\n");
                text.append("Root mean squared error            ");
                text.append(Utils.
                        doubleToString(rootMeanSquaredError(), 12, 4)
                        + "\n");
                if (!m_NoPriors) {
                    text.append("Relative absolute error            ");
                    text.append(Utils.doubleToString(relativeAbsoluteError(),
                            12, 4) + " %\n");
                    text.append("Root relative squared error        ");
                    text.append(Utils.doubleToString(rootRelativeSquaredError(),
                            12, 4) + " %\n");
                }
                if (m_CoverageStatisticsAvailable) {
                    text.append("Coverage of cases (" + Utils.doubleToString(m_ConfLevel, 4, 2) + " level)     ");
                    text.append(Utils.doubleToString(coverageOfTestCasesByPredictedRegions(),
                            12, 4) + " %\n");
                    if (!m_NoPriors) {
                        text.append("Mean rel. region size (" + Utils.doubleToString(m_ConfLevel, 4, 2) + " level) ");
                        text.append(Utils.doubleToString(sizeOfPredictedRegions(), 12, 4) + " %\n");
                    }
                }
            }
            if (Utils.gr(unclassified(), 0)) {
                text.append("UnClassified Instances             ");
                text.append(Utils.doubleToString(unclassified(), 12, 4) + "     "
                        + Utils.doubleToString(pctUnclassified(),
                                12, 4) + " %\n");
            }
            text.append("Total Number of Instances          ");
            text.append(Utils.doubleToString(m_WithClass, 12, 4) + "\n");
            if (m_MissingClass > 0) {
                text.append("Ignored Class Unknown Instances            ");
                text.append(Utils.doubleToString(m_MissingClass, 12, 4) + "\n");
            }
        } catch (Exception ex) {
            // Should never occur since the class is known to be nominal
            // here
            System.err.println("Arggh - Must be a bug in Evaluation class");
        }

        return text.toString();
    }

}
