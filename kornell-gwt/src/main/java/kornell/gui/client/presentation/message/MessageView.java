package kornell.gui.client.presentation.message;

import com.google.gwt.user.client.ui.IsWidget;

public interface MessageView  extends IsWidget {
	public interface Presenter extends IsWidget {

	}

	void setPresenter(Presenter presenter);
}
