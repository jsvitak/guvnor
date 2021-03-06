/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.enums.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.ui.client.handlers.CopyPopup;
import org.kie.guvnor.commons.ui.client.handlers.DeletePopup;
import org.kie.guvnor.commons.ui.client.handlers.RenameCommand;
import org.kie.guvnor.commons.ui.client.handlers.RenamePopup;
import org.kie.guvnor.commons.ui.client.menu.FileMenuBuilder;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
import org.kie.guvnor.enums.client.EnumResourceType;
import org.kie.guvnor.enums.model.EnumModelContent;
import org.kie.guvnor.enums.service.EnumService;
import org.kie.guvnor.errors.client.widget.ShowBuilderErrorsWidget;
import org.kie.guvnor.metadata.client.resources.i18n.MetadataConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.viewsource.client.screen.ViewSourceView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnMayClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.LoadingPopup;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.events.ResourceCopiedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceDeletedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceRenamedEvent;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * This is the default rule editor widget (just text editor based) - more to come later.
 */
@Dependent
@WorkbenchEditor(identifier = "EnumEditor", supportedTypes = { EnumResourceType.class })
public class EnumEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( String content );

        String getContent();

        boolean isDirty();

        void setNotDirty();

        boolean confirmClose();
    }

    @Inject
    private View view;

    @Inject
    private ViewSourceView viewSource;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Caller<EnumService> enumService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<ResourceDeletedEvent> resourceDeletedEvent;

    @Inject
    private Event<ResourceRenamedEvent> resourceRenamedEvent;

    @Inject
    private Event<ResourceCopiedEvent> resourceCopiedEvent;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    @New
    private FileMenuBuilder menuBuilder;
    private Menus           menus;

    private final MetadataWidget metadataWidget = new MetadataWidget();

    private Path         path;
    private PlaceRequest place;
    private boolean      isReadOnly;

    @OnStart
    public void onStart( final Path path,
                         final PlaceRequest place ) {
        this.path = path;
        this.place = place;
        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;
        makeMenuBar();

        multiPage.addWidget( view, CommonConstants.INSTANCE.EditTabTitle() );
        multiPage.addPage( new Page( viewSource, CommonConstants.INSTANCE.SourceTabTitle() ) {
            @Override
            public void onFocus() {
                viewSource.setContent( view.getContent() );
            }

            @Override
            public void onLostFocus() {
                viewSource.clear();
            }
        } );

        multiPage.addPage( new Page( metadataWidget, MetadataConstants.INSTANCE.Metadata() ) {
            @Override
            public void onFocus() {
                metadataService.call(
                        new RemoteCallback<Metadata>() {
                            @Override
                            public void callback( Metadata metadata ) {
                                metadataWidget.setContent( metadata,
                                                           isReadOnly );
                            }
                        } ).getMetadata( path );
            }

            @Override
            public void onLostFocus() {
                // Nothing to do here
            }
        } );

        enumService.call( new RemoteCallback<EnumModelContent>() {
            @Override
            public void callback( EnumModelContent response ) {
                view.setContent( response.getModel().getDRL() );
            }
        } ).load( path );
    }

    private void makeMenuBar() {
        FileMenuBuilder fileMenuBuilder = menuBuilder.addValidation( new Command() {
            @Override
            public void execute() {
                LoadingPopup.showMessage( CommonConstants.INSTANCE.WaitWhileValidating() );
                enumService.call( new RemoteCallback<BuilderResult>() {
                    @Override
                    public void callback( BuilderResult response ) {
                        final ShowBuilderErrorsWidget pop = new ShowBuilderErrorsWidget( response );
                        LoadingPopup.close();
                        pop.show();
                    }
                } ).validate( path,
                              view.getContent() );
            }
        } );

        if ( isReadOnly ) {
            fileMenuBuilder.addRestoreVersion( path );
        } else {
            fileMenuBuilder.addSave( new Command() {
                @Override
                public void execute() {
                    onSave();
                }
            } ).addDelete( new Command() {
                @Override
                public void execute() {
                    onDelete();
                }
            } ).addRename( new Command() {
                @Override
                public void execute() {
                    onRename();
                }
            } ).addCopy( new Command() {
                @Override
                public void execute() {
                    onCopy();
                }
            } );
        }
        menus = fileMenuBuilder.build();
    }

    @OnSave
    public void onSave() {
        new SaveOperationService().save( path, new CommandWithCommitMessage() {
            @Override
            public void execute( final String commitMessage ) {
                enumService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( Void response ) {
                        view.setNotDirty();
                        notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
                    }
                } ).save( path,
                          view.getContent(),
                          metadataWidget.getContent(),
                          commitMessage );
            }
        } );
    }

    public void onDelete() {
        DeletePopup popup = new DeletePopup( new CommandWithCommitMessage() {
            @Override
            public void execute( final String comment ) {
                enumService.call( new RemoteCallback<Path>() {
                    @Override
                    public void callback( Path response ) {
                        view.setNotDirty();
                        metadataWidget.resetDirty();
                        notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
                        resourceDeletedEvent.fire( new ResourceDeletedEvent( path ) );
                        placeManager.closePlace( place );
                    }
                } ).delete( path,
                            comment );
            }
        } );

        popup.show();
    }

    public void onRename() {
        RenamePopup popup = new RenamePopup( new RenameCommand() {
            @Override
            public void execute( final String newName,
                                 final String comment ) {
                enumService.call( new RemoteCallback<Path>() {
                    @Override
                    public void callback( Path response ) {
                        view.setNotDirty();
                        metadataWidget.resetDirty();
                        notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
                        resourceRenamedEvent.fire( new ResourceRenamedEvent( path,
                                                                             response ) );
                    }
                } ).rename( path,
                            newName,
                            comment );
            }
        } );

        popup.show();
    }

    public void onCopy() {
        CopyPopup popup = new CopyPopup( new RenameCommand() {
            @Override
            public void execute( final String newName,
                                 final String comment ) {
                enumService.call( new RemoteCallback<Path>() {
                    @Override
                    public void callback( Path response ) {
                        view.setNotDirty();
                        metadataWidget.resetDirty();
                        notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
                        resourceCopiedEvent.fire( new ResourceCopiedEvent( path,
                                                                           response ) );
                    }
                } ).copy( path,
                          newName,
                          comment );
            }
        } );

        popup.show();
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnMayClose
    public boolean checkIfDirty() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Enum Editor [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
