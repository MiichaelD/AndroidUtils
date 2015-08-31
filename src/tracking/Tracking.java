package tracking;

import java.util.Map;

import android.app.Activity;
import android.content.Context;

public abstract class Tracking {
	
	public abstract void init(Context context);

	/** Since this service can handle push notifications, we let it know the current registration*/
	public void setPushRegistrationId(String regId) { }
	
	/** If you have surveys or notifications, and you have set AutoShowMixpanelUpdates set to false,
         the onResume function is a good place to call the functions to display surveys or
         in app notifications. It is safe to call both these methods right after each other,
         since they do nothing if a notification or survey is already showing.  */
	public void onResume(Activity act){ }
	
	/** To preserve battery life, the Mixpanel library will store  events rather than send
		 them immediately. This means it is important to call flush() to send any unsent events
         before your application is taken out of memory.  */
	public void onPause(){ }
	
	public void onPlayerIdUpdated(String playerId){ }
	
	/** Track an event. Events have a string name, and an optional set of name/value pairs that describe
	 * the properties of that event.
     *
     * @param eventName The name of the event to send
     * @param properties A Map containing the key value pairs of the properties to include in this event.
     *                   Pass null if no extra properties exist.*/
	public abstract void track(String event, Map<String, Object> properties);
	
	/** Begin timing of an event. Calling timeEvent("Thing") will not send an event, but
     * when you eventually call track("Thing"), your tracked event will be sent with a "$duration"
     * property, representing the number of seconds between your calls.*/
	public void time(String event){ }
}
