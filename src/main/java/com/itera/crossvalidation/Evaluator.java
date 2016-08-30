/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.crossvalidation;

import Learning.Learning;
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
    public int correct;
    public int incorrect;
    public int withClass;
    public String[] classNames;

    public void evaluateClassifier(Learning model, Data testData) {
        this.nClass = testData.getNumClasses();
        this.nDocs = testData.getNumDocs();

        classNames = new String[nClass];
        for (int i = 0; i < nClass; i++) {
            classNames[i] = testData.getClasses().get(i);
        }

        ArrayList<IndexValue> adjList;
        confusionMatrix = new double[nClass][nClass];
        for (int docId = 0; docId < nDocs; docId++) {
            adjList = testData.getAdjListDoc(docId);
            int realClassDoc = testData.getClassDocument(docId);
            double[] conf = model.classifyDocumentConficences(adjList);
            int predClassDoc = argmax(conf);
            confusionMatrix[realClassDoc][predClassDoc] += 1;

            if (realClassDoc != predClassDoc) {
                incorrect++;
            } else {
                correct++;
            }
            withClass += 1;
        }
    }

    public static int argmax(double[] a) {
        double max = a[0];
        int idx_max = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
                idx_max = i;
            }
        }
        return idx_max;
    }

    /**
     * Gets the number of instances correctly classified (that is, for which a
     * correct prediction was made). (Actually the sum of the weights of these
     * instances)
     *
     * @return the number of correctly classified instances
     */
    public final double correct() {

        return correct;
    }

    /**
     * Gets the percentage of instances correctly classified (that is, for which
     * a correct prediction was made).
     *
     * @return the percent of correctly classified instances (between 0 and 100)
     */
    public final double pctCorrect() {
        return 100 * correct / withClass;
    }

    /**
     * Gets the number of instances incorrectly classified (that is, for which
     * an incorrect prediction was made). (Actually the sum of the weights of
     * these instances)
     *
     * @return the number of incorrectly classified instances
     */
    public final double incorrect() {
        return incorrect;
    }

    /**
     * Gets the percentage of instances incorrectly classified (that is, for
     * which an incorrect prediction was made).
     *
     * @return the percent of incorrectly classified instances (between 0 and
     * 100)
     */
    public final double pctIncorrect() {
        return 100 * incorrect / withClass;
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
    public String toSummaryString() {

        StringBuffer text = new StringBuffer();

        text.append("Summary" + "\n");
        text.append("Correctly Classified Instances     ");
        text.append(correct() + "     " + pctCorrect() + " %\n");
        text.append("Incorrectly Classified Instances   ");
        text.append(incorrect() + "     " + pctIncorrect() + " %\n");
        text.append("Kappa statistic                    ");
        text.append(kappa() + "\n");

        text.append("Total Number of Instances          ");
        text.append(withClass + "\n");
        text.append("\n\n");
        text.append(toMatrixString("ConfusionMatrix"));
        return text.toString();
    }

    /**
     * Outputs the performance statistics as a classification confusion matrix.
     * For each class value, shows the distribution of predicted class values.
     *
     * @param title the title for the confusion matrix
     * @return the confusion matrix as a String
     * 
     */
    public String toMatrixString(String title) {

        StringBuffer text = new StringBuffer();
        char[] IDChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'};
        int IDWidth;
        boolean fractional = false;

        // Find the maximum value in the matrix
        // and check for fractional display requirement
        double maxval = 0;
        for (int i = 0; i < this.nClass; i++) {
            for (int j = 0; j < this.nClass; j++) {
                double current = this.confusionMatrix[i][j];
                if (current < 0) {
                    current *= -10;
                }
                if (current > maxval) {
                    maxval = current;
                }
                double fract = current - Math.rint(current);
                if (!fractional && ((Math.log(fract) / Math.log(10)) >= -2)) {
                    fractional = true;
                }
            }
        }

        IDWidth = 1 + Math.max((int) (Math.log(maxval) / Math.log(10)
                + (fractional ? 3 : 0)),
                (int) (Math.log(this.nClass)
                / Math.log(IDChars.length)));
        text.append(title).append("\n");
        for (int i = 0; i < this.nClass; i++) {
            if (fractional) {
                text.append(" ").append(num2ShortID(i, IDChars, IDWidth - 3))
                        .append("   ");
            } else {
                text.append(" ").append(num2ShortID(i, IDChars, IDWidth));
            }
        }
        text.append("   <-- classified as\n");
        for (int i = 0; i < this.nClass; i++) {
            for (int j = 0; j < this.nClass; j++) {
                text.append(" ").append(this.confusionMatrix[i][j]);
            }
            text.append(" | ").append(num2ShortID(i, IDChars, IDWidth))
                    .append(" = ").append(classNames[i]).append("\n");
        }
        return text.toString();
    }

    /**
     * Method for generating indices for the confusion matrix.
     *
     * @param num integer to format
     * @param IDChars	the characters to use
     * @param IDWidth	the width of the entry
     * @return the formatted integer as a string
     */
    protected String num2ShortID(int num, char[] IDChars, int IDWidth) {

        char ID[] = new char[IDWidth];
        int i;

        for (i = IDWidth - 1; i >= 0; i--) {
            ID[i] = IDChars[num % IDChars.length];
            num = num / IDChars.length - 1;
            if (num < 0) {
                break;
            }
        }
        for (i--; i >= 0; i--) {
            ID[i] = ' ';
        }

        return new String(ID);
    }

}
