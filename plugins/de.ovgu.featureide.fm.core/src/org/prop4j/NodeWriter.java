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
package org.prop4j;

/**
 * Converts a propositional node to a String object.
 *
 * @author Thomas Thuem
 * @author Timo G&uuml;nther
 */
public class NodeWriter {

	/**
	 * The type of notation of the formula.
	 *
	 * @author Timo G&uuml;nther
	 */
	public enum Notation {
		/**
		 * <p> The infix notation. Operators are written between operands where possible.
		 *
		 * <p> Examples: <ul> <li><em>A &amp; B &amp; C</em></li> <li><em>A =&gt; B &lt;=&gt; -A | B</em></li> <li><em>atleast2(A, B, C, D) &amp; atmost3(A, B,
		 * C, D)</em></li> </ul>
		 */
		INFIX,
		/**
		 * <p> The prefix notation. Operators are written before the operands.
		 *
		 * <p> Examples: <ul> <li><em>(&amp; A B C)</em></li> <li><em>(&lt;=&gt; (=&gt; A B) (| (- A) B)</em></li> <li><em>(&amp; (atleast2 A B C D) (atmost3 A
		 * B C D))</em></li> </ul>
		 */
		PREFIX,
		/**
		 * <p> The postfix notation. Operators are written after the operands.
		 *
		 * <p> Examples: <ul> <li><em>(A B C &amp;)</em></li> <li><em>((A B =&gt;) ((A -) B |) &lt;=&gt;)</em></li> <li><em>((A B C D atleast2) (A B C D
		 * atmost3) &gt;)</em></li> </ul>
		 */
		POSTFIX,
	}

	public enum LineFormat {
		/**
		 * <p> Return a single line without any line breaks. </p>
		 */
		SINGLE,
		/**
		 * <p> Return multiple lines with indentation corresponding to nesting depth of the nodes. </p>
		 */
		TREE,
	}

	/** Denotes an unsupported symbol. */
	public static final String noSymbol = "?";
	/**
	 * Symbols for a logical representation. These are best used for displaying to the user due to brevity and beauty. Since they consist of unwieldy Unicode
	 * characters, do not use them for editing or serialization; in these cases, instead use {@link #textualSymbols long} or {@link #shortSymbols short textual
	 * symbols} respectively.
	 */
	public static final String[] logicalSymbols = new String[] { "\u00AC", "\u2227", "\u2228", "\u21D2", "\u21D4", ", ", "choose", "atleast", "atmost" };
	/**
	 * Symbols for a long textual representation. These are best used for editing by the user due to simplicity and ease of handling. Use {@link #logicalSymbols
	 * logical symbols} for displaying to the user and {@link #shortSymbols short textual symbols} for serialization.
	 */
	public static final String[] textualSymbols = new String[] { "not", "and", "or", "implies", "iff", ", ", "choose", "atleast", "atmost" };
	/**
	 * Symbols for a short textual representation. Best used for serialization since they fall in the ASCII range but are still relatively short. Use
	 * {@link #logicalSymbols} for displaying to the user and {@link #textualSymbols long textual symbols} for editing by the user.
	 */
	public static final String[] shortSymbols = new String[] { "-", "&", "|", "=>", "<=>", ", ", "choose", "atleast", "atmost" };
	/**
	 * Symbols for a representation like in Java. These are inherently incomplete and should only be used if absolutely necessary.
	 */
	public static final String[] javaSymbols = new String[] { "!", "&&", "||", noSymbol, "==", ", ", noSymbol, noSymbol, noSymbol };

	public static final String[] latexSymbols =
		new String[] { "\\lnot", "\\land", "\\lor", "\\Rightarrow", "\\Leftrightarrow", ", ", "choose", "atleast", "atmost" };

	/** The propositional node to convert. */
	private final Node root;

	/** The symbols for the operations: not, and, or, implies, iff, separator, choose, atleast, atmost. */
	private String[] symbols = shortSymbols;
	/** The notation to use. */
	private Notation notation = Notation.INFIX;
	/** The line format to use. */
	private LineFormat lineFormat = LineFormat.SINGLE;
	/** If true, this writer will always place brackets, even if they are semantically irrelevant. */
	private boolean enforceBrackets = false;
	/** If true, this writer will enquote variables if they contain whitespace. */
	private boolean enquoteWhitespace = false;
	/** If true, this writer will always enquote variables. */
	private boolean enquoteAlways = false;

	/**
	 * Constructs a new instance of this class with the given node to transform. By default, the set of short symbols and infix notation are used, brackets are
	 * only placed if necessary, and variables containing whitespace will not be enquoted.
	 *
	 * @param root node to transform; not null
	 */
	public NodeWriter(Node root) {
		this.root = root;
	}

	/**
	 * Sets the symbols to use for the operations. By index, these are: <ol start="0"> <li>{@link Not}</li> <li>{@link And}</li> <li>{@link Or}</li>
	 * <li>{@link Implies}</li> <li>{@link Equals}</li> <li>the separator joining the operands of the following operations</li> <li>{@link Choose}</li>
	 * <li>{@link AtLeast}</li> <li>{@link AtMost}</li> </ol> By default, the set of short symbols is used.
	 *
	 * @param symbols symbols for the operations; not null
	 * @see #logicalSymbols
	 * @see #textualSymbols
	 * @see #shortSymbols
	 * @see #javaSymbols
	 */
	public void setSymbols(String[] symbols) {
		this.symbols = symbols;
	}

	/**
	 * Returns the symbols to use for the operations.
	 *
	 * @return the symbols to use for the operations
	 */
	protected String[] getSymbols() {
		return symbols;
	}

	/**
	 * Sets the notation to use. By default, this is the {@link Notation#INFIX infix} notation.
	 *
	 * @param notation notation to use
	 */
	public void setNotation(Notation notation) {
		this.notation = notation;
	}

	/**
	 * Returns the notation to use.
	 *
	 * @return the notation to use
	 */
	protected Notation getNotation() {
		return notation;
	}

	public LineFormat getLineFormat() {
		return lineFormat;
	}

	public void setLineFormat(LineFormat lineFormat) {
		this.lineFormat = lineFormat;
	}

	/**
	 * Sets the enforcing brackets flag. If true, this writer will always place brackets, even if they are semantically irrelevant.
	 *
	 * @param enforceBrackets
	 */
	public void setEnforceBrackets(boolean enforceBrackets) {
		this.enforceBrackets = enforceBrackets;
	}

	/**
	 * Returns the enforcing brackets flag.
	 *
	 * @return the enforcing brackets flag
	 */
	protected boolean isEnforceBrackets() {
		return enforceBrackets;
	}

	public boolean isEnquoteAlways() {
		return enquoteAlways;
	}

	public void setEnquoteAlways(boolean enquoteAlways) {
		this.enquoteAlways = enquoteAlways;
	}

	/**
	 * Sets the enquoting whitespace flag. If true, this writer will enquote variables if they contain whitespace.
	 *
	 * @param enquoteWhitespace
	 */
	public void setEnquoteWhitespace(boolean enquoteWhitespace) {
		this.enquoteWhitespace = enquoteWhitespace;
	}

	/**
	 * Returns the enquoting whitespace flag.
	 *
	 * @return the enquoting whitespace flag
	 */
	protected boolean isEnquoteWhitespace() {
		return enquoteWhitespace;
	}

	/**
	 * Converts the given node into the specified textual representation.
	 *
	 * @return the textual representation; not null
	 */
	public String nodeToString() {
		final StringBuilder sb = new StringBuilder();
		nodeToString(root, null, sb, -1);
		return sb.toString();
	}

	/**
	 * Converts a node into the specified textual representation.
	 *
	 * @param node propositional node to convert; not null
	 * @param parent the class of the node's parent; null if not available (i.e. the current node is the root node)
	 * @param sb the {@link StringBuilder} containing the textual representation.
	 */
	protected void nodeToString(Node node, Class<? extends Node> parent, StringBuilder sb, int depth) {
		if (node == null) {
			sb.append(String.valueOf(node));
		} else {
			if (node instanceof Not) {
				final Node child = node.children[0];
				if (child instanceof Literal) {
					final Literal l = (Literal) child;
					node = new Literal(l.var, !l.positive);
				}
			}
			if (node instanceof Literal) {
				literalToString((Literal) node, parent, sb, depth + 1);
			} else {
				operationToString(node, parent, sb, depth + 1);
			}
		}
	}

	/**
	 * Converts a literal into the specified textual representation.
	 *
	 * @param l a literal to convert; not null
	 * @param parent the class of the node's parent; null if not available (i.e. the current node is the root node)
	 * @param sb the {@link StringBuilder} containing the textual representation.
	 */
	protected void literalToString(Literal l, Class<? extends Node> parent, StringBuilder sb, int depth) {
		switch (lineFormat) {
		case SINGLE:
			break;
		case TREE:
			if (depth > 0) {
				sb.append('\n');
			}
			for (int i = 0; i < depth; i++) {
				sb.append('\t');
			}
			break;
		default:
			throw new IllegalStateException("Unknown line format: " + lineFormat);
		}
		final String s = variableToString(l.var);
		if (!l.positive) {
			final Notation notation = getNotation();
			switch (notation) {
			case INFIX:
				sb.append(getSymbols()[0]);
				sb.append((getSymbols() == textualSymbols) ? " " : "");
				sb.append(s);
				break;
			case PREFIX:
				sb.append('(');
				sb.append(getSymbols()[0]);
				sb.append(' ');
				sb.append(s);
				sb.append(')');
				break;
			case POSTFIX:
				sb.append('(');
				sb.append(s);
				sb.append(' ');
				sb.append(getSymbols()[0]);
				sb.append(')');
				break;
			default:
				throw new IllegalStateException("Unknown notation: " + notation);
			}
		} else {
			sb.append(s);
		}
	}

	/**
	 * Converts a variable into the specified textual representation.
	 *
	 * @param variable a variable to convert; not null
	 * @return the textual representation; not null
	 */
	protected String variableToString(Object variable) {
		final String s = String.valueOf(variable);
		return isEnquoteAlways() || (isEnquoteWhitespace() && (containsWhitespace(s) || equalsSymbol(s))) ? '"' + s + '"' : s;
	}

	/**
	 * Converts an operation (i.e. a node that is not a literal) into the specified textual representation.
	 *
	 * @param node an operation to convert; not null
	 * @param parent the class of the node's parent; null if not available (i.e. the current node is the root node)
	 * @param sb the {@link StringBuilder} containing the textual representation.
	 */
	protected void operationToString(Node node, Class<? extends Node> parent, StringBuilder sb, int depth) {
		switch (lineFormat) {
		case SINGLE:
			break;
		case TREE:
			if (depth > 0) {
				sb.append('\n');
			}
			for (int i = 0; i < depth; i++) {
				sb.append('\t');
			}
			break;
		default:
			throw new IllegalStateException("Unknown line format: " + lineFormat);
		}
		final Node[] children = node.getChildren();
		if (children.length == 0) {
			sb.append("()");
			return;
		}

		final String operator = getOperator(node);
		final Notation notation = getNotation();
		switch (notation) {
		case INFIX:
			if (isInfixCompatibleOperation(node)) {
				final int orderParent;
				final int orderChild;
				final boolean parenthesis = (isEnforceBrackets() || ((orderParent = getOrder(parent)) > (orderChild = getOrder(node.getClass())))
					|| ((orderParent == orderChild) && (orderParent == getOrder(Implies.class))));
				if (parenthesis) {
					sb.append('(');
				}
				nodeToString(children[0], node.getClass(), sb, depth);
				for (int i = 1; i < children.length; i++) {
					sb.append(' ');
					sb.append(operator);
					sb.append(' ');
					nodeToString(children[i], node.getClass(), sb, depth);
				}
				if (parenthesis) {
					sb.append(')');
				}
			} else {
				sb.append(operator);
				if ((node instanceof Not) && (getSymbols() == textualSymbols)) {
					sb.append(' ');
				}
				sb.append('(');
				nodeToString(children[0], node.getClass(), sb, depth);
				for (int i = 1; i < children.length; i++) {
					sb.append(getSymbols()[5]);
					nodeToString(children[i], node.getClass(), sb, depth);
				}
				sb.append(')');
			}
			break;
		case PREFIX:
			sb.append('(');
			sb.append(operator);
			sb.append(' ');
			nodeToString(children[0], node.getClass(), sb, depth);
			for (int i = 1; i < children.length; i++) {
				sb.append(' ');
				nodeToString(children[i], node.getClass(), sb, depth);
			}
			sb.append(')');
			break;
		case POSTFIX:
			sb.append('(');
			nodeToString(children[0], node.getClass(), sb, depth);
			for (int i = 1; i < children.length; i++) {
				sb.append(' ');
				nodeToString(children[i], node.getClass(), sb, depth);
			}
			sb.append(' ');
			sb.append(operator);
			sb.append(')');
			break;
		default:
			throw new IllegalStateException("Unknown notation: " + notation);
		}
	}

	/**
	 * Returns true iff the given operation can be written in infix notation. For example, this is true for operations such as {@link And}, which can be written
	 * as <em>A and B</em> instead of <em>and(A, B)</em>. By contrast, this is false for unary operations (i.e. {@link Not}). This is also false for
	 * {@link Choose}, {@link AtLeast} and {@link AtMost}.
	 *
	 * @param node operation in question
	 * @return true iff the given operation can be written in infix notation
	 */
	protected boolean isInfixCompatibleOperation(Node node) {
		return (node instanceof And) || (node instanceof Or) || (node instanceof Implies) || (node instanceof Equals);
	}

	/**
	 * Assigns a number to every type of node. For instance, that {@link And} has a higher order than {@link Or} means that <em>(A and B or C)</em> is equal to
	 * <em>((A and B) or C)</em>.
	 *
	 * @param nodeClass type of node; not null
	 * @return the order assigned to the type of node
	 * @throws IllegalArgumentException if the node type is not recognized
	 */
	protected int getOrder(Class<? extends Node> nodeClass) throws IllegalArgumentException {
		if (nodeClass == null) {
			return -1;
		}
		if (nodeClass.equals(Not.class)) {
			return 0;
		}
		if (nodeClass.equals(AtMost.class) || nodeClass.equals(AtLeast.class) || nodeClass.equals(Choose.class)) {
			return 1;
		}
		if (nodeClass.equals(Equals.class)) {
			return 2;
		}
		if (nodeClass.equals(Implies.class)) {
			return 3;
		}
		if (nodeClass.equals(Or.class)) {
			return 4;
		}
		if (nodeClass.equals(And.class)) {
			return 5;
		}
		throw new IllegalArgumentException("Unrecognized node type: " + nodeClass);
	}

	/**
	 * Returns the operator for the given node.
	 *
	 * @param node an operation; not null
	 * @return the operator
	 * @throws IllegalArgumentException if the node type is not recognized
	 */
	protected String getOperator(Node node) throws IllegalArgumentException {
		if (node instanceof Not) {
			return getSymbols()[0];
		}
		if (node instanceof And) {
			return getSymbols()[1];
		}
		if (node instanceof Or) {
			return getSymbols()[2];
		}
		if (node instanceof Implies) {
			return getSymbols()[3];
		}
		if (node instanceof Equals) {
			return getSymbols()[4];
		}
		if (node instanceof Choose) {
			return getSymbols()[6] + ((Choose) node).n;
		}
		if (node instanceof AtLeast) {
			return getSymbols()[7] + ((AtLeast) node).min;
		}
		if (node instanceof AtMost) {
			return getSymbols()[8] + ((AtMost) node).max;
		}
		throw new IllegalArgumentException("Unrecognized node type: " + node.getClass());
	}

	/**
	 * Returns true iff the given string equals one of the symbols.
	 *
	 * @param s string potentially equaling a symbol; not null
	 * @return whether the string equals one of the symbols
	 */
	private boolean equalsSymbol(String s) {
		for (final String symbol : getSymbols()) {
			if (s.equalsIgnoreCase(symbol)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true iff the given string contains a whitespace character.
	 *
	 * @param s string potentially containing whitespace; not null
	 * @return whether the string contains whitespace
	 */
	private static boolean containsWhitespace(String s) {
		return s.matches(".*?\\s+.*");
	}

}
