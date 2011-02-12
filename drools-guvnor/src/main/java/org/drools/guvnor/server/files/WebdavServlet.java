/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.files;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.webdav.IWebdavStore;
import net.sf.webdav.WebDavServletBean;

/**
 * Taken from the webdav servlet project. Modified to be more useful.
 * But most of the logic is still in webdav servlet library (which at the time
 * of writing was included as source as it was easier - needed some fixes).
 *
 *
 */
public class WebdavServlet extends WebDavServletBean {
    private static final long serialVersionUID = 510l;

    public void init() throws ServletException {

        // Parameters from web.xml
        String clazzName = WebDAVImpl.class.getName();

        File root = new File( "" );// getFileRoot();

        IWebdavStore webdavStore = constructStore( clazzName,
                                                   root );

        String lazyFolderCreationOnPutValue = getInitParameter( "lazyFolderCreationOnPut" );
        boolean lazyFolderCreationOnPut = lazyFolderCreationOnPutValue != null && lazyFolderCreationOnPutValue.equals( "1" );

        String dftIndexFile = getInitParameter( "default-index-file" );
        String insteadOf404 = getInitParameter( "instead-of-404" );

        int noContentLengthHeader = 0;

        super.init( webdavStore,
                    dftIndexFile,
                    insteadOf404,
                    noContentLengthHeader,
                    lazyFolderCreationOnPut );
    }

    @Override
    protected void service(HttpServletRequest req,
                           HttpServletResponse resp) throws ServletException,
                                                    IOException {
        //love you
        long time = System.currentTimeMillis();
        
        String auth = req.getHeader( "Authorization" );
        if ( !RestAPIServlet.allowUser( auth ) ) {
            resp.setHeader( "WWW-Authenticate",
                            "BASIC realm=\"users\"" );
            resp.sendError( HttpServletResponse.SC_UNAUTHORIZED );
        } else {
            super.service( req,
                           resp );
        }

        //System.err.println("WebDAV servlet time: " + (System.currentTimeMillis() - time));
    }

    protected IWebdavStore constructStore(String clazzName,
                                          File root) {
        IWebdavStore webdavStore;
        try {
            Class clazz = WebdavServlet.class.getClassLoader().loadClass( clazzName );

            Constructor ctor = clazz.getConstructor( new Class[]{File.class} );

            webdavStore = (IWebdavStore) ctor.newInstance( new Object[]{root} );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new RuntimeException( "some problem making store component",
                                        e );
        }
        return webdavStore;
    }

}
