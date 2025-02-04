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
package de.ovgu.featureide.fm.ui.editors.featuremodel.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;

import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.event.FeatureIDEEvent;
import de.ovgu.featureide.fm.core.base.event.FeatureIDEEvent.EventType;
import de.ovgu.featureide.fm.core.explanations.fm.FeatureModelExplanation;
import de.ovgu.featureide.fm.core.explanations.fm.MultipleAnomaliesExplanation;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.ui.editors.FeatureDiagramViewer;
import de.ovgu.featureide.fm.ui.editors.IGraphicalFeatureModel;
import de.ovgu.featureide.fm.ui.editors.featuremodel.editparts.FeatureEditPart;
import de.ovgu.featureide.fm.ui.editors.featuremodel.operations.FeatureModelOperationWrapper;
import de.ovgu.featureide.fm.ui.editors.featuremodel.operations.FocusOnAnomaliesOperation;
import de.ovgu.featureide.fm.ui.editors.featuremodel.operations.FocusOnExplanationOperation;

/**
 * The {@link FocusOnAnomaliesAction} collapses all features but those affected by a given feature model anomaly type. It also produces the combined
 * {@link MultipleAnomaliesExplanation} for the affected features.
 *
 * @author Benedikt Jutz
 */
public abstract class FocusOnAnomaliesAction extends Action {

	/**
	 * <code>viewer</code> contains the diagram viewer with the graphical feature model whose features we expand or collapse.
	 */
	protected final FeatureDiagramViewer viewer;
	/**
	 * <code>fm</code> contains the actual graphical model.
	 */
	protected final IGraphicalFeatureModel fm;

	/**
	 * Creates a new {@link FocusOnAnomaliesAction}
	 *
	 * @param viewer - {@link FeatureDiagramViewer}
	 * @param name - {@link String}
	 */
	protected FocusOnAnomaliesAction(FeatureDiagramViewer viewer, String name) {
		super(name);
		this.viewer = viewer;
		fm = viewer.getGraphicalFeatureModel();
	}

	/**
	 * Executes the appropriate {@link FocusOnAnomaliesOperation} to focus on the correct features, then gets the necessary explanations and focuses on the
	 * feature model root to show them.
	 *
	 * @see {@link Action#run}
	 */
	@Override
	public void run() {
		final IFeatureModel featureModel = fm.getFeatureModelManager().getVarObject();
		// First focus on the anomalous features...
		final FocusOnAnomaliesOperation anomalyFocusOperation = getAnomalyFocusOperation();
		final boolean hasAnomalies = anomalyFocusOperation.findAnomalousElements();
		FeatureModelOperationWrapper.run(anomalyFocusOperation);

		// ... then get an explanation from them. Configure the anomaly types to get explanations for before that.
		// Also don't create explanations if no anomalies exist, and handle void feature models.
		final FeatureModelAnalyzer analyzer = FeatureModelManager.getInstance(featureModel).getVariableFormula().getAnalyzer();
		// The subject of explanation is either an IFeatureModel or IFeature, who only share the IEventManager type. It is pointless to parameterize explanation
		// in this context.
		@SuppressWarnings("rawtypes")
		final FeatureModelExplanation explanation;
		if (hasAnomalies) {
			analyzer.setMultipleAnomalyExplanationTypes(anomalyFocusOperation.featureAnomalies, anomalyFocusOperation.constraintAnomalies);
			if (analyzer.isValid(null)) {
				explanation = analyzer.getMultipleAnomaliesExplanation();
			} else {
				explanation = analyzer.getVoidFeatureModelExplanation();
			}
			FeatureModelOperationWrapper.run(new FocusOnExplanationOperation(fm, explanation));
		} else {
			explanation = null;
		}

		// Should an explanation exist, it belongs to the root feature, so we need to select it.
		if (explanation != null) {
			final IFeature root = featureModel.getStructure().getRoot().getFeature();
			final FeatureEditPart rootPart = (FeatureEditPart) viewer.getEditPartRegistry().get(fm.getGraphicalFeature(root));
			viewer.getSelectionManager().deselectAll();
			rootPart.setSelected(EditPart.SELECTED_PRIMARY);
			viewer.getSelectionManager().appendSelection(rootPart);
		}
		// Finally, request a redraw of the the feature diagram, thus updating the layout and shown anomalies.
		fm.getFeatureModelManager().getVarObject().fireEvent(FeatureIDEEvent.getDefault(EventType.REDRAW_DIAGRAM));
	}

	/**
	 * Returns the appropriate {@link FocusOnAnomaliesAction} that specifies on which anomalies we focus on.
	 *
	 * @return new {@link FocusOnAnomaliesOperation}
	 */
	protected abstract FocusOnAnomaliesOperation getAnomalyFocusOperation();
}
