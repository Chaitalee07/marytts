/**
 * Copyright 2007 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * Permission is hereby granted, free of charge, to use and distribute
 * this software and its documentation without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of this work, and to
 * permit persons to whom this work is furnished to do so, subject to
 * the following conditions:
 * 
 * 1. The code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 * 2. Any modifications must be clearly marked as such.
 * 3. Original authors' names are not deleted.
 * 4. The authors' names are not used to endorse or promote products
 *    derived from this software without specific prior written
 *    permission.
 *
 * DFKI GMBH AND THE CONTRIBUTORS TO THIS WORK DISCLAIM ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS, IN NO EVENT SHALL DFKI GMBH NOR THE
 * CONTRIBUTORS BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL
 * DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */

package de.dfki.lt.mary.unitselection.adaptation.codebook;

/**
 * @author oytun.turk
 *
 */
public class WeightedCodebookMapperParams {
    public int numBestMatches; // Number of best matches in codebook
    
    public int distanceMeasure; // Distance measure for comparing source training and transformation features
    public static int LSF_INVERSE_HARMONIC_DISTANCE = 1;
    public static int LSF_EUCLIDEAN_DISTANCE = 2;
    public static int LSF_MAHALANOBIS_DISTANCE = 3;
    public static int DEFAULT_DISTANCE_MEASURE = LSF_INVERSE_HARMONIC_DISTANCE;
    
    public int weightingMethod; // Method for weighting best codebook matches
    public static int GAUSSIAN_HALF_WINDOW = 1;
    public static int EXPONENTIAL_HALF_WINDOW = 2;
    public static int TRIANGLE_HALF_WINDOW = 3;
    public static int DEFAULT_WEIGHTING_METHOD = GAUSSIAN_HALF_WINDOW;
    
    public double weightingSteepness; // Steepness of weighting function in range [MIN_STEEPNESS, MAX_STEEPNESS]
    public static double MIN_STEEPNESS = 0.0;
    public static double MAX_STEEPNESS = 1.0;
    public static double DEFAULT_WEIGHTING_STEEPNESS = 0.8;
    
    ////Mean and variance of a specific distance measure can be optionally kept in the follwoing
    // two parameters for z-normalization
    public double distanceMean; 
    public double distanceVariance;
    public static double DEFAULT_DISTANCE_MEAN = 0.0;
    public static double DEFAULT_DISTANCE_VARIANCE = 1.0;
    
    public WeightedCodebookMapperParams()
    {
        distanceMeasure = DEFAULT_DISTANCE_MEASURE;
        weightingMethod = DEFAULT_WEIGHTING_METHOD;
        weightingSteepness = DEFAULT_WEIGHTING_STEEPNESS;
        distanceMean = DEFAULT_DISTANCE_MEAN;
        distanceVariance = DEFAULT_DISTANCE_VARIANCE;
    }
    
    public WeightedCodebookMapperParams(WeightedCodebookMapperParams w)
    {
        distanceMeasure = w.distanceMeasure;
        weightingMethod = w.weightingMethod;
        weightingSteepness = w.weightingSteepness;
        distanceMean = w.distanceMean;
        distanceVariance = w.distanceVariance;
    }

}
