/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.guided.dtable.client.wizard.pages;

import java.util.List;

import org.kie.guvnor.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.kie.guvnor.guided.dtable.model.ConditionCol52;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;
import org.kie.guvnor.guided.dtable.model.Pattern52;
import org.uberfire.client.mvp.UberView;

/**
 * View and Presenter definitions for the Fact Pattern Constraints page
 */
public interface FactPatternConstraintsPageView
        extends
        UberView<FactPatternConstraintsPageView.Presenter>,
        RequiresValidator {

    interface Presenter {

        void selectPattern( Pattern52 pattern );

        void setChosenConditions( Pattern52 pattern,
                                  List<ConditionCol52> conditions );

        String[] getOperatorCompletions( Pattern52 selectedPattern,
                                         ConditionCol52 selectedCondition );

        void stateChanged();

        GuidedDecisionTable52.TableFormat getTableFormat();

        boolean hasEnum( Pattern52 selectedPattern,
                         ConditionCol52 selectedCondition );

        boolean requiresValueList( Pattern52 selectedPattern,
                                   ConditionCol52 selectedCondition );

        void assertDefaultValue( Pattern52 selectedPattern,
                                 ConditionCol52 selectedCondition );

    }

    void setDTCellValueWidgetFactory( DTCellValueWidgetFactory factory );

    void setAvailablePatterns( List<Pattern52> patterns );

    void setAvailableFields( List<AvailableField> fields );

    void setChosenConditions( List<ConditionCol52> conditions );

    void setArePatternBindingsUnique( boolean arePatternBindingsUnique );

    void setAreConditionsDefined( boolean areConditionsDefined );

}
