/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */
package freenet.support;

import java.util.Arrays;
import java.util.Comparator;

/**
 * byte[], but can be put into HashSet etc *by content*.
 * @author toad
 */
public class ByteArrayWrapper implements Comparable<ByteArrayWrapper> {
	
	private final byte[] buf;
	private final int hashCode;
	
	public static final Comparator<ByteArrayWrapper> FAST_COMPARATOR = new Comparator<ByteArrayWrapper>() {

		@Override
		public int compare(ByteArrayWrapper o1, ByteArrayWrapper o2) {
			if (o1==o2) return 0;
			int h1 = o1.hashCode;
			int h2 = o2.hashCode;
			if(h1 > h2) return 1;
			if(h1 < h2) return -1;
			return o1.compareTo(o2);
		}
		
	};
	
	public ByteArrayWrapper(byte[] data) {
		buf = data;
		hashCode = Fields.hashCode(buf);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof ByteArrayWrapper) {
			return Arrays.equals(((ByteArrayWrapper) o).buf, buf);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	/** DO NOT MODIFY THE RETURNED DATA! */
	public byte[] get() {
		return buf;
	}

	@Override
	public int compareTo(ByteArrayWrapper arg) {
		if(this == arg) return 0;
		return Fields.compareBytes(buf, arg.buf);
	}
}
