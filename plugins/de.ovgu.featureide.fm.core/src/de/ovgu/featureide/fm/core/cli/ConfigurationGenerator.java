/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.core.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import de.ovgu.featureide.fm.core.analysis.cnf.CNF;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.analysis.cnf.SolutionList;
import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.AllConfigurationGenerator;
import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.IConfigurationGenerator;
import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.PairWiseConfigurationGenerator;
import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.RandomConfigurationGenerator;
import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.SPLCAToolConfigurationGenerator;
import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.twise.TWiseConfigurationGenerator;
import de.ovgu.featureide.fm.core.io.ProblemList;
import de.ovgu.featureide.fm.core.io.csv.ConfigurationListFormat;
import de.ovgu.featureide.fm.core.io.dimacs.DIMACSFormatCNF;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.job.LongRunningWrapper;
import de.ovgu.featureide.fm.core.job.monitor.ConsoleMonitor;

/**
 * Command line interface for several functions of FeatureIDE.
 *
 * @author Sebastian Krieter
 */
public class ConfigurationGenerator implements ICLIFunction {

	private String algorithm;
	private Path outputFile;
	private Path fmFile;
	private int t;
	private int m;
	private int limit;

	@Override
	public String getName() {
		return "genconfig";
	}

	@Override
	public void run(List<String> args) {
		parseArguments(args);
		final CNF cnf = new CNF();
		final ProblemList lastProblems = FileHandler.load(fmFile, cnf, new DIMACSFormatCNF());
		if (lastProblems.containsError()) {
			throw new IllegalArgumentException(lastProblems.getErrors().get(0).error);
		} else {
			IConfigurationGenerator generator = null;
			if (algorithm == null) {
				throw new IllegalArgumentException("No algorithm specified!");
			}
			switch (algorithm.toLowerCase()) {
			case "icpl": {
				generator = new SPLCAToolConfigurationGenerator(cnf, "ICPL", t, limit);
				break;
			}
			case "chvatal": {
				generator = new SPLCAToolConfigurationGenerator(cnf, "Chvatal", t, limit);
				break;
			}
			case "incling": {
				generator = new PairWiseConfigurationGenerator(cnf, limit);
				break;
			}
			case "yasa": {
				generator = new TWiseConfigurationGenerator(cnf, t);
				((TWiseConfigurationGenerator) generator).setIterations(m);
				break;
			}
			case "random": {
				generator = new RandomConfigurationGenerator(cnf, limit);
				break;
			}
			case "all": {
				generator = new AllConfigurationGenerator(cnf, limit);
				break;
			}
			default:
				throw new IllegalArgumentException("No algorithm specified!");
			}
			final List<LiteralSet> result = LongRunningWrapper.runMethod(generator, new ConsoleMonitor<>());
			FileHandler.save(outputFile, new SolutionList(cnf.getVariables(), result), new ConfigurationListFormat());
		}
	}

	private void resetArguments() {
		algorithm = null;
		outputFile = null;
		fmFile = null;
		t = 0;
		m = 1;
		limit = Integer.MAX_VALUE;
	}

	private void parseArguments(List<String> args) {
		resetArguments();
		for (final Iterator<String> iterator = args.iterator(); iterator.hasNext();) {
			final String arg = iterator.next();
			if (arg.startsWith("-")) {
				switch (arg.substring(1)) {
				case "a": {
					algorithm = getArgValue(iterator, arg);
					break;
				}
				case "o": {
					outputFile = Paths.get(getArgValue(iterator, arg));
					break;
				}
				case "fm": {
					fmFile = Paths.get(getArgValue(iterator, arg));
					break;
				}
				case "t": {
					t = Integer.parseInt(getArgValue(iterator, arg));
					break;
				}
				case "m": {
					m = Integer.parseInt(getArgValue(iterator, arg));
					break;
				}
				case "l": {
					limit = Integer.parseInt(getArgValue(iterator, arg));
					break;
				}
				default: {
					throw new IllegalArgumentException(arg);
				}
				}
			} else {
				throw new IllegalArgumentException(arg);
			}
		}
	}

	private String getArgValue(final Iterator<String> iterator, final String arg) {
		if (iterator.hasNext()) {
			return iterator.next();
		} else {
			throw new IllegalArgumentException("No value specified for " + arg);
		}
	}

}