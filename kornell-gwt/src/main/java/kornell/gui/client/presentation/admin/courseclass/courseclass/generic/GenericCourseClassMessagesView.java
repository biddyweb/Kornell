package kornell.gui.client.presentation.admin.courseclass.courseclass.generic;

import kornell.api.client.KornellSession;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.message.MessagePresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericCourseClassMessagesView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseClassMessagesView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	PlaceController placeCtrl;
	ViewFactory viewFactory;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;

	private MessagePresenter presenter;

	@UiField
	FlowPanel messagesPanel;
	
	public GenericCourseClassMessagesView(final KornellSession session, EventBus bus, PlaceController placeCtrl, ViewFactory viewFactory,
			MessagePresenter presenter, CourseClassTO courseClassTO) {
		this.placeCtrl = placeCtrl;
		this.viewFactory = viewFactory;
		this.presenter = presenter;
		initWidget(uiBinder.createAndBindUi(this));		
		initData();
	}

	public void initData() {
		messagesPanel.setVisible(false);
		messagesPanel.add(getReportPanel());
		messagesPanel.setVisible(true);

	}

	private FlowPanel getReportPanel() {
		FlowPanel reportPanel = new FlowPanel();
		reportPanel.addStyleName("reportPanel");
		reportPanel.add(getMessagesInfo());
		reportPanel.add(getMessagesContent());

		return reportPanel;
	}

	private FlowPanel getMessagesInfo() {
		FlowPanel messagesInfo = new FlowPanel();
		messagesInfo.addStyleName("titlePanel");

		Label infoTitle = new Label("Mensagens");
		infoTitle.addStyleName("title");
		messagesInfo.add(infoTitle);

		Label infoText = new Label("Mensagens entre os alunos e os responsáveis pela turma.");
		infoText.addStyleName("subTitle");
		messagesInfo.add(infoText);

		return messagesInfo;
	}

	private FlowPanel getMessagesContent() {
		FlowPanel reportContentPanel = new FlowPanel();
		reportContentPanel.add(presenter.asWidget());
		return reportContentPanel;
	}

}