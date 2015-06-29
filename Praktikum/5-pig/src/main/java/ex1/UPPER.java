package ex1;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.util.WrappedIOException;

public class UPPER extends EvalFunc<String> {
	@Override
	public String exec(Tuple input) throws IOException {
		if (input == null || input.size() == 0)
			return null;
		try {
			String str = (String) input.get(2);
			return str.toUpperCase();
		} catch (Exception e) {
			throw WrappedIOException.wrap("Caught exception processing input row ", e);
		}
	}
}