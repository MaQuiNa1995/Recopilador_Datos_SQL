package es.cic.cmunoz.frontend.ventanas.principal;

import es.cic.cmunoz.frontend.interfaces.Presenter;

public interface RootPresenter extends Presenter<RootView> {
    public void setContent(Presenter<?> presenter);
}
