package com.univocity.envlp.stamp;

public interface Address extends ObjectWithId {

	enum State {
		USED,
		UNUSED
	}

	State state();

	default boolean used() {
		return state() == State.USED;
	}

	default boolean unused() {
		return state() == State.UNUSED;
	}
}
