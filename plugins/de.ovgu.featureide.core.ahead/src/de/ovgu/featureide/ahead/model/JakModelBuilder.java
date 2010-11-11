/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2010  FeatureIDE Team, University of Magdeburg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.ahead.model;

import java.util.LinkedList;

import mixin.AST_Modifiers;
import mixin.AST_ParList;
import mixin.AST_Program;
import mixin.AST_TypeName;
import mixin.AstCursor;
import mixin.AstNode;
import mixin.AstToken;
import mixin.DecNameDim;
import mixin.FldVarDec;
import mixin.MethodDcl;
import mixin.MthDector;

import org.eclipse.core.resources.IFile;

import de.ovgu.featureide.ahead.AheadCorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.core.fstmodel.IFSTModel;
import de.ovgu.featureide.core.fstmodel.IField;


/**
 * This builder builds the JakProjectModel, by extracting features, 
 * methods and fields from classes to build. 
 * 
 * @author Tom Brosch
 * @author Constanze Adler
 */
public class JakModelBuilder {

	private JakModel model;

	public JakModelBuilder(IFeatureProject featureProject) {
		IFSTModel oldModel = featureProject.getFSTModel();
		if (oldModel != null)
			oldModel.markObsolete();

		model = new JakModel(featureProject.getProjectName());
		featureProject.setFSTModel(model);
	}

	/**
	 * Adds a class to the jak project model
	 * 
	 * @param className
	 *            Name of the class
	 * @param sources
	 *            source files that were composed to build this class
	 * @param composedASTs
	 *            composed ahead ASTs during the composition step
	 * @param ownASTs
	 *            ahead ASTs of each source file without composing
	 */
	public void addClass(String className, LinkedList<IFile> sources,
			AST_Program[] composedASTs, AST_Program[] ownASTs) {
		Class currentClass = null;
		// Parse the name and the ownASTs to know to which IFiles this class
		// file belongs to

		
		// This class doesnt exist, than create a new class object
		if (model.classes.containsKey(className)) {
			currentClass = model.classes.get(className);
		} else {
			currentClass = new Class(className);
			model.classes.put(className, currentClass);
		}

		for (int i = 0; i < sources.size(); i++) {
			if (!model.classesMap.containsKey(sources.get(i)))
				model.classesMap.put(sources.get(i), currentClass);
		}
		try {
			updateAst(currentClass, sources, composedASTs, ownASTs);
		} catch (Throwable e) {
			AheadCorePlugin.getDefault().logError(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ovgu.featureide.core.jakprojectmodel.IClass#updateAst(java.util.Vector,
	 * mixin.AST_Program[], mixin.AST_Program[])
	 */
	public void updateAst(Class currentClass, LinkedList<IFile> sources,
			AST_Program[] composedASTs, AST_Program[] ownASTs) {
		IFile currentFile = null;
		AstCursor c = new AstCursor();

		
		
		Method newMethod = null;
		LinkedList<Field> newFields = null;
		int lineNumber = -1;
		currentClass.methods.clear();
		currentClass.fields.clear();

		for (int i = 0; i < sources.size(); i++) {
			currentFile = sources.get(i);
			
		
			// Parse the tree and every method that was found add it to
			// both vectors

			for (c.First(ownASTs[i]); c.More(); c.PlusPlus()) {
				if (c.node instanceof MethodDcl) {

					// Get the method from the Ast
					// This method was not new in this file, than just update
					// the own and available flag
					// Put it back to the methodsMap

					newMethod = getMethod((MethodDcl) c.node);
					if (currentClass.methods.containsKey(newMethod
							.getIdentifier())) {
						newMethod = currentClass.methods.get(newMethod
								.getIdentifier());
						currentClass.methods.remove(newMethod.getIdentifier());
					}

					lineNumber = getLineNumber(c.node);
					newMethod.setOwn(currentFile);
					newMethod.setLineNumber(currentFile, lineNumber);
					currentClass.methods.put(newMethod.getIdentifier(),
							newMethod);

					c.Sibling();
				}
				if (c.node instanceof FldVarDec) {
					newFields = getFields((FldVarDec) c.node);
					for (IField field : newFields) {

						if (currentClass.fields.containsKey(field
								.getIdentifier())) {
							field = currentClass.fields.get(field
									.getIdentifier());
							currentClass.fields.remove(field.getIdentifier());
						}

						field.setOwn(currentFile);
						field.setLineNumber(currentFile, getLineNumber(c.node));
						currentClass.fields.put(field.getIdentifier(), field);
					}
					c.Sibling();
				}
				
		
			}
			Feature f = getFeature(currentClass, currentFile);
			if (!model.features.containsKey(f.getName())) {
				model.features.put(f.getName(), f);
			}
			else{
				model.features.get(f.getName()).classes.put(currentClass.getName(),currentClass);
			}

		}
	}

	private int getLineNumber(AstNode node) {
		AstCursor cur = new AstCursor();
		for (cur.First(node); cur.More(); cur.PlusPlus())
			if (cur.node != null && cur.node.tok != null
					&& cur.node.tok[0] != null)
				return ((AstToken) cur.node.tok[0]).lineNum();
		return -1;
	}

	private Method getMethod(MethodDcl methDcl) {
		AstCursor cur = new AstCursor();
		String type = "";
		String name = "";
		String modifiers = "";
		LinkedList<String> paramTypes = new LinkedList<String>();

		// Travers the Subtree and catch the name of the method,
		// its return type and parameter types;

		for (cur.First(methDcl); cur.More(); cur.PlusPlus()) {
			if (cur.node instanceof MthDector) {

				// Get the name of the Method
				name = ((MthDector) cur.node).getQName().GetName();

				// Travers the list of parameters if the method has some
				AST_ParList list = ((MthDector) cur.node).getAST_ParList();
				if (list != null) {
					AstCursor listCur = new AstCursor();
					for (listCur.First(list); listCur.More(); listCur
							.PlusPlus()) {
						if (listCur.node instanceof AST_TypeName) {
							paramTypes.add(((AST_TypeName) listCur.node)
									.GetName());
						}
					}
				}

				cur.Sibling(); // This subtree was complete analysed so we can
				// skip it
			} else if (cur.node instanceof AST_TypeName) {

				// Get the return type of the method
				type = ((AST_TypeName) cur.node).GetName();
				cur.Sibling(); // This subtree was complete analysed so we can
				// skip it
			}
			else if (cur.node instanceof AST_Modifiers) {

				// Get the modifiers of the method
				modifiers = ((AST_Modifiers) cur.node).toString().trim();
				cur.Sibling(); // This subtree was complete analysed so we can
				// skip it
				
			}

		}

		return new Method(name, paramTypes, type, modifiers);
	}

	private LinkedList<Field> getFields(FldVarDec fieldDcl) {
		AstCursor cur = new AstCursor();
		String type = "";
		String modifiers = "";

		LinkedList<Field> fields = new LinkedList<Field>();

		// Travers the Subtree and get the type and
		// all variable qualifiers

		for (cur.First(fieldDcl); cur.More(); cur.PlusPlus()) {

			if (cur.node instanceof AST_TypeName) {

				// Get the return type of the method
				type = ((AST_TypeName) cur.node).GetName();
				cur.Sibling(); // This subtree was complete analysed so we can
				// skip it
			}
			else if (cur.node instanceof AST_Modifiers) {

				// Get modifiers of the field
				modifiers = ((AST_Modifiers) cur.node).toString().trim();
				cur.Sibling(); // This subtree was complete analysed so we can
				// skip it
			}
			else if (cur.node instanceof DecNameDim) {
				// to do: find out the dimension more correctly
				fields.add(new Field(((DecNameDim) cur.node).getQName()
						.GetName(), type, 0, modifiers));
			}

		}

		return fields;
	}
	
	private Feature getFeature(Class currentClass, IFile currentFile){
		String featureName = currentFile.getFullPath().segment(currentFile.getFullPath().segmentCount()-2);
		Feature f = new Feature(featureName);
		f.classes.put(currentClass.getName(), currentClass);
		f.classes.get(currentClass.getName()).setJakfile(currentFile);
		return f;
	}

	public void clearFeatures() {
		model.features.clear();
	}
}
