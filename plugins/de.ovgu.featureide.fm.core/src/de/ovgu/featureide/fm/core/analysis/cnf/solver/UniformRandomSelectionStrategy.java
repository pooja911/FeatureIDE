/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2017  FeatureIDE team, University of Magdeburg, Germany
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
package de.ovgu.featureide.fm.core.analysis.cnf.solver;

import static org.sat4j.core.LiteralsUtils.negLit;
import static org.sat4j.core.LiteralsUtils.posLit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.minisat.core.IPhaseSelectionStrategy;

/**
 * Uses a sample of configurations to achieve a phase selection that corresponds to a uniform distribution of configurations in the configuration space.
 *
 * @author Sebastian Krieter
 */
public class UniformRandomSelectionStrategy implements IPhaseSelectionStrategy {

	private static final long serialVersionUID = 1L;

	public static final Random RAND = new Random(123456789);

	private final LinkedList<int[]> usedSamples = new LinkedList<>();
	private final LinkedList<int[]> notUsedSamples = new LinkedList<>();

	private final int[] model;
	private final int[] ratio;

	public UniformRandomSelectionStrategy(List<int[]> sample) {
		usedSamples.addAll(sample);
		model = new int[sample.get(0).length];
		ratio = new int[sample.get(0).length];

		for (final int[] solution : usedSamples) {
			for (int i = 0; i < solution.length; i++) {
				if (solution[i] > 0) {
					ratio[i]++;
				}
			}
		}
	}

	public void undo(int var) {
		updateRatioUnset(model[var - 1]);
		model[var - 1] = 0;
	}

	@Override
	public void assignLiteral(int p) {
		final int literal = LiteralsUtils.toDimacs(p);
		model[LiteralsUtils.var(p) - 1] = literal;
		updateRatioSet(literal);
	}

	@Override
	public void init(int nlength) {}

	@Override
	public void init(int var, int p) {}

	@Override
	public int select(int var) {
		return (RAND.nextInt(usedSamples.size()) < ratio[var - 1]) ? posLit(var) : negLit(var);
	}

	@Override
	public void updateVar(int p) {}

	@Override
	public void updateVarAtDecisionLevel(int q) {}

	@Override
	public String toString() {
		return "uniform random phase selection";
	}

	private void updateRatioUnset(int literal) {
		for (final Iterator<int[]> iterator = notUsedSamples.iterator(); iterator.hasNext();) {
			final int[] solution = iterator.next();
			if (solution[Math.abs(literal) - 1] == -literal) {
				iterator.remove();
				usedSamples.addFirst(solution);
				for (int j = 0; j < solution.length; j++) {
					if (solution[j] > 0) {
						ratio[j]++;
					}
				}
			}
		}
	}

	private void updateRatioSet(int literal) {
		for (final Iterator<int[]> iterator = usedSamples.iterator(); iterator.hasNext();) {
			final int[] solution = iterator.next();
			if (solution[Math.abs(literal) - 1] == -literal) {
				iterator.remove();
				notUsedSamples.addFirst(solution);
				for (int j = 0; j < solution.length; j++) {
					if (solution[j] > 0) {
						ratio[j]--;
					}
				}
			}
		}
	}

}