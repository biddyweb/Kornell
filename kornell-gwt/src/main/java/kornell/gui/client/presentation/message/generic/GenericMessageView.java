package kornell.gui.client.presentation.message.generic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kornell.core.to.ChatThreadMessageTO;
import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.message.MessageView;
import kornell.gui.client.presentation.util.FormHelper;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


public class GenericMessageView extends Composite implements MessageView {

	interface GenericMessageUiBinder extends UiBinder<Widget, GenericMessageView> {
	}

	private static GenericMessageUiBinder uiBinder = GWT.create(GenericMessageUiBinder.class);
	private static FormHelper formHelper = GWT.create(FormHelper.class);
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private MessageView.Presenter presenter;


	private List<Label> sideItems;
	Map<Label, ChatThreadMessageTO> dateLabelsMap;

  @UiField FlowPanel sidePanel;
  @UiField FlowPanel threadPanel;
  @UiField ScrollPanel threadPanelItemsScroll;
  @UiField FlowPanel threadPanelItems;
  @UiField Label threadTitle;
  @UiField TextArea messageTextArea;
  @UiField Button btnSend;

	public GenericMessageView() {
		initWidget(uiBinder.createAndBindUi(this));
    ensureDebugId("genericMessageInboxView");
	}
	
	@Override
  protected void onEnsureDebugId(String baseID) {
		sidePanel.ensureDebugId(baseID + "-sidePanel");
		threadPanel.ensureDebugId(baseID + "-threadPanel");
  }

	@Override
	public void setPresenter(Presenter p) {
		presenter = p;
	}

	@Override
  public void updateSidePanel(List<UnreadChatThreadTO> unreadChatThreadsTO, String selectedChatThreadUUID) {
		sidePanel.clear();
		sideItems = new ArrayList<Label>();
		for (final UnreadChatThreadTO unreadChatThreadTO : unreadChatThreadsTO) {
			final Label label = new Label();
			label.addStyleName("threadListItem");
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for (Label lbl : sideItems) {
	          lbl.removeStyleName("selected");
          }
          label.addStyleName("selected");
					presenter.threadClicked(unreadChatThreadTO);
					setLabelContent(unreadChatThreadTO, label, true);
				}
			});
			if(unreadChatThreadTO.getChatThreadUUID().equals(selectedChatThreadUUID)){
        label.addStyleName("selected");
				setLabelContent(unreadChatThreadTO, label, true);
			} else {
				setLabelContent(unreadChatThreadTO, label, false);
			}
			sidePanel.add(label);
			sideItems.add(label);
    }
  }

	private void setLabelContent(final UnreadChatThreadTO unreadChatThreadTO, final Label label, boolean markAsRead) {
	  String appendCount = !"0".equals(unreadChatThreadTO.getUnreadMessages()) && !markAsRead ? " (" + unreadChatThreadTO.getUnreadMessages() + ")" : "";
	  appendCount = "<span class=\"unreadCount\">" + appendCount + "</span>";
	  label.getElement().setInnerHTML(unreadChatThreadTO.getChatThreadName() + appendCount);
  }

	@Override
  public void updateThreadPanel(ChatThreadMessagesTO chatThreadMessagesTO, UnreadChatThreadTO unreadChatThreadTO, String currentUserFullName) {
		threadTitle.setText(unreadChatThreadTO.getChatThreadName());
		dateLabelsMap = new HashMap<Label, ChatThreadMessageTO>();
		
		threadPanelItems.clear();
		addMessagesToThreadPanel(chatThreadMessagesTO, currentUserFullName);
    
    messageTextArea.addKeyUpHandler(new KeyUpHandler() {
        @Override
        public void onKeyUp(KeyUpEvent event) {
          if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER && event.isAnyModifierKeyDown() && event.isControlKeyDown())
            doSend(null);
        }
    });
	  Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
		      @Override
		      public void execute() {
		        messageTextArea.setFocus(true);
		      }
		});
  }

	private void scrollToBottom() {
	  Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
		      @Override
		      public void execute() {
		    		threadPanelItemsScroll.scrollToBottom();
		      }
		});
  }

	@Override
	public void addMessagesToThreadPanel(ChatThreadMessagesTO chatThreadMessagesTO, String currentUserFullName) {
	  for (final ChatThreadMessageTO chatThreadMessageTO : chatThreadMessagesTO.getChatThreadMessageTOs()) {
			FlowPanel threadMessageWrapper = new FlowPanel();
			threadMessageWrapper.addStyleName("threadMessageWrapper");
			Label header = new Label("");
			
			header.addStyleName("threadMessageHeader");
			if(!currentUserFullName.equals(chatThreadMessageTO.getSenderFullName())){
				header.addStyleName("rightText");
				threadMessageWrapper.addStyleName("overrideWrapper");
			}
			threadMessageWrapper.add(header);
			
			Label item = new Label(chatThreadMessageTO.getMessage());
			item.addStyleName("threadMessageItem");
			threadMessageWrapper.add(item);
			
			threadPanelItems.add(threadMessageWrapper);
			dateLabelsMap.put(header, chatThreadMessageTO);
    }
	  updateDateLabelValues(chatThreadMessagesTO.getServerTime());
	  if(chatThreadMessagesTO.getChatThreadMessageTOs().size() > 0)
	  	scrollToBottom();
  }

	private void updateDateLabelValues(Date serverTime) {
		Iterator<Entry<Label, ChatThreadMessageTO>> it = dateLabelsMap.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<Label, ChatThreadMessageTO> pairs = (Map.Entry<Label, ChatThreadMessageTO>)it.next();
    		pairs.getKey().setText(getDateLabelValue(serverTime, pairs.getValue()));
    }
  }

	private String getDateLabelValue(Date serverTime, final ChatThreadMessageTO chatThreadMessageTO) {
	  String dateStr = chatThreadMessageTO.getSenderFullName() + " - " + formHelper.doIt(formHelper.getJudFromString(chatThreadMessageTO.getSentAt()), serverTime);
	  return dateStr;
  }

	@UiHandler("btnSend")
	void doSend(ClickEvent e) {
		if(messageTextArea.getText().length() > 0)
			presenter.sendMessage(messageTextArea.getText());
		messageTextArea.setText("");
    messageTextArea.setFocus(true);
	}
}