package org.kie.guvnor.guided.dtable.model.adaptors;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.guided.dtable.model.Pattern52;
import org.kie.guvnor.guided.rule.model.CEPWindow;
import org.kie.guvnor.guided.rule.model.FactPattern;
import org.kie.guvnor.guided.rule.model.FieldConstraint;

/**
 * Adaptor to use RuleModel class in GuidedDecisionTable
 */
@Portable
public class Pattern52FactPatternAdaptor extends FactPattern {

    private static final long serialVersionUID = 540l;

    private Pattern52 pattern;

    public Pattern52FactPatternAdaptor() {
    }

    public Pattern52FactPatternAdaptor( final Pattern52 pattern ) {
        PortablePreconditions.checkNotNull( "pattern",
                                            pattern );
        this.pattern = pattern;
    }

    @Override
    public boolean isBound() {
        return pattern.isBound();
    }

    @Override
    public String getBoundName() {
        return pattern.getBoundName();
    }

    @Override
    public String getFactType() {
        return pattern.getFactType();
    }

    @Override
    public boolean isNegated() {
        return pattern.isNegated();
    }

    @Override
    public void setBoundName( final String boundName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNegated( final boolean isNegated ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addConstraint( final FieldConstraint constraint ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeConstraint( final int idx ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldConstraint[] getFieldConstraints() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFieldConstraints( final List<FieldConstraint> sortedConstraints ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFactType( final String factType ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWindow( final CEPWindow window ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CEPWindow getWindow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumberOfConstraints() {
        throw new UnsupportedOperationException();
    }

}