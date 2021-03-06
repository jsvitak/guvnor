/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.globals.backend.server;

import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.guvnor.commons.service.source.DRLBaseSourceService;

public class GlobalsSourceService
        extends DRLBaseSourceService {

    private static final String PATTERN = ".global.drl";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    protected IOService getIOService() {
        return ioService;
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

}
