/*
* generated by Xtext
*/
package org.xtext.example.ui.outline;

import org.eclipse.xtext.ui.editor.outline.actions.DefaultContentOutlineNodeAdapterFactory;

public class DJOutlineNodeAdapterFactory extends DefaultContentOutlineNodeAdapterFactory {

	private static final Class<?>[] types = { 
		// provide list of classes to adapt to, e.g.:
		// Entity.class
		// Service.class
	};

	@Override
	public Class<?>[] getAdapterList() {
		return types;
	}

}

