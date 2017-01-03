/**
 * Copyright 2000-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package marytts.modules.acoustic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;
import marytts.datatypes.MaryXML;
import marytts.modeling.features.FeatureRegistry;
import marytts.modeling.features.FeatureVector;
import marytts.modeling.features.TargetFeatureComputer;
import marytts.modules.synthesis.Voice;
import marytts.util.dom.MaryDomUtils;

import marytts.modules.InternalModule;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.TreeWalker;

/**
 * Read a simple phone string and generate default acoustic parameters.
 *
 * @author Marc Schr&ouml;der
 */

public class TargetFeatureLister extends InternalModule {

	public TargetFeatureLister(MaryDataType outputType) {
		super("TargetFeatureLister", MaryDataType.ACOUSTPARAMS, outputType, null);
	}

	public TargetFeatureLister() {
		this(MaryDataType.TARGETFEATURES);
	}

	public MaryData process(MaryData d) throws Exception {
		Voice voice = d.getDefaultVoice();
		String features = d.getOutputParams();
		TargetFeatureComputer featureComputer;
		if (voice != null) {
			featureComputer = FeatureRegistry.getTargetFeatureComputer(voice, features);
		} else {
			Locale locale = d.getLocale();
			assert locale != null;
			featureComputer = FeatureRegistry.getTargetFeatureComputer(locale, features);
		}
		assert featureComputer != null : "Cannot get a feature computer!";
		Document doc = d.getDocument();
		// First, get the list of segments and boundaries in the current document
		TreeWalker tw = MaryDomUtils.createTreeWalker(doc, doc, MaryXML.PHONE, MaryXML.BOUNDARY);
		List<Element> segmentsAndBoundaries = new ArrayList<Element>();
		Element e;
		while ((e = (Element) tw.nextNode()) != null) {
			segmentsAndBoundaries.add(e);
		}
		// Second, construct targets
		String out = listTargetFeatures(featureComputer, segmentsAndBoundaries);
		MaryData result = new MaryData(outputType(), d.getLocale());
		result.setPlainText(out);
		return result;
	}

	/**
	 * For the given elements and using the given feature computer, create a string representation of the target features.
	 *
	 * @param featureComputer
	 *            featureComputer
	 * @param segmentsAndBoundaries
	 *            segmentsAndBoundaries
	 * @return a multi-line string.
	 */
	public String listTargetFeatures(TargetFeatureComputer featureComputer, List<Element> segmentsAndBoundaries) {
		String pauseSymbol = featureComputer.getPauseSymbol();
		List<Element> targets = overridableCreateTargetsWithPauses(segmentsAndBoundaries, pauseSymbol);
		// Third, compute the feature vectors and convert them to text
		String header = featureComputer.getAllFeatureProcessorNamesAndValues();
		StringBuilder text = new StringBuilder();
		StringBuilder bin = new StringBuilder();
		for (Element target : targets) {
			FeatureVector features = featureComputer.computeFeatureVector(target);
			text.append(featureComputer.toStringValues(features)).append("\n");
			bin.append(features.toString()).append("\n");
		}

		// Leave an empty line between sections:
		String out = header + "\n" + text + "\n" + bin;
		return out;
	}

	/**
	 * Return directly the targets, and set in each target its feature vector
	 *
	 * @param featureComputer
	 *            featureComputer
	 * @param segmentsAndBoundaries
	 *            segmentsAndBoundaries
	 * @return targets
	 */
	public List<FeatureVector> getListTargetFeatures(TargetFeatureComputer featureComputer, List<Element> segmentsAndBoundaries) {
		String pauseSymbol = featureComputer.getPauseSymbol();
		List<Element> targets = overridableCreateTargetsWithPauses(segmentsAndBoundaries, pauseSymbol);
        List<FeatureVector> target_features = new ArrayList<FeatureVector>();

		for (Element target : targets) {
            target_features.add(featureComputer.computeFeatureVector(target));
        }
		return target_features;
	}

	/**
	 * Access the code from within the our own code so that a subclass can override it. Use this rather than the public static
	 * method in local code.
	 *
	 * @param segmentsAndBoundaries
	 *            segmentsAndBoundaries
	 * @param pauseSymbol
	 *            pauseSymbol
	 * @return TargetFeatureLister
	 */
	protected List<Element> overridableCreateTargetsWithPauses(List<Element> segmentsAndBoundaries, String pauseSymbol) {
		return TargetFeatureLister.createTargetsWithPauses(segmentsAndBoundaries, pauseSymbol);
	}

	/**
	 * Create the list of targets from the segments to be synthesized Prepend and append pauses if necessary
	 *
	 * @param segmentsAndBoundaries
	 *            a list of MaryXML phone and boundary elements
	 * @param silenceSymbol
	 *            silenceSymbol
	 * @return a list of Target objects
	 */
	public static List<Element> createTargetsWithPauses(List<Element> segmentsAndBoundaries, String silenceSymbol) {
		List<Element> targets = new ArrayList<Element>();
		if (segmentsAndBoundaries.size() == 0)
			return segmentsAndBoundaries;

		Element last = segmentsAndBoundaries.get(segmentsAndBoundaries.size() - 1);
		if (!last.getTagName().equals(MaryXML.BOUNDARY)) {
			Element finalPause = MaryXML.createElement(last.getOwnerDocument(), MaryXML.BOUNDARY);
			Element token = (Element) MaryDomUtils.getAncestor(last, MaryXML.TOKEN);
			Element parent = (Element) token.getParentNode();
			parent.appendChild(finalPause);
			segmentsAndBoundaries.add(finalPause);
		}

		return segmentsAndBoundaries;
	}
}