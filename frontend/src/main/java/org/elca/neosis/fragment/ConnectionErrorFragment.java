package org.elca.neosis.fragment;

import org.elca.neosis.factory.ObservableResourceFactory;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;

@Fragment(
        id = ConnectionErrorFragment.ID,
        viewLocation = "",
        scope = Scope.PROTOTYPE,
        resourceBundleLocation = ObservableResourceFactory.RESOURCE_BUNDLE_NAME
)
public class ConnectionErrorFragment {
    public static final String ID = "ConnectionErrorFragment";
}
