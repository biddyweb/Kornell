package kornell.gui.client.event;

import kornell.core.shared.to.UserInfoTO;

import com.google.gwt.event.shared.GwtEvent;

public class LoginEvent extends GwtEvent<LoginEventHandler> {
	public static final Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
	private UserInfoTO user;
	
	public LoginEvent(UserInfoTO user) {
		this.user = user;
	}

	@Override
	public Type<LoginEventHandler> getAssociatedType() {	
		return TYPE;
	}

	@Override
	protected void dispatch(LoginEventHandler handler) {
		handler.onLogin(user);
	}

}
