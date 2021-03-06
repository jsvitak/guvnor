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
package org.kie.guvnor.commons.ui.client.workitems;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.guvnor.datamodel.model.workitems.PortableParameterDefinition;

/**
 * A Widget to display a Work Item parameter
 */
public abstract class WorkItemParameterWidget extends Composite {

    protected PortableParameterDefinition ppd;

    protected IBindingProvider bindingProvider;

    public WorkItemParameterWidget( PortableParameterDefinition ppd,
                                    IBindingProvider bindingProvider ) {
        this.ppd = ppd;
        this.bindingProvider = bindingProvider;
        initWidget( getWidget() );
    }

    protected abstract Widget getWidget();

}
