package test;

import aQute.bnd.main.*;
import junit.framework.*;

public class TestBuild extends TestCase {

	public void testBndBuild() {
		bnd.main(new String[] {"build"});
	}
}
