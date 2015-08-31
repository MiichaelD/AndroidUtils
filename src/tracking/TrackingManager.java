package tracking;

public abstract class TrackingManager {
	
	protected enum Service {};
	
	private static TrackingManager instance = null;
	
	public abstract void init(Service serv);
	
	public static TrackingManager shared(){
		if (instance == null){
			throw new IllegalStateException("TrackingManager should be initialized before trying to get instance");
		}
		return instance;
	}
	
	public abstract Tracking getService();
}
