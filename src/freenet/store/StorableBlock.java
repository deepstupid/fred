package freenet.store;

public interface StorableBlock {
	
	byte[] getRoutingKey();
	
	byte[] getFullKey();

}
