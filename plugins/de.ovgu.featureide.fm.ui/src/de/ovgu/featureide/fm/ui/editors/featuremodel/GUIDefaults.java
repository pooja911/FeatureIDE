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
package de.ovgu.featureide.fm.ui.editors.featuremodel;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ovgu.featureide.fm.ui.FMUIPlugin;
import de.ovgu.featureide.fm.ui.properties.FMPropertyManager;

/**
 * Default colors, fonts, images and borders for the feature diagram.<br> It is recommended to use {@link FMPropertyManager} for colors and borders instead of
 * these values.
 *
 * @author Thomas Thuem
 */
public interface GUIDefaults {

	// general settings

	/**
	 * an Unicode font to be able to display constraints at the feature diagram correctly
	 */
	public static final Font DEFAULT_FONT = new Font(null, new FontData("Arial Unicode MS", 8, SWT.NORMAL));
	public static final Font DEFAULT_FONT_BOLD = new Font(null, new FontData("Arial Unicode MS", 8, SWT.BOLD));

	public static final Color DIAGRAM_BACKGROUND = ColorConstants.white;

	// concrete, hidden, dead and abstract features

	public static final Color FEATURE_FOREGROUND = GUIBasics.createColor(0.0, 0.0, 0.0);
	public static final Insets FEATURE_INSETS = new Insets(3, 6, 3, 6);// 4, 8, 4, 8

	public static final Color CONCRETE_BACKGROUND = GUIBasics.createColor(0.8, 0.8, 1.0);
	public static final Color CONCRETE_BORDER_COLOR = GUIBasics.createBorderColor(CONCRETE_BACKGROUND);
	public static final Border CONCRETE_BORDER = new LineBorder(CONCRETE_BORDER_COLOR, 1);
	public static final Color CONCRETE_SELECTED_BORDER_COLOR = CONCRETE_BORDER_COLOR;
	public static final Border CONCRETE_SELECTED_BORDER = new LineBorder(CONCRETE_SELECTED_BORDER_COLOR, 3);

	public static final Color ABSTRACT_BACKGROUND = GUIBasics.createColor(0.95, 0.95, 1.0);
	public static final Color ABSTRACT_BORDER_COLOR = GUIBasics.createBorderColor(ABSTRACT_BACKGROUND);
	public static final Border ABSTRACT_BORDER = new LineBorder(ABSTRACT_BORDER_COLOR, 1);
	public static final Color ABSTRACT_SELECTED_BORDER_COLOR = ABSTRACT_BORDER_COLOR;
	public static final Border ABSTRACT_SELECTED_BORDER = new LineBorder(ABSTRACT_SELECTED_BORDER_COLOR, 3);

	public static final Color HIDDEN_FOREGROUND = GUIBasics.createColor(0.4, 0.4, 0.4);
	public static final Color HIDDEN_BACKGROUND = GUIBasics.createColor(0.8, 0.8, 1.0);
	public static final Color HIDDEN_BORDER_COLOR = GUIBasics.createBorderColor(HIDDEN_BACKGROUND);
	public static final Border HIDDEN_BORDER = new LineBorder(HIDDEN_BORDER_COLOR, 1, Graphics.LINE_DASH);
	public static final Border HIDDEN_BORDER_LEGEND = new LineBorder(DIAGRAM_BACKGROUND, 1, SWT.LINE_DOT);
	public static final Border HIDDEN_SELECTED_BORDER = new LineBorder(HIDDEN_BORDER_COLOR, 3, Graphics.LINE_DASH);

	public static final Color DEAD_BACKGROUND = GUIBasics.createColor(1.0, 0.8, 0.8);
	public static final Color DEAD_BORDER_COLOR = GUIBasics.createBorderColor(DEAD_BACKGROUND);
	public static final Border DEAD_BORDER = new LineBorder(DEAD_BORDER_COLOR, 1);
	public static final Border DEAD_SELECTED_BORDER = new LineBorder(DEAD_BORDER_COLOR, 3);

	public static final Color INHERITED_BORDER_COLOR = GUIBasics.createColor(107, 228, 0);
	public static final Color IMPORTED_BORDER_COLOR = GUIBasics.createColor(128, 0, 128);
	public static final Color INTERFACED_BORDER_COLOR = GUIBasics.createColor(0, 191, 255);

	// connections and decorators

	public static final Color CONNECTION_FOREGROUND = GUIBasics.createColor(75, 85, 99);
	public static final Color NEW_CONNECTION_FOREGROUND = GUIBasics.createColor(0.4, 1.0, 0.4);
	public static final Color VOID_CONNECTION_FOREGROUND = GUIBasics.createColor(1.0, 0.4, 0.4);

	public static final Color DECORATOR_FOREGROUND = CONNECTION_FOREGROUND;
	public static final Color DECORATOR_BACKGROUND = DIAGRAM_BACKGROUND;
	public static final int SOURCE_ANCHOR_DIAMETER = 9;

	public static final int TARGET_ANCHOR_DIAMETER = 25;
	public static final int TARGET_ANCHOR_DIAMETER_VERTICAL = 38;

	// constraints

	public static final Color CONSTRAINT_BORDER_UNSELECTED_COLOR = CONNECTION_FOREGROUND;
	public static final Color CONSTRAINT_BORDER_SELECTED_COLOR = CONNECTION_FOREGROUND;

	// cross-tree constraints

	public static final Color CONSTRAINT_FOREGROUND = CONNECTION_FOREGROUND;
	public static final Insets CONSTRAINT_INSETS = new Insets(2, 8, 2, 8);
	public static final Color CONSTRAINT_BACKGROUND = GUIBasics.createColor(1.0, 1.0, 1.0);
	public static final Color IMPLICIT_CONSTRAINT = GUIBasics.createColor(1.0, 0.0, 0.0);
	public static final Border IMPLICIT_CONSTRAINT_BORDER = new LineBorder(IMPLICIT_CONSTRAINT, 2);

	// false constraints

	public static final Color WARNING_BACKGROUND = GUIBasics.createColor(1.0, 1.0, 0.6);
	public static final Color ERROR_BACKGROUND = DEAD_BACKGROUND;

	// cell editor for renaming features / editing constraints

	public static final Insets CELL_EDITOR_INSETS = new Insets(0, 4, 0, 4);
	public static final Dimension CELL_EDITOR_MINSIZE = new Dimension(50, 5);

	// space between features for layouting

	public static final int LAYOUT_MARGIN_X = 20;
	public static final int LAYOUT_MARGIN_Y = 40;
	public static final int FEATURE_SPACE_X = 5;
	public static final int FEATURE_SPACE_Y = 30 + 20;
	public static final int CONSTRAINT_SPACE_Y = 5 + 20;

	// space between text and bounds for collapsed decorator
	public static final int COLLAPSED_DECORATOR_X_SPACE = 3;
	public static final int COLLAPSED_DECORATOR_Y_SPACE = 2;
	public static final int COLLAPSED_DECORATOR_FEATURE_SPACE = -1;
	public static final int COLLAPSED_DECORATOR_ARC_RADIUS = 5;

	// legend
	public static final int LEGEND_WIDTH = 105;
	public static final Color LEGEND_FOREGROUND = CONNECTION_FOREGROUND;
	public static final Color LEGEND_BACKGROUND = DIAGRAM_BACKGROUND;
	public static final Color LEGEND_BORDER_COLOR = LEGEND_FOREGROUND;
	public static final Color LEGEND_MOVING_FEEDBACK_VALID = GUIBasics.createColor(0, 120, 0);
	public static final Color LEGEND_MOVING_FEEDBACK_INVALID = GUIBasics.createColor(120, 0, 0);
	public static final int LEGEND_MOVING_FEEDBACK_BORDER_WIDTH = 2;
	public static final int LEGEND_MOVING_FEEDBACK_ALPHA = 35;
	public static final Border LEGEND_BORDER = new LineBorder(LEGEND_BORDER_COLOR, 1);

	/*
	 * All images should be declared here, so an image can not be created twice.
	 */
	public static final Image IMAGE_UNDEFINED = FMUIPlugin.getImage("undefined.png");
	public static final Image IMAGE_UNDEFINED_DARK = FMUIPlugin.getImage("undefined_dark.png");
	public static final Image IMAGE_SELECTED = FMUIPlugin.getImage("selected.png");
	public static final Image IMAGE_SELECTED_DARK = FMUIPlugin.getImage("selected_dark.png");
	public static final Image IMAGE_DESELECTED = FMUIPlugin.getImage("deselected.png");
	public static final Image IMAGE_DESELECTED_DARK = FMUIPlugin.getImage("deselected_dark.png");
	public static final Image IMAGE_ASELECTED = FMUIPlugin.getImage("aselected.png");
	public static final Image IMAGE_ASELECTED_DARK = FMUIPlugin.getImage("aselected_dark.png");
	public static final Image IMAGE_ADESELECTED = FMUIPlugin.getImage("adeselected.png");
	public static final Image IMAGE_ADESELECTED_DARK = FMUIPlugin.getImage("adeselected_dark.png");

	public static final Image HELP_IMAGE = FMUIPlugin.getImage("help.gif");
	public static final Image ERROR_IMAGE = FMUIPlugin.getImage("icon_error.gif");
	public static final Image BANNER_IMAGE = FMUIPlugin.getImage("title_banner.gif");
	public static final Image WARNING_IMAGE = FMUIPlugin.getImage("message_warning.gif");

	public static final Image OPERATOR_SYMBOL = FMUIPlugin.getImage("operator_symbol.gif");
	public static final Image FEATURE_SYMBOL = FMUIPlugin.getImage("FeatureIconSmall.ico");

	public static final Image IMG_OPTIONAL = FMUIPlugin.getImage("optional.png");
	public static final Image IMG_OPTIONAL_DARK = FMUIPlugin.getImage("optional_dark.png");
	public static final Image IMG_MANDATORY = FMUIPlugin.getImage("mandatory.png");
	public static final Image IMG_MANDATORY_DARK = FMUIPlugin.getImage("mandatory_dark.png");
	public static final Image IMG_OR = FMUIPlugin.getImage("or.png");
	public static final Image IMG_OR_DARK = FMUIPlugin.getImage("or_dark.png");
	public static final Image IMG_XOR = FMUIPlugin.getImage("xor.png");
	public static final Image IMG_XOR_DARK = FMUIPlugin.getImage("xor_dark.png");

	public static final Image PLUS_IMAGE = FMUIPlugin.getImage("plus.gif");
	public static final Image MINUS_IMAGE = FMUIPlugin.getImage("minus.gif");
	public static final Image ZERO_IMAGE = FMUIPlugin.getImage("zero.gif");
	public static final Image PLUS_MINUS_IMAGE = FMUIPlugin.getImage("plusminus.gif");

	public static final Image DEFAULT_IMAGE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	public static final Image ERROR_IMAGE_TSK = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

	public static final Image IMAGE_EMPTY = FMUIPlugin.getImage("empty.png");

	public static final Image FM_ERROR = FMUIPlugin.getImage("fmerror.png");
	public static final Image FM_WARNING = FMUIPlugin.getImage("fmwarning.png");
	public static final Image FM_INFO = FMUIPlugin.getImage("fminfo.png");

	public static final Image IMAGE_EXPAND = FMUIPlugin.getImage("expand.gif");
	public static final Image IMAGE_COLLAPSE = FMUIPlugin.getImage("collapse.gif");
	public static final Image IMAGE_AUTOEXPAND_GROUP = FMUIPlugin.getImage("tree02.png");
	public static final Image IMAGE_AUTOEXPAND_GROUP_DARK = FMUIPlugin.getImage("tree02_dark.png");
	public static final Image IMAGE_NEXT = FMUIPlugin.getImage("arrow_down.png");
	public static final Image IMAGE_NEXT_DARK = FMUIPlugin.getImage("arrow_down_dark.png");
	public static final Image IMAGE_PREVIOUS = FMUIPlugin.getImage("arrow_up.png");
	public static final Image IMAGE_PREVIOUS_DARK = FMUIPlugin.getImage("arrow_up_dark.png");
	public static final Image IMAGE_RESOLVE = FMUIPlugin.getImage("synch_toc_nav.gif");
	public static final Image IMAGE_INCREASE_FONT = FMUIPlugin.getImage("increase-font-size.png");
	public static final Image IMAGE_INCREASE_FONT_DARK = FMUIPlugin.getImage("increase-font-size_dark.png");
	public static final Image IMAGE_DECREASE_FONT = FMUIPlugin.getImage("decrease-font-size.png");
	public static final Image IMAGE_DECREASE_FONT_DARK = FMUIPlugin.getImage("decrease-font-size_dark.png");
	public static final ImageDescriptor IMAGE_EXPORT_AS = FMUIPlugin.getDefault().getImageDescriptor("icons/export_wiz.gif");
}
