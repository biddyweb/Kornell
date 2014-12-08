package kornell.gui.client.presentation.admin.course.course;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.entity.EntityFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.mvp.PlaceUtils;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesPlace;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class AdminCoursePresenter implements AdminCourseView.Presenter {
	private AdminCourseView view;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private KornellSession session;
	private PlaceController placeController;
	private EventBus bus;
	Place defaultPlace;
	EntityFactory entityFactory;
	private ViewFactory viewFactory;

	public AdminCoursePresenter(KornellSession session,
			PlaceController placeController, EventBus bus, Place defaultPlace,
			EntityFactory entityFactory, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.bus = bus;
		this.defaultPlace = defaultPlace;
		this.entityFactory = entityFactory;
		this.viewFactory = viewFactory;

		init();
	}

	private void init() {
		if (session.isPlatformAdmin()) {
			view = getView();
			view.setPresenter(this);      
			view.init();
		} else {
			GWT.log("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}
	
	@Override
	public Course getNewCourse() {
		return entityFactory.newCourse().as();
	}

	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminCourseView getView() {
		return viewFactory.getAdminCourseView();
	}

	@Override
  public void upsertCourse(Course course) {
		LoadingPopup.show();
		if(course.getUUID() == null){
			session.courses().create(course, new Callback<Course>() {
				@Override
				public void ok(Course course) {
						LoadingPopup.hide();
						KornellNotification.show("Curso criado com sucesso!");
						PlaceUtils.reloadCurrentPlace(bus, placeController);
				}		
				
				@Override
				public void conflict(String errorMessage){
					LoadingPopup.hide();
					KornellNotification.show(errorMessage, AlertType.ERROR, 2500);
				}
			});
		} else {
			session.course(course.getUUID()).update(course, new Callback<Course>() {
				@Override
				public void ok(Course course) {
						LoadingPopup.hide();
						KornellNotification.show("Alterações salvas com sucesso!");
						placeController.goTo(new AdminCoursesPlace());
				}		
				
				@Override
				public void conflict(String errorMessage){
					LoadingPopup.hide();
					KornellNotification.show(errorMessage, AlertType.ERROR, 2500);
				}
			});
		}
  }
}