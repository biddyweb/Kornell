package kornell.gui.client.presentation.admin.courseclass.courseclass.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClassAdminRole;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.People;
import kornell.core.entity.Person;
import kornell.core.entity.Role;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.entity.Roles;
import kornell.core.to.CourseClassTO;
import kornell.core.to.RoleTO;
import kornell.core.to.RolesTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassView.Presenter;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.formfield.PeopleMultipleSelect;

import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Typeahead;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public class GenericCourseClassAdminsView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseClassAdminsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private FormHelper formHelper = GWT.create(FormHelper.class);

	private KornellSession session;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	
	PeopleMultipleSelect peopleMultipleSelect;

	@UiField
	Form form;
	@UiField
	FlowPanel adminsFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	private UserInfoTO user;
	private CourseClassTO courseClassTO;
	private List<KornellFormFieldWrapper> fields;
	
	public GenericCourseClassAdminsView(final KornellSession session,
			Presenter presenter, CourseClassTO courseClassTO) {
		this.session = session;
		this.user = session.getCurrentUser();
		this.courseClassTO = courseClassTO;
		formHelper = new FormHelper();
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnOK.setText("OK".toUpperCase());
		btnCancel.setText("Limpar".toUpperCase());

		this.courseClassTO = courseClassTO;
		
		initData();
	}

	public void initData() {
		adminsFields.clear();
		FlowPanel fieldPanelWrapper = new FlowPanel();
		fieldPanelWrapper.addStyleName("fieldPanelWrapper courseClassAdminField");
		
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		Label lblLabel = new Label("Administradores da Turma");
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);
		fieldPanelWrapper.add(labelPanel);
		
		
		session.courseClass(courseClassTO.getCourseClass().getUUID()).getAdmins(RoleCategory.BIND_WITH_PERSON,
				new Callback<RolesTO>() {
			@Override
			public void ok(RolesTO to) {
				for (RoleTO roleTO : to.getRoleTOs()) {
					String item = roleTO.getPerson().getEmail() != null ?
							roleTO.getPerson().getEmail() :
								roleTO.getPerson().getCPF();
					if(roleTO.getPerson().getFullName() != null && !"".equals(roleTO.getPerson().getFullName())){
						item += " (" +roleTO.getPerson().getFullName()+")";
					}
					peopleMultipleSelect.addItem(item, roleTO.getPerson().getUUID());
				}
			}
		});
		peopleMultipleSelect = new PeopleMultipleSelect(session);
		fieldPanelWrapper.add(peopleMultipleSelect.asWidget());
		
		fieldPanelWrapper.add(formHelper.getImageSeparator());
		
		adminsFields.add(fieldPanelWrapper);
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) {
		if(session.isInstitutionAdmin()){
			Roles roles = entityFactory.newRoles().as();
			List<Role> rolesList = new ArrayList<Role>();
			ListBox multipleSelect = peopleMultipleSelect.getMultipleSelect();
			for (int i = 0; i < multipleSelect.getItemCount(); i++) {
				String personUUID = multipleSelect.getValue(i);
				Role role = entityFactory.newRole().as();
				CourseClassAdminRole courseClassAdminRole = entityFactory.newCourseClassAdminRole().as();
				role.setPersonUUID(personUUID);
				role.setRoleType(RoleType.courseClassAdmin);
				courseClassAdminRole.setCourseClassUUID(courseClassTO.getCourseClass().getUUID());
				role.setCourseClassAdminRole(courseClassAdminRole);
				rolesList.add(role);  
			}
			roles.setRoles(rolesList);
			session.courseClass(courseClassTO.getCourseClass().getUUID()).updateAdmins(roles, new Callback<Roles>() {
				@Override
				public void ok(Roles to) {
					KornellNotification.show("Os administradores da turma foram atualizados com sucesso.", AlertType.SUCCESS);
				}
			});
		}
		
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		initData();
	}

}