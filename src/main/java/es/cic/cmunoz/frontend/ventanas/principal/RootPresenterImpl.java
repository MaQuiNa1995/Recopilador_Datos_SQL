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
package es.cic.cmunoz.frontend.ventanas.principal;

import com.vaadin.spring.annotation.UIScope;
import es.cic.cmunoz.frontend.interfaces.Presenter;
import es.cic.cmunoz.frontend.interfaces.View;

import javax.inject.Inject;
import javax.inject.Named;

@UIScope
@Named("Incidencias Analyzer")
public class RootPresenterImpl implements RootPresenter {

    @Inject
    private RootView view;

    @Override
    public View getView() {
        view.iniciarVentana();
        return this.view;
    }

    @Override
    public void setContent(final Presenter<?> presenter) {
        this.view.setContent(presenter.getView());
    }

}
