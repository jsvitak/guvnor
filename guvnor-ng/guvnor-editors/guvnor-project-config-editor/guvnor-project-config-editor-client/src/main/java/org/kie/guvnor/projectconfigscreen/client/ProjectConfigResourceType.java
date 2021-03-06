package org.kie.guvnor.projectconfigscreen.client;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.file.ResourceType;

@ApplicationScoped
public class ProjectConfigResourceType implements ResourceType {

    @Override
    public String getShortName() {
        return "project imports";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "project";
    }

    @Override
    public String getSuffix() {
        return "imports";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( getPrefix() + "." + getSuffix() );
    }
}
