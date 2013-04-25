package org.deltaj.generator;

import com.google.inject.Inject;
import java.util.Arrays;
import org.deltaj.deltaj.ClassAddition;
import org.deltaj.deltaj.DeltaAction;
import org.deltaj.deltaj.DeltaModule;
import org.deltaj.deltaj.Method;
import org.deltaj.deltaj.Statement;
import org.deltaj.deltaj.StatementBlock;
import org.deltaj.generator.DeltaJStatementConstraintGenerator;
import org.deltaj.typing.DeltaJTypeSystem;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class DeltaJConstraintsGenerator implements IGenerator {
  @Inject
  private DeltaJTypeSystem typeSystem;
  
  @Inject
  private DeltaJStatementConstraintGenerator statementConstraintGenerator;
  
  public void doGenerate(final Resource resource, final IFileSystemAccess fsa) {
    TreeIterator<EObject> _allContents = resource.getAllContents();
    Iterable<EObject> _iterable = IteratorExtensions.<EObject>toIterable(_allContents);
    Iterable<DeltaModule> _filter = IterableExtensions.<DeltaModule>filter(_iterable, org.deltaj.deltaj.DeltaModule.class);
    for (final DeltaModule delta : _filter) {
      this.compile(delta, fsa);
    }
  }
  
  public void compile(final DeltaModule delta, final IFileSystemAccess fsa) {
    String _name = delta.getName();
    String _operator_plus = StringExtensions.operator_plus("constraints/", _name);
    String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, ".deltajconstraints");
    CharSequence _compile = this.compile(delta);
    fsa.generateFile(_operator_plus_1, _compile);
  }
  
  public CharSequence compile(final DeltaModule delta) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("delta ");
    String _name = delta.getName();
    _builder.append(_name, "");
    _builder.append(" with {");
    _builder.newLineIfNotEmpty();
    {
      EList<DeltaAction> _deltaActions = delta.getDeltaActions();
      for(final DeltaAction action : _deltaActions) {
        _builder.append("\t");
        CharSequence _compile = this.compile(action);
        _builder.append(_compile, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _compile(final DeltaAction action) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("/* not yet implemented */");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _compile(final ClassAddition action) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("adds ");
    org.deltaj.deltaj.Class _class_ = action.getClass_();
    String _name = _class_.getName();
    _builder.append(_name, "");
    _builder.append(" with {");
    _builder.newLineIfNotEmpty();
    {
      org.deltaj.deltaj.Class _class__1 = action.getClass_();
      EList<Method> _methods = _class__1.getMethods();
      for(final Method method : _methods) {
        _builder.append("\t");
        CharSequence _compile = this.compile(method);
        _builder.append(_compile, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence compile(final Method method) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = method.getName();
    _builder.append(_name, "");
    _builder.append(" with {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    StatementBlock _body = method.getBody();
    EList<Statement> _statements = _body.getStatements();
    CharSequence _genConstraints = this.statementConstraintGenerator.genConstraints(this.typeSystem, _statements);
    _builder.append(_genConstraints, "	");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence compile(final DeltaAction action) {
    if (action instanceof ClassAddition) {
      return _compile((ClassAddition)action);
    } else if (action != null) {
      return _compile(action);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(action).toString());
    }
  }
}
