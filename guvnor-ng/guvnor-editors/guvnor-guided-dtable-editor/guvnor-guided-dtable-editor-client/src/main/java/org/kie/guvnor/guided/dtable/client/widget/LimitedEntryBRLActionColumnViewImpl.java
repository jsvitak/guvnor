/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.guvnor.guided.dtable.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import org.kie.guvnor.datamodel.model.IAction;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.guided.dtable.client.resources.i18n.Constants;
import org.kie.guvnor.guided.dtable.model.ActionCol52;
import org.kie.guvnor.guided.dtable.model.BRLActionVariableColumn;
import org.kie.guvnor.guided.dtable.model.BRLColumn;
import org.kie.guvnor.guided.dtable.model.BRLRuleModel;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;
import org.kie.guvnor.guided.dtable.model.LimitedEntryBRLActionColumn;
import org.kie.guvnor.guided.rule.client.editor.RuleModellerConfiguration;
import org.kie.guvnor.guided.rule.model.RuleModel;
import org.kie.guvnor.guided.template.model.RuleModelCloneVisitor;
import org.uberfire.backend.vfs.Path;

/**
 * An editor for a Limited Entry BRL Action Columns
 */
public class LimitedEntryBRLActionColumnViewImpl extends AbstractLimitedEntryBRLColumnViewImpl<IAction, BRLActionVariableColumn>
        implements
        LimitedEntryBRLActionColumnView {

    private Presenter presenter;

    public LimitedEntryBRLActionColumnViewImpl( final Path path,
                                                final DataModelOracle oracle,
                                                final GuidedDecisionTable52 model,
                                                final LimitedEntryBRLActionColumn column,
                                                final EventBus eventBus,
                                                final boolean isNew,
                                                final boolean isReadOnly ) {
        super( path,
               oracle,
               model,
               column,
               eventBus,
               isNew,
               isReadOnly );

        setTitle( Constants.INSTANCE.ActionBRLFragmentConfiguration() );
    }

    protected boolean isHeaderUnique( String header ) {
        for ( ActionCol52 o : model.getActionCols() ) {
            if ( o.getHeader().equals( header ) ) {
                return false;
            }
        }
        return true;
    }

    protected BRLRuleModel getRuleModel( BRLColumn<IAction, BRLActionVariableColumn> column ) {
        BRLRuleModel ruleModel = new BRLRuleModel( model );
        List<IAction> definition = column.getDefinition();
        ruleModel.rhs = definition.toArray( new IAction[ definition.size() ] );
        return ruleModel;
    }

    protected RuleModellerConfiguration getRuleModellerConfiguration() {
        return new RuleModellerConfiguration( true,
                                              false,
                                              true );
    }

    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    protected void doInsertColumn() {
        this.editingCol.setDefinition( Arrays.asList( this.ruleModel.rhs ) );
        presenter.insertColumn( (LimitedEntryBRLActionColumn) this.editingCol );
    }

    @Override
    protected void doUpdateColumn() {
        this.editingCol.setDefinition( Arrays.asList( this.ruleModel.rhs ) );
        presenter.updateColumn( (LimitedEntryBRLActionColumn) this.originalCol,
                                (LimitedEntryBRLActionColumn) this.editingCol );
    }

    @Override
    protected BRLColumn<IAction, BRLActionVariableColumn> cloneBRLColumn( BRLColumn<IAction, BRLActionVariableColumn> col ) {
        LimitedEntryBRLActionColumn clone = new LimitedEntryBRLActionColumn();
        clone.setHeader( col.getHeader() );
        clone.setHideColumn( col.isHideColumn() );
        clone.setDefinition( cloneDefinition( col.getDefinition() ) );
        return clone;
    }

    @Override
    protected boolean isDefined() {
        return this.ruleModel.rhs.length > 0;
    }

    private List<IAction> cloneDefinition( List<IAction> definition ) {
        RuleModelCloneVisitor visitor = new RuleModelCloneVisitor();
        RuleModel rm = new RuleModel();
        for ( IAction action : definition ) {
            rm.addRhsItem( action );
        }
        RuleModel rmClone = visitor.visitRuleModel( rm );
        List<IAction> clone = new ArrayList<IAction>();
        for ( IAction action : rmClone.rhs ) {
            clone.add( action );
        }
        return clone;
    }

}
