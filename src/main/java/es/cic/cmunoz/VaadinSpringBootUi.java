/*
 * Copyright 2017 cmunoz.
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
package es.cic.cmunoz;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import es.cic.cmunoz.frontend.ventanas.principal.RootPresenter;

import javax.inject.Inject;

@SpringUI(path = "")
public class VaadinSpringBootUi extends UI {

    private static final long serialVersionUID = -3178066481319642074L;

    @Inject
    private RootPresenter rootPresenter;

    @Override
    protected void init(final VaadinRequest request) {
        setContent(this.rootPresenter.getView()
                .getComponent(Component.class));
    }

}
