package com.univocity.envlp.stamp;

import java.util.*;

public interface Addresses {

	List<Address> used();

	List<Address> unused();

	List<Address> all();

	default Address next() {
		return unused().get(0);
	}
}

