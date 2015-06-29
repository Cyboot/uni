package ex1;

import java.io.IOException;
import java.util.List;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

public class UPPER extends EvalFunc<String> {
	@Override
	public String exec(Tuple input) throws IOException {
		System.out.println(input);
		List<Object> all = input.getAll();
		System.out.println(all);

		if (input == null || input.size() == 0)
			return null;
		try {
			String str = (String) input.get(0);
			return str.toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}