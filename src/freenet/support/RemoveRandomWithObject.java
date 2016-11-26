package freenet.support;

public interface RemoveRandomWithObject<T> extends RemoveRandom {

	T getObject();

	boolean isEmpty();

	void setObject(T client);

}
