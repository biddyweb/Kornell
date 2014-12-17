package kornell.gui.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import kornell.gui.client.presentation.util.KornellNotification;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;

public class Kornell implements EntryPoint {
	
	Logger logger = Logger.getLogger(Kornell.class.getName());
	ClientFactory clientFactory = GWT.create(ClientFactory.class);
	
	@Override
	public void onModuleLoad() {
		final long t0 = System.currentTimeMillis();
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
      		startLMS();
      		long t1 = System.currentTimeMillis();
      		logger.info("Kornell GWT started in ["+(t1-t0)+" ms]" );
        }
    });
	}

	private void startLMS() {
		clientFactory.startApp();
		clientFactory.logState();
	}
	
	public ClientFactory getClientFactory(){
		return clientFactory;
	}

}
