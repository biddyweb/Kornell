package kornell.gui.client.presentation.admin.courseclass.courseclass.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseClassTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericCourseClassReportItemView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseClassReportItemView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private String BASE_IMAGES_PATH = "skins/first/icons/";
	private String ADMIN_IMAGES_PATH = BASE_IMAGES_PATH + "admin/";
	private String LIBRARY_IMAGES_PATH = BASE_IMAGES_PATH + "courseLibrary/";
	private KornellSession session;
	private CourseClassTO currentCourseClass;
	private String type;
	private String name;
	private String description;
	
	private CheckBox xlsCheckBox;
	
	private HandlerRegistration downloadHandler;
	
	public static final String COURSE_CLASS_INFO = "courseClassInfo";
	public static final String CERTIFICATE = "certificate";
	
	@UiField
	Image certificationIcon;
	@UiField
	Label lblName;
	@UiField
	Label lblDescription;
	@UiField
	FlowPanel optionPanel;
	@UiField
	Anchor lblGenerate;
	@UiField
	Anchor lblDownload;


	public GenericCourseClassReportItemView(EventBus eventBus, KornellSession session, CourseClassTO currentCourseClass,
			String type) {
		this.session = session;
		this.currentCourseClass = currentCourseClass;
		this.type = type;
		initWidget(uiBinder.createAndBindUi(this));
		display();
	}
	
	private void display() {
		if(CERTIFICATE.equals(this.type))
			displayCertificate();
		else
			displayCourseClassInfo();
	}

	private void displayCourseClassInfo() {
	  this.name = "Relatório de detalhes da turma";
		this.description = "Geração do relatório de detalhes da turma, contendo os dados da turma e informações sobre as matrículas da mesma.";
		
		certificationIcon.setUrl(ADMIN_IMAGES_PATH + type + ".png");
		lblName.setText(name);
		lblDescription.setText(description);
		lblGenerate.setText("Gerar");
		lblGenerate.addStyleName("cursorPointer");

		lblDownload.setText("-");
		lblDownload.removeStyleName("cursorPointer");
		lblDownload.addStyleName("anchorToLabel");
		lblDownload.setEnabled(false);
		
		Image img = new Image(LIBRARY_IMAGES_PATH + "xls.png");
		xlsCheckBox = new CheckBox("Gerar em formato Excel");
		
		optionPanel.add(img);
		optionPanel.add(xlsCheckBox);
		
		img.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				xlsCheckBox.setValue(!xlsCheckBox.getValue());				
			}
		});
		
		lblGenerate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				KornellNotification.show("Aguarde um instante...", AlertType.INFO, 2000);
				String url = StringUtils.composeURL(session.getApiUrl(), "/report/courseClassInfo/?courseClassUUID="
						+ currentCourseClass.getCourseClass().getUUID() + "&fileType=" + (xlsCheckBox.getValue() ? "xls" : "pdf"));
				Window.Location.assign(url);
			}
		});
  }

	private void displayCertificate() {
	  this.name = "Certificados de conclusão de curso";
		this.description = "Geração do certificado de todos os alunos desta turma que concluíram o curso. A geração pode chegar a levar a alguns minutos, dependendo do tamanho da turma. Assim que o relatório for gerado, ele estará disponível para ser baixado aqui.";
		
		certificationIcon.setUrl(ADMIN_IMAGES_PATH + type + ".png");
		lblName.setText(name);
		lblDescription.setText(description);
		lblGenerate.setText("Gerar");
		lblGenerate.addStyleName("cursorPointer");
		
		lblGenerate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				displayCertificateActionCell(null);
				LoadingPopup.show();
				session.report().generateCourseClassCertificate(currentCourseClass.getCourseClass().getUUID(), new Callback<String>() {
					
					@Override
					public void ok(String url) {
						KornellNotification.show("Os certificados foram gerados.", AlertType.INFO, 2000);
						displayCertificateActionCell(url);
						LoadingPopup.hide();
					}
					
					@Override
					public void internalServerError(KornellErrorTO kornellErrorTO) {
						KornellNotification.show("Erro na geração dos certificados. Certifique-se que existem alunos que concluíram o curso nessa turma.", AlertType.ERROR, 3000);
						displayCertificateActionCell(null);
						LoadingPopup.hide();
					}
				});
			}
		});

		session.report().courseClassCertificateExists(currentCourseClass.getCourseClass().getUUID(), new Callback<String>() {
			@Override
			public void ok(String str) {
				displayCertificateActionCell(str);
			}
			
			@Override
			public void internalServerError(KornellErrorTO kornellErrorTO) {
				displayCertificateActionCell(null);
			}
		});
  }

	private void displayCertificateActionCell(final String url) {
	  if(url != null && !"".equals(url)) {
			lblDownload.setText("Baixar");
			lblDownload.addStyleName("cursorPointer");
			lblDownload.removeStyleName("anchorToLabel");
			downloadHandler = lblDownload.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open(url,"","");
				}
			});
		} else {
			lblDownload.setText("Não disponível");
			lblDownload.removeStyleName("cursorPointer");
			lblDownload.addStyleName("anchorToLabel");
			lblDownload.setEnabled(false);
			if(downloadHandler != null){
				downloadHandler.removeHandler();
			}
		}
  }

}
