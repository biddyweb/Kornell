package kornell.scorm.client.scorm12;
import static kornell.scorm.client.scorm12.Scorm12.logger;
public class SCORM12Binder {
	public static void bind(SCORM12Adapter api){
		nativeBind(api);
		logger.info("SCORM 1.2 API Adapter bound to window.");
	}
	
	public static native void nativeBind(SCORM12Adapter api) /*-{		
		var API = $wnd.API || {};
		//[instance-expr.]@class-name::method-name(param-signature)(arguments)
		API.LMSInitialize = function(param) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSInitialize(Ljava/lang/String;)(param);			
		}
		
		API.LMSFinish = function(param) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSFinish(Ljava/lang/String;)(param);			
		}
		
		API.LMSGetLastError = function(){
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSGetLastError()();
		}
		
		API.LMSGetValue = function(param) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSGetValue(Ljava/lang/String;)(param);			
		} 

		API.LMSCommit = function(param) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSCommit(Ljava/lang/String;)(param);			
		}
		
		API.LMSSetValue = function(param,value) {
			switch(typeof value){
				case "number":
					var dbl = @java.lang.Double::valueOf(D)(value);
					return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSSetDouble(Ljava/lang/String;Ljava/lang/Double;)(param,dbl);
				case "string":
					return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSSetString(Ljava/lang/String;Ljava/lang/String;)(param,value);
				default:
					throw "SCORM 1.2 Unsupported value type"
			}
		}
		
		API.launch = function(param) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::launch(Ljava/lang/String;)(param);			
		}
				
		$wnd.API = API;
	}-*/;
}
