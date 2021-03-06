package kornell.gui.client.presentation.message.compose;

import kornell.api.client.Callback;
import kornell.api.client.ChatThreadsClient;
import kornell.api.client.KornellSession;
import kornell.core.entity.EntityFactory;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.personnel.MrPostman;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class MessageComposePresenter implements MessageComposeView.Presenter {
	private FormHelper formHelper = GWT.create(FormHelper.class);
	
	private MessageComposeView view;
	private PlaceController placeCtrl;
	private ChatThreadsClient threadsClient;
	private ViewFactory viewFactory;

	public MessageComposePresenter(PlaceController placeCtrl, KornellSession session, ViewFactory viewFactory, EntityFactory entityFactory) {
		this.placeCtrl = placeCtrl;
		this.threadsClient = session.chatThreads();
		this.viewFactory = viewFactory;
	}

	@Override
	public void init() {
		if(view == null){
			view = viewFactory.getMessageComposeView();
			view.setPresenter(this);
		}
		
		//check if it's inside the classroom to preselect the recipient
		view.show(getCourseClassUUIDFromPlace());
	}

	private String getCourseClassUUIDFromPlace() {
		if(placeCtrl.getWhere() instanceof ClassroomPlace){
	    return Dean.getInstance().getCourseClassTO().getCourseClass().getUUID();
		}
	  return null;
  }

	@Override
	public void okButtonClicked() {
		if(validateMessage()){
			String messageText = view.getMessageText().getFieldPersistText();
			Callback<Void> chatThreadCallback = new Callback<Void>() {
				@Override
				public void ok(Void to) {
					KornellNotification.show("Mensagem enviada com sucesso!");
					MrPostman.hide();
				}
			};
			String entityUUID = view.getRecipient().getFieldPersistText();
			threadsClient.postMessageToCourseClassThread(messageText, entityUUID, chatThreadCallback);
		}
	}

	@Override
  public void cancelButtonClicked() {
		MrPostman.hide();
  }
	
	private boolean validateMessage() {	
		view.clearErrors();		
		if (!formHelper.isLengthValid(view.getMessageText().getFieldPersistText(), 1, 1000)) {
			view.getMessageText().setError("Preencha o corpo da mensagem.");
		}
		return !view.checkErrors();
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

}
