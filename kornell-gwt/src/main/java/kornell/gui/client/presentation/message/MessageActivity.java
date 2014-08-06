package kornell.gui.client.presentation.message;

import kornell.gui.client.ClientFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class MessageActivity extends AbstractActivity{
	private static MessagePresenter presenter;
	
	public MessageActivity(ClientFactory clientFactory) {
	    if(presenter == null){
	    	presenter = new MessagePresenter(clientFactory);
	    }
	 }
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);		
	}

}
